package org.volunteered.apps.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "language")
class Language(
    @Id
    @GeneratedValue
    var id: Int? = null,

    @NotNull
    var name: String
)

