package org.hyperskill.phrases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface PhraseDao {
    @Insert
    suspend fun insert(phrase: Phrase)

    @Delete
    suspend fun delete(phrase: Phrase)

    @Query("SELECT * FROM phrases")
    suspend fun getAll(): List<Phrase>
}