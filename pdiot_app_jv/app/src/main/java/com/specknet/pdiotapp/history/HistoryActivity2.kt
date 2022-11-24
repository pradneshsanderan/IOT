package com.specknet.pdiotapp.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import com.google.firebase.database.*
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.RESpeckPacketHandler
import java.util.*

class HistoryActivity2 : AppCompatActivity() {
    private lateinit var calendarView: CalendarView

    //Info card on the bottom
    private lateinit var vcDate: TextView
    private lateinit var vcActivities: TextView
    private lateinit var vcTotalTime: TextView
    private lateinit var vcButton: Button

    //Database variables
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    //Extras to pass onto the dashboard.
    var dateToPass = "18-11-2022"
    lateinit var dbToPass: DataSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history2)

        calendarView = findViewById(R.id.calendarView)
        vcDate = findViewById(R.id.viewcard_date)
        vcActivities = findViewById(R.id.viewcard_activities)
        vcTotalTime = findViewById(R.id.viewcard_totaltime)
        vcButton = findViewById(R.id.viewcard_button)

        firebaseDatabase = FirebaseDatabase.getInstance("https://pdiotdb-29732-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = firebaseDatabase.getReference().child("activities")


        vcButton.isClickable = false

        calendarView.setOnDateChangeListener { calendarView, year, month, day ->
            val month = month + 1
            vcDate.text = "$day-$month-$year"

            var timer: Long
            vcTotalTime.text = " "
            vcButton.isClickable = false

            //TODO: DATA fetch for this date.
            databaseReference.child("Respeck").child("$day-$month-$year").get()
                .addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    if (it.childrenCount != 0L) {
                        vcActivities.text = "You have activities recorded on this date"

                        //Provide a link to the dashboard.
                        dateToPass = "$day-$month-$year"
                        dbToPass = it

                        vcButton.setOnClickListener() {
                            var newIntent = Intent(this, DashboardActivity::class.java)
                            newIntent.putExtra("date", dateToPass)
                            startActivity(newIntent)
                        }

                        vcButton.isClickable = true

                        timer = 2 * it.childrenCount
                        val minutes = timer / 60
                        val seconds = timer % 60
                        vcTotalTime.text = "Total activity time: $minutes m $seconds s"
                    } else {
                        vcActivities.text = "No activities recorded on this date"
                    }
                }.addOnFailureListener {
            }
        }
    }


}