package com.example.firstpage.TransportFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.*
import com.example.firstpage.AttractionFragment.Attraction
import com.example.firstpage.AttractionFragment.HotAttractions
import com.example.firstpage.AttractionFragment.MyRecyclerViewAdapter_Transportation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_first_page.*
import kotlinx.android.synthetic.main.activity_schedules.*
import kotlinx.android.synthetic.main.activity_schedules.btn_att
import kotlinx.android.synthetic.main.activity_schedules.btn_profile
import kotlinx.android.synthetic.main.activity_schedules.btn_travel
import org.jetbrains.anko.db.NULL
import java.time.Duration
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule
import android.widget.Button as Button
import kotlinx.android.synthetic.main.activity_schedules.btn_home as btn_home1


class Schedules : AppCompatActivity(){
    internal lateinit var db: FirebaseFirestore
    internal lateinit var recyclerView_Trans: RecyclerView
    internal lateinit var ScheduleArrayList: ArrayList<Attraction>
    var Auth= FirebaseAuth.getInstance()
    internal lateinit var adapter: MyRecyclerViewAdapter_Trans
    lateinit var timearraylist: Array<String>
    lateinit var stoptimearraylist: ArrayList<Int>

    lateinit var startdate : String
    var TempSchedule = ArrayList<Attraction>()

    var id=""
    //below two code for all attraction maps
    private var mapsDialog: Dialog? = null
    var spotImgs= arrayOf(R.mipmap.spot_01,R.mipmap.spot_02,
        R.mipmap.spot_03,R.mipmap.spot_04,R.mipmap.spot_05,R.mipmap.spot_06,
        R.mipmap.spot_07,R.mipmap.spot_08,R.mipmap.spot_09,R.mipmap.spot_10)

    @SuppressLint("ResourceAsColor", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedules)
        setUpFirebase()
        loadingDialog(3000)
        var lockprivacy = findViewById<ImageButton>(R.id.lock)
//        var MapsBtn = findViewById<Button>(R.id.MapsBtn)

        if(intent.getStringExtra("traveltempid")!=null){
            id =intent.getStringExtra("traveltempid")
        }

        else if(intent.getStringExtra("EXTRA_ID")!=null){
            id =intent.getStringExtra("EXTRA_ID")
        }
        else if(intent.getStringExtra("MYEXTRA_ID")!=null){//my_transport_attraction
            id =intent.getStringExtra("MYEXTRA_ID")
        }
        else if(intent.getStringExtra("REEXTRA_ID")!=null){//my_transport_attraction
            id =intent.getStringExtra("REEXTRA_ID")
        }

        else{
            id=intent.getStringExtra("comfirm")
        }


        ScheduleArrayList = ArrayList()
        db.collection("Schedule").document(id).get().addOnSuccessListener {
            schename.text=it.getString("ScheduleName")}

        loadDataFromFirebase()
        setUpRecyclerView()

        var logout=findViewById<TextView>(R.id.logoutlink)
        logout.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                var intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        //判斷是不是從publictravel_new頁面來的
        if(intent.getStringExtra("publictravel")==null){
            val callprivacy=  db.collection("Schedule")
            callprivacy.document(id).get().addOnSuccessListener {
                var lock = it.getString("privacy")
                if(lock=="False"){
                    lockprivacy.setImageResource(R.drawable.ic_unlocked_schedule)
                }else{
                    lockprivacy.setImageResource(R.drawable.ic_locked_schedule)

                }
            }
            lock.setOnClickListener {
                val callprivacy=  db.collection("Schedule")
                callprivacy.document(id).get().addOnSuccessListener {
                    var lock=  it.getString("privacy")
                    if(lock=="False"){
                        lockprivacy.setImageResource(R.drawable.ic_locked_schedule)
                        Toast.makeText(this, "行程不公開", Toast.LENGTH_LONG).show()

                        val data = hashMapOf(
                            "privacy" to "True"
                        )
                        callprivacy.document(id).update(data as Map<String, Any>)
                    }else{
                        lockprivacy.setImageResource(R.drawable.ic_unlocked_schedule)
                        Toast.makeText(this, "行程設為公開", Toast.LENGTH_LONG).show()

                        val data = hashMapOf(
                            "privacy" to "False"
                        )
                        callprivacy.document(id).update(data as Map<String, Any>)
                    }
                }//end of callprivacy.successlistener
            } // end of lock

            editschedule.setOnClickListener {
                var intent= Intent(this, schedule_edit::class.java)
                timearraylist=adapter.TimeArrayList
                stoptimearraylist = adapter.StopTimeArray
                intent.putExtra("Time_array", timearraylist)
                intent.putIntegerArrayListExtra("EditStopTime",stoptimearraylist)

                var mode = adapter.transmode_fk
                startdate = adapter.startime
                intent.putExtra("Edit_ID", id)
                intent.putExtra("StartDate", startdate)
                intent.putStringArrayListExtra("Transport_mode", mode)
                startActivity(intent)
            } //end of edit
        }else{
            showHide(editschedule)
            showHide(lock)
        }

        MapsBtn.setOnClickListener {
            showMaps(ScheduleArrayList)
        }

        btn_home.setOnClickListener {
            val intent = Intent(this, FirstPageActivity::class.java)
            startActivity(intent)
        }
        btn_att.setOnClickListener {
            val intent = Intent(this, HotAttractions::class.java)
            startActivity(intent)
        }
        btn_travel.setOnClickListener {
            val intent = Intent(this, TravelActivity::class.java)
            startActivity(intent)
        }
        btn_profile.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDataFromFirebase() {
        if (ScheduleArrayList.size > 0)
            ScheduleArrayList.clear()

        FirebaseFirestore.getInstance().collection("Schedule").document(id)
            .get().addOnSuccessListener {
                var tempid=  it.get("ScheduleList") as ArrayList<String>
                var weatherAuto = it.getString("autoSchedule")
                FirebaseFirestore.getInstance().collection("Attraction")
                    .get()
                    .addOnCompleteListener { task ->
                        for(i in 0 until tempid.size){
                            for (querySnapshot in task.result!!) {
                                if (querySnapshot.id==tempid[i]){
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
                                    ScheduleArrayList.add(attraction)
                                    break
//                                    Log.d("temparray",ScheduleArrayList.toString())
                                }
                            }
                        }
                        if(weatherAuto=="True"){
                            if(ScheduleArrayList.size>=1){
                                TempSchedule = calculatedistance(ScheduleArrayList)
                                ScheduleArrayList = TempSchedule
                            }
                            adapter= MyRecyclerViewAdapter_Trans(this@Schedules,TempSchedule)
                            recyclerView_Trans.adapter = adapter
                        }else if(weatherAuto=="False"){
                            adapter= MyRecyclerViewAdapter_Trans(this@Schedules,ScheduleArrayList)
                            recyclerView_Trans.adapter = adapter
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
        recyclerView_Trans = findViewById(R.id.transportation_main)
        recyclerView_Trans.setHasFixedSize(true)
        recyclerView_Trans.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
    }

    @SuppressLint("MissingPermission", "ResourceAsColor")
    fun showMaps(ScheduleArrayList : ArrayList<Attraction>){

        //all attractions maps
        mapsDialog = Dialog(this)
        mapsDialog!!.setContentView(R.layout.maps_all_att)
        mapsDialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))

        db.collection("Schedule").document(id).get().addOnSuccessListener {

            val BigMap = mapsDialog!!.findViewById(R.id.big_maps) as MapView
            BigMap.onCreate(mapsDialog!!.onSaveInstanceState())
            BigMap.onResume()

            BigMap.getMapAsync { googleMap ->

                var lng = 0.0 //經度
                var lat = 0.0 //緯度
                var att = LatLng(lat, lng) // your lat lng
                var attName =""
                var countSpot=0

                googleMap.uiSettings.isZoomControlsEnabled = true

                for (eachAtt in ScheduleArrayList) {

                    lat = eachAtt.spotlat.toDouble()
                    lng = eachAtt.spotlong.toDouble()
                    att = LatLng(lat, lng)
                    attName = eachAtt.getspotname()

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(att))
                    googleMap.addMarker(MarkerOptions().position(att).title(attName)
                        .icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(resources, spotImgs[countSpot]))))

                    countSpot++

                }

                googleMap.animateCamera(CameraUpdateFactory.zoomTo(7f), 2000, null)
            }


            mapsDialog!!.show()

        }

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

    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.INVISIBLE
//            View.VISIBLE
        }
    }

}



