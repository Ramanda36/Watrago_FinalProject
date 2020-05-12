package com.example.firstpage.AttractionFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.*
import com.example.firstpage.TransportFragment.MyRecyclerViewAdapter_Trans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_fragment_attractions.*
import kotlinx.android.synthetic.main.search_dialog.*
import java.util.ArrayList
import java.util.Locale.filter
import java.util.*




class MyAttractions : AppCompatActivity() {
    var Auth=FirebaseAuth.getInstance()
    lateinit var spotprefer:Any
    lateinit var dateprefer:Any
    internal lateinit var db: FirebaseFirestore
    internal lateinit var recyclerView_my: RecyclerView
    val TAG = "MyAttractions"
    var myspotArrayList= ArrayList<myAttraction>()
    internal lateinit var adapter: MyattractionAdapter
    lateinit var tempdata: DocumentReference
    var dialog: Dialog? = null
    var tempinsert=""
    val filteredList_ct = ArrayList<myAttraction>()
    var temp=false
    val checkedLocationArray = booleanArrayOf(
        false, false, false, false, false, false, false, false, false,
        false, false, false, false, false, false, false, false, false,false)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_attractions)
        loadingDialog(6000)
        verifyUserIsLogin()
        setUpFirebase()


        setUpRecyclerView()
        calculatedis()
        loadDataFromFirebase()

        NewSpot.setOnClickListener {
            val intent = Intent(this, RecommendAttractions::class.java)
            startActivity(intent)
        }

        hotspot.setOnClickListener {
            val intent = Intent(this, HotAttractions::class.java)
            startActivity(intent)
        }
        logoutlink.setOnClickListener {
            Auth.signOut()
            if(Auth.currentUser==null){
                Toast.makeText(this, "Successfully logout:)", Toast.LENGTH_LONG).show()
                var intent=Intent(this, LoginActivity::class.java)

                startActivity(intent)

            }
        }


        hot_att_search_btn.setOnClickListener {
            search_dialog()
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

    }

    private fun calculatedis() {
        val caldis=  db.collection("Attraction")
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

    private fun filter(text: String) {
        val filteredList = ArrayList<myAttraction>()

        for (item in myspotArrayList) {
            if (item.getmyspotname().toLowerCase().contains(text.toLowerCase())) {
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
                for (item in myspotArrayList) {
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
                            Collections.sort(filteredList_ct,myAttraction.byhotm37)
                        }else  if (dateprefer == false){
                            Collections.sort(filteredList_ct,myAttraction.byhota37)
                        }
                    }else if(spotprefer > 4 || spotprefer < 6){
                        if(dateprefer == true){
                            Collections.sort(filteredList_ct,myAttraction.byhotm55)
                        }else if(dateprefer == false){
                            Collections.sort(filteredList_ct,myAttraction.byhota55)
                        }
                    }else if(spotprefer>5){
                        if(dateprefer == true){
                            Collections.sort(filteredList_ct,myAttraction.byhotm73)
                        }else if(dateprefer == false){
                            Collections.sort(filteredList_ct,myAttraction.byhota73)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }else{
            adapter.filterList(myspotArrayList)
        }
    }
    private fun loadDataFromFirebase() {
        if (myspotArrayList.size > 0)
            myspotArrayList.clear()
        tempdata=FirebaseFirestore.getInstance().collection("UserInfo").document(Auth.uid.toString())
        tempdata.get().addOnSuccessListener {
            var tempid=  it.get("Collection") as ArrayList<String>
            FirebaseFirestore.getInstance().collection("Attraction")
                .get()
                .addOnCompleteListener { task ->
                    for(i in 0 until tempid.size){
                        for (querySnapshot in task.result!!) {
//                        for(i in 0 until tempid.size){
                            if (querySnapshot.id==tempid[i]){
                                val attraction = myAttraction(
                                    querySnapshot.getString("AttractionName"),
                                    querySnapshot.getString("PhotoUrl"),
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
                                myspotArrayList.add(attraction)
                                break
//                                    Log.d("temparray",ScheduleArrayList.toString())
                            }
                        }
                    }
                    adapter= MyattractionAdapter(this@MyAttractions, myspotArrayList)
                    recyclerView_my.adapter = adapter
//                adapter= MyRecyclerViewAdapter_Transportation(this@Schedules,ScheduleArrayList)
                }
        }


//        db.collection("Attraction").orderBy("GoogleRate")
//            .get()
//            .addOnCompleteListener { task ->
//                for (querySnapshot in task.result!!) {
//
//                    val attraction = myAttraction(
//                        querySnapshot.getString("AttractionName"),
//                        querySnapshot.getString("PhotoUrl"),
//                        querySnapshot.id)
//                    myspotArrayList.add(attraction)
//                }
//                adapter= MyattractionAdapter(this@MyAttractions, myspotArrayList)
//
//                recyclerView_my.adapter = adapter
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this@MyAttractions, "problem!", Toast.LENGTH_SHORT).show()
//                Log.w("--|--", e.message)
//            }
    }

    private fun setUpFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun setUpRecyclerView() {
        recyclerView_my = findViewById(R.id.recyclerView_my)
        recyclerView_my.setHasFixedSize(true)
        recyclerView_my.layoutManager = GridLayoutManager(this,2)
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
            db.collection("UserInfo").document(Auth.uid.toString()).get().addOnSuccessListener {
                spotprefer = it.get("SpotPrefer")!! as Long
                dateprefer = it.get("DurationPrefer")!! as Boolean
//                Log.d("temppppp", temp.toString())
                if (!temp) {
                    dialog!!.disblock.setBackgroundResource(com.example.firstpage.R.color.block)
                    if (filteredList_ct.size != 0) {
                        Collections.sort(filteredList_ct, myAttraction.bydis.reversed())
                    } else {
                        Collections.sort(myspotArrayList, myAttraction.bydis.reversed())
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
                                Collections.sort(filteredList_ct, myAttraction.byhotm37)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, myAttraction.byhota37)
                            }
                        } else if ((spotprefer as Long) > 4 || (spotprefer as Long) < 6) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, myAttraction.byhotm55)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, myAttraction.byhota55)
                            }
                        } else if ((spotprefer as Long) > 5) {
                            if (dateprefer == true) {
                                Collections.sort(filteredList_ct, myAttraction.byhotm73)
                            } else if (dateprefer == false) {
                                Collections.sort(filteredList_ct, myAttraction.byhota73)
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
                                Collections.sort(myspotArrayList, myAttraction.byhotm37)
                            } else if (dateprefer == false) {
                                Collections.sort(myspotArrayList, myAttraction.byhota37)
                            }
                        } else if ((spotprefer as Long) > 4 || (spotprefer as Long) < 6) {
                            if (dateprefer == true) {
                                Collections.sort(myspotArrayList, myAttraction.byhotm55)
                            } else if (dateprefer == false) {
                                Collections.sort(myspotArrayList, myAttraction.byhota55)
                            }
                        } else if ((spotprefer as Long) > 5) {
                            if (dateprefer == true) {
                                Collections.sort(myspotArrayList, myAttraction.byhotm73)
                            } else if (dateprefer == false) {
                                Collections.sort(myspotArrayList, myAttraction.byhota73)
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
        loadimgdialog!!.setContentView(R.layout.loading_content)
        loadimgdialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        loadimgdialog!!.show()
        loadimgdialog!!.setCancelable(false)

        Handler().postDelayed({loadimgdialog!!.dismiss()},second)
    }
}


