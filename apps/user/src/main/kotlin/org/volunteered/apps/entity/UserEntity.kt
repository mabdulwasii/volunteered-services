package org.volunteered.apps.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.volunteered.libs.common.v1.Gender
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.Size

@Entity
@Table(name = "user", indexes = [Index(columnList = "email", unique = true)])
class UserEntity(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @Column(name = "first_name")
    var firstName: String,

    @Column(name = "last_name")
    var lastName: String,

    @Email
    var email: String,

    @Size(max = 2)
    var country: String,

    var phone: String? = null,

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

    var cv: String? = null,

    var portfolio: String? = null,

    var profilePhoto: String? = null,

    var website: String? = null,

    var linkedin: String? = null,

    var facebook: String? = null,

    var twitter: String? = null,

    var skype: String? = null,

    var github: String? = null
)
