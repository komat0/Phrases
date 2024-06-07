package org.hyperskill.phrases

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class Phrase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phrase: String
)