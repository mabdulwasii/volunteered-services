package org.volunteered.apps.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.volunteered.apps.entity.UserEntity
import org.volunteered.libs.proto.common.v1.Gender
import org.volunteered.libs.proto.common.v1.user
import org.volunteered.libs.proto.common.v1.websiteAndSocialMediaUrls
import org.volunteered.libs.proto.user.v1.createUserRequest

@SpringBootTest
internal class DtoTransformerTest {
    @Test
    fun `should transform createUserRequest to UserEntity`(){
        val createUserRequest = createUserRequest {
            firstName = DEFAULT_FIRST_NAME
            lastName = DEFAULT_LAST_NAME
            email = DEFAULT_EMAIL
            country = DEFAULT_COUNTRY
        }

        val transFormedUserEntity =
            DtoTransformer.transformCreateUserRequestToUserEntity(createUserRequest)

        assertEquals(DEFAULT_FIRST_NAME, transFormedUserEntity.firstName)
        assertEquals(DEFAULT_LAST_NAME, transFormedUserEntity.lastName)
        assertEquals(DEFAULT_EMAIL, transFormedUserEntity.email)
        assertEquals(DEFAULT_COUNTRY, transFormedUserEntity.country)
    }

    @Test
    fun `should transform User entity to user dto`() {
        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY
        )

        val transformedUserDTO = DtoTransformer.transformUserEntityToUserDto(userEntity)

        assertEquals(DEFAULT_ID, transformedUserDTO.id)
        assertEquals(DEFAULT_FIRST_NAME, transformedUserDTO.firstName)
        assertEquals(DEFAULT_LAST_NAME, transformedUserDTO.lastName)
        assertEquals(DEFAULT_EMAIL, transformedUserDTO.email)
        assertEquals(DEFAULT_COUNTRY, transformedUserDTO.country)
    }

    @Test
    fun `should throw exception if userEntity id is null`() {
        val userEntity = UserEntity(
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY
        )

        assertThrows<NullPointerException> { DtoTransformer.transformUserEntityToUserDto(userEntity) }
    }

    @Test
    fun `should build user entity from user dto`() {
        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY,
        )
        val user = user {
            firstName = "Bright"
            lastName = "James"
            email = "user@example.com"
            country = "FR"
            phone = "+704564747"
            city = "Paris"
            bio = "Some bio"
            gender = Gender.MALE
            cv = "cv"
            portfolio = "portfolio"
            profilePhoto = "profile photo"
            webAndSocialUrls = websiteAndSocialMediaUrls {
                website = "www.website.com"
                linkedin = "www.linked.com/profile"
                facebook = "www.facebook.com/profile"
                twitter = "www.twitter.com/profile"
                skype = "www.skype.com/profile"
                github = "www.github.com/profile"
            }
        }

        DtoTransformer.buildUserEntityFromUserDto(user, userEntity)

        assertEquals(DEFAULT_ID, userEntity.id)
        assertEquals(user.firstName, userEntity.firstName)
        assertEquals(user.lastName, userEntity.lastName)
        assertEquals(user.email, userEntity.email)
        assertEquals(user.country, userEntity.country)
        assertEquals(user.phone, userEntity.phone)
        assertEquals(user.city, userEntity.city)
        assertEquals(user.bio, userEntity.bio)
        assertEquals(user.gender, userEntity.gender)
        assertEquals(user.cv, userEntity.cv)
        assertEquals(user.portfolio, userEntity.portfolio)
        assertEquals(user.profilePhoto, userEntity.profilePhoto)
        assertEquals(user.webAndSocialUrls.website, userEntity.website)
        assertEquals(user.webAndSocialUrls.website, userEntity.website)
        assertEquals(user.webAndSocialUrls.linkedin, userEntity.linkedin)
        assertEquals(user.webAndSocialUrls.facebook, userEntity.facebook)
        assertEquals(user.webAndSocialUrls.twitter, userEntity.twitter)
        assertEquals(user.webAndSocialUrls.skype, userEntity.skype)
        assertEquals(user.webAndSocialUrls.github, userEntity.github)
    }

    @Test
    fun `should update user entity field from user dto if value is set`() {
        val userEntity = UserEntity(
            id = DEFAULT_ID,
            firstName = DEFAULT_FIRST_NAME,
            lastName = DEFAULT_LAST_NAME,
            email = DEFAULT_EMAIL,
            country = DEFAULT_COUNTRY,
            cv = CV,
            bio = BIO
        )
        val user = user {
            firstName = "Bright"
            lastName = ""
            bio = ""
            cv = ""
        }

        DtoTransformer.buildUserEntityFromUserDto(user, userEntity)

        assertEquals(user.firstName, userEntity.firstName)
        assertNotEquals(user.lastName, userEntity.lastName)
        assertNotEquals(user.bio, userEntity.bio)
        assertNotEquals(user.cv, userEntity.cv)
        assertEquals(DEFAULT_LAST_NAME, userEntity.lastName)
        assertEquals(DEFAULT_EMAIL, userEntity.email)
        assertEquals(DEFAULT_COUNTRY, userEntity.country)
        assertEquals(BIO, userEntity.bio)
        assertEquals(CV, userEntity.cv)
    }
    companion object {
        const val DEFAULT_FIRST_NAME = "Babel"
        const val DEFAULT_LAST_NAME = "Wright"
        const val DEFAULT_EMAIL = "admin@example.com"
        const val DEFAULT_COUNTRY = "NG"
        const val DEFAULT_ID = 1L
        const val CV = "cv"
        const val BIO = "my bio"
    }
}