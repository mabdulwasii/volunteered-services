package org.volunteered.apps.user.service.impl

import com.google.protobuf.BoolValue
import com.google.protobuf.Empty
import com.google.protobuf.boolValue
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.volunteered.apps.user.exception.UserAlreadyExistsException
import org.volunteered.apps.user.exception.UserDoesNotExistException
import org.volunteered.apps.user.repository.LanguageRepository
import org.volunteered.apps.user.repository.SkillRepository
import org.volunteered.apps.user.repository.UserRepository
import org.volunteered.apps.user.service.UserService
import org.volunteered.apps.user.util.DtoTransformer
import org.volunteered.libs.core.extension.whenNotEmpty
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.user.v1.CreateUserRequest
import org.volunteered.libs.proto.user.v1.DeleteUserRequest
import org.volunteered.libs.proto.user.v1.ExistsByIdRequest
import org.volunteered.libs.proto.user.v1.GetUserByEmailRequest
import org.volunteered.libs.proto.user.v1.GetUserByIdRequest

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val skillRepository: SkillRepository,
    private val languageRepository: LanguageRepository
) : UserService {
    override suspend fun createUser(request: CreateUserRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException("User already exists")
        }

        val userEntity = DtoTransformer.transformCreateUserRequestToUserEntity(request)
        val createdUserEntity = userRepository.save(userEntity)

        return DtoTransformer.transformUserEntityToUserDto(createdUserEntity)
    }

    override suspend fun existsById(request: ExistsByIdRequest): BoolValue {
        return boolValue {
            value = userRepository.existsById(request.id)
        }
    }

    override suspend fun getUserById(request: GetUserByIdRequest): User {
        val userEntity = userRepository.findByIdOrNull(request.id)

        return userEntity?.let { DtoTransformer.transformUserEntityToUserDto(it) }
            ?: throw UserDoesNotExistException("User does not exist")
    }

    override suspend fun getUserByEmail(request: GetUserByEmailRequest): User {
        val userEntity = userRepository.findByEmail(request.email)

        return userEntity?.let { DtoTransformer.transformUserEntityToUserDto(it) }
            ?: throw UserDoesNotExistException("User does not exist")
    }

    @Transactional
    override suspend fun updateUser(request: User): User {
        val userEntity = userRepository.findByIdOrNull(request.id)

        return userEntity?.let {
            request.mainSkillsList.whenNotEmpty { skills ->
                it.mainSkills = skillRepository.findByNameIn(skills).toSet()
            }
            request.otherSkillsList.whenNotEmpty { skills ->
                it.otherSkills = skillRepository.findByNameIn(skills).toSet()
            }
            request.spokenLanguagesList.whenNotEmpty { languages ->
                it.spokenLanguages = languageRepository.findByNameIn(languages).toSet()
            }

            DtoTransformer.buildUserEntityFromUserDto(request, it)
            val updatedUserEntity = userRepository.save(it)

            return DtoTransformer.transformUserEntityToUserDto(updatedUserEntity)
        } ?: throw UserDoesNotExistException("Invalid user")
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Empty {
        userRepository.deleteById(request.id)
        return Empty.getDefaultInstance()
    }
}
