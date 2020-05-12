package com.example.firstpage.AttractionFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_new_attractions.*
import kotlinx.android.synthetic.main.fragment_fragment_attractions.*
import kotlinx.android.synthetic.main.fragment_fragment_attractions.btn_home
import kotlinx.android.synthetic.main.fragment_fragment_attractions.btn_profile
import kotlinx.android.synthetic.main.fragment_fragment_attractions.btn_travel
import kotlinx.android.synthetic.main.fragment_fragment_attractions.hotspot
import kotlinx.android.synthetic.main.fragment_fragment_attractions.logoutlink
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.search_dialog.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RecommendAttractions : AppCompatActivity() {

    internal lateinit var recommendDB: FirebaseFirestore
    internal lateinit var recyclerView_new: RecyclerView
    internal lateinit var spotnameArrayList: ArrayList<RecommendAttractionClass>
    var Auth= FirebaseAuth.getInstance()
    var temp=false
    lateinit var spotprefer:Any
    lateinit var dateprefer:Any
    var tempinsert=""
    var collectionid = ArrayList<String>()
    var spotidtext = ""
    var ArrayListsimilarity = ArrayList<String>()

    var dialog: Dialog? = null
    val filteredList_ct = java.util.ArrayList<RecommendAttractionClass>()

    val checkedLocationArray = booleanArrayOf(
        false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false,false)
    internal lateinit var adapter: RecommendAttractionAdapter
    @RequiresApi(Build.VERSION_CODES.N)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_attractions)
        spotnameArrayList = ArrayList()

        loadingDialog(7000)
        loadCollectionDB()
        setUpRecyclerView()
        setUpFirebase()
        calculatedis()
        verifyUserIsLogin()
//        loadDataFromFirebase()

        sortview()

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

    private fun loadCollectionDB(){
        if (spotnameArrayList.size > 0)
            spotnameArrayList.clear()
        var tempdb=FirebaseFirestore.getInstance().collection("UserInfo").document(Auth.uid.toString())
        tempdb.get().addOnSuccessListener {
            collectionid=  it.get("Collection") as ArrayList<String>
            if(collectionid.size!=0){
                for(i in 0..collectionid.size-1){
                    if(i!=collectionid.size-1){
                        spotidtext += collectionid[i] + ","
                    }else{
                        spotidtext += collectionid[i]
                    }
                }
                getSimilarity(spotidtext)
            }else{
                loadHotDataFromFirebase()
            }
        }
    }

    private fun getSimilarity(arrayid : String){
        doAsync {
            var simmilarurl = "http://120.126.18.144:5000/AttractionSimilar?AttractionID="+arrayid
//            Log.d("url",simmilarurl)
            ArrayListsimilarity = sendGetSimmilar(simmilarurl)
            loadDataFromFirebase(ArrayListsimilarity)
//            Log.d("get123",ArrayListsimilarity[0])
        }
    }

    private fun sendGetSimmilar(url:String): ArrayList<String> {
        var SimarityArrayList = ArrayList<String>()
        var similaridAns = ""
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            val data = connection.inputStream.bufferedReader().readText()
            var jsonObject = JSONObject(data)
            var jsonArray = jsonObject.getJSONArray("SimilarID")
            for(i in 0..(jsonArray.length()-1)){
                similaridAns = jsonArray.get(i).toString()
//                Log.d("similaridAns",similaridAns)
                SimarityArrayList.add(similaridAns)
            }
        }finally {
            connection.disconnect()
            return (SimarityArrayList)
        }
    }

    private fun loadDataFromFirebase(SimilarityArray : ArrayList<String>) {
        if (spotnameArrayList.size > 0)
            spotnameArrayList.clear()

        var tempdb=recommendDB.collection("Attraction")
        tempdb.get().addOnCompleteListener { task ->
            for (i in 0 until SimilarityArray.size) {
                for (querySnapshot in task.result!!) {
//                            for(i in 0 until tempid.size){
                    if (querySnapshot.id == SimilarityArray[i]) {
                        val attraction = RecommendAttractionClass(
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
                        break
                    }
                }
            }
            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
            recyclerView_new.adapter = adapter
        }
//        adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
//        recyclerView_new.adapter = adapter

//        for(index in 0..SimilarityArray.size-1){
//            tempdb.document(SimilarityArray[index]).get().addOnSuccessListener{
//                val attraction = Attraction(
//                    it.getString("Address"),
//                    it.getString("AttractionName"),
//                    it.getString("PhotoUrl"),
//                    SimilarityArray[index],
//                    it.getDouble("SpotPrefer"),
//                    it.getDouble("Distance"),
//                    it.getString("BusinessHour"),
//                    it.getString("Address"),
//                    it.getString("TEL"),
//                    it.getString("latitude"),
//                    it.getString("longitude")
//                )
//                Addarray(attraction)
//                spotnameArrayList.add(attraction)
//            }
//        }
//        adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
//        recyclerView_new.adapter = adapter

    }

    private fun filter(text: String) {
        val filteredList = ArrayList<RecommendAttractionClass>()
        for (item in spotnameArrayList) {
            if (item.getspotname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)
    }
    private fun filtercountry(Array1 : java.util.ArrayList<String>) {
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

                recommendDB.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
                    var spotprefer = it.get("SpotPrefer")!! as Long
                    var dateprefer = it.get("DurationPrefer")
                    if(spotprefer<5){
                        if (dateprefer == true) {
                            Collections.sort(filteredList_ct,RecommendAttractionClass.byhotm37)
                        }else  if (dateprefer == false){
                            Collections.sort(filteredList_ct,RecommendAttractionClass.byhota37)
                        }
                    }else if(spotprefer > 4 || spotprefer < 6){
                        if(dateprefer == true){
                            Collections.sort(filteredList_ct,RecommendAttractionClass.byhotm55)
                        }else if(dateprefer == false){
                            Collections.sort(filteredList_ct,RecommendAttractionClass.byhota55)
                        }
                    }else if(spotprefer>5){
                        if(dateprefer == true){
                            Collections.sort(filteredList_ct,RecommendAttractionClass.byhotm73)
                        }else if(dateprefer == false){
                            Collections.sort(filteredList_ct,RecommendAttractionClass.byhota73)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }else{
            adapter.filterList(spotnameArrayList)
        }
    }

    private fun calculatedis() {
        val caldis=  recommendDB.collection("Attraction")
        caldis.whereEqualTo("latitude",true).get()
            .addOnCompleteListener { task ->
                for (querySnapshot in task.result!!) {
                    var lat:Double
                    var lon:Double

                    if( querySnapshot.getString("latitude").toString()=="NA"){
                        lat=-10000.0
                    }
                    else {
//                        Log.d("test11233",querySnapshot.id)
//                        Log.d("test11233",querySnapshot.getString("AttractionName"))
//                        Log.d("test11233",querySnapshot.getString("latitude"))
                        lat= querySnapshot.getString("latitude")!!.toDouble()!!
                    }

                    if( querySnapshot.getString("longitude").toString()=="NA"){
                        lon =-10000.0
                    }
                    else{
                        lon= querySnapshot.getString("longitude")!!.toDouble()!!
                    }

                    val results = FloatArray(1)
                    if (lat != null) {
                        if (lon != null) {
                            Location.distanceBetween(25.033730,121.387486,
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
        if(temp){
            dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.block)
        }else{
            dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.white)

        }
        dialog!!.disblock.setOnClickListener {
            recommendDB.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
                spotprefer = it.get("SpotPrefer")!! as Long
                dateprefer = it.get("DurationPrefer")!! as Boolean
//                Log.d("temppppp", temp.toString())
                if (!temp) {
                    dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.block)
                    if (filteredList_ct.size != 0) {
                        Collections.sort(filteredList_ct, RecommendAttractionClass.bydis2.reversed())
                    } else {
                        Collections.sort(spotnameArrayList, RecommendAttractionClass.bydis2.reversed())
//               Log.d("spotnameArrayList",spotnameArrayList.size.toString())
                    }
                    temp = true
                } else {
                    dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.white)
                    if (filteredList_ct.size != 0) {
//                   db.collection("UserInfo").document(Auth.uid.toString()).get()
//                       .addOnSuccessListener {
//                           var spotprefer = it.get("SpotPrefer")!! as Long
//                           var dateprefer = it.get("DurationPrefer")
                        if ((spotprefer as Long) < 5) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, RecommendAttractionClass.byhotm37)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, RecommendAttractionClass.byhota37)
                            }
                        } else if ((spotprefer as Long) > 4 || (spotprefer as Long) < 6) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, RecommendAttractionClass.byhotm55)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, RecommendAttractionClass.byhota55)
                            }
                        } else if ((spotprefer as Long) > 5) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, RecommendAttractionClass.byhotm73)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, RecommendAttractionClass.byhota73)
//                               }
                            }
                        }
//                   adapter.notifyDataSetChanged()
                    } else {
//                   db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
//                       var spotprefer = it.get("SpotPrefer")!! as Long
//                       var dateprefer = it.get("DurationPrefer")
                        if ((spotprefer as Long) < 5) {
                            if (dateprefer == true) {
                                Collections.sort(spotnameArrayList, RecommendAttractionClass.byhotm37)
                            } else if (dateprefer == false) {
                                Collections.sort(spotnameArrayList, RecommendAttractionClass.byhota37)
                            }
                        } else if ((spotprefer as Long) > 4 || (spotprefer as Long) < 6) {
                            if (dateprefer == true) {
                                Collections.sort(spotnameArrayList, RecommendAttractionClass.byhotm55)
                            } else if (dateprefer == false) {
                                Collections.sort(spotnameArrayList, RecommendAttractionClass.byhota55)
                            }
                        } else if ((spotprefer as Long) > 5) {
                            if (dateprefer == true) {
                                Collections.sort(spotnameArrayList, RecommendAttractionClass.byhotm73)
                            } else if (dateprefer == false) {
                                Collections.sort(spotnameArrayList, RecommendAttractionClass.byhota73)
                            }
//                       }
                        }

                    }
//               adapter.notifyDataSetChanged()
                    temp = false

                }
                adapter.notifyDataSetChanged()
            }
        }
//        Log.d("tempppppp",temp.toString())


//       dialog!!.search_dialog_button.setOnClickListener {
//            dialog!!.dismiss()
//        }
        dialog!!.txt_search_att.setText(tempinsert)
        dialog!!.txt_search_att.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                tempinsert=s.toString()
                filter(tempinsert)
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
            location.setPositiveButton("確定") { dialogcountry, which ->
                //                Log.d("setPositiveButton","fun  function")
                dialogcountry.dismiss()
                if(filteredList_ct.size>0){
                    filteredList_ct.clear()
                }
                val cnlist= java.util.ArrayList<String>()
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
                dialog!!.dismiss()
            }
            location.setNegativeButton("取消") { dialogcountry, which ->
                dialogcountry.dismiss()
            }
            location.show()
        }
//        search_dialog_button.setOnClickListener {
//            spin_location.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                if(spin_location.getItemAtPosition(position).equals("選擇縣市")){
//                    Toast.makeText(this@HotAttractions,position, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//
//            }
//
//        }
//
//        }
    }

    private fun loadHotDataFromFirebase() {

        if (spotnameArrayList.size > 0)
            spotnameArrayList.clear()

        recommendDB.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
            var spotprefer = it.get("SpotPrefer")!! as Long
            var dateprefer = it.get("DurationPrefer")

            if (spotprefer < 5) {
                if (dateprefer == true) {
                    recommendDB.collection("Attraction").orderBy("Monprefer37",Query.Direction.DESCENDING)//.whereEqualTo("latitude",true)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
                                val attraction = RecommendAttractionClass(
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
                            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
                            recyclerView_new.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    recommendDB.collection("Attraction").orderBy("Allprefer37",Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
                                val attraction = RecommendAttractionClass(
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
                            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
                            recyclerView_new.adapter = adapter
                        }
                }
            }else if (spotprefer > 5) {
                if (dateprefer == true){
                    recommendDB.collection("Attraction").orderBy("Monprefer73",Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
                                val attraction = RecommendAttractionClass(
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
                            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
                            recyclerView_new.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    recommendDB.collection("Attraction").orderBy("Allprefer73",Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
                                val attraction = RecommendAttractionClass(
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
                            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
                            recyclerView_new.adapter = adapter
                        }
                }
            }else if (spotprefer > 4 || spotprefer < 6) {
                if (dateprefer == true) {
                    recommendDB.collection("Attraction").orderBy("Monprefer55",Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
                                val attraction = RecommendAttractionClass(
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
                            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
                            recyclerView_new.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    recommendDB.collection("Attraction").orderBy("Allprefer55", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener { task ->
                            for (querySnapshot in task.result!!) {
                                val attraction = RecommendAttractionClass(
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
                            adapter = RecommendAttractionAdapter(this@RecommendAttractions, spotnameArrayList)
                            recyclerView_new.adapter = adapter
                        }
                }
            }

        }

//        db.collection("Attraction")//.orderBy("Hotsort")
//            .limit(1000)
//            .get()
//            .addOnCompleteListener { task ->
//                for (querySnapshot in task.result!!) {
//                    val attraction = Attraction(
//                        querySnapshot.getString("Address"),
//                        querySnapshot.getString("AttractionName"),
//                        querySnapshot.getString("PhotoUrl"),
//                        querySnapshot.id,
////                        querySnapshot.getDouble("latitude"),
////                        querySnapshot.getDouble("longitude"),
//                        querySnapshot.getDouble("SpotPrefer"),
//                        querySnapshot.getDouble("Distance"),
//                        querySnapshot.getString("BusinessHour"),
//                        querySnapshot.getString("Address"),
//                        querySnapshot.getString("TEL")
//                    )
//                    spotnameArrayList.add(attraction)
//                }
//                adapter= MyRecyclerViewAdapter(this@HotAttractions, spotnameArrayList)
//                recyclerView_main.adapter = adapter
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this@HotAttractions, "problem!", Toast.LENGTH_SHORT).show()
////                Log.w("--|--", e.message)
//            }
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
        recommendDB = FirebaseFirestore.getInstance()
    }

    private fun setUpRecyclerView() {
        recyclerView_new = findViewById(R.id.recyclerView_new)
        recyclerView_new.setHasFixedSize(true)
        recyclerView_new.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
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
