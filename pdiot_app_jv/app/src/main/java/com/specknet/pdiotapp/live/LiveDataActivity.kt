package com.specknet.pdiotapp.live

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.ThingyLiveData
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class LiveDataActivity : AppCompatActivity() {

    // global graph variables
    lateinit var dataSet_res_accel_x: LineDataSet
    lateinit var dataSet_res_accel_y: LineDataSet
    lateinit var dataSet_res_accel_z: LineDataSet

    lateinit var dataSet_thingy_accel_x: LineDataSet
    lateinit var dataSet_thingy_accel_y: LineDataSet
    lateinit var dataSet_thingy_accel_z: LineDataSet

    var time = 0f
    lateinit var allRespeckData: LineData

    lateinit var allThingyData: LineData

    lateinit var respeckChart: LineChart
    lateinit var thingyChart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var thingyLiveUpdateReceiver: BroadcastReceiver
    lateinit var looperRespeck: Looper
    lateinit var looperThingy: Looper
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var databaseReference2: DatabaseReference
    lateinit var dateToday: String

    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)
    val filterTestThingy = IntentFilter(Constants.ACTION_THINGY_BROADCAST)

    //val accel_x_values arrays:
    val accel_x_arr_respeck = mutableListOf<Float>()
    val accel_y_arr_respeck = mutableListOf<Float>()
    val accel_z_arr_respeck = mutableListOf<Float>()
    val gyro_x_arr_respeck = mutableListOf<Float>()
    val gyro_y_arr_respeck = mutableListOf<Float>()
    val gyro_z_arr_respeck = mutableListOf<Float>()

    val accel_x_arr_thingy = mutableListOf<Float>()
    val accel_y_arr_thingy = mutableListOf<Float>()
    val accel_z_arr_thingy = mutableListOf<Float>()
    val gyro_x_arr_thingy = mutableListOf<Float>()
    val gyro_y_arr_thingy = mutableListOf<Float>()
    val gyro_z_arr_thingy = mutableListOf<Float>()
    val temp = Calendar.getInstance()
    val year = temp[Calendar.YEAR]
    val month = temp[Calendar.MONTH]
    val dayOfMonth = temp[Calendar.DAY_OF_MONTH]
    val today = "$dayOfMonth-$month-$year"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data)

        setupCharts()

        // set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            @SuppressLint("NewApi")
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData)

                    // get all relevant intent contents
                    // get all relevant intent contents
                    val accel_x = liveData.accelX
                    val accel_y = liveData.accelY
                    val accel_z = liveData.accelZ
                    val gyro_x = liveData.gyro.x
                    val gyro_y = liveData.gyro.x
                    val gyro_z = liveData.gyro.x

                    accel_x_arr_respeck.add(accel_x)
                    accel_y_arr_respeck.add(accel_y)
                    accel_z_arr_respeck.add(accel_z)
                    gyro_x_arr_respeck.add(gyro_x)
                    gyro_y_arr_respeck.add(gyro_y)
                    gyro_z_arr_respeck.add(gyro_z)

                    time += 1
                    updateGraph("respeck", accel_x, accel_y, accel_z)

                    Log.d("MESSAGE: ", "UPDATED GRAPHS RESPECK")

                    if(accel_x_arr_respeck.size == 50) {

                        Log.d("MESSAGE: ", "SIZE = 50")
                        val encapsulate = JSONArray()
                        for(i in 0..49) {
                            val sensor_readings = JSONArray()
                            sensor_readings.put(accel_x_arr_respeck[i])
                            sensor_readings.put(accel_y_arr_respeck[i])
                            sensor_readings.put(accel_z_arr_respeck[i])
                            sensor_readings.put(gyro_x_arr_respeck[i])
                            sensor_readings.put(gyro_y_arr_respeck[i])
                            sensor_readings.put(gyro_z_arr_respeck[i])

                            encapsulate.put(sensor_readings)
                        }
                        val data_json_encapsulate = JSONObject()
                        data_json_encapsulate.put("data", encapsulate)
                        Log.d("MESSAGE: ", "SENDING TO API")
                        postVolley_respeck(data_json_encapsulate)

                        accel_x_arr_respeck.clear()
                        accel_y_arr_respeck.clear()
                        accel_z_arr_respeck.clear()
                        accel_x_arr_respeck.clear()
                        accel_y_arr_respeck.clear()
                        accel_z_arr_respeck.clear()

                    }
                }
            }
        }

        // register receiver on another thread
        val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
        handlerThreadRespeck.start()
        looperRespeck = handlerThreadRespeck.looper
        val handlerRespeck = Handler(looperRespeck)
        this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)

        // set up the broadcast receiver
        thingyLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_THINGY_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.THINGY_LIVE_DATA) as ThingyLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData)

                    // get all relevant intent contents
                    val accel_x = liveData.accelX
                    val accel_y = liveData.accelY
                    val accel_z = liveData.accelZ
                    val gyro_x = liveData.gyro.x
                    val gyro_y = liveData.gyro.x
                    val gyro_z = liveData.gyro.x

                    accel_x_arr_thingy.add(accel_x)
                    accel_y_arr_thingy.add(accel_y)
                    accel_z_arr_thingy.add(accel_z)
                    gyro_x_arr_thingy.add(gyro_x)
                    gyro_y_arr_thingy.add(gyro_y)
                    gyro_z_arr_thingy.add(gyro_z)

                    time += 1
                    updateGraph("thingy", accel_x, accel_y, accel_z)

                    Log.d("MESSAGE: ", "UPDATED GRAPHS THINGY")

                    if(accel_x_arr_thingy.size == 50) {
                        Log.d("MESSAGE: ", "SIZE = 50")
                        val encapsulate = JSONArray()
                        for(i in 0..49) {
                            val sensor_readings = JSONArray()
                            sensor_readings.put(accel_x_arr_thingy[i])
                            sensor_readings.put(accel_y_arr_thingy[i])
                            sensor_readings.put(accel_z_arr_thingy[i])
                            sensor_readings.put(gyro_x_arr_thingy[i])
                            sensor_readings.put(gyro_y_arr_thingy[i])
                            sensor_readings.put(gyro_z_arr_thingy[i])

                            encapsulate.put(sensor_readings)
                        }
                        val data_json_encapsulate = JSONObject()
                        data_json_encapsulate.put("data", encapsulate)
                        Log.d("MESSAGE: ", "SENDING TO API")
                        postVolley_thingy(data_json_encapsulate)

                        accel_x_arr_thingy.clear()
                        accel_y_arr_thingy.clear()
                        accel_z_arr_thingy.clear()
                        accel_x_arr_thingy.clear()
                        accel_y_arr_thingy.clear()
                        accel_z_arr_thingy.clear()

                    }

                }
            }
        }

        // register receiver on another thread
        val handlerThreadThingy = HandlerThread("bgThreadThingyLive")
        handlerThreadThingy.start()
        looperThingy = handlerThreadThingy.looper
        val handlerThingy = Handler(looperThingy)
        this.registerReceiver(thingyLiveUpdateReceiver, filterTestThingy, null, handlerThingy)

    }


    fun postVolley_respeck(itemsArray: JSONObject){

        val url = "https://respeck-dot-flask-ml-predict.nw.r.appspot.com/predict"
        firebaseDatabase = FirebaseDatabase.getInstance("https://pdiotdb-29732-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = firebaseDatabase.getReference().child("activities").child("Respeck")
        //databaseReference2 = firebaseDatabase.getReference().child("steps")
        // change strResp.toString() to the variable that represents the number of steps. add toString()
//        databaseReference.child(today).child(Calendar.getInstance().time.toString()).setValue(strResp.toString())

        val jsonRequest = object : JsonObjectRequest(
            Request.Method.POST, url, itemsArray,
            Response.Listener { response ->
                // response
                var strResp = response.getString("prediction")
                Log.d("PREDICTION", strResp)

                var textView1 = findViewById<TextView>(R.id.textView7)
                textView1.setText(strResp)


                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())

                databaseReference.child(today).child(Calendar.getInstance().time.toString()).setValue(strResp.toString())

                var textView3 = findViewById<TextView>(R.id.textView6)
                textView3.setText(currentDate)

            },
            Response.ErrorListener { error ->
                Log.d("ERROR API", error.toString())
                //Log.d("ERROR API", "error => ${error.}")
            }) {
            @Throws(AuthFailureError::class)
            override fun getBodyContentType(): String {
                return "application/json"
            }
            /*
            override fun getHeaders(): Map<String, String> {
                val apiHeader = HashMap<String, String>()
                apiHeader["Authorization"] = "Bearer $cusToken"
                return apiHeader
            }*/
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonRequest)
    }

    fun postVolley_thingy(itemsArray: JSONObject){

        val url = "https://thingy-dot-flask-ml-predict.nw.r.appspot.com/predict"
        firebaseDatabase = FirebaseDatabase.getInstance("https://pdiotdb-29732-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = firebaseDatabase.getReference().child("activities").child("Thingy")
        val jsonRequest = object : JsonObjectRequest(
            Request.Method.POST, url, itemsArray,
            Response.Listener { response ->
                // response
                var strResp = response.getString("prediction")
                Log.d("PREDICTION", strResp)

                var textView2 = findViewById<TextView>(R.id.textView9)
                textView2.setText(strResp)

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())

                var textView4 = findViewById<TextView>(R.id.textView8)
                textView4.setText(currentDate)
                databaseReference.child(today).child(Calendar.getInstance().time.toString()).setValue(strResp.toString())

                var textView1 = findViewById<TextView>(R.id.textView7)

                val res1: String = textView1.text.toString()
                val res2: String = textView2.text.toString()

                val final_res: String

                if(res1.equals("sitting") || res1.equals("lying_on_stomach") || res1.equals("lying_on_left_side") || res1.equals("lying_on_right_side") ||
                    res1.equals("lying_on_back") || res1.equals("walking")){
                    final_res = res1
                    //databaseReference.child(today).child(Calendar.getInstance().time.toString()).setValue(final_res.toString())
                } else {
                    final_res = res2
                    //databaseReference.child(today).child(Calendar.getInstance().time.toString()).setValue(final_res.toString())
                }

                var textView_final_timestamp = findViewById<TextView>(R.id.textView11)
                var textView_final_res = findViewById<TextView>(R.id.textView12)
                textView_final_timestamp.setText(currentDate)
                textView_final_res.setText(final_res)

            },
            Response.ErrorListener { error ->
                Log.d("ERROR API", error.toString())
                //Log.d("ERROR API", "error => ${error.}")
            }) {
            @Throws(AuthFailureError::class)
            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonRequest)
    }


    fun setupCharts() {
        respeckChart = findViewById(R.id.respeck_chart)
        thingyChart = findViewById(R.id.thingy_chart)

        // Respeck

        time = 0f
        val entries_res_accel_x = ArrayList<Entry>()
        val entries_res_accel_y = ArrayList<Entry>()
        val entries_res_accel_z = ArrayList<Entry>()

        dataSet_res_accel_x = LineDataSet(entries_res_accel_x, "Accel X")
        dataSet_res_accel_y = LineDataSet(entries_res_accel_y, "Accel Y")
        dataSet_res_accel_z = LineDataSet(entries_res_accel_z, "Accel Z")

        dataSet_res_accel_x.setDrawCircles(false)
        dataSet_res_accel_y.setDrawCircles(false)
        dataSet_res_accel_z.setDrawCircles(false)

        dataSet_res_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_res_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_res_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsRes = ArrayList<ILineDataSet>()
        dataSetsRes.add(dataSet_res_accel_x)
        dataSetsRes.add(dataSet_res_accel_y)
        dataSetsRes.add(dataSet_res_accel_z)

        allRespeckData = LineData(dataSetsRes)
        respeckChart.data = allRespeckData
        respeckChart.invalidate()

        // Thingy

        time = 0f
        val entries_thingy_accel_x = ArrayList<Entry>()
        val entries_thingy_accel_y = ArrayList<Entry>()
        val entries_thingy_accel_z = ArrayList<Entry>()

        dataSet_thingy_accel_x = LineDataSet(entries_thingy_accel_x, "Accel X")
        dataSet_thingy_accel_y = LineDataSet(entries_thingy_accel_y, "Accel Y")
        dataSet_thingy_accel_z = LineDataSet(entries_thingy_accel_z, "Accel Z")

        dataSet_thingy_accel_x.setDrawCircles(false)
        dataSet_thingy_accel_y.setDrawCircles(false)
        dataSet_thingy_accel_z.setDrawCircles(false)

        dataSet_thingy_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_thingy_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_thingy_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsThingy = ArrayList<ILineDataSet>()
        dataSetsThingy.add(dataSet_thingy_accel_x)
        dataSetsThingy.add(dataSet_thingy_accel_y)
        dataSetsThingy.add(dataSet_thingy_accel_z)

        allThingyData = LineData(dataSetsThingy)
        thingyChart.data = allThingyData
        thingyChart.invalidate()
    }

    fun updateGraph(graph: String, x: Float, y: Float, z: Float) {
        // take the first element from the queue
        // and update the graph with it
        if (graph == "respeck") {
            dataSet_res_accel_x.addEntry(Entry(time, x))
            dataSet_res_accel_y.addEntry(Entry(time, y))
            dataSet_res_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allRespeckData.notifyDataChanged()
                respeckChart.notifyDataSetChanged()
                respeckChart.invalidate()
                respeckChart.setVisibleXRangeMaximum(150f)
                respeckChart.moveViewToX(respeckChart.lowestVisibleX + 40)
            }
        } else if (graph == "thingy") {
            dataSet_thingy_accel_x.addEntry(Entry(time, x))
            dataSet_thingy_accel_y.addEntry(Entry(time, y))
            dataSet_thingy_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allThingyData.notifyDataChanged()
                thingyChart.notifyDataSetChanged()
                thingyChart.invalidate()
                thingyChart.setVisibleXRangeMaximum(150f)
                thingyChart.moveViewToX(thingyChart.lowestVisibleX + 40)
            }
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        unregisterReceiver(thingyLiveUpdateReceiver)
        looperRespeck.quit()
        looperThingy.quit()
    }
}
