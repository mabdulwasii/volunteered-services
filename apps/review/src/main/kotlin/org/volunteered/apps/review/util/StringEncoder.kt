package org.volunteered.apps.review.util

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class StringEncoder {
    companion object {
        fun hashValue(stringValue: String?): String {
            val random = SecureRandom()
            val salt = ByteArray(16)
            random.nextBytes(salt)

            val spec: KeySpec = PBEKeySpec(stringValue?.toCharArray(), salt, 65536, 128)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

            val encoded = factory.generateSecret(spec).encoded
            return String(encoded)
        }
    }
}
