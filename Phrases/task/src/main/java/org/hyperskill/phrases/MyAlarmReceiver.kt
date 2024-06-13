package org.hyperskill.phrases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room


class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val notificationHelper = NotificationHelper(context)
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "phrases.db")
                .allowMainThreadQueries()
                .build()
            val phraseDao = db.getPhraseDao()
            val randomPhrase = phraseDao.getRandomPhrase()
            if (randomPhrase != null) {
                val notificationTitle = "Your phrase of the day"
                val notificationId = 393939
                notificationHelper.sendNotification(
                    notificationTitle,
                    randomPhrase.phrase,
                    notificationId
                )
            }
        }
    }
}