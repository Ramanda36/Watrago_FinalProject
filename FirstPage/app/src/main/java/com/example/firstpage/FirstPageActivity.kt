package com.example.firstpage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.firstpage.TransportFragment.Transport_Attraction
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_first_page.*
import kotlinx.android.synthetic.main.activity_first_page.btn_travel
import kotlinx.android.synthetic.main.activity_first_page.logoutlink
import kotlinx.android.synthetic.main.activity_schedules.*
import kotlinx.android.synthetic.main.activity_schedules.btn_att
import kotlinx.android.synthetic.main.activity_schedules.btn_profile
import kotlinx.android.synthetic.main.activity_travel.*
import org.jetbrains.anko.activityManager
import kotlinx.android.synthetic.main.activity_first_page.btn_att as btn_att1
import kotlinx.android.synthetic.main.activity_first_page.btn_profile as btn_profile1
import kotlinx.android.synthetic.main.activity_schedules.btn_travel as btn_travel1
import android.view.Gravity
import android.R.attr.gravity
import android.os.Handler
//import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.getAttributes
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.*
import com.google.firebase.firestore.Query
import java.util.ArrayList


class FirstPageActivity : AppCompatActivity() {

  //  val manager = supportFragmentManager
    val TAG = "FirstPageActivity"

    var HotFirstDB = FirebaseFirestore.getInstance()

    var Auth= FirebaseAuth.getInstance()

//    internal lateinit var viewPager : ViewPager

    var spotArrayList=ArrayList<Attraction>()

    val uid=Auth.uid

    internal lateinit var recyclerView_first: RecyclerView
    internal lateinit var adapter: FirstPageRecyclerviewAdapter

    var stringarray =  arrayOf(
        "工作時別跟我談夢想，我的夢想就是不工作",
        "想去哪?讓Watrago帶你去",
        "全世界的仰望，不及你一個回眸",
        "如果不出去走走，你或許以為這就是世界",
        "把遊玩過的風和日麗寫成日記，才發現你走在我的每個風景裡",
        "給自己一個窺探世界的機會",
        "選擇不當走馬看花的路人，眼珠裝上快門，Watrago成為能凝結時間的旅人",
        "旅行的理由不需要闡述太多，一個字就可以概括全部:走!",
        "夢想並不奢侈，只要勇敢邁出第一步",
        "一個人旅行在路上遇見最真實的自己",
        "讓watrago帶著你的小夥伴一起去旅行吧!!!",
        "人生至少要有兩次衝動，一為奮不顧身的愛情，一為說走就走的旅行。你沒有愛情，只能旅行了",
        "旅行，不要害怕錯過什麼，因為你的人生已經錯了!QAQ",
        "有的事情現在不做，就一輩子也不會做了",
        "一群人，一個Watrago，一場說走就走的旅行",
        "我和旅行之間只隔著一疊錢",
        "旅行是減少脂肪和荷包的最好方式",
        "喔~~~是誰住在深海的大鳳梨裡~Watrago~go!!!",
        "我以為我很頹廢，今天我才知道我已經報廢了",
        "長的像戴程揚，沒關係。但是你出來嚇人就是沒有公德心",
        "保護自己，愛護他人。請不要半夜出來嚇人",
        "我們的目標，向錢看，向厚賺!",
        "人家有的是背景，咱什麼都沒有",
        "別逼我，否則我偉大起來，一發不可收拾",
        "每當我吃飽喝足的時候，我就會想起來減肥這件正經事",
        "不要和我比懶，我懶得和你比")

    var rnds =0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_page)
//        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        logoutlink.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                var intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        rnds = (0..stringarray.size-1).random()
//        Log.d("rnds", rnds.toString())
        textView3.text = stringarray[rnds]

        verifyUserIsLogin()
        if(uid!=null){
            setUpFirebase()
            setUpRecyclerView()
            loadDataFromFirebase()
        }


//        viewPager = findViewById<View>(R.id.viewPager) as ViewPager
//        val adapter = ViewPageAdapter(this)
//        viewPager.adapter = adapter

        HotFirstDB.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
            txt_name.text=it.getString("UserName")
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

        new_trip_btn.setOnClickListener {
            val intent = Intent(this, TravelActivity::class.java)
            startActivity(intent)
        }
        logoutlink.setOnClickListener{
            val intent=Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

//        loadingDialog(4500)


    } //On Create

    private fun loadDataFromFirebase() {

        if (spotArrayList.size > 0)
            spotArrayList.clear()

        HotFirstDB.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
            var spotprefer = it.get("SpotPrefer")!! as Long
            var dateprefer = it.get("DurationPrefer")
            if (spotprefer < 5) {
                if (dateprefer == true) {
                    HotFirstDB.collection("Attraction").orderBy("Monprefer37",Query.Direction.DESCENDING)//.whereEqualTo("latitude",true)
                        .limit(10)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
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
                                spotArrayList.add(attraction)
                            }
                            adapter = FirstPageRecyclerviewAdapter(this@FirstPageActivity, spotArrayList)
                            recyclerView_first.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    HotFirstDB.collection("Attraction").orderBy("Allprefer37",Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
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
                                spotArrayList.add(attraction)
                            }
                            adapter =FirstPageRecyclerviewAdapter(this@FirstPageActivity, spotArrayList)
                            recyclerView_first.adapter = adapter
                        }
                }
            }else if (spotprefer > 5) {
                if (dateprefer == true) {
                    HotFirstDB.collection("Attraction").orderBy("Monprefer73",Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
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
                                spotArrayList.add(attraction)
                            }
                            adapter = FirstPageRecyclerviewAdapter(this@FirstPageActivity, spotArrayList)
                            recyclerView_first.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    HotFirstDB.collection("Attraction").orderBy("Allprefer73",Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
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
                                spotArrayList.add(attraction)
                            }
                            adapter = FirstPageRecyclerviewAdapter(this@FirstPageActivity, spotArrayList)
                            recyclerView_first.adapter = adapter
                        }
                }
            }else if (spotprefer > 4 || spotprefer < 6) {
                if (dateprefer == true) {
                    HotFirstDB.collection("Attraction").orderBy("Monprefer55",Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
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
                                spotArrayList.add(attraction)
                            }
                            adapter = FirstPageRecyclerviewAdapter(this@FirstPageActivity, spotArrayList)
                            recyclerView_first.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    HotFirstDB.collection("Attraction").orderBy("Allprefer55", Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
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
                                spotArrayList.add(attraction)
                            }
                            adapter =FirstPageRecyclerviewAdapter(this@FirstPageActivity, spotArrayList)
                            recyclerView_first.adapter = adapter
                        }
                }
            }
        }
    }

    private fun setUpFirebase() {
        HotFirstDB = FirebaseFirestore.getInstance()
    }

    private fun setUpRecyclerView() {
        recyclerView_first = findViewById(R.id.recyclerView_main)
        recyclerView_first.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView_first.layoutManager = linearLayoutManager
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
    }

    private fun verifyUserIsLogin() {

        if(uid==null){
            logoutlink.text="LOGIN"
            Toast.makeText(this, "Need to login.", Toast.LENGTH_LONG).show()
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
}
