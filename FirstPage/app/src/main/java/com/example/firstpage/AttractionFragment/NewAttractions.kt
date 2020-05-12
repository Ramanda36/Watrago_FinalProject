package com.example.firstpage.AttractionFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_fragment_attractions.*
import java.util.ArrayList
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.firstpage.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_new_attractions.*
import kotlinx.android.synthetic.main.fragment_fragment_attractions.hotspot
import kotlinx.android.synthetic.main.fragment_fragment_attractions.logoutlink
import kotlinx.android.synthetic.main.search_dialog.*
//import kotlinx.android.synthetic.main.fragment_fragment_attractions.searchSpot
//import kotlinx.android.synthetic.main.fragment_fragment_attractions.sortbydistance
//import kotlinx.android.synthetic.main.fragment_fragment_attractions.sortbyhot
import org.w3c.dom.Attr
import java.util.*
import kotlin.collections.HashMap
import kotlinx.android.synthetic.main.activity_new_attractions.btn_home as btn_home1
import kotlinx.android.synthetic.main.activity_new_attractions.btn_profile as btn_profile1
import kotlinx.android.synthetic.main.activity_new_attractions.btn_travel as btn_travel1


class NewAttractions : AppCompatActivity() {
    internal lateinit var db: FirebaseFirestore
    internal lateinit var recyclerView_new: RecyclerView
    internal lateinit var spotnameArrayList: ArrayList<Attraction>
    var Auth=FirebaseAuth.getInstance()

    var dialog: Dialog? = null

    val filteredList_ct = ArrayList<Attraction>()

    val checkedLocationArray = booleanArrayOf(
        false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false,false)

    internal lateinit var adapter: NewAttractionAdapter
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_attractions)
        spotnameArrayList = ArrayList()
        setUpRecyclerView()
        setUpFirebase()
        calculatedis()
        verifyUserIsLogin()
        loadDataFromFirebase()
        sortview()
//       val searchspot = findViewById<EditText>(R.id.searchSpot)
//        searchSpot.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                filter(s.toString())
//            }
//        })
        //GetDataFromFirebase()
        // AddDataToFirebase()
        //  val spot1=findViewById<View>(R.id.spot1) as ImageView
        hotspot.setOnClickListener {
            val intent = Intent(this, HotAttractions::class.java)
            startActivity(intent)
        }

        Myspot.setOnClickListener {
            val intent = Intent(this, MyAttractions::class.java)
            startActivity(intent)
        }

        logoutlink.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()

                val intent=Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        btn_home.setOnClickListener {
            val intent = Intent(this, FirstPageActivity::class.java)
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
        new_att_search_btn.setOnClickListener {
            search_dialog()
        }
    }

    private fun calculatedis() {
        val caldis=  db.collection("Attraction")
        caldis.get()
            .addOnCompleteListener { task ->
                for (querySnapshot in task.result!!) {
                    var lat:Double
                    var lon:Double

                    if( querySnapshot.getString("latitude")=="NA"){
                        lat=-10000.0
                    }
                    else {
                        lat= querySnapshot.getString("latitude")?.toDouble()!!
                    }
                    if( querySnapshot.getString("longitude")=="NA"){
                        lon =-10000.0
                    }
                    else{
                        lon= querySnapshot.getString("longitude")?.toDouble()!!
                    }

                    val results = FloatArray(1)
                    if (lat != null) {
                        if (lon != null) {
                            Location.distanceBetween(121.535317,25.021961,
                                lat, lon,results)
                        }
                    }
                    results[0]= (results[0]*0.001).toFloat()
                    //  Log.d(TAG,results[0].toString())
                    val data = hashMapOf(
                        "Distance" to results[0]
                    )
                    caldis.document(querySnapshot.id).update(data as Map<String, Any>)
                }
            }
    }

    private fun filter(text: String) {
        val filteredList = ArrayList<Attraction>()
        for (item in spotnameArrayList) {
            if (item.getspotname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)
    }

    private fun filtercountry(Array1 : ArrayList<String>) {
//        val filteredList_ct = ArrayList<Attraction>()
        if(Array1.size!=0){
            var text =""
            for(i in 0..Array1.size-1){
                text = Array1[i]
//            Log.d("text111",text)
                for (item in spotnameArrayList) {
                    if (item.getcountry().contains(text)) {
                        filteredList_ct.add(item)
                    }
                }
                adapter.filterList(filteredList_ct)

                db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
                    var spotprefer = it.get("SpotPrefer")!! as Long
                    var dateprefer = it.get("DurationPrefer")
                    if(spotprefer<5){
                        if (dateprefer == true) {
                            Collections.sort(filteredList_ct,Attraction.byhotm37)
                        }else  if (dateprefer == false){
                            Collections.sort(filteredList_ct,Attraction.byhota37)
                        }
                    }else if(spotprefer > 4 || spotprefer < 6){
                        if(dateprefer == true){
                            Collections.sort(filteredList_ct,Attraction.byhotm55)
                        }else if(dateprefer == false){
                            Collections.sort(filteredList_ct,Attraction.byhota55)
                        }
                    }else if(spotprefer>5){
                        if(dateprefer == true){
                            Collections.sort(filteredList_ct,Attraction.byhotm73)
                        }else if(dateprefer == false){
                            Collections.sort(filteredList_ct,Attraction.byhota73)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }else{
            adapter.filterList(spotnameArrayList)
        }

    }

    private fun loadDataFromFirebase() {
        if (spotnameArrayList.size > 0)
            spotnameArrayList.clear()
        var tempdb=db.collection("Attraction")
        tempdb.limit(1000).get()
            .addOnCompleteListener { task ->
                for (querySnapshot in task.result!!) {
                    if(querySnapshot.get("Source")=="OpenData") {
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

                        spotnameArrayList.add(attraction)
                    }
                    adapter= NewAttractionAdapter(this@NewAttractions, spotnameArrayList)
                    recyclerView_new.adapter = adapter
                }

            }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sortview() {
//        sortbyhot.setOnClickListener{
//            Collections.sort(spotnameArrayList,Attraction.byhot)
//            adapter.notifyDataSetChanged()
//        }
//        sortbydistance.setOnClickListener{
//            Collections.sort(spotnameArrayList,Attraction.bydis.reversed())
//            adapter.notifyDataSetChanged()
//        }
    }

    private fun setUpFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun setUpRecyclerView() {
        recyclerView_new = findViewById(R.id.recyclerView_new)
        recyclerView_new.setHasFixedSize(true)
        recyclerView_new.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
    }

    private fun GetDataFromFirebase() {
        var spotlist: MutableList<String> = mutableListOf()
        var spotnamelist: MutableList<String> = mutableListOf()
        db.collection("Attraction")
            .get()
            .addOnCompleteListener(
                OnCompleteListener< QuerySnapshot>() {

                }
            )
//                if (task.isSuccessful) {
//                    for (document in task.result!!) {
//                        Log.d(TAG, document.data.get("PhotoUrl").toString())
//                        Log.d(TAG, document.data.get("AttractionName").toString())
//
//                        spotlist.add(document.data.get("PhotoUrl").toString())
//                        spotnamelist.add(document.data.get("AttractionName").toString())
//
//                        // Log.d(TAG,spotlist[0])
//
//                    }
//                    textview.setText(spotnamelist[0])
//
//
//                   // Picasso.with(this).load(spotlist[5]).fit().into(spot6)
//                    //無法照順序放url到imageview中

    }

    private fun AddDataToFirebase() {

        val newContact: HashMap<String, Any> = HashMap()
        newContact.put("AttractionName", "棧貳庫")
        newContact.put(
            "PhotoUrl",
            "https://tluxe-aws.hmgcdn.com/public/article/2017/atl_20190227194506_205.jpg")
        newContact.put("Poster", "345")

        db.collection("Attraction").document("test6")
            .set(newContact)
            .addOnSuccessListener { void: Void? ->
                Toast.makeText(this, "Successfully uploaded to the database :)", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { exception: java.lang.Exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
            }
    }
    private fun verifyUserIsLogin() {
        val uid=Auth.uid
        if(uid==null){
            val intent=Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    private fun search_dialog(){

//        var dialog: Dialog?
        dialog = Dialog(this)
        dialog!!.setContentView(R.layout.search_dialog)
        dialog!!.setTitle("search")
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_light))
        val wmlp = dialog!!.window.attributes
        wmlp.gravity = Gravity.TOP or Gravity.LEFT

        dialog!!.show()

//        sortview()
        dialog!!.dissort.setOnClickListener {
            Collections.sort(spotnameArrayList,Attraction.bydis.reversed())
            adapter.notifyDataSetChanged()
        }

//        dialog!!.search_dialog_button.setOnClickListener {
//            dialog!!.dismiss()
//        }
        dialog!!.txt_search_att.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
        dialog!!.btn_location.setOnClickListener {
            val location = AlertDialog.Builder(this)
            val LocationArray = arrayOf(
                "台北市",
                "新北市",
                "基隆市",
                "桃園市",
                "新竹市",
                "新竹縣",
                "苗栗縣",
                "台中市",
                "彰化縣",
                "南投縣",
                "雲林縣",
                "嘉義市",
                "嘉義縣",
                "台南市",
                "高雄市",
                "屏東縣",
                "宜蘭縣",
                "花蓮縣",
                "台東縣"
            )

            val LocationsList = Arrays.asList(*LocationArray)
            location.setTitle("請選擇縣市")
            location.setMultiChoiceItems(LocationArray, checkedLocationArray) { dialog, which, ischecked ->
                checkedLocationArray[which] = ischecked
//                val currentItem = LocationsList[which]
//                Toast.makeText(applicationContext,currentItem,Toast.LENGTH_SHORT).show()
            }
            location.setPositiveButton("確定") { dialog, which ->
                //                Log.d("setPositiveButton","fun  function")
                dialog.dismiss()
                if(filteredList_ct.size>0){
                    filteredList_ct.clear()
                }
                val cnlist=ArrayList<String>()
                var tx = "你輸入的縣市："
                for (i in checkedLocationArray.indices) {
                    val checked = checkedLocationArray[i]
                    if (checked) {
                        tx = tx + "\n" + LocationsList[i]
                        cnlist.add(LocationsList[i])
                    }

                }
                if(tx != "你輸入的縣市：")
                    Toast.makeText(this, tx, Toast.LENGTH_SHORT).show()
                filtercountry(cnlist)
            }
            location.setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            location.show()
        }
    }

}
