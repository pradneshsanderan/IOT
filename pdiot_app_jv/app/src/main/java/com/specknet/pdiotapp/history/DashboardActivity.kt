package com.specknet.pdiotapp.history


import android.graphics.Color
import android.os.Bundle
import android.util.Log
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


class DashboardActivity : AppCompatActivity() {

    //Database variables
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    lateinit var dateText: TextView

    //Chart variables
    lateinit var pieChart: PieChart
    lateinit var barChart: BarChart

    //Steps
    lateinit var stepCounter: TextView
    var stepsTracker = 0

    //Data Variables
    lateinit var dataMap : MutableMap<String, Long>
    var dataTotal = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        firebaseDatabase = FirebaseDatabase.getInstance("https://pdiotdb-29732-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = firebaseDatabase.getReference().child("activities")


        pieChart = findViewById(R.id.dash_mainpiechart)
        barChart = findViewById(R.id.dash_barchart)

        dateText = findViewById(R.id.dash_date_field)
        dateText.text = intent.getStringExtra("date")

        stepCounter = findViewById(R.id.dash_steps)


        collectData(intent.getStringExtra("date")!!)



    }

    //Gather data and put in readable format
    fun collectData(date: String) {

        dataMap = mutableMapOf<String, Long>()
        databaseReference.child("Respeck").child(date).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.childrenCount}")
            for (child in it.children) {
                if (dataMap.containsKey(child.value.toString())) {
                    dataMap[child.value.toString()] = dataMap[child.value.toString()]!!.toLong() + 2L
                } else {
                    dataMap[child.value.toString()] = 2
                }
                dataTotal +=2
            }
            setupPieChart()
            loadPieChart()
            setupBarChart()
            loadBarChart()
            calculateSteps()
        }.addOnFailureListener{
        }
    }

    fun loadPieChart() {
        var entries = mutableListOf<PieEntry>()
        for (entry in dataMap.keys) {
            entries.add( PieEntry(dataMap[entry]!!.toFloat()/dataTotal.toFloat(), entry))
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
        pieChart.centerText = "Activity Spread"
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
        for (entry in dataMap.keys) {
            xLabels.add(entry.replace("_"," "))
            entries.add(BarEntry( j.toFloat() , dataMap[entry]!!.toFloat() ))
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

    private fun calculateSteps(){
        for (activity in dataMap.keys){
            if (activity == "walking") {
                stepsTracker += 3 * dataMap[activity]!!.toInt()
            } else if (activity == "running"){
                stepsTracker += 5 * dataMap[activity]!!.toInt()
            } else if (activity == "ascending_stairs"){
                stepsTracker += 2 * dataMap[activity]!!.toInt()
            } else if (activity == "descending_stairs"){
                stepsTracker += 2 * dataMap[activity]!!.toInt()
            }
        }
        stepCounter.text = "Total steps: " + stepsTracker.toString()
    }


}
