package org.hyperskill.phrases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhraseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(phrase: Phrase)

    @Delete
    suspend fun delete(phrase: Phrase)

    @Query("SELECT * FROM phrases")
    suspend fun getAll(): List<Phrase>

    @Query("SELECT * FROM phrases WHERE id = :id")
    suspend fun get(id: Long): Phrase
}