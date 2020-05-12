package com.example.firstpage

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_travel.*
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import com.example.firstpage.TransportFragment.*
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_schedules.*
import kotlinx.android.synthetic.main.activity_travel.btn_att
import kotlinx.android.synthetic.main.activity_travel.btn_home
import kotlinx.android.synthetic.main.activity_travel.btn_profile
import kotlinx.android.synthetic.main.activity_travel.logoutlink
import kotlinx.android.synthetic.main.fragment_fragment_attractions.*
import kotlinx.android.synthetic.main.insert_sche_name_dialog.*
import kotlinx.android.synthetic.main.insert_sche_name_dialog.schename
import kotlinx.android.synthetic.main.sele_transpot_dialog.*
import kotlinx.android.synthetic.main.travel_activity_list_item.*
import kotlin.collections.ArrayList


class collect_travel : AppCompatActivity(){

    internal lateinit var db: FirebaseFirestore
    var Auth= FirebaseAuth.getInstance()
    internal lateinit var recyclerView_my_travel: RecyclerView
    internal lateinit var travelArraylist: java.util.ArrayList<myAttraction>
    internal lateinit var adapter: collect_travel_adapter
    var test = java.util.ArrayList<String>()
    var  tempid=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_travel)
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
        publicSchedule.setOnClickListener {
            val intent = Intent(this, public_travel::class.java)
            startActivity(intent)

        }
        mysche.setOnClickListener {
            val intent = Intent(this, TravelActivity::class.java)
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
        db.collection("Schedule").get()
            .addOnCompleteListener { task ->
                for (querySnapshot in task.result!!) {
                    var traveluser= querySnapshot.get("ScheduleCollect") as ArrayList<String>
                    for(i in 0 until traveluser.size)
                    if(traveluser[i]==Auth.uid.toString()){
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
                    adapter= collect_travel_adapter(this@collect_travel, travelArraylist)
                    recyclerView_my_travel.adapter = adapter
                }
            }

    }
    private fun setUpFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun verifyUserIsLogin() {
        val uid=Auth.uid
        if(uid==null){
            val intent= Intent(this,LoginActivity::class.java)
            intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

}
