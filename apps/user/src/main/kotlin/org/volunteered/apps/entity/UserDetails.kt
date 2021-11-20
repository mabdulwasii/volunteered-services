package org.volunteered.apps.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "user")
class UserDetails (
    @Id @GeneratedValue var id: Long,
    @NotNull var firstName : String,
    @NotNull var lastName : String,
    @NotNull var phone : String,
    @Email var email : String,
    var country : String,
    var city : String,
    @Size(max = 2000) var bio : String,
    @Enumerated var gender : Gender,

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "user_main_skill")
    var mainSkills : Set<Skill> = HashSet(),

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "user_other_skill")
    var otherSkills : Set<Skill> = HashSet(),

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "user_language")
    var spokenLanguages : Set<Language> = HashSet(),

    @Column(name = "resume_url") var resumeUrl : String,
    @Column(name = "portfolio_url")var portfolioUrl : String,
    @Column(name = "profile_photo_url") var profilePhotoUrl : String,
    @OneToOne var userSocialMedia: UserSocialMedia
    )




@Entity
@Table(name = "user_social_media")
class UserSocialMedia (
    @Id @GeneratedValue var id: Long,
    var linkedinUrl : String? = null,
    var facebookUrl : String? = null,
    var twitterUrl : String? = null,
    var skypeUrl : String? = null,
    var websiteUrl : String? = null,
)



@Entity
@Table(name = "skill")
class Skill (
    @Id @GeneratedValue var id: Long,
    @NotNull var name : String,
)



@Entity
@Table(name = "language")
class Language (
    @Id @GeneratedValue var id: Long,
    @NotNull var name : String,
)


enum class Gender{
    MALE, FEMALE, NON_BINARY
}
