package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.phrases.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity(), RecyclerAdapter.OnDeleteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var appDatabase: AppDatabase
    private lateinit var phraseAdapter: RecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDatabase = (application as MyApplication).database

        notificationHelper = NotificationHelper(this)

        setupRecyclerView()

        binding.reminderTextView.setOnClickListener {
            if (appDatabase.getPhraseDao().getAllPhrases().isEmpty()) {
                Toast.makeText(this, "No phrases available to set a reminder.", Toast.LENGTH_SHORT).show()

                binding.reminderTextView.text = "No reminder set"
            } else {
                val timePickerDialog = TimePickerDialog(
                    this@MainActivity,
                    { _, hourOfDay, minute ->
                        val currentTime = Calendar.getInstance()
                        val selectedTime = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                        }

                        if (selectedTime.before(currentTime)) {
                            selectedTime.add(Calendar.DAY_OF_MONTH, 1)
                        }

                        val alarmIntent = Intent(this, MyAlarmReceiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0)
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            selectedTime.timeInMillis,
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                        )

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

        binding.addButton.setOnClickListener {
            showAddPhraseDialog()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        phraseAdapter = RecyclerAdapter(
            appDatabase
                .getPhraseDao()
                .getAllPhrases(), this
        )

        recyclerView.adapter = phraseAdapter
    }

    private fun showAddPhraseDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_phrase, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editText)

        with(builder) {
            setTitle("Add New Phrase")
            setPositiveButton("Add") { dialog, _ ->
                val phrase = editText.text.toString()
                if (phrase.isNotEmpty()) {
                    val newPhrase = Phrase(0, phrase)
                    appDatabase.getPhraseDao().insert(newPhrase)
                    phraseAdapter.updateData(appDatabase.getPhraseDao().getAllPhrases())
                }
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setView(dialogLayout)
            show()
        }
    }

    override fun onDeleteClick(phrase: Phrase) {

        appDatabase.getPhraseDao().delete(phrase)
        val allPhrases = appDatabase.getPhraseDao().getAllPhrases()
        phraseAdapter.updateData(allPhrases)
        if (allPhrases.isEmpty()) {
            binding.reminderTextView.text = "No reminder set"
        }
    }
}