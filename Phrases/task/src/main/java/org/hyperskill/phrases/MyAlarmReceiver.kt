package org.hyperskill.phrases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationHelper = NotificationHelper(context!!)
        val randomPhrase = PhraseRepository.phraseList.random()
        val notificationTitle = "Your phrase of the day"
        val notificationId = 393939
        notificationHelper.sendNotification(notificationTitle, randomPhrase, notificationId)
    }
}