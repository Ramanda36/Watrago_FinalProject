package com.example.firstpage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_travel.*
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.HotAttractions
import com.example.firstpage.AttractionFragment.MyAttractions
import com.example.firstpage.AttractionFragment.myAttraction
import com.example.firstpage.Helper.MyButton
import com.example.firstpage.Helper.MySwipeHelper
import com.example.firstpage.Listener.MyButtonClickListener
import com.example.firstpage.TransportFragment.Schedules
import com.example.firstpage.TransportFragment.Transport_Attraction
import com.example.firstpage.TransportFragment.TravelActivityAdapter
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_schedules.*
import kotlinx.android.synthetic.main.activity_travel.btn_att
import kotlinx.android.synthetic.main.activity_travel.btn_home
import kotlinx.android.synthetic.main.activity_travel.btn_profile
import kotlinx.android.synthetic.main.activity_travel.logoutlink
import kotlinx.android.synthetic.main.fragment_fragment_attractions.*
import kotlinx.android.synthetic.main.insert_sche_name_dialog.*
import kotlinx.android.synthetic.main.insert_sche_name_dialog.schename
import kotlinx.android.synthetic.main.sele_transpot_dialog.*
import kotlin.collections.ArrayList


class TravelActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    internal lateinit var db: FirebaseFirestore
    var Auth= FirebaseAuth.getInstance()
    internal lateinit var recyclerView_my_travel: RecyclerView
    internal lateinit var travelArraylist: java.util.ArrayList<myAttraction>
    internal lateinit var adapter: TravelActivityAdapter
    var stoptimArray=ArrayList<Int>()
//    var a=0
    var stadate=""
    var enddate=""
    var trans=""
    var test = java.util.ArrayList<String>()
    var schename=""
    var  tempid=""
    var presetschelist=ArrayList<String>()

    var dayString = "" //1101
    var autoschedule = "True" //預設都要優化路程 1119

    var userlike =  java.util.ArrayList<String>()
    var usercollect =  java.util.ArrayList<String>()



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var a=0
        a++

        if(dayOfMonth/10<1)
            dayString = "0" + dayOfMonth.toString()
        else
            dayString = dayOfMonth.toString()

        stadate = year.toString()+(month+1).toString()+dayString

//        dateText.setText(date)
        if(a<2){
            selecEndDate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)
        travelArraylist = java.util.ArrayList()
        setUpRecyclerView()
        setUpFirebase()
        verifyUserIsLogin()
        loadDataFromFirebase()
        logoutlink.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                val intent= Intent(this, Transport_Attraction::class.java)
                startActivity(intent)
            }
        }
        addnewsch.setOnClickListener {
            selecStaDate()
        }

        object : MySwipeHelper(this,recyclerView_my_travel,200){
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>) {
                buffer.add(
                    MyButton(this@TravelActivity,
                        "Delete",30,R.drawable.garbage,
                        Color.parseColor("#fda769"),
                        object : MyButtonClickListener {
                            override fun onClick(pos: Int) {
                                db.collection("Schedule").document(travelArraylist[pos].mySpotid).delete()
                                (adapter).removeItem(viewHolder)
                            }
                        })
                )
            }

        }

        var publicSchedule = findViewById<Button>(R.id.publicSchedule)
        publicSchedule.setOnClickListener {
            val intent = Intent(this, public_travel::class.java)
            startActivity(intent)
        }
        saveSchedule.setOnClickListener {
            val intent = Intent(this, collect_travel::class.java)
            startActivity(intent)
        }
        btn_home.setOnClickListener {
            val intent = Intent(this, FirstPageActivity::class.java)
            startActivity(intent)
        }
        btn_profile.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
        btn_att.setOnClickListener {
            val intent = Intent(this, HotAttractions::class.java)
            startActivity(intent)
        }

    }

    private fun setUpRecyclerView() {
        recyclerView_my_travel = findViewById(R.id.recyclerView_my_travel)
        recyclerView_my_travel.setHasFixedSize(true)
        recyclerView_my_travel.layoutManager = LinearLayoutManager(this)
    }

    private fun loadDataFromFirebase() {
        if (travelArraylist.size > 0)
            travelArraylist.clear()
        db.collection("Schedule")
            .get()
            .addOnCompleteListener { task ->
                for (querySnapshot in task.result!!) {
                    var username= querySnapshot.getString("User")
                    if(username==Auth.uid.toString()){
                        val attraction = myAttraction(
                            querySnapshot.getString("ScheduleName"),
                            querySnapshot.getString("StartDate"),
                            querySnapshot.id,
                            querySnapshot.getDouble("Distance"),
                            querySnapshot.getString("longitude"),
                            querySnapshot.getString("latitude"),
                            querySnapshot.getString("Address"),
                            querySnapshot.getString("TEL"),
                            querySnapshot.getString("Country"),
                            querySnapshot.getString("Allprefer37"),
                            querySnapshot.getString("Allprefer55"),
                            querySnapshot.getString("Allprefer73"),
                            querySnapshot.getString("Monprefer37"),
                            querySnapshot.getString("Monprefer55"),
                            querySnapshot.getString("Monprefer73"),
                            querySnapshot.getString("privacy"),
                            querySnapshot.getString("LikeCount")
                            )
                        travelArraylist.add(attraction)
                    }
                    adapter= TravelActivityAdapter(this@TravelActivity, travelArraylist)
                    recyclerView_my_travel.adapter = adapter
                }

            }

    }
    private fun setUpFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    @SuppressLint("ResourceAsColor")
    private fun selecTrans() {
        var mydialog: Dialog? = null
        mydialog = Dialog(this)
        mydialog.setContentView(R.layout.sele_transpot_dialog)
        mydialog.setTitle("NEW SCHEDULE NAME:")
        mydialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        mydialog.show()

        mydialog.car.setOnClickListener {
            test.add("driving")
            stoptimArray.add(45)
            trans="driving"
            mydialog.dismiss()
            inputSchName()
        }
        mydialog.bus.setOnClickListener {
            test.add("bus")
            stoptimArray.add(45)
            trans="bus"
            mydialog.dismiss()
            inputSchName()
        }
        mydialog.walk.setOnClickListener {
            trans="walking"
            test.add("walking")
            stoptimArray.add(45)
            mydialog.dismiss()
            inputSchName()
        }
        mydialog.subway.setOnClickListener {
            trans="subway"
            test.add("subway")
            stoptimArray.add(45)
            mydialog.dismiss()
            inputSchName()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun inputSchName() {
        var mydialog: Dialog? = null
        mydialog = Dialog(this)
        mydialog.setContentView(R.layout.insert_sche_name_dialog)
        mydialog.setTitle("NEW SCHEDULE NAME:")
        mydialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        mydialog.show()

        mydialog.cancelsche.setOnClickListener {
            mydialog.dismiss()
        }
        mydialog.comfirmsche.setOnClickListener {
            mydialog.dismiss()
            schename=mydialog.schename.text.toString()
            val caldis=  db.collection("Schedule")
            var id= caldis.document().id
            tempid=id
//            deliverid(tempid)
            caldis.document(id).get().addOnSuccessListener {
                val data = hashMapOf(
                    "StartDate" to stadate,
                    "EndDate" to enddate,
                    "StopTime" to stoptimArray,
                    "PreTransport" to trans, //1112
                    "ScheduleName" to schename,
                    "User" to Auth.uid,
                    "ScheduleList" to presetschelist,
                    "autoSchedule" to autoschedule, //1119
                    "privacy" to "True",//1203
                    "LikeCount" to "0" //1206
                )
                caldis.document(id).set(data as Map<String, Any>)
                val transtest = hashMapOf(
                    "Transport" to test,
                    "Schedulelike" to userlike, //1206
                    "ScheduleCollect" to usercollect //1206

                )
                caldis.document(id).update(transtest as Map<String, Any>)
            }

//            var intent= Intent(this,My_Transport_Attraction::class.java)
            var intent= Intent(this,Transport_Attraction::class.java)

            intent.putExtra("EXTRA_SESSION_ID", tempid)
            startActivity(intent)
        }
    }

    private fun selecStaDate() {
        val stadatePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        stadatePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 22)
        // Set calendar to 1 month next
        calendar.add(Calendar.MONTH, 1)
        stadatePickerDialog.datePicker.maxDate = calendar.timeInMillis

        stadatePickerDialog.show()
//        while(!stadatePickerDialog.isShowing){
//            selecEndDate()
//        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun selecEndDate() {
        val enddatePickerDialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        enddatePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis

        // Set calendar to 1 day next from today
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 22)
        // Set calendar to 1 month next
        calendar.add(Calendar.MONTH, 1)
        enddatePickerDialog.datePicker.maxDate = calendar.timeInMillis

        enddatePickerDialog.show()
        enddatePickerDialog.setOnDateSetListener { view_, year, month, dayOfMonth ->
            enddate = year.toString()+(month+1).toString()+dayOfMonth.toString()
            selecTrans()

        }
//        if(!enddatePickerDialog.isShowing){
//            selecTrans()
//        }

    }
    private fun verifyUserIsLogin() {
        val uid=Auth.uid
        if(uid==null){
            val intent=Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

}
