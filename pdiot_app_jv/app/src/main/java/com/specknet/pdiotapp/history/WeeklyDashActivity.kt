package com.specknet.pdiotapp.history

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.specknet.pdiotapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeeklyDashActivity : AppCompatActivity() {
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    //Chart variables
    lateinit var pieChart: PieChart
    lateinit var barChart: BarChart

    //Days Active Views
    lateinit var day1_img: ImageView
    lateinit var day1_tv: TextView
    lateinit var day2_img: ImageView
    lateinit var day2_tv: TextView
    lateinit var day3_img: ImageView
    lateinit var day3_tv: TextView
    lateinit var day4_img: ImageView
    lateinit var day4_tv: TextView
    lateinit var day5_img: ImageView
    lateinit var day5_tv: TextView
    lateinit var day6_img: ImageView
    lateinit var day6_tv: TextView
    lateinit var day7_img: ImageView
    lateinit var day7_tv: TextView

    var days_img_arr: ArrayList<ImageView> = arrayListOf()
    var days_tv_arr: ArrayList<TextView> = arrayListOf()


    var days = arrayOfNulls<String>(7)
    var dataMaps = arrayOfNulls<MutableMap<String, Long>>(7)
    var totalDataMap : MutableMap<String, Long> = mutableMapOf()

    var dataTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_dash)

        day1_img = findViewById(R.id.day1)
        day1_tv = findViewById(R.id.tv_day1)
        day2_img = findViewById(R.id.day2)
        day2_tv = findViewById(R.id.tv_day2)
        day3_img = findViewById(R.id.day3)
        day3_tv = findViewById(R.id.tv_day3)
        day4_img = findViewById(R.id.day4)
        day4_tv = findViewById(R.id.tv_day4)
        day5_img = findViewById(R.id.day5)
        day5_tv = findViewById(R.id.tv_day5)
        day6_img = findViewById(R.id.day6)
        day6_tv = findViewById(R.id.tv_day6)
        day7_img = findViewById(R.id.day7)
        day7_tv = findViewById(R.id.tv_day7)

        days_img_arr.add(day1_img)
        days_img_arr.add(day2_img)
        days_img_arr.add(day3_img)
        days_img_arr.add(day4_img)
        days_img_arr.add(day5_img)
        days_img_arr.add(day6_img)
        days_img_arr.add(day7_img)

        days_tv_arr.add(day1_tv)
        days_tv_arr.add(day2_tv)
        days_tv_arr.add(day3_tv)
        days_tv_arr.add(day4_tv)
        days_tv_arr.add(day5_tv)
        days_tv_arr.add(day6_tv)
        days_tv_arr.add(day7_tv)

        pieChart = findViewById(R.id.pieChart_wkdsh)
        barChart = findViewById(R.id.bar_chart_wkdsh)

        firebaseDatabase = FirebaseDatabase.getInstance("https://pdiotdb-29732-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = firebaseDatabase.getReference().child("activities")
        // Getting the strings for the days this week
        getWeekField()
        makeDataDicts()

    }

    fun makeDataDicts() {


        //Call the data base for each day and store data in the array of maps
        databaseReference.child("Respeck").get().addOnSuccessListener {
            Log.i("makeDataDicts", "Database connection successful")
            var i = 0
            for (d in days) {
                if (it.child(d!!).childrenCount != 0L) {
                    Log.i("makeDataDicts", "Day: " + d + " Present. Stored in pos " + i)
                    var dataMap :MutableMap<String, Long> = mutableMapOf()
                    for (child in it.child(d!!).children) {
                        if (dataMap.containsKey(child.value.toString())) {

                            dataMap[child.value.toString()] = dataMap[child.value.toString()]!!.toLong() + 2L
                            totalDataMap[child.value.toString()] = totalDataMap[child.value.toString()]!!.toLong() + 2L
                        } else {
                            dataMap[child.value.toString()] = 2
                            if (totalDataMap.containsKey(child.value.toString())) {

                                totalDataMap[child.value.toString()] = totalDataMap[child.value.toString()]!!.toLong() + 2L

                            } else {
                                totalDataMap[child.value.toString()] = 2
                            }
                        }
                        dataTotal +=2
                    }
                    dataMaps.set(i, dataMap)

                }
                i++
            }

            //DataShould be collected at this point.
            setActiveDays()
            setupPieChart()
            loadPieChart()
            setupBarChart()
            loadBarChart()
        }
    }

    fun setActiveDays () {
        val sdft = SimpleDateFormat("dd-MM-yyyy")
        val sdf = SimpleDateFormat("EEE")
        var i = 0
        for (day in days) {
            val d = sdft.parse(day)
            val dayOfTheWeek = sdf.format(d)
            days_tv_arr[6-i].text = dayOfTheWeek
            if (dataMaps[i] == null) {
                Log.v("setActiveDays", dayOfTheWeek + "  "  + day + " Not Active")
                days_img_arr[6-i].setImageResource(R.drawable.day_missed)
            } else {
                Log.v("setActiveDays", dayOfTheWeek + "  "  + day + " Active")
                //Log.v("setActiveDays", dataMaps[6-i]!!.entries.toString())
                days_img_arr[6-i].setImageResource(R.drawable.day_hit)

            }
            i++
        }



    }



    fun getWeekField() {
        days = arrayOfNulls<String>(7)
        val format: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        val calendar1: Calendar = Calendar.getInstance()
        calendar1.setFirstDayOfWeek(calendar1.time.day)
        //calendar1.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        for (i in 0..6) {
            days[i] = format.format(calendar1.getTime())
            calendar1.add(Calendar.DAY_OF_MONTH, -1)
        }
    }


    fun loadPieChart() {
        var entries = mutableListOf<PieEntry>()
        for (entry in totalDataMap.keys) {
            entries.add( PieEntry(totalDataMap[entry]!!.toFloat()/dataTotal.toFloat(), entry.replace("_", " ")))
        }

        var colors = mutableListOf<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }
        for (color in ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color)
        }

        var pieDataSet : PieDataSet
        pieDataSet =  PieDataSet(entries, "Activities Performed")
        pieDataSet.setColors(colors)

        var pieData : PieData
        pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        pieData.setValueFormatter( PercentFormatter(pieChart))
        pieData.setValueTextSize(15f)
        pieData.setValueTextColor(Color.BLACK)

        pieChart.data = pieData
        pieChart.invalidate()

    }
    fun setupPieChart(){
        pieChart.setDrawHoleEnabled(true)
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(8f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "What You've Been Doing This Week"
        pieChart.setCenterTextSize(22f)
        pieChart.setCenterTextColor(Color.BLACK)
    }
    fun setupBarChart(){
        barChart.setMaxVisibleValueCount(60)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false);

    }

    private fun loadBarChart() {

        val entries: ArrayList<BarEntry> = ArrayList()
        val xLabels: ArrayList<String> = ArrayList()
        var j: Int = 0
        for (entry in totalDataMap.keys) {
            xLabels.add(entry.replace("_"," "))
            entries.add(BarEntry( j.toFloat() , totalDataMap[entry]!!.toFloat() ))
            j+=1
        }

        val barDataSet: BarDataSet
        barDataSet = BarDataSet(entries, "Activities (seconds)")
        for (color in ColorTemplate.VORDIPLOM_COLORS){
            barDataSet.addColor(color)
        }
        val barData: BarData = BarData(barDataSet)
        barData.barWidth = 0.9f
        barChart.data = barData

        barChart.getXAxis().setValueFormatter(IndexAxisValueFormatter(xLabels))
        barChart.xAxis.labelRotationAngle = 90f
        barChart.xAxis.setGranularity(1f); // only intervals of 1 day
        barChart.invalidate()
    }



}