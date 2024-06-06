package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.phrases.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity(), RecyclerAdapter.OnDeleteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataList: MutableList<DataClass>
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationHelper: NotificationHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationHelper = NotificationHelper(this)

        setupRecyclerView()

        binding.reminderTextView.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this@MainActivity,
                { _, hourOfDay, minute ->
                    // Получаем текущее время
                    val currentTime = Calendar.getInstance()

                    // Получаем выбранное время из TimePickerDialog
                    val selectedTime = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                    }

                    // Если выбранное время уже прошло, устанавливаем уведомление на следующий день
                    if (selectedTime.before(currentTime)) {
                        selectedTime.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    // Создаем намерение для широковещательного сообщения
                    val alarmIntent = Intent(this, MyAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)

                    // Получаем экземпляр AlarmManager
                    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    // Устанавливаем повторяющийся будильник с ежедневным интервалом
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        selectedTime.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )

                    // Обновляем текст у TextView
                    val timeFormat = String.format("%02d:%02d", hourOfDay, minute)
                    val remindingText = "Reminder set for $timeFormat"
                    binding.reminderTextView.text = remindingText
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

    }

    private fun setupRecyclerView() {
        dataList = mutableListOf()
        getData()
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val adapter = RecyclerAdapter(dataList, this)
        recyclerView.adapter = adapter
    }

    private fun getData() {
        for (i in PhraseRepository.phraseList.indices) {
            val dataClass = DataClass(PhraseRepository.phraseList[i], "Delete")
            dataList.add(dataClass)
        }
    }

    override fun onDeleteClick(position: Int) {
        if (position in 0 until dataList.size) {
            dataList.removeAt(position)
            recyclerView.adapter?.notifyItemRemoved(position)
        }
    }
}
