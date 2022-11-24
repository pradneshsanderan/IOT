package com.specknet.pdiotapp.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import java.lang.StringBuilder

private lateinit var respeckOutputData: StringBuilder
lateinit var respeckReceiver: BroadcastReceiver
lateinit var looperRespeck: Looper
val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)
private var mIsRespeckRecording = true
var respeckOn = false
var time = 0
lateinit var respeckLiveUpdateReceiver: BroadcastReceiver

lateinit var dataArray : Array<FloatArray?>
lateinit var dataArrayTemp : Array<FloatArray?>

class LiveClassificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_classification)

        //Set up arrays

        //Change these to edit the size of the data and how often it gets sent to the model
        var windowSize = 200
        var overlap = 100
        //windowSize must be bigger than overlap

        dataArray = arrayOfNulls(windowSize)
        dataArrayTemp = arrayOfNulls(windowSize)


        // set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData

                    //Check this tag to see if data is coming through
                    Log.d("Class", "onReceive: liveData = " + liveData)

                    // get all relevant intent contents
                    val x = liveData.accelX
                    val y = liveData.accelY
                    val z = liveData.accelZ
                    val gyrox = liveData.gyro.x
                    val gyroy = liveData.gyro.y
                    val gyroz = liveData.gyro.z
                    var currentData = floatArrayOf(x,y,z,gyrox,gyroy,gyroz)


                    dataArray.set(time, currentData)

                    //Sliding window bit
                    if (overlap >= time){
                        dataArrayTemp.set(overlap - time, currentData)
                    }

                    if (time >= windowSize) {
                        //TODO: put values in correct format, (numpy Array?)
                        //TODO: push values to model

                        //Change to the next window
                        dataArray = dataArrayTemp
                        dataArrayTemp = arrayOfNulls(windowSize)

                        time = 0
                    } else {
                        time += 1
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
    }
}