package com.example.firstpage.TransportFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.Attraction
import com.example.firstpage.Helper.MyButton
import com.example.firstpage.Helper.MySwipeHelper
import com.example.firstpage.Listener.MyButtonClickListener
import com.example.firstpage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import kotlinx.android.synthetic.main.activity_schedules.*
import java.util.*
import kotlin.collections.ArrayList

class schedule_edit : AppCompatActivity() {

    internal lateinit var db: FirebaseFirestore
    var Auth= FirebaseAuth.getInstance()
    lateinit var editlist:ArrayList<String>
    internal lateinit var adapter: schedule_edit_adapter
    internal lateinit var recyclerView_edit: RecyclerView
    internal lateinit var ScheduleList: ArrayList<Attraction>
    var temp=""
    var temptext = ArrayList<String>()
    var tempid = ArrayList<String>()
    lateinit var timearray:Array<String>
    lateinit var StopTimeArray:ArrayList<Int> //1031
    //    lateinit var transModeArray:Array<String>
    var mode_edit = ArrayList<String>()
    lateinit var startDate : String
    var weatherauto = ""
    lateinit var tempdb : CollectionReference
    lateinit var dataddd : HashMap<String,String>

    var TempSchedule = ArrayList<Attraction>()

    var getid = ""
    var moveifitem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)

        loadingDialog(3000)

        var id = intent.getStringExtra("Edit_ID")
        getid = id
        var time = intent.getStringArrayExtra("Time_array")
        var starttime = intent.getStringExtra("StartDate")

        StopTimeArray = intent.getIntegerArrayListExtra("EditStopTime")//1031

        var ModeEdit = intent.getStringArrayListExtra("Transport_mode")
        mode_edit = ModeEdit
        startDate = starttime
        temp=id
        timearray=time
        ScheduleList = java.util.ArrayList()
        editlist = java.util.ArrayList()

        setUpFirebase()
        db.collection("Schedule").document(id).get().addOnSuccessListener {
            setschename.setText(it.getString("ScheduleName"))
        }
        setschename.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                tempdb=db.collection("Schedule")
                tempdb.document(id).get().addOnSuccessListener {
                    dataddd = hashMapOf(
                        "ScheduleName" to s.toString()
                    )
                }
            }
        })
        FirebaseFirestore.getInstance().collection("Schedule")
            .document(temp).get().addOnSuccessListener {
                tempid = it.get("ScheduleList") as ArrayList<String>
                if(tempid.size!=0) {
                    FirebaseFirestore.getInstance().collection("Attraction").get().addOnCompleteListener { task ->
                        for (i in 0 until tempid.size) {
                            for (querySnapshot in task.result!!) {
                                if (querySnapshot.id == tempid[i]) {
                                    if (tempid[i] == querySnapshot.id) {
                                        temptext.add(querySnapshot.getString("AttractionName")!!)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        setUpRecyclerView()
        loadDataFromFirebase()

        next_button.setOnClickListener {
            saveschedule()
            var intent=Intent(this,Transport_Attraction::class.java)
            intent.putExtra("sche_ID",temp)
            intent.putStringArrayListExtra("spotname", temptext)
            intent.putStringArrayListExtra("spotid", tempid)

            Handler().postDelayed({
                startActivity(intent)
            },1000)
        }

        comfirm_edit_button.setOnClickListener {
            saveschedule()
            val intent = Intent(this, Schedules::class.java)
            intent.putExtra("comfirm", temp)
            Handler().postDelayed({
                startActivity(intent)
            },1000)

        }

        val swipe = object : MySwipeHelper(this,recyclerView_edit,200){
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>) {
                buffer.add(MyButton(this@schedule_edit,
                    "Delete",30,R.drawable.garbage,
                    Color.parseColor("#fda769"),
                    object : MyButtonClickListener{
                        override fun onClick(pos: Int) {
                            (adapter).removeItem(viewHolder)
                        }
                    }))
            }

        }

//        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback (0 , ItemTouchHelper.LEFT){
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//               false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
//                (adapter).removeItem(viewHolder)
//            }
//        }

//        val itemTouchHelperWipe = ItemTouchHelper(itemTouchHelperCallback)
//        itemTouchHelperWipe.attachToRecyclerView(recyclerView_edit)

        val itemTouchHelperMove = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourceposition = viewHolder.adapterPosition
                val targetposition = target.adapterPosition
                Collections.swap(ScheduleList,sourceposition,targetposition)
                adapter.notifyItemMoved(sourceposition,targetposition)
                moveifitem = true

//                Log.d("ScheduleList",ScheduleList[0].spotid)
                return  true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        itemTouchHelperMove.attachToRecyclerView(recyclerView_edit)

    } // end of onCreate

    private fun saveschedule(){
        val j = 0
        val caldis=  db.collection("Schedule")
        caldis.document(temp).get().addOnSuccessListener {
            tempdb.document(getid).update(dataddd as Map<String, Any>)

            var tempschedule=  it.get("ScheduleList") as ArrayList<String>
            for( i in 0..ScheduleList.size-1){
                editlist.add(ScheduleList.get(i).spotid)
                val data = hashMapOf(
                    "ScheduleList" to editlist,
                    "Transport" to mode_edit,
                    "StopTime" to adapter.PreStopTime //1031
                )
                caldis.document(temp).update(data as Map<String, Any>)
            }
            for(i in 0..editlist.size-1){
                if(editlist[i]!=tempschedule[i] && moveifitem){
                    weatherauto = "False"
                    val data2 = hashMapOf(
                        "autoSchedule" to weatherauto
                    )
                    caldis.document(temp).update(data2 as Map<String, Any>)
                    break
                }else
                    continue
            }
        }
        Handler().postDelayed({
            startActivity(intent)
        },1000)
    }

    private fun loadDataFromFirebase() {
        if (ScheduleList.size > 0)
            ScheduleList.clear()
//        if (spotpicArrayList.size > 0)
//            spotpicArrayList.clear()

        FirebaseFirestore.getInstance().collection("Schedule")
            .document(temp).get().addOnSuccessListener {
                var tempid=  it.get("ScheduleList") as ArrayList<String>
                var weatherAuto = it.getString("autoSchedule")
                FirebaseFirestore.getInstance().collection("Attraction")
                    .get()
                    .addOnCompleteListener { task ->
                        for(i in 0 until tempid.size){
                            for (querySnapshot in task.result!!) {
//                            for(i in 0 until tempid.size){
                                if (tempid[i]==querySnapshot.id){
                                    val attraction = Attraction(
                                        querySnapshot.getString("Address"),
                                        querySnapshot.getString("AttractionName"),
                                        querySnapshot.getString("PhotoUrl"),
                                        querySnapshot.id,
                                        querySnapshot.getDouble("Distance"),
                                        querySnapshot.getString("BusinessHour"),
                                        querySnapshot.getString("TEL"),
                                        querySnapshot.getString("longitude"),
                                        querySnapshot.getString("latitude"),
                                        querySnapshot.getString("Country"),
                                        querySnapshot.getString("Allprefer37"),
                                        querySnapshot.getString("Allprefer55"),
                                        querySnapshot.getString("Allprefer73"),
                                        querySnapshot.getString("Monprefer37"),
                                        querySnapshot.getString("Monprefer55"),
                                        querySnapshot.getString("Monprefer73")
                                    )
                                    ScheduleList.add(attraction)
                                    break
                                }
                            }
                        }
                        if(weatherAuto=="True"){
                            if(ScheduleList.size>=1){
                                TempSchedule = calculatedistance(ScheduleList)
                                ScheduleList = TempSchedule
                            }
                            adapter= schedule_edit_adapter(this@schedule_edit,TempSchedule)
                            recyclerView_edit.adapter = adapter
                        }else if(weatherAuto=="False"){
                            adapter= schedule_edit_adapter(this@schedule_edit,ScheduleList)
                            recyclerView_edit.adapter = adapter
                        }
                    }
            }
    }

    private fun calculatedistance(ScheduleArrayList : ArrayList<Attraction>): ArrayList<Attraction> {
        val results2 = FloatArray(1)
        var max = 0f
        var tempmax = 0f
        var index = 0
        var center = calCenterPoint(ScheduleArrayList)
        var splitans = center.split("!")
        var latitude = splitans[0]
        var longitude = splitans[1]
        lateinit var edgespot : Attraction
        for(x in 0..ScheduleArrayList.size-1){
            Location.distanceBetween(ScheduleArrayList[x].spotlat.toDouble(),ScheduleArrayList[x].spotlong.toDouble(),
                latitude.toDouble(),longitude.toDouble(),results2)
            tempmax = (results2[0]*0.001).toFloat()
            if(max<tempmax){
                max = tempmax
                edgespot = ScheduleArrayList[x]
                index = x
            }
        }
        if(edgespot!=ScheduleArrayList[0]){
            var tempitem = ScheduleArrayList[0]
            ScheduleArrayList[0] = edgespot
            ScheduleArrayList[index] = tempitem
        }

        val results = FloatArray(1)
        var tempschedule = ArrayList<Attraction>()
        var distanceArray = Array(ScheduleArrayList.size){FloatArray(ScheduleArrayList.size)}
        for(i in 0..distanceArray.size-1){
            for(j in 0..distanceArray.size-1){
                Location.distanceBetween(ScheduleArrayList[i].spotlat.toDouble(),ScheduleArrayList[i].spotlong.toDouble(),
                    ScheduleArrayList[j].spotlat.toDouble(),ScheduleArrayList[j].spotlong.toDouble(),results)
                distanceArray[i][j]= (results[0]*0.001).toFloat()
            }
        }
        tempschedule.add(ScheduleArrayList[0])
        for(k in 0..distanceArray.size-1){
            distanceArray[k][0]=10000000f
        }
        var lastcol = 0
        var row = 0
        while(tempschedule.size!=ScheduleArrayList.size){
            var tempcol = 0
            if(distanceArray[row][tempcol]==0f){
                tempcol+=1
            }
            var min = distanceArray[row][tempcol]
            lastcol = row
            for(i in 0..distanceArray.size-1){
                if(distanceArray[lastcol][i]< min && distanceArray[lastcol][i]!=0f){
                    min = distanceArray[lastcol][i]
                    row = i
                }
            }
            for(j in 0..distanceArray.size-1){
                distanceArray[j][lastcol]=10000000f
            }
            tempschedule.add(ScheduleArrayList[row])
        }
        return tempschedule
    }

    private fun calCenterPoint(ScheduleArrayList : ArrayList<Attraction>): String {
        val total = ScheduleArrayList.size-1
        var X = 0.0
        var Y = 0.0
        var Z = 0.0
        for (g in 0..total) {
            var lat = 0.0
            var lon= 0.0
            var x = 0.0
            var y = 0.0
            var z = 0.0
            lat = ScheduleArrayList[g].spotlat.toDouble() * Math.PI / 180
            lon = ScheduleArrayList[g].spotlong.toDouble() * Math.PI / 180;
            x = Math.cos(lat) * Math.cos(lon)
            y = Math.cos(lat) * Math.sin(lon)
            z = Math.sin(lat)
            X += x
            Y += y
            Z += z
        }
        X = X / total
        Y = Y / total
        Z = Z / total
        var Lon = Math.atan2(Y, X)
        var Hyp = Math.sqrt(X * X + Y * Y)
        var Lat = Math.atan2(Z, Hyp)
        var Latitude = Lat*180 / Math.PI
        var Longitude = Lon * 180 / Math.PI
        var mapans = Latitude.toString() + "!" + Longitude.toString()
        return mapans
    }


    private fun setUpFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun setUpRecyclerView() {
        recyclerView_edit = findViewById(R.id.transportation_edit)
        recyclerView_edit.setHasFixedSize(true)
        recyclerView_edit.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("ResourceAsColor")
    private fun loadingDialog(second : Long){
        var loadimgdialog: Dialog? = null

        //loading dialog
        loadimgdialog = Dialog(this)
        loadimgdialog!!.setContentView(R.layout.loading_content)
        loadimgdialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        loadimgdialog!!.show()
        loadimgdialog!!.setCancelable(false)

        Handler().postDelayed({loadimgdialog!!.dismiss()},second)
    }

}

