package org.volunteered.apps

import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.Server
import io.grpc.ServerInterceptors
import io.grpc.health.v1.HealthCheckResponse
import io.grpc.protobuf.services.HealthStatusManager
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.volunteered.apps.service.UserService
import org.volunteered.libs.service.interceptors.UnknownStatusInterceptor
import java.security.Security
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

class UserApplication(private val port: Int) {
    private var serverCredentials = InsecureServerCredentials.create()
    private val healthStatusManager = HealthStatusManager()
    private val userService = UserService()

    private val server: Server = Grpc
        .newServerBuilderForPort(port, serverCredentials)
        .addService(userService)
        .addService(ProtoReflectionService.newInstance())
        .addService(healthStatusManager.healthService)
        .addService(ServerInterceptors.intercept(userService, UnknownStatusInterceptor()))
        .build()

    fun start() {
        server.start()
        logger.info { "Server started, listening on: $port" }
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.atInfo().log("*** shutting down gRPC server since JVM is shutting down")
                this@UserApplication.stop()
                logger.atInfo().log("*** server shut down")
            }
        )
    }

    private fun stop() {
        healthStatusManager.setStatus("", HealthCheckResponse.ServingStatus.NOT_SERVING)
        logger.atInfo().log("Gracefully stopping... (press Ctrl+C again to force)")
        server.shutdown()
        try {
            if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                server.shutdownNow()
                server.awaitTermination(5, TimeUnit.SECONDS)
            }
        } catch (ex: InterruptedException) {
            server.shutdownNow()
        }
    }

    fun blockUntilShutdown() {
        healthStatusManager.setStatus("", HealthCheckResponse.ServingStatus.SERVING)
        server.awaitTermination()
    }
}

fun main() {
    Security.addProvider(BouncyCastleProvider())

    val port = System.getenv("PORT")?.toInt() ?: 5001
    val server = UserApplication(port)
    server.start()
    server.blockUntilShutdown()
}