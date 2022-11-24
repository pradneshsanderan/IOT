package com.specknet.pdiotapp.history

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.RESpeckPacketHandler.Companion.TAG
import java.util.*


lateinit var firebaseDatabase: FirebaseDatabase
lateinit var databaseReference: DatabaseReference


class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val temp = Calendar.getInstance()
        val year = temp[Calendar.YEAR]
        val month = temp[Calendar.MONTH]
        val dayOfMonth = temp[Calendar.DAY_OF_MONTH]
        val today = "$dayOfMonth-$month-$year"

        var walkingResp = 0
        var runningResp = 0
        var lyingOnStomachResp= 0
        var sittingStraightResp = 0
        var sittingBentForwardResp = 0
        var sittingBentBackwardResp = 0
        var lyingDownOnLeftSideResp = 0
        var lyingDownOnRightSideResp = 0
        var lyingDownOnBackResp= 0
        var ascendingStairsResp= 0
        var descendingstairsResp = 0
        var movementsResp = 0
        var deskWorkResp= 0
        var standingResp = 0
        var stepsResp = 0

        var walkingThi = 0
        var runningThi = 0
        var lyingOnStomachThi= 0
        var sittingStraightThi = 0
        var sittingBentForwardThi = 0
        var sittingBentBackwardThi = 0
        var lyingDownOnLeftSideThi= 0
        var lyingDownOnRightSideThi= 0
        var lyingDownOnBackThi= 0
        var ascendingStairsThi= 0
        var descendingstairsThi = 0
        var movementsThi = 0
        var deskWorkThi= 0
        var standingThi = 0
        var stepsThi = 0
        

        val walkingRespResults = findViewById<TextView>(R.id.textView43)
        val runningRespResult = findViewById<TextView>(R.id.textView41)
        val lyingOnStomachRespResult = findViewById<TextView>(R.id.textView36)
        val sittingStraightRespResults = findViewById<TextView>(R.id.textView32)
        val sittingBentForwardRespResults = findViewById<TextView>(R.id.textView33)
        val sittingBentBackwardRespResults = findViewById<TextView>(R.id.textView34)
        val lyingDownOnLeftSideRespResults = findViewById<TextView>(R.id.textView35)
        val lyingDownOnRightSideRespResults = findViewById<TextView>(R.id.textView37)
        val lyingDownOnBackRespResults = findViewById<TextView>(R.id.textView38)
        val ascendingStairsRespResults = findViewById<TextView>(R.id.textView40)
        val descendingstairsRespResults = findViewById<TextView>(R.id.textView39)
        val movementsRespResults = findViewById<TextView>(R.id.textView42)
        val deskWorkRespResults = findViewById<TextView>(R.id.textView44)
        val standingRespResults = findViewById<TextView>(R.id.textView45)
        val stepsRespResults = findViewById<TextView>(R.id.stepstextview1)

        val walkingThiResults = findViewById<TextView>(R.id.textView54)
        val runningThiResults = findViewById<TextView>(R.id.textView47)
        val lyingOnStomachThiResults = findViewById<TextView>(R.id.textView49)
        val sittingStraightThiResults = findViewById<TextView>(R.id.textView52)
        val sittingBentForwardThiResults = findViewById<TextView>(R.id.textView58)
        val sittingBentBackwardThiResults = findViewById<TextView>(R.id.textView56)
        val lyingDownOnLeftSideThiResults = findViewById<TextView>(R.id.textView51)
        val lyingDownOnRightSideThiResults = findViewById<TextView>(R.id.textView53)
        val lyingDownOnBackThiResults = findViewById<TextView>(R.id.textView55)
        val ascendingStairsThiResults = findViewById<TextView>(R.id.textView50)
        val descendingstairsThiResults = findViewById<TextView>(R.id.textView60)
        val movementsThiResults = findViewById<TextView>(R.id.textView48)
        val deskWorkThiResults = findViewById<TextView>(R.id.textView57)
        val standingThiResults = findViewById<TextView>(R.id.textView59)
        val stepsThiResults = findViewById<TextView>(R.id.stepstextview2)

        firebaseDatabase = FirebaseDatabase.getInstance("https://pdiotdb-29732-default-rtdb.europe-west1.firebasedatabase.app")
        databaseReference = firebaseDatabase.getReference().child("activities")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG,Calendar.DATE.toString())
                Log.d(TAG,today.toString())
                //fetching respeck history
                for(child in dataSnapshot.child("Respeck").child(today).children){
                    Log.d(TAG,"respeck")
                    if (child.value.toString() == "walking"){
                        walkingResp +=2
                        stepsResp += 3
                    }
                    else if (child.value.toString() == "running"){
                        runningResp += 2
                        stepsResp += 5
                    }
                    else if( child.value.toString() == "sitting_straight"){
                       sittingStraightResp+=2
                    }
                    else if (child.value.toString() == "sitting_bent_forward"){
                        sittingBentForwardResp+=2
                    }
                    else if (child.value.toString() == "sitting_bent_backward"){
                        sittingBentBackwardResp +=2
                    }
                    else if (child.value.toString() == "lying_on_left_side"){
                        lyingDownOnLeftSideResp+=2
                    }
                    else if (child.value.toString() == "lying_on_right_side"){
                        lyingDownOnRightSideResp+=2
                    }
                    else if (child.value.toString() == "lying_down_on_back"){
                        lyingDownOnBackResp+=2
                    }
                    else if (child.value.toString() == "ascending_stairs"){
                        ascendingStairsResp+=2
                        stepsResp += 2
                    }
                    else if (child.value.toString() == "descending_stairs"){
                        descendingstairsResp+=2
                        stepsResp += 2
                    }
                    else if (child.value.toString() == "movements"){
                        movementsResp+=2
                    }
                    else if (child.value.toString() == "desk_work"){
                        deskWorkResp+=2
                    }
                    else if (child.value.toString() == "standing"){
                        standingResp+=2
                    }
                    else if (child.value.toString() == "lying_on_stomach"){
                        Log.d(TAG,"respeck lying")
                        lyingOnStomachResp+=2
                    }
                }

                //fetching thingy history
                for(child in dataSnapshot.child("Thingy").child(today).children){
                    Log.d(TAG,"Thingy")
                    if (child.value.toString() == "walking"){
                        walkingThi +=2
                        stepsThi += 3
                    }
                    else if (child.value.toString() == "running"){
                        runningThi += 2
                        stepsThi += 5
                    }
                    else if( child.value.toString() == "sitting_straight"){
                        sittingStraightThi+=2
                    }
                    else if (child.value.toString() == "sitting_bent_forward"){
                        sittingBentForwardThi+=2
                    }
                    else if (child.value.toString() == "sitting_bent_backward"){
                        sittingBentBackwardThi +=2
                    }
                    else if (child.value.toString() == "lying_on_left_side"){
                        lyingDownOnLeftSideThi+=2
                    }
                    else if (child.value.toString() == "lying_on_right_side"){
                        lyingDownOnRightSideThi+=2
                    }
                    else if (child.value.toString() == "lying_down_on_back"){
                        lyingDownOnBackThi+=2
                    }
                    else if (child.value.toString() == "ascending_stairs"){
                        ascendingStairsThi+=2
                        stepsThi += 2
                    }
                    else if (child.value.toString() == "descending_stairs"){
                        descendingstairsThi+=2
                        stepsResp += 2
                    }
                    else if (child.value.toString() == "movement"){
                        movementsThi+=2
                    }
                    else if (child.value.toString() == "desk_work"){
                        deskWorkThi+=2
                    }
                    else if (child.value.toString() == "standing"){
                        standingThi+=2
                    }
                    else if (child.value.toString() == "lying_on_stomach"){
                        Log.d(TAG,"thingy lying")
                        lyingOnStomachThi+=2
                    }
                }

                //display respeck results
                walkingRespResults.text = walkingResp.toString()
                runningRespResult.text = runningResp.toString()
                sittingStraightRespResults.text = sittingStraightResp.toString()
                sittingBentForwardRespResults.text = sittingBentForwardResp.toString()
                sittingBentBackwardRespResults.text = sittingBentBackwardResp.toString()
                lyingDownOnBackRespResults.text = lyingDownOnBackResp.toString()
                lyingDownOnRightSideRespResults.text = lyingDownOnRightSideResp.toString()
                lyingDownOnLeftSideRespResults.text = lyingDownOnLeftSideResp.toString()
                lyingOnStomachRespResult.text = lyingOnStomachResp.toString()
                ascendingStairsRespResults.text = ascendingStairsResp.toString()
                descendingstairsRespResults.text = descendingstairsResp.toString()
                movementsRespResults.text = movementsResp.toString()
                deskWorkRespResults.text = deskWorkResp.toString()
                standingRespResults.text = standingResp.toString()
                stepsRespResults.text = stepsThi.toString()


                //display thingy results
                walkingThiResults.text = walkingThi.toString()
                runningThiResults.text = runningThi.toString()
                sittingStraightThiResults.text = sittingStraightThi.toString()
                sittingBentForwardThiResults.text = sittingBentForwardThi.toString()
                sittingBentBackwardThiResults.text = sittingBentBackwardThi.toString()
                lyingDownOnBackThiResults.text = lyingDownOnBackThi.toString()
                lyingDownOnRightSideThiResults.text = lyingDownOnRightSideThi.toString()
                lyingDownOnLeftSideThiResults.text = lyingDownOnLeftSideThi.toString()
                lyingOnStomachThiResults.text = lyingOnStomachThi.toString()
                ascendingStairsThiResults.text = ascendingStairsThi.toString()
                descendingstairsThiResults.text = descendingstairsThi.toString()
                movementsThiResults.text = movementsThi.toString()
                deskWorkThiResults.text = deskWorkThi.toString()
                standingThiResults.text = standingThi.toString()
                stepsThiResults.text = stepsThi.toString()



            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.d(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.addValueEventListener(postListener)

        //display respeck results
        walkingRespResults.text = walkingResp.toString()
        runningRespResult.text = runningResp.toString()
        sittingStraightRespResults.text = sittingStraightResp.toString()
        sittingBentForwardRespResults.text = sittingBentForwardResp.toString()
        sittingBentBackwardRespResults.text = sittingBentBackwardResp.toString()
        lyingDownOnBackRespResults.text = lyingDownOnBackResp.toString()
        lyingDownOnRightSideRespResults.text = lyingDownOnRightSideResp.toString()
        lyingDownOnLeftSideRespResults.text = lyingDownOnLeftSideResp.toString()
        lyingOnStomachRespResult.text = lyingOnStomachResp.toString()
        ascendingStairsRespResults.text = ascendingStairsResp.toString()
        descendingstairsRespResults.text = descendingstairsResp.toString()
        movementsRespResults.text = movementsResp.toString()
        deskWorkRespResults.text = deskWorkResp.toString()
        standingRespResults.text = standingResp.toString()
        stepsRespResults.text = standingResp.toString()


        //display thingy results
        walkingThiResults.text = walkingThi.toString()
        runningThiResults.text = runningThi.toString()
        sittingStraightThiResults.text = sittingStraightThi.toString()
        sittingBentForwardThiResults.text = sittingBentForwardThi.toString()
        sittingBentBackwardThiResults.text = sittingBentBackwardThi.toString()
        lyingDownOnBackThiResults.text = lyingDownOnBackThi.toString()
        lyingDownOnRightSideThiResults.text = lyingDownOnRightSideThi.toString()
        lyingDownOnLeftSideThiResults.text = lyingDownOnLeftSideThi.toString()
        lyingOnStomachThiResults.text = lyingOnStomachThi.toString()
        ascendingStairsThiResults.text = ascendingStairsThi.toString()
        descendingstairsThiResults.text = descendingstairsThi.toString()
        movementsThiResults.text = movementsThi.toString()
        deskWorkThiResults.text = deskWorkThi.toString()
        standingThiResults.text = standingThi.toString()
        stepsThiResults.text = standingThi.toString()



        //TODO add step reader from db
        //TODO add date chooser




    }
}