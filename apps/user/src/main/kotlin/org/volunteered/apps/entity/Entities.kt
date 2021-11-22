package org.volunteered.apps.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "user")
class UserDetails (
    @NotNull var firstName : String,
    @NotNull var lastName : String,
    @NotNull var phone : String,
    @Email var email : String,
    var country : String,
    var city : String? = null,
    @Size(max = 2000) var bio : String? = null,
    @Enumerated var gender : Gender? = null,

    @JsonIgnore
    @ManyToMany(targetEntity= Skill::class, fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "user_main_skill")
    var mainSkills : Set<Skill> = HashSet(),

    @JsonIgnore
    @ManyToMany(targetEntity= Skill::class, fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "user_other_skill")
    var otherSkills : Set<Skill> = HashSet(),

    @JsonIgnore
    @ManyToMany(targetEntity= Language::class, fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(name = "user_language")
    var spokenLanguages : Set<Language> = HashSet(),

    @Column(name = "resume_url") var resumeUrl : String? = null,
    @Column(name = "portfolio_url")var portfolioUrl : String? = null,
    @Column(name = "profile_photo_url") var profilePhotoUrl : String? = null,
    @OneToOne var userSocialMedia: UserSocialMedia? = null,
    @Id @GeneratedValue var id: Long? = null

    )




@Entity
@Table(name = "user_social_media")
class UserSocialMedia (
    @Id @GeneratedValue var id: Long? = null,
    var linkedinUrl : String? = null,
    var facebookUrl : String? = null,
    var twitterUrl : String? = null,
    var skypeUrl : String? = null,
    var websiteUrl : String? = null,
)


@Entity
@Table(name = "skill")
class Skill (
    @NotNull var name : String,
    @Id @GeneratedValue var id: Long? = null,
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
