package com.example.firstpage.TransportFragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.Attraction
import com.example.firstpage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.collections.ArrayList
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.db.NULL
import org.json.JSONException
import java.io.IOException
import java.net.URLDecoder
import java.util.*


class MyRecyclerViewAdapter_Trans(var schedules: Schedules, var ScheduleList: ArrayList<Attraction>)
    : RecyclerView.Adapter<MyRecyclerViewHolder_Transportation>() {

    var TimeArrayList = Array(ScheduleList.size,{"0"}) //交通工具抵達時間
    //    var TempTimeArrayList = Array(ScheduleList.size,{"0"})
    var DrIVINGTimeArrayList = Array(ScheduleList.size,{"0"}) //開車、走路抵達時間
    var PublicTranstime = Array(ScheduleList.size,{0}) //交通工具花費時間
    var Dtime = Array(ScheduleList.size,{0}) //開車、走路花費時間

    var startime=""

    var StopTimeArray = ArrayList<Int>() //停留時間 1031
    var stoptransferans = ""

    var tempArray = ArrayList<String>()
    var transmode_fk = ArrayList<String>()
    var tempmode ="" //大眾轉換開車模式
    var pretransmode = ""

    var stoptime = 0 //預設停留時間的變數
    private var mydialog: Dialog? = null

    //for google maps
    var longitude =0.0
    var latitude = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder_Transportation {
        val layoutInflater = LayoutInflater.from(schedules.baseContext)
        val view = layoutInflater.inflate(R.layout.transport_list_item, parent, false)
        return MyRecyclerViewHolder_Transportation(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyRecyclerViewHolder_Transportation, position: Int) {

        val db = FirebaseFirestore.getInstance()

        var Auth= FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance().collection("UserInfo")
            .document(Auth.uid.toString()).get().addOnSuccessListener {
                var transgap = it.getDouble("transporttime")!!

                schedules.db.collection("Schedule").document(schedules.id).get().addOnSuccessListener {
                    startime = it.getString("StartDate")!!
                    tempArray = it.get("Transport") as ArrayList<String>
                    StopTimeArray = it.get("StopTime") as ArrayList<Int>
                    pretransmode = it.getString("PreTransport")!!
                    if(tempArray.size==1){
                        for(i in 0..ScheduleList.size-2){
                            if(StopTimeArray.size<=ScheduleList.size){
                                StopTimeArray.add(StopTimeArray[0])
                            } //1031
                            if(transmode_fk.size<=ScheduleList.size-1){
                                transmode_fk.add(tempArray[0])
                            }
                        }
//                        Log.d("size",transmode_fk.size.toString())
                    }else{
                        transmode_fk = tempArray
                        StopTimeArray = StopTimeArray //1031
                        while(transmode_fk.size<=ScheduleList.size-1)
                            transmode_fk.add(pretransmode)
                        while(StopTimeArray.size<=ScheduleList.size-1)
                            StopTimeArray.add(45) //1112
                    }
                    if(ScheduleList.size!=1){
                        bind(ScheduleList,position,transgap,holder,transmode_fk[position],startime)
                    }else{
                        holder.mTranSpot.text = ScheduleList[0].getspotname()
                    }
                }
            }

        mydialog = Dialog(this.schedules)
        mydialog!!.setContentView(R.layout.dialog_test)
        mydialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        mydialog!!.window!!.attributes.gravity =  Gravity.BOTTOM
        holder.mTranSpot.setOnClickListener {

            val address = mydialog!!.findViewById<TextView>(R.id.address)
            val tel = mydialog!!.findViewById<TextView>(R.id.tel)
            val AttName = mydialog!!.findViewById<TextView>(R.id.AttName)

            val txtSunTime = mydialog!!.findViewById<TextView>(R.id.txtSunTime)
            val txtMonTime = mydialog!!.findViewById<TextView>(R.id.txtMonTime)
            val txtTueTime = mydialog!!.findViewById<TextView>(R.id.txtTueTime)
            val txtWedTime = mydialog!!.findViewById<TextView>(R.id.txtWedTime)
            val txtThuTime = mydialog!!.findViewById<TextView>(R.id.txtThuTime)
            val txtFriTime = mydialog!!.findViewById<TextView>(R.id.txtFriTime)
            val txtSatTime = mydialog!!.findViewById<TextView>(R.id.txtSatTime)

            var SunTimeS = ""
            var SunTimeC = ""
            var MonTimeS = ""
            var MonTimeC = ""
            var TueTimeS = ""
            var TueTimeC = ""
            var WedTimeS = ""
            var WedTimeC = ""
            var ThuTimeS = ""
            var ThuTimeC = ""
            var FriTimeS = ""
            var FriTimeC = ""
            var SatTimeS = ""
            var SatTimeC = ""

            var StatusSun = ""
            var StatusMon = ""
            var StatusTue = ""
            var StatusWed = ""
            var StatusThu = ""
            var StatusFri = ""
            var StatusSat = ""

            //for google maps
            longitude =ScheduleList[holder.adapterPosition].getSpotlong().toDouble()
            latitude = ScheduleList[holder.adapterPosition].getSpotlat().toDouble()

            val mMapView = mydialog!!.findViewById(R.id.mapView) as MapView

            mMapView.onCreate(mydialog!!.onSaveInstanceState())
            mMapView.onResume()

            mMapView.getMapAsync { googleMap ->
                val posisiabsen = LatLng(latitude,longitude) ////your lat lng

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen))
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.addMarker(MarkerOptions().position(posisiabsen).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.spot_only
                    )))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
            }

            var ScheduleSpotID = ScheduleList[holder.adapterPosition].getSpotid()

            db.collection("Attraction").document(ScheduleSpotID).get().addOnSuccessListener {

                SunTimeS= it.getString("OpenSunS").toString()
                SunTimeC= it.getString("OpenSunC").toString()

                MonTimeS= it.getString("OpenMonS").toString()
                MonTimeC= it.getString("OpenMonC").toString()

                TueTimeS= it.getString("OpenTueS").toString()
                TueTimeC= it.getString("OpenTueC").toString()

                WedTimeS= it.getString("OpenWedS").toString()
                WedTimeC= it.getString("OpenWedC").toString()

                ThuTimeS= it.getString("OpenThuS").toString()
                ThuTimeC= it.getString("OpenThuC").toString()

                FriTimeS= it.getString("OpenFriS").toString()
                FriTimeC= it.getString("OpenFriC").toString()

                SatTimeS= it.getString("OpenSatS").toString()
                SatTimeC= it.getString("OpenSatC").toString()


                StatusSun= it.getString("StatusSun").toString()
                StatusMon= it.getString("StatusMon").toString()
                StatusTue= it.getString("StatusTUE").toString()
                StatusWed= it.getString("StatusWed").toString()
                StatusThu= it.getString("StatusThu").toString()
                StatusFri= it.getString("StatusFri").toString()
                StatusSat= it.getString("StatusSat").toString()


                if(StatusSun=="NA"&&StatusMon=="NA"&&StatusTue=="NA"&&StatusWed=="NA"&&StatusThu=="NA"&&StatusFri=="NA"&&StatusSat=="NA"){

                    txtSunTime.setBackgroundColor(Color.WHITE)
                    txtSunTime.text="[目前網路上沒資料歐~]"
                    txtSunTime.setTextColor(Color.LTGRAY)
                    txtMonTime.visibility=View.INVISIBLE
                    txtTueTime.visibility=View.INVISIBLE
                    txtWedTime.visibility=View.INVISIBLE
                    txtThuTime.visibility=View.INVISIBLE
                    txtFriTime.visibility=View.INVISIBLE
                    txtSatTime.visibility=View.INVISIBLE

                }else{
                    if(StatusSun=="TRUE"){
                        txtSunTime.setTextColor(Color.GRAY)
                        txtSunTime.setBackgroundResource(R.drawable.att_info_bhour_sun)
                        txtSunTime.visibility=View.VISIBLE
                        txtSunTime.text=SunTimeS+" - "+SunTimeC
                    }else if(StatusSun=="FALSE"){
                        txtSunTime.setTextColor(Color.GRAY)
                        txtSunTime.setBackgroundResource(R.drawable.att_info_bhour_sun)
                        txtSunTime.visibility=View.VISIBLE
                        txtSunTime.text="未開店"
                    }

                    if(StatusMon=="TRUE"){
                        txtMonTime.visibility=View.VISIBLE
                        txtMonTime.text=MonTimeS+" - "+MonTimeC
                    }else if(StatusMon=="FALSE"){
                        txtMonTime.visibility=View.VISIBLE
                        txtMonTime.text="未開店"
                    }

                    if(StatusTue=="TRUE"){
                        txtTueTime.visibility=View.VISIBLE
                        txtTueTime.text=TueTimeS+" - "+TueTimeC
                    }else if(StatusTue=="FALSE"){
                        txtTueTime.visibility=View.VISIBLE
                        txtTueTime.text="未開店"
                    }

                    if(StatusWed=="TRUE"){
                        txtWedTime.visibility=View.VISIBLE
                        txtWedTime.text=WedTimeS+" - "+WedTimeC
                    }else if(StatusWed=="FALSE"){
                        txtWedTime.visibility=View.VISIBLE
                        txtWedTime.text="未開店"
                    }

                    if(StatusThu=="TRUE"){
                        txtThuTime.visibility=View.VISIBLE
                        txtThuTime.text=ThuTimeS+" - "+ThuTimeC
                    }else if(StatusThu=="FALSE"){
                        txtThuTime.visibility=View.VISIBLE
                        txtThuTime.text="未開店"
                    }

                    if(StatusFri=="TRUE"){
                        txtFriTime.visibility=View.VISIBLE
                        txtFriTime.text=FriTimeS+" - "+FriTimeC
                    }else if(StatusFri=="FALSE"){
                        txtFriTime.visibility=View.VISIBLE
                        txtFriTime.text="未開店"
                    }

                    if(StatusSat=="TRUE"){
                        txtSatTime.visibility=View.VISIBLE
                        txtSatTime.text=SatTimeS+" - "+SatTimeC
                    }else if(StatusSat=="FALSE"){
                        txtSatTime.visibility=View.VISIBLE
                        txtSatTime.text="未開店"
                    }
                }
            }

            if(ScheduleList[holder.adapterPosition].getspotAddress()=="NA"){
                address.text ="[目前網路上沒資料歐~]"
                address.setTextColor(Color.LTGRAY)
            }else{
                address.text =  ScheduleList[holder.adapterPosition].getspotAddress()
                address.setTextColor(Color.GRAY)
            }

            if(ScheduleList[holder.adapterPosition].getTel()=="NA"){
                tel.text ="[目前網路上沒資料歐~]"
                tel.setTextColor(Color.LTGRAY)
            }else{
                tel.text =  ScheduleList[holder.adapterPosition].getTel()
                tel.setTextColor(Color.GRAY)
            }
            AttName.text =  ScheduleList[holder.adapterPosition].getspotname()
//            if (!schedules.isFinishing) {
            mydialog!!.show()
//            }
        }

    }

    private fun bind(ScheduleList: ArrayList<Attraction>, position: Int, TransGap: Double, holder: MyRecyclerViewHolder_Transportation,transmode:String,startime:String) {
        var TransMode = ""
        var Date = startime
        var connectITernet =""
        var TransTime = ""
        var TimeHour = ""
        var TimeMin = ""
        var HourMin = ""
        var DrivingTime = ""

        holder.mTranSpot.text = ScheduleList.get(position).getspotname() //景點名 顯現
        var orig = ScheduleList.get(position).getspotAddress()
        var dest = ""
        orig = URLEncoder.encode(orig)
//            Log.d("size",ScheduleList.size.toString())

        if(position!=ScheduleList.size){
            if(position==0){
                HourMin = holder.mtranstime.text.toString() //預設 9:00
                TimeArrayList[0] = HourMin
                DrIVINGTimeArrayList[0] = HourMin
//                    TempTimeArrayList[0] = HourMin
            }

            if(position+1!=ScheduleList.size){
                dest = ScheduleList.get(position+1).getspotAddress()
                dest = URLEncoder.encode(dest)
            }

            TransMode = transmode
            if(TransMode=="subway"||TransMode=="bus"||TransMode=="train"){
                doAsync {
                    if(position>0) {
                        var LastDest = ScheduleList.get(position).getspotAddress()
                        LastDest = URLEncoder.encode(LastDest)
                        var LastOrig = ScheduleList.get(position-1).getspotAddress()
                        LastOrig = URLEncoder.encode(LastOrig)

                        //bus part
                        while(TimeArrayList[position-1] == "0"){
                            Thread.sleep(3000)
                        }
                        TransTime = TimeArrayList[position-1]
//                        Log.d("TransTime1",TimeArrayList[position-1]+" "+position.toString())

                        var Time = TransTime.split(":")
                        TimeHour = Time[0]
                        TimeMin = Time[1]

                        HourMin = sendTime(TimeHour,TimeMin,LastOrig,LastDest,position,Date,transmode_fk[(position-1)])
                    } // end of if(position>0)

                    if(position+1!=ScheduleList.size){
                        HourMin = URLEncoder.encode(HourMin)
                        val TransportUrlDriving = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+"driving"+"&desti="+dest+"&orig="+orig
                        try{
                            tempmode = TransMode
                            val TransportUrl = "http://120.126.18.144:2000/TransportationInform_N?departureTime="+HourMin+"%3A00&departureDay="+Date+"&Tmode="+TransMode+"&dest="+dest+"&orig="+orig
                            //      !!bus part!!
                            connectITernet = sendGet(TransportUrl,TransportUrlDriving,"showdialog")
                            //      !!driving part!!
                            DrivingTime = sendDrivingGet(TransportUrlDriving)

                            PublicTranstime[position] = TransferTime(connectITernet)
//                    Log.d("PublicTranstime",PublicTranstime[position].toString())
                            Dtime [position]=  TransferTime(DrivingTime)
                        }catch (e:Exception){
                            try{
                                if(TransMode!="bus"){
                                    tempmode = "train"
                                    transmode_fk[position] = "train"
                                    val TransportUrl = "http://120.126.18.144:2000/TransportationInform_N?departureTime="+HourMin+"%3A00&departureDay="+Date+"&Tmode="+transmode_fk[position]+"&dest="+dest+"&orig="+orig
                                    //      !!train part!!
                                    connectITernet = sendGet(TransportUrl,TransportUrlDriving,"showdialog")
                                    //      !!driving part!!
                                    DrivingTime = sendDrivingGet(TransportUrlDriving)

                                    PublicTranstime[position] = TransferTime(connectITernet)
                                    Dtime [position]=  TransferTime(DrivingTime)
                                }else{
                                    TransMode = "driving"
                                    transmode_fk[position] = "driving"
                                    connectITernet = sendDrivingGet(TransportUrlDriving)
                                }
                            }catch (e:Exception){
                                TransMode = "driving"
                                transmode_fk[position] = "driving"
                                connectITernet = sendDrivingGet(TransportUrlDriving)
                            }
                        }
                    } // end of if(position+1!=ScheduleList.size)
                    uiThread {
                        stoptransferans=stoptimeTransfer(StopTimeArray[position])
                        var timeGapHour=0
                        holder.mtranstime.text = TimeArrayList[position]
                        if(TransMode=="bus" && position!=ScheduleList.size-1){
                            holder.mtransmode.text = "搭公車約花 "+connectITernet
                            holder.mimgTrans.setImageResource(R.drawable.ic_bus)
                        }else if(TransMode=="subway" && position!=ScheduleList.size-1){
                            holder.mtransmode.text = "搭捷運約花 "+connectITernet
                            holder.mimgTrans.setImageResource(R.drawable.ic_mrt)
                        }else if(TransMode=="train" && position!=ScheduleList.size-1){
                            holder.mtransmode.text = "搭火車約花 "+connectITernet
                            holder.mimgTrans.setImageResource(R.drawable.ic_train)
                        }else if (TransMode=="driving" && position!=ScheduleList.size-1) {
                            if(tempmode=="bus"){
                                tempmode = "公車"
                            }else if(tempmode=="subway"){
                                tempmode = "捷運"
                            }else if(tempmode=="train")
                                tempmode = "火車"
                            holder.mtransmode.text = "由於搭"+tempmode+"目前沒有相關路徑，因此幫你自動改以開車模式"+'\n'+"開車約花 " + connectITernet
                            holder.mimgTrans.setImageResource(R.drawable.ic_car)
                        }
                        if(position+1!=ScheduleList.size){
                            holder.stayTime.text = "預計停留" + stoptransferans
//                            Log.d("why?",PublicTranstime[position].toString()+",,,,"+TransGap+",,,,"+Dtime[position].toString())
                            if((PublicTranstime[position]*TransGap) > Dtime[position]) {
                                var timegap = PublicTranstime[position] - Dtime[position]
                                if(timegap>60){
                                    while(timegap>60){
                                        timegap -= 60
                                        timeGapHour+=1
                                    }
                                    holder.mtransmode.text =  holder.mtransmode.text as String +'\n' +"開車時間大約快"+timeGapHour+"小時"+timegap+"分鐘喔~~"
//                                    Log.d("問號了", holder.mtransmode.text as String)
                                }
                                else{
                                    holder.mtransmode.text = holder.mtransmode.text as String + '\n' +"開車時間大約快"+timegap+"分鐘喔~~"
//                                    Log.d("問號了", holder.mtransmode.text as String)
                                }
                            } // end of if((PublicTranstime[position]*TransGap) > Dtime[position])
                        } // end of if(position+1!=ScheduleList.size)
                    } //end of uithread
                } //end of doasync
            }  // end of if(TransMode=="subway"||TransMode=="bus")
            if(TransMode=="driving"||TransMode=="walking"){
                doAsync {
                    if(position>0) {
                        var LastDest = ScheduleList.get(position).getspotAddress()
                        LastDest = URLEncoder.encode(LastDest)
                        var LastOrig = ScheduleList.get(position-1).getspotAddress()
                        LastOrig = URLEncoder.encode(LastOrig)

                        //bus part
//                    ||DrIVINGTimeArrayList[position-1]!=TempTimeArrayList[position-1]
                        while(TimeArrayList[position-1] == "0" ){
                            Thread.sleep(3000)
                        }
                        TransTime = TimeArrayList[position-1]
//                        Log.d("TransTime1",TimeArrayList[position-1]+" "+position.toString())

                        var Time = TransTime.split(":")
                        TimeHour = Time[0]
                        TimeMin = Time[1]
                        HourMin = sendTime(TimeHour,TimeMin,LastOrig,LastDest,position,Date,transmode_fk[position-1])
                    } // end of if(position>0)

                    if(position+1!=ScheduleList.size){
                        HourMin = URLEncoder.encode(HourMin)
                        var TransportUrlDriving = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+TransMode+"&desti="+dest+"&orig="+orig
//                    Log.d("TransportUrlDriving",TransportUrlDriving)
                        try{
                            DrivingTime = sendDrivingGet(TransportUrlDriving)
                        }catch (e :Exception){
                            tempmode = "driving"
                            TransMode = "walking"
                            TransportUrlDriving = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+TransMode+"&desti="+dest+"&orig="+orig
                            DrivingTime = sendDrivingGet(TransportUrlDriving)
                        }
                    } // end of if(position+1!=ScheduleList.size)

                    uiThread{
                        if(position!=ScheduleList.size-1){
                            stoptransferans=stoptimeTransfer(StopTimeArray[position])
                            holder.stayTime.text = "預計停留" + stoptransferans
                        }
                        holder.mtranstime.text = TimeArrayList[position]
                        if(TransMode=="driving" && position!=ScheduleList.size-1){
                            holder.mtransmode.text = "開車約花 "+DrivingTime
                            holder.mimgTrans.setImageResource(R.drawable.ic_car)
                        }else if(TransMode=="walking" && position!=ScheduleList.size-1){
                            if(tempmode!="driving") {
                                holder.mtransmode.text = "走路約花 " + DrivingTime
                                holder.mimgTrans.setImageResource(R.drawable.ic_walk)
                            }
                            else{
                                holder.mtransmode.text = "由於路程很近，因此幫您轉為走路方式\n走路約花 "+DrivingTime
                                holder.mimgTrans.setImageResource(R.drawable.ic_walk)
                            }
                        }
                    } // end of uiThread
                } // end of doasync
            } // end of else if(TransMode=="driving"||TransMode=="walking")
        } //end of position!=ScheduleList.size
    } // end of bind function

    fun sendTime (Hour :String,Min :String,orig :String, dest:String,position: Int, Date: String,TransMode:String): String {
        var mode = TransMode
        var finalHour = Hour
        var finalMin = Min
        var tempHour = ""
        var tempMin = ""
        var connectITernet = ""
        var TransportUrl = ""
        var DriveUrl = ""
        if(mode=="subway"||mode=="train"){
            TransportUrl = "http://120.126.18.144:2000/TransportationInform_N?departureTime="+finalHour+"%3A"+finalMin+"%3A00&departureDay="+Date+"&Tmode="+mode+"&dest="+dest+"&orig="+orig
            DriveUrl = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+"driving"+"&desti="+dest+"&orig="+orig
//            Log.d("urlsendtime",TransportUrl)
            try{
                connectITernet = sendGet(TransportUrl,DriveUrl,"transportation")
                if(connectITernet==""){
                    TransportUrl = "http://120.126.18.144:2000/TransportationInform_N?departureTime="+finalHour+"%3A"+finalMin+"%3A00&departureDay="+Date+"&Tmode="+"train"+"&dest="+dest+"&orig="+orig

                    connectITernet = sendGet(TransportUrl,DriveUrl,"transportation")
                    if(connectITernet=="")
                        connectITernet = sendDrivingGet(DriveUrl)
                }
            }catch (e:Exception){
                TransportUrl = "http://120.126.18.144:2000/TransportationInform_N?departureTime="+finalHour+"%3A"+finalMin+"%3A00&departureDay="+Date+"&Tmode="+"train"+"&dest="+dest+"&orig="+orig
                connectITernet = sendGet(TransportUrl,DriveUrl,"transportation")
                if(connectITernet=="")
                    connectITernet = sendDrivingGet(DriveUrl)
            }
        }else if(mode=="bus"){
            TransportUrl = "http://120.126.18.144:2000/TransportationInform_N?departureTime="+finalHour+"%3A"+finalMin+"%3A00&departureDay="+Date+"&Tmode="+mode+"&dest="+dest+"&orig="+orig
            DriveUrl = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+"driving"+"&desti="+dest+"&orig="+orig
//            Log.d("urlsendtime2",TransportUrl)
            try{
                connectITernet = sendGet(TransportUrl,DriveUrl,"transportation")
                if(connectITernet==""){
                    connectITernet = sendDrivingGet(DriveUrl)
                }
            }catch (e:Exception){
//                Log.d("urlsendtime",DriveUrl)
                connectITernet = sendDrivingGet(DriveUrl)
//                Log.d("connectITernet",connectITernet)
            }
        }
        else{
            try{
                DriveUrl = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+mode+"&desti="+dest+"&orig="+orig
                connectITernet = sendDrivingGet(DriveUrl)
//            Log.d("DriveUrl",DriveUrl)
                if(connectITernet==""){
                    connectITernet = sendDrivingGet("http://120.126.18.144:7000/DriveWalkTransportation?mode="+"walking"+"&desti="+dest+"&orig="+orig)
                }
            }catch (e:Exception){
//                Log.d("urlsendtime",DriveUrl)
                connectITernet = sendDrivingGet("http://120.126.18.144:7000/DriveWalkTransportation?mode="+"walking"+"&desti="+dest+"&orig="+orig)
//                Log.d("connectITernet",connectITernet)
            }
//            Log.d("connectITernet",connectITernet)
        }

        if(connectITernet.contains("小時")){
            var temp = connectITernet.split("小時"," ")
            tempHour = temp[0]
//            Log.d("tempHour",tempHour)
            if(connectITernet.contains("分鐘")){
                var temp2 = connectITernet.split("分鐘"," ")
                tempMin = temp2[2]
                stoptime = StopTimeArray[position-1] //1031
//                stoptime = 45 //預設停留45 min
//                Log.d("tempMin",tempMin)
            }
            finalHour = (Hour.toInt() + tempHour.toInt()).toString()
        }
        else{
            var temp = connectITernet.split("分鐘"," ")
            tempMin = temp[0]
            stoptime = StopTimeArray[position-1] //1031
//            stoptime = 45 //預設停留45 min
        }
//        Log.d("finalmin",(Min.toInt() + tempMin.toInt()+stoptime).toString())
        if(tempMin!=""){
            finalMin = (Min.toInt() + tempMin.toInt()+stoptime).toString()
        }else{
            tempMin = "0"
            finalMin = (Min.toInt() + tempMin.toInt()+stoptime).toString()
        }

        while(finalMin.toInt() >= 60){
            finalMin = (finalMin.toInt() - 60).toString()
            finalHour = (finalHour.toInt()+1).toString()
        }

        if (finalHour.length < 2) {
            finalHour = "0" + finalHour
//               Log.d("FH", finalHour)
        }
        if(finalMin.length < 2){
            finalMin = "0" + finalMin
//              Log.d("FH", finalMin)
        }

        TimeArrayList[position] = finalHour+":"+finalMin
//            TempTimeArrayList[position] = finalHour+":"+finalMin
//        Log.d("urlFinalTime",TimeArrayList[position])
        return TimeArrayList[position]

    }

    fun sendDrivingGet(url :String): String{
        val connection2 = URL(url).openConnection() as HttpURLConnection
        try {
            val data = connection2.inputStream.bufferedReader().readText()
            var jsonObject = JSONObject(data).get("TotalTime")
//            Log.d("jsonObject", jsonObject.toString())
//            var Mtemp = jsonObject.toString().split("分鐘"," ")
            jsonObject=jsonObject.toString().replace("[", "").replace("]", "")
            jsonObject=jsonObject.toString().replace("\"", "").replace("\"", "")
//            Log.d(ContentValues.TAG, jsonObject.toString())
//            Log.d("why?",jsonObject.toString())
            return (jsonObject.toString())
        }finally {
            connection2.disconnect()
        }
    }

    fun sendGet( url :String, drivingurl:String , functions : String): String {
        var ans = ""
        val connection = URL(url).openConnection() as HttpURLConnection
        val data = connection.inputStream.bufferedReader().readText()
        try {
            if(functions == "transportation"){
                var jsonArray2 = JSONArray(data)
                val jsonArray = JSONArray(jsonArray2.getJSONArray(0).toString())
                val jsonObject = JSONObject(jsonArray.get(0).toString())
                ans =jsonObject.get("CostTime").toString()
            }else if(functions == "showdialog"){
                var jsonArray2 = JSONArray(data)
                val jsonArray = JSONArray(jsonArray2.getJSONArray(0).toString())
                val jsonObject = JSONObject(jsonArray.get(0).toString())
                ans =jsonObject.get("CostTime").toString()
            }
        }catch (e:Exception){
            if(functions == "transportation")
                sendDrivingGet(drivingurl)
        }
        finally {
            return(ans)
            connection.disconnect()
        }
    }

    fun TransferTime(costtime : String):Int{
        var hour = 0
        var min = 0
        if(costtime.contains("小時")&&costtime.contains("分鐘")){
            var htemp = costtime.split("小時"," ")
            hour = (htemp[0].toInt())*60
            var mtemp2 = costtime.split("分鐘"," ")
//            Log.d("mtemp2",mtemp2[2])
            min = mtemp2[2].toInt()
        }else if(costtime.contains("小時")){
            var htemp = costtime.split("小時"," ")
            hour = (htemp[0].toInt())*60
        }else{
            var mtemp2 = costtime.split("分鐘"," ")
            min = mtemp2[0].toInt()
        }
        var finalTime = hour+min
        return finalTime
    }

    fun stoptimeTransfer(stoptime:Int):String{
        var stophour = 0
        var stopmin = 0
        var stopans = ""
        if(stoptime>=60){
            stophour = stoptime/60
            stopmin = stoptime%60
            if(stopmin!=0){
                stopans = stophour.toString() + "小時" + stopmin.toString() + "分鐘"
            }else{
                stopans = stophour.toString() + "小時"
            }
        }else{
            stopmin = stoptime
            stopans = stopmin.toString() + "分鐘"
        }
        return stopans
    }

    override fun getItemCount(): Int {
        return ScheduleList.size
    }

}


//fun removeItem(viewHolder: RecyclerView.ViewHolder){
//    removePosition = viewHolder.adapterPosition
//    removedItem = ScheduleList.get(viewHolder.adapterPosition).getspotname()
//    ScheduleList.removeAt(viewHolder.adapterPosition)
//    notifyItemRemoved(viewHolder.adapterPosition)
//}

//private var removePosition : Int = 0
//private var removedItem : String = ""


//        holder.mTranSpot.setOnClickListener {
//            var trans_desctiption = transportdialog.findViewById<TextView>(R.id.transportation_description)
//            doAsync {
//                var clickorig = ScheduleList[holder.adapterPosition].getspotAddress()
//                clickorig = URLEncoder.encode(clickorig)
//                var clickdest = ScheduleList[(holder.adapterPosition)+1].getspotAddress()
//                var unen_clickdest = ScheduleList[(holder.adapterPosition)+1].getspotname()
//                clickdest = URLEncoder.encode(clickdest)
//                var time =  TimeArrayList[holder.adapterPosition]
//                time = URLEncoder.encode(time)
//                var description = ""
//                if(transmode=="bus"||transmode=="subway"){
//                    var url = "http://120.126.18.144:3000/TransportationInform?departureTime="+time+"%3A00&departureDay="+"20191025"+"&Tmode="+"bus"+"&dest="+clickdest+"&orig="+clickorig
//                    description = sendGet(url,"showdialog")
////                    Log.d("description",description)
//                    uiThread{
//                        trans_desctiption.text = description
//                    }
//                }
//                else{
//                    var url = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+"driving"+"&desti="+clickdest+"&orig="+clickorig
//                    description = sendDrivingGet(url)
//                    uiThread{
//                        trans_desctiption.text ="到"+unen_clickdest+ " 約花 "+description
//                    }
//                }
//            }
////                if (!context.) {
//            transportdialog.show()
////                }
//        } // end of holder.mTranSpot.setOnClickListener