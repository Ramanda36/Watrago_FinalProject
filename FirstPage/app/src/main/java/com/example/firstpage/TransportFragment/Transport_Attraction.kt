package com.example.firstpage.TransportFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.LoginActivity
//import com.example.firstpage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.transport_attraction.*
import java.util.*
import kotlin.collections.ArrayList
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstpage.AttractionFragment.*
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_recom__transport__attraction.*
import kotlinx.android.synthetic.main.fragment_fragment_attractions.*
import kotlinx.android.synthetic.main.fragment_fragment_attractions.hot_att_search_btn
import kotlinx.android.synthetic.main.search_dialog.*
import kotlinx.android.synthetic.main.transport_attraction.Myspot
import kotlinx.android.synthetic.main.transport_attraction.alreadyseleted
import kotlinx.android.synthetic.main.transport_attraction.confirm
import kotlinx.android.synthetic.main.transport_attraction.logoutlink


class Transport_Attraction : AppCompatActivity() {
    internal lateinit var db: FirebaseFirestore
    internal lateinit var recyclerView_main: RecyclerView
    internal lateinit var spotnameArrayList: ArrayList<Attraction>
    var Auth=FirebaseAuth.getInstance()
    var temp= ArrayList<String>()
    var temptemp=false
     var tempnull ="11"
    lateinit var spotprefer:Any
    lateinit var dateprefer:Any
    var deliver = ArrayList<String>()
    var tempinsert=""
    //    var transp = ArrayList<String>()
    var  tempid=""
    var schid=""
    internal lateinit var adapter: MyRecyclerViewAdapter_Transportation
    val TAG = "HotAttractions"
    var TempB =""
    var storeid:String=""
//    var count=0
    var travelid=""
    var dialog: Dialog? = null
    val filteredList_ct = java.util.ArrayList<Attraction>()
    val checkedLocationArray = booleanArrayOf(
        false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false,false)
    var autoschedule = ""
    var myspotname= ArrayList<String>()
    var myspotid= ArrayList<String>()
    var myschid=""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.firstpage.R.layout.activity_transport__attraction)
        loadingDialog(3000)
        setUpFirebase()
        spotnameArrayList = ArrayList()
        verifyUserIsLogin()
        setUpRecyclerView()
        calculatedis()
        loadDataFromFirebase()
        if( intent.getStringExtra("sche_ID")!=null){
            schid = intent.getStringExtra("sche_ID")//從edit來的
            tempnull=schid
            myspotname = intent.getStringArrayListExtra("spotname")
            myspotid = intent.getStringArrayListExtra("spotid")
            tempid=schid
        }
        else if(intent.getStringExtra("EXTRA_SESSION_ID")!=null){//新創一個行程過來ㄉ
            travelid = intent.getStringExtra("EXTRA_SESSION_ID")//sessionId改成tempid
            tempid=travelid
        }
        else if(intent.getStringExtra("REEXTRA_ID_TO_HOTATTRACTION")!=null){//從推薦景點選完行程景點過來ㄉ
            myspotname = intent.getStringArrayListExtra("spotname")
            myspotid = intent.getStringArrayListExtra("spotid")
            tempnull=intent.getStringExtra("sche_ID")
            myschid = intent.getStringExtra("REEXTRA_ID_TO_HOTATTRACTION")//sessionId改成tempid
            tempid=myschid
        }
        else if(intent.getStringExtra("REEXTRA_ID_TO_MYATTRACTION")!=null){//從我的景點選完行程景點過來ㄉ
            myspotname = intent.getStringArrayListExtra("spotname")

            myspotid = intent.getStringArrayListExtra("spotid")
            myschid = intent.getStringExtra("REEXTRA_ID_TO_MYATTRACTION")//sessionId改成tempid
            tempnull=intent.getStringExtra("sche_ID")
            tempid=myschid
        }



        confirm.setOnClickListener {
            val caldis=  db.collection("Schedule")
            caldis.document(tempid).get().addOnSuccessListener {
                if( tempnull=="11"){
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("行程優化排程")
                    builder.setMessage("想讓系統幫你排出更順的行程嗎?")
                    builder.setPositiveButton("YES"){dialog, which ->
                        autoschedule = "True"
                        val data2 = hashMapOf(
                            "autoSchedule" to autoschedule
                        )
                        caldis.document(tempid).update(data2 as Map<String, Any>)
                        val data = hashMapOf(
                            "ScheduleList" to adapter.checkspot
                        )
                        caldis.document(tempid).update(data as Map<String, Any>)
                        val intent = Intent(this, Schedules::class.java)
                        intent.putExtra("EXTRA_ID", tempid)
                        startActivity(intent)
                    }

                    builder.setNegativeButton("No"){dialog,which ->
                        autoschedule = "False"
                        val data2 = hashMapOf(
                            "autoSchedule" to autoschedule
                        )
                        caldis.document(tempid).update(data2 as Map<String, Any>)
                        val data = hashMapOf(
                            "ScheduleList" to adapter.checkspot
                        )
                        caldis.document(tempid).update(data as Map<String, Any>)
                        val intent = Intent(this, Schedules::class.java)
                        intent.putExtra("EXTRA_ID", tempid)
                        startActivity(intent)
                    }
                    builder.setNeutralButton("Cancel"){_,_ ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }//end of if( intent.getStringExtra("sche_ID")==null)
                else{
                    val data = hashMapOf(
                        "ScheduleList" to adapter.checkspot
                    )
                    caldis.document(tempid).update(data as Map<String, Any>)
                    val intent = Intent(this, Schedules::class.java)
                    intent.putExtra("EXTRA_ID", tempid)
                    startActivity(intent)
                }
            } // end of caldis.document(tempid).get().addOnSuccessListener
        }


        Recomspot.setOnClickListener {
            val intent = Intent(this, Recom_Transport_Attraction::class.java)
            intent.putExtra("RE_EXTRA_ID", tempid)
//            if(tempnull!="11"){
                intent.putExtra("sche_ID",tempnull)

//            }
            intent.putStringArrayListExtra("spotname", adapter.selectedspot)
            intent.putStringArrayListExtra("spotid", adapter.checkspot)
            startActivity(intent)

        }
        Myspot.setOnClickListener {
            val intent = Intent(this, My_Transport_Attraction::class.java)
            intent.putExtra("EXTRA_ID", tempid)
//            if(intent.getStringExtra("sche_ID")==""){
//                var temp =intent.getStringExtra("sche_ID")
//                intent.putExtra("sche_ID",temp)
//
//            }
//            if(tempnull!="11"){
                intent.putExtra("sche_ID",tempnull)

//            }
            intent.putStringArrayListExtra("spotname", adapter.selectedspot)
            intent.putStringArrayListExtra("spotid", adapter.checkspot)
            startActivity(intent)
        }
        hot_att_search_btn.setOnClickListener {
            search_dialog()

        }
        logoutlink.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                val intent=Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun calculatedis() {
        val caldis=  db.collection("Attraction")
        caldis.whereEqualTo("latitude",true).limit(3000).get()
            .addOnCompleteListener { task ->
                for (querySnapshot in task.result!!) {
                    var lat:Double
                    var lon:Double

                    if( querySnapshot.getString("latitude").toString()=="NA"){
                        lat=-10000.0
                    }
                    else {

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
    fun showSelectedSpot( a: ArrayList<String>,b:ArrayList<String> ) {//a:name,b:id
        deliver=a
        temp=b
        alreadyseleted.setText("已選擇:"+deliver.toString().replace("[", "").replace("]", ""))
    }

    private fun loadDataFromFirebase() {
        if (spotnameArrayList.size > 0)
            spotnameArrayList.clear()

        db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
            var spotprefer = it.get("SpotPrefer")!! as Long
            var dateprefer = it.get("DurationPrefer")

            if (spotprefer < 5) {
                if (dateprefer == true) {
                    db.collection("Attraction").orderBy("Monprefer37", Query.Direction.DESCENDING)//.whereEqualTo("latitude",true)
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
                                spotnameArrayList.add(attraction)
                            }
                            adapter = MyRecyclerViewAdapter_Transportation(this@Transport_Attraction, spotnameArrayList)
                            recyclerView_main.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    db.collection("Attraction").orderBy("Allprefer37",Query.Direction.DESCENDING)
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
                                spotnameArrayList.add(attraction)
                            }
                            adapter = MyRecyclerViewAdapter_Transportation(this@Transport_Attraction, spotnameArrayList)
                            recyclerView_main.adapter = adapter
                        }
                }
            }else if (spotprefer > 5) {
                if (dateprefer == true) {
                    db.collection("Attraction").orderBy("Monprefer73",Query.Direction.DESCENDING)
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
                                spotnameArrayList.add(attraction)
                            }
                            adapter = MyRecyclerViewAdapter_Transportation(this@Transport_Attraction, spotnameArrayList)
                            recyclerView_main.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    db.collection("Attraction").orderBy("Allprefer73",Query.Direction.DESCENDING)
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
                                spotnameArrayList.add(attraction)
                            }
                            adapter = MyRecyclerViewAdapter_Transportation(this@Transport_Attraction, spotnameArrayList)
                            recyclerView_main.adapter = adapter
                        }
                }
            }else if (spotprefer > 4 || spotprefer < 6) {
                if (dateprefer == true) {
                    db.collection("Attraction").orderBy("Monprefer55",Query.Direction.DESCENDING)
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
                                spotnameArrayList.add(attraction)
                            }
                            adapter = MyRecyclerViewAdapter_Transportation(this@Transport_Attraction, spotnameArrayList)
                            recyclerView_main.adapter = adapter
                        }
                }
                if (dateprefer == false) {
                    db.collection("Attraction").orderBy("Allprefer55",Query.Direction.DESCENDING)
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
                                spotnameArrayList.add(attraction)
                            }
                            adapter = MyRecyclerViewAdapter_Transportation(this@Transport_Attraction, spotnameArrayList)
                            recyclerView_main.adapter = adapter
                        }
                }
            }

        }
    }

    private fun filter(text: String) {
        val filteredList = java.util.ArrayList<Attraction>()
        for (item in spotnameArrayList) {
            if (item.getspotname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)
    }
    private fun filtercountry(Array1 : java.util.ArrayList<String>) {
        if(Array1.size!=0){
            var text =""
            for(i in 0..Array1.size-1){
                text = Array1[i]
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
    private fun setUpFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun setUpRecyclerView() {
        recyclerView_main = findViewById(com.example.firstpage.R.id.recyclerView_main)
        recyclerView_main.setHasFixedSize(true)
        recyclerView_main.layoutManager = GridLayoutManager(this,2) as RecyclerView.LayoutManager?
    }
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    private fun search_dialog(){

//        var dialog: Dialog?
        dialog = Dialog(this)
        dialog!!.setContentView(com.example.firstpage.R.layout.search_dialog)
        dialog!!.setTitle("search")
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_light))
        val wmlp = dialog!!.window.attributes
        wmlp.gravity = Gravity.TOP or Gravity.LEFT

        dialog!!.show()
        if(temptemp){
            dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.block)
        }else{
            dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.white)

        }
        dialog!!.disblock.setOnClickListener {
            db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
                spotprefer = it.get("SpotPrefer")!! as Long
                dateprefer = it.get("DurationPrefer")!! as Boolean
//                Log.d("temppppp", temp.toString())
                if (!temptemp) {
                    dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.block)
                    if (filteredList_ct.size != 0) {
                        Collections.sort(filteredList_ct, Attraction.bydis.reversed())
                    } else {
                        Collections.sort(spotnameArrayList, Attraction.bydis.reversed())
//               Log.d("spotnameArrayList",spotnameArrayList.size.toString())
                    }
                    temptemp = true
                } else {
                    dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.white)
                    if (filteredList_ct.size != 0) {
//                   db.collection("UserInfo").document(Auth.uid.toString()).get()
//                       .addOnSuccessListener {
//                           var spotprefer = it.get("SpotPrefer")!! as Long
//                           var dateprefer = it.get("DurationPrefer")
                        if ((spotprefer as Long) < 5) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, Attraction.byhotm37)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, Attraction.byhota37)
                            }
                        } else if ((spotprefer as Long) > 4 || (spotprefer as Long) < 6) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, Attraction.byhotm55)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, Attraction.byhota55)
                            }
                        } else if ((spotprefer as Long) > 5) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, Attraction.byhotm73)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, Attraction.byhota73)
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
                                Collections.sort(spotnameArrayList, Attraction.byhotm37)
                            } else if (dateprefer == false) {
                                Collections.sort(spotnameArrayList, Attraction.byhota37)
                            }
                        } else if ((spotprefer as Long) > 4 || (spotprefer as Long) < 6) {
                            if (dateprefer == true) {
                                Collections.sort(spotnameArrayList, Attraction.byhotm55)
                            } else if (dateprefer == false) {
                                Collections.sort(spotnameArrayList, Attraction.byhota55)
                            }
                        } else if ((spotprefer as Long) > 5) {
                            if (dateprefer == true) {
                                Collections.sort(spotnameArrayList, Attraction.byhotm73)
                            } else if (dateprefer == false) {
                                Collections.sort(spotnameArrayList, Attraction.byhota73)
                            }
//                       }
                        }

                    }
//               adapter.notifyDataSetChanged()
                    temptemp = false

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
    private fun verifyUserIsLogin() {
        val uid=Auth.uid
        if(uid==null){
            val intent=Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun loadingDialog(second : Long){
        var loadimgdialog: Dialog? = null

        //loading dialog
        loadimgdialog = Dialog(this)
        loadimgdialog!!.setContentView(com.example.firstpage.R.layout.loading_content)
        loadimgdialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        loadimgdialog!!.show()
        loadimgdialog!!.setCancelable(false)

        Handler().postDelayed({loadimgdialog!!.dismiss()},second)
    }
}