package com.example.firstpage.TransportFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.Attraction
import com.example.firstpage.Helper.MySwipeHelper
import com.example.firstpage.R
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class schedule_edit_adapter(var Schedules_edit: schedule_edit, var ScheduleList: ArrayList<Attraction>)
    : RecyclerView.Adapter<MyRecyclerViewHolder_Transportation>() {

    private var removePosition : Int = 0
    private var removedItem : String = ""

    var PreStopTime = ArrayList<Int>()
    var clockselected = false
    var stopttransfer = ""
    var stophour = 0
    var stopmin = 0
    var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder_Transportation {
        val layoutInflater = LayoutInflater.from(Schedules_edit.baseContext)
        val view = layoutInflater.inflate(R.layout.sche_edit_list_item, parent, false)
        return MyRecyclerViewHolder_Transportation(view)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder){
        removePosition = viewHolder.adapterPosition
        removedItem = ScheduleList.get(viewHolder.adapterPosition).getspotname()
        ScheduleList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        notifyDataSetChanged()

//        Snackbar.make(viewHolder.itemView, "$removedItem deleted.",Snackbar.LENGTH_LONG).setAction("UNDO"){
//            ScheduleList.add(removePosition,removedItem)
//            notifyItemInserted(removePosition)
//        }.show()
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        holder.mTranSpot.text=ScheduleList[position].getspotname()
//        holder.EDtransTime.text=Schedules_edit.timearray[position]
        PreStopTime = Schedules_edit.StopTimeArray //1031

        val transportdialog = Dialog(this.Schedules_edit)
        transportdialog!!.setContentView(R.layout.transportation_dialog)
        transportdialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        var trans_desctiption = transportdialog.findViewById<TextView>(R.id.transportation_description)
        var dialog_bus = transportdialog.findViewById<ImageButton>(R.id.bus)
        var dialog_driving = transportdialog.findViewById<ImageButton>(R.id.driving)
        var dialog_subway = transportdialog.findViewById<ImageButton>(R.id.subway)
        var dialog_walking = transportdialog.findViewById<ImageButton>(R.id.walk)

        var description = ""
        var clickorig = ""
        var clickdest = ""
        var unen_clickdest = ""
        var time = ""
        var startdate = Schedules_edit.startDate
        var url = ""
        var modechinese = ""

        if(holder.adapterPosition!=ScheduleList.size-1){
            holder.EDstoptime.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener{ timepicker, hour, min ->
                    cal.set(Calendar.HOUR,hour)
                    cal.set(Calendar.MINUTE,min)
                    timepicker.setIs24HourView(true)
                    clockselected = true
                    holder.EDstoptime.setImageResource(R.drawable.ic_icon_clock_selected)
//                    holder.EDstoptime.text ="預計停留 "+ SimpleDateFormat("HH 小時 mm 分鐘").format(cal.time)
                    stophour = hour
                    stopmin = min
                    PreStopTime[holder.adapterPosition]=(hour*60+min)
                }
                while(count==0){
                    if(Schedules_edit.StopTimeArray[position]>=60){
                        stopttransfer=stoptimeTransfer(Schedules_edit.StopTimeArray[position])
                        val temp = stopttransfer.split("小時","分鐘")
                        stophour = temp[0].toInt()
                        if(temp[1]!=""){
                            stopmin = temp[1].toInt()
                        }else{
                            stopmin = 0
                        }
                    }else{
                        stopmin = Schedules_edit.StopTimeArray[position]
                    }
                    count++
                }
                TimePickerDialog(this.Schedules_edit,timeSetListener,stophour,stopmin,true).show()
            }//1031


            holder.mTranSpot.setOnClickListener {
                doAsync {
                    clickorig = ScheduleList[holder.adapterPosition].getspotAddress()
                    clickorig = URLEncoder.encode(clickorig)
                    clickdest = ScheduleList[(holder.adapterPosition)+1].getspotAddress()
                    unen_clickdest = ScheduleList[(holder.adapterPosition)+1].getspotname()
                    clickdest = URLEncoder.encode(clickdest)

                    time =  Schedules_edit.timearray[holder.adapterPosition]
                    time = URLEncoder.encode(time)

//                    Schedules_edit.mode_edit[holder.adapterPosition] = Schedules_edit.transModeArray[holder.adapterPosition]
                    if(Schedules_edit.mode_edit[holder.adapterPosition]=="bus"||Schedules_edit.mode_edit[holder.adapterPosition]=="subway"||Schedules_edit.mode_edit[holder.adapterPosition]=="train"){
                        if(Schedules_edit.mode_edit[holder.adapterPosition]=="bus"){
                            modechinese = "公車"
                            dialog_bus.setImageResource(R.drawable.ic_icon_transport_bus_click)
                        }else if (Schedules_edit.mode_edit[holder.adapterPosition]=="subway"){
                            modechinese = "捷運"
                            dialog_subway.setImageResource(R.drawable.ic_icon_transport_subway_click)
                        }else {
                            dialog_subway.setImageResource(R.drawable.ic_icon_transport_subway_click)
                            modechinese = "火車"
                        }
                        url = "http://120.126.18.144:3000/TransportationInform?departureTime="+time+"%3A00&departureDay="+startdate+"&Tmode="+"bus"+"&dest="+clickdest+"&orig="+clickorig
//                        Log.d("editurlbus",url)
                        description = sendGet(url,"showdialog")
                        uiThread{
                            if(description==""){
                                trans_desctiption.text = "由於搭乘"+modechinese+"並無相關路徑，請改變其他交通方式"
                            }else{
                                trans_desctiption.text = description
                            }
                        }
                    }
                    else{
                        if(Schedules_edit.mode_edit[holder.adapterPosition]=="walking"){
                            dialog_walking.setImageResource(R.drawable.ic_icon_transport_walk_click)
                        }else{
                            dialog_driving.setImageResource(R.drawable.ic_icon_transport_car_click)
                        }
                        url = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+Schedules_edit.mode_edit[holder.adapterPosition]+"&desti="+clickdest+"&orig="+clickorig
//                        Log.d("editurldrive",url)
                        description = sendDrivingGet(url)
                        uiThread{
                            trans_desctiption.text ="到"+unen_clickdest+ " 約花 "+description
                        }
                    }
                } // end of doasync
                transportdialog.show()

                transportdialog.findViewById<ImageButton>(R.id.bus).setOnClickListener{
                    doAsync {
                        Schedules_edit.mode_edit[holder.adapterPosition] = "bus"
                        modechinese = "公車"
                        url = "http://120.126.18.144:3000/TransportationInform?departureTime="+time+"%3A00&departureDay="+startdate+"&Tmode="+"bus"+"&dest="+clickdest+"&orig="+clickorig
                        description = sendGet(url,"showdialog")
//                        mode[holder.adapterPosition]= "bus"
                        uiThread {
                            dialog_bus.setImageResource(R.drawable.ic_icon_transport_bus_click)
                            dialog_driving.setImageResource(R.drawable.ic_icon_transport_car)
                            dialog_subway.setImageResource(R.drawable.ic_icon_transport_subway)
                            dialog_walking.setImageResource(R.drawable.ic_icon_transport_walk)
                            if(description==""){
                                trans_desctiption.text = "由於搭乘"+modechinese+"並無相關路徑，請改變其他交通方式"
                            }else{
                                trans_desctiption.text = description
                            }
                        }
                    }
                    Toast.makeText(this.Schedules_edit, "交通工具換成公車", Toast.LENGTH_LONG).show()

                } // end of transportdialog BUS button

                transportdialog.findViewById<ImageButton>(R.id.subway).setOnClickListener{
                    doAsync {
                        Schedules_edit.mode_edit[holder.adapterPosition] = "subway"
                        url = "http://120.126.18.144:3000/TransportationInform?departureTime="+time+"%3A00&departureDay="+startdate+"&Tmode="+"subway"+"&dest="+clickdest+"&orig="+clickorig
//                        Log.d("url",url)
                        modechinese = "捷運"
                        description = sendGet(url,"showdialog")
//                        mode[holder.adapterPosition]= "subway"
                        uiThread {
                            dialog_bus.setImageResource(R.drawable.ic_icon_transport_bus)
                            dialog_driving.setImageResource(R.drawable.ic_icon_transport_car)
                            dialog_subway.setImageResource(R.drawable.ic_icon_transport_subway_click)
                            dialog_walking.setImageResource(R.drawable.ic_icon_transport_walk)
                            if(description==""){
                                trans_desctiption.text = "由於搭乘"+modechinese+"並無相關路徑，請改變其他交通方式"
                            }else{
                                trans_desctiption.text = description
                            }
                        }
                    }
                    Toast.makeText(this.Schedules_edit, "交通工具換成捷運或火車", Toast.LENGTH_LONG).show()

                } // end of transportdialog SUBWAY button

                transportdialog.findViewById<ImageButton>(R.id.driving).setOnClickListener{
                    doAsync {
                        Schedules_edit.mode_edit[holder.adapterPosition] = "driving"
                        url = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+"driving"+"&desti="+clickdest+"&orig="+clickorig
//                        Log.d("durl",url)
                        description = sendDrivingGet(url)
//                        mode[holder.adapterPosition]= "driving"
                        uiThread {
                            dialog_bus.setImageResource(R.drawable.ic_icon_transport_bus)
                            dialog_driving.setImageResource(R.drawable.ic_icon_transport_car_click)
                            dialog_subway.setImageResource(R.drawable.ic_icon_transport_subway)
                            dialog_walking.setImageResource(R.drawable.ic_icon_transport_walk)
                            trans_desctiption.text ="到"+unen_clickdest+ " 約花 "+description
                        }
                    }
                    Toast.makeText(this.Schedules_edit, "交通工具換成汽車", Toast.LENGTH_LONG).show()

                } // end of transportdialog DRIVING button

                transportdialog.findViewById<ImageButton>(R.id.walk).setOnClickListener{
                    doAsync {
                        Schedules_edit.mode_edit[holder.adapterPosition] = "walking"
                        url = "http://120.126.18.144:7000/DriveWalkTransportation?mode="+"walking"+"&desti="+clickdest+"&orig="+clickorig
//                        Log.d("wurl",url)
                        description = sendDrivingGet(url)
//                        mode[holder.adapterPosition]= "driving"
                        uiThread {
                            dialog_bus.setImageResource(R.drawable.ic_icon_transport_bus)
                            dialog_driving.setImageResource(R.drawable.ic_icon_transport_car)
                            dialog_subway.setImageResource(R.drawable.ic_icon_transport_subway)
                            dialog_walking.setImageResource(R.drawable.ic_icon_transport_walk_click)
                            trans_desctiption.text ="到"+unen_clickdest+ " 約花 "+description
                        }
                    }
                    Toast.makeText(this.Schedules_edit, "交通方式換成走路", Toast.LENGTH_LONG).show()

                } // end of transportdialog DRIVING button

            } // end of holder.mTranSpot.setOnClickListener
        }
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
            return (jsonObject.toString())
        }finally {
            connection2.disconnect()
        }
    }

    fun sendGet( url :String, functions : String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        var ans = ""
        try {
            val data = connection.inputStream.bufferedReader().readText()
            var jsonArray2 = JSONArray(data)
            if(functions == "showdialog"){
                var route =""
                var transitTime = ""
                val jsonArray = JSONArray(jsonArray2.getJSONArray(0).toString())
                for(i in 0..(jsonArray.length()-1)){
                    val jsonObject = JSONObject(jsonArray.get(i).toString())
                    route =jsonObject.get("TransitRoutes").toString()
                    transitTime = jsonObject.get("TransitTime").toString()
                    ans += route + "大約花 " + transitTime + '\n'
//                    Log.d("routra",route+","+transitTime)
                }
            }
        }catch (e: Exception){
            ans = ""
        }finally {
            return(ans)
            connection.disconnect()
        }
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