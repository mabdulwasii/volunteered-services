package org.volunteered.apps.user.service.impl

import com.google.protobuf.Empty
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.volunteered.apps.user.entity.Language
import org.volunteered.apps.user.entity.Skill
import org.volunteered.apps.user.entity.UserEntity
import org.volunteered.apps.user.exception.UserAlreadyExistsException
import org.volunteered.apps.user.exception.UserDoesNotExistException
import org.volunteered.apps.user.repository.LanguageRepository
import org.volunteered.apps.user.repository.SkillRepository
import org.volunteered.apps.user.repository.UserRepository
import org.volunteered.libs.proto.common.v1.User
import org.volunteered.libs.proto.common.v1.user
import org.volunteered.libs.proto.user.v1.createUserRequest
import org.volunteered.libs.proto.user.v1.deleteUserRequest
import org.volunteered.libs.proto.user.v1.getUserByEmailRequest
import org.volunteered.libs.proto.user.v1.getUserByIdRequest

@SpringBootTest
internal class UserServiceImplTest {
    private val userRepository = mockk<UserRepository>()
    private val skillRepository = mockk<SkillRepository>()
    private val languageRepository = mockk<LanguageRepository>()
    private val service = UserServiceImpl(userRepository, skillRepository, languageRepository)

    private val javaSkill = Skill(1, "Java")
    private val kotlinSkill = Skill(1, "Kotlin")
    private val readingSkill = Skill(1, "Reading")
    private val english = Language(1, "English")
    private val french = Language(1, "French")

    val mainSkills = setOf(javaSkill, kotlinSkill)
    val otherSkills = setOf(readingSkill)
    val spokenLanguages = setOf(english, french)

    @BeforeEach
    fun setUp() {
        clearMocks(userRepository, skillRepository, languageRepository)
    }

    @Test
    fun `should not create user if email exists`(): Unit = runBlocking {
        val request = createUserRequest {
            firstName = DEFAULT_FIRST_NAME
            lastName = DEFAULT_LAST_NAME
            email = DEFAULT_EMAIL
            country = DEFAULT_COUNTRY
        }
        every { userRepository.existsByEmail(DEFAULT_EMAIL) } returns true

        assertThrows<UserAlreadyExistsException> {
            service.createUser(request)
        }
    }

    @Test
    fun `should create user if email does not exists`(): Unit = runBlocking {
        val request = createUserRequest {
            firstName = DEFAULT_FIRST_NAME
            lastName = DEFAULT_LAST_NAME
            email = DEFAULT_EMAIL
            country = DEFAULT_COUNTRY
        }
        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            country = request.country
        )

        every { userRepository.existsByEmail("admin@example.com") } returns false
        every { userRepository.save(any()) } returns userEntity
        val createdUser = service.createUser(request)

        assertNotNull(createdUser)
        assertEquals(createdUser::class.simpleName, User::class.simpleName)
        assertEquals(request.email, createdUser.email)
        assertEquals(request.firstName, createdUser.firstName)
        assertEquals(request.lastName, createdUser.lastName)
        assertEquals(request.country, createdUser.country)
    }

    @Test
    fun `should not get user if Id is invalid`(): Unit = runBlocking {
        val request = getUserByIdRequest {
            id = INVALID_ID
        }

        every { userRepository.findByIdOrNull(request.id) } returns null

        assertThrows<UserDoesNotExistException> { service.getUserById(request) }
    }

    @Test
    fun `should get user if id is valid`(): Unit = runBlocking {
        val request = getUserByIdRequest {
            id = INVALID_ID
        }
        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY
        )

        every { userRepository.findByIdOrNull(request.id) } returns userEntity

        val retrievedUser = service.getUserById(request)

        assertNotNull(retrievedUser)
        assertEquals(retrievedUser::class.simpleName, User::class.simpleName)
        assertEquals(DEFAULT_EMAIL, retrievedUser.email)
        assertEquals(DEFAULT_FIRST_NAME, retrievedUser.firstName)
        assertEquals(DEFAULT_LAST_NAME, retrievedUser.lastName)
        assertEquals(DEFAULT_COUNTRY, retrievedUser.country)
        assertEquals(DEFAULT_ID, retrievedUser.id)
    }

    @Test
    fun `should throw exception if user does not exist`() : Unit = runBlocking {
        val request = getUserByEmailRequest {
            email = DEFAULT_EMAIL
        }

        every { userRepository.findByEmail(request.email) } returns null

        assertThrows<UserDoesNotExistException> { service.getUserByEmail(request) }
    }

    @Test
    fun `should get user`(): Unit = runBlocking {
        val request = getUserByEmailRequest {
            email = DEFAULT_EMAIL
        }
        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY
        )

        every { userRepository.findByEmail(request.email) } returns userEntity

        val retrievedUser = service.getUserByEmail(request)

        assertNotNull(retrievedUser)
        assertEquals(retrievedUser::class.simpleName, User::class.simpleName)
        assertEquals(DEFAULT_EMAIL, retrievedUser.email)
        assertEquals(DEFAULT_FIRST_NAME, retrievedUser.firstName)
        assertEquals(DEFAULT_LAST_NAME, retrievedUser.lastName)
        assertEquals(DEFAULT_COUNTRY, retrievedUser.country)
    }

    @Test
    fun `should not update user if user is invalid`() : Unit = runBlocking {
        val user = user {
            id = DEFAULT_ID
            firstName = DEFAULT_FIRST_NAME
            lastName = DEFAULT_LAST_NAME
            email = DEFAULT_EMAIL
            country = DEFAULT_COUNTRY
        }

        every { userRepository.findByIdOrNull(DEFAULT_ID) } returns null

        assertThrows<UserDoesNotExistException> { service.updateUser(user) }
    }

    @Test
    fun `should update user if user is valid`(): Unit = runBlocking {

        val user = user {
            id = DEFAULT_ID
            firstName = DEFAULT_FIRST_NAME
            lastName = DEFAULT_LAST_NAME
            email = DEFAULT_EMAIL
            country = DEFAULT_COUNTRY
        }

        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY,
            mainSkills = mainSkills,
            otherSkills = otherSkills,
            spokenLanguages = spokenLanguages
        )

        every { userRepository.findByIdOrNull(DEFAULT_ID) } returns userEntity
        every { userRepository.save(any()) } returns userEntity

        val updatedUser = service.updateUser(user)

        assertNotNull(updatedUser)
        assertEquals(DEFAULT_FIRST_NAME, updatedUser.firstName)
        assertEquals(DEFAULT_LAST_NAME, updatedUser.lastName)
        assertEquals(DEFAULT_EMAIL, updatedUser.email)
        assertEquals(DEFAULT_COUNTRY, updatedUser.country)
        assertEquals(mainSkills.size, updatedUser.mainSkillsList.size)
        assertEquals(otherSkills.size, updatedUser.otherSkillsList.size)
        assertEquals(spokenLanguages.size, updatedUser.spokenLanguagesList.size)
    }

    @Test
    fun `should delete user`(): Unit = runBlocking {
        val request = deleteUserRequest {
            id = DEFAULT_ID
        }

        every { userRepository.deleteById(DEFAULT_ID) } returns Unit
        val result = service.deleteUser(request)

        assertNotNull(result)
        assertEquals(result, Empty.getDefaultInstance())
    }
    companion object {
        const val DEFAULT_FIRST_NAME = "Babel"
        const val DEFAULT_LAST_NAME = "Wright"
        const val DEFAULT_EMAIL = "admin@example.com"
        const val DEFAULT_COUNTRY = "NG"
        const val INVALID_ID = 999L
        const val DEFAULT_ID = 1L
    }
}
