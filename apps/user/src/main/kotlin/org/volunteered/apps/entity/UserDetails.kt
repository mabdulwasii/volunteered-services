package org.volunteered.apps.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.volunteered.apps.entity.enumeraion.Gender
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "user", indexes = [Index(columnList = "email", unique = true)])
class UserDetails(

    @Id
    @GeneratedValue
    var id: Long? = null,

    @NotNull
    @Column(name = "first_name")
    var firstName: String? = null,

    @NotNull
    @Column(name = "last_name")
    var lastName: String? = null,

    @NotNull
    @Column(name = "phone")
    var phone: String? = null,

    @Email
    @Column(name = "email")
    var email: String? = null,

    @Size(max = 2)
    var country: String,

    var city: String? = null,

    @Size(max = 2000)
    var bio: String? = null,

    @Enumerated
    var gender: Gender? = null,

    @JsonIgnore
    @ManyToMany(targetEntity = Skill::class, fetch = FetchType.LAZY)
    @JoinTable(name = "user_main_skill")
    var mainSkills: Set<Skill> = HashSet(),

    @JsonIgnore
    @ManyToMany(targetEntity = Skill::class, fetch = FetchType.LAZY)
    @JoinTable(name = "user_other_skill")
    var otherSkills: Set<Skill> = HashSet(),

    @JsonIgnore
    @ManyToMany(targetEntity = Language::class, fetch = FetchType.LAZY)
    @JoinTable(name = "user_language")
    var spokenLanguages: Set<Language> = HashSet(),

    @Column(name = "cv")
    var cv: String? = null,

    @Column(name = "portfolio")
    var portfolio: String? = null,

    @Column(name = "profile_photo")
    var profilePhoto: String? = null,

    @Column(name = "website")
    var website: String? = null,

    @Column(name = "linkedin")
    var linkedin: String? = null,

    @Column(name = "facebook")
    var facebook: String? = null,

    @Column(name = "twitter")
    var twitter: String? = null,

    @Column(name = "skype")
    var skype: String? = null,

    @Column(name = "github")
    var github: String? = null

)
