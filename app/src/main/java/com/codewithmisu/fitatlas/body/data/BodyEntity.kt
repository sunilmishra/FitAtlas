package com.codewithmisu.fitatlas.body.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codewithmisu.fitatlas.body.domain.BodyParts

@Entity(tableName = "table_body")
data class BodyEntity(
    @PrimaryKey
    val name: String,
    val imageUrl: String
)

fun BodyEntity.toDomain(): BodyParts {
    return BodyParts(
        name = name,
        imageUrl = imageUrl
    )
}