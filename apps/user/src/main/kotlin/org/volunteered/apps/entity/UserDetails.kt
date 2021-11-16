package org.volunteered.apps.entity

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

@Entity
@Table(name = "user")
class UserDetails (
    @Id @GeneratedValue var id: Long? = null,

    @NotNull var firstName : String,
    @NotNull var lastName : String,
    @NotNull var phone : String,
    @Email var email : String,
    var country : String)
