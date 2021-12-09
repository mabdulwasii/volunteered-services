package org.volunteered.apps.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "benefit")
class Benefit(
    @Id
    @GeneratedValue
    var id: Long? = null,

    @NotNull
    var name: String
)