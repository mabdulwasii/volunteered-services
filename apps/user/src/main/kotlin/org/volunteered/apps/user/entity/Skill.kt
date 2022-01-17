package org.volunteered.apps.user.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "skill")
class Skill(
    @Id
    @GeneratedValue
    var id: Int? = null,

    @NotNull
    var name: String
)
