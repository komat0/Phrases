package org.hyperskill.phrases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhraseDao {
    @Insert
    fun insert(vararg phrases: Phrase)

    @Delete
    fun delete(vararg phrases: Phrase)

    @Query("SELECT phrase FROM phrases")
    fun getPhrase(): List<String>

    @Query("SELECT * FROM phrases")
    fun getAllPhrases(): List<Phrase>

    @Query("SELECT * FROM phrases ORDER BY RANDOM() LIMIT 1")
    fun getRandomPhrase(): Phrase?
}