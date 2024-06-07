package org.hyperskill.phrases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val dao = (context?.applicationContext as MyApplication).database.phraseDao()
        GlobalScope.launch(Dispatchers.IO) {
            val phrases = dao.getAll()
            if (phrases.isNotEmpty()) {
                val randomPhrase = phrases.random().phrase
                NotificationHelper(context).sendNotification("Your phrase of the day", randomPhrase, 393939)
            }
        }
    }
}