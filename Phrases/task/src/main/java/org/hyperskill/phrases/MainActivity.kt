package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            val dao = (application as MyApplication).database.phraseDao()
            GlobalScope.launch(Dispatchers.IO) {
                val phrases = dao.getAll()
                withContext(Dispatchers.Main) {
                    if (phrases.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "No phrases available to set a reminder",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.reminderTextView.text = "No reminder set"
                    } else {
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
                                val alarmIntent =
                                    Intent(this@MainActivity, MyAlarmReceiver::class.java)
                                val pendingIntent =
                                    PendingIntent.getBroadcast(this@MainActivity, 0, alarmIntent, 0)

                                // Получаем экземпляр AlarmManager
                                val alarmManager =
                                    getSystemService(Context.ALARM_SERVICE) as AlarmManager

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
            }
        }

        binding.addButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_add_phrase, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)

            with(builder) {
                setTitle("Add Phrase")
                setView(dialogLayout)
                setPositiveButton("Add") { _, _ ->
                    val phraseText = editText.text.toString()
                    if (phraseText.isNotEmpty()) {
                        val dao = (application as MyApplication).database.phraseDao()
                        GlobalScope.launch(Dispatchers.IO) {
                            dao.insert(Phrase(phrase = phraseText))
                            loadPhrases()
                        }
                    }
                }
                setNegativeButton("Cancel", null)
                show()
            }
        }

        loadPhrases()

    }

    private fun loadPhrases() {
        val dao = (application as MyApplication).database.phraseDao()
        GlobalScope.launch(Dispatchers.IO) {
            val phrases = dao.getAll()
            withContext(Dispatchers.Main) {
                dataList.clear()
                dataList.addAll(phrases.map { DataClass(it.phrase, "Delete") })
                recyclerView.adapter?.notifyDataSetChanged()
            }
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
            val phrase = dataList[position]
            val dao = (application as MyApplication).database.phraseDao()
            GlobalScope.launch(Dispatchers.IO) {
                dao.delete(Phrase(phrase = phrase.phrase))
                val phrases = dao.getAll()
                if (phrases.isEmpty()) {
                    val alarmManager =
                        getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent =
                        Intent(this@MainActivity, MyAlarmReceiver::class.java)
                    val pendingIntent =
                        PendingIntent.getBroadcast(this@MainActivity, 0, alarmIntent, 0)
                    alarmManager.cancel(pendingIntent)
                    withContext(Dispatchers.Main) {
                        binding.reminderTextView.text = "No reminder set"
                    }
                }
                loadPhrases()
            }
        }
    }
}
