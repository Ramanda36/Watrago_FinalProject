package com.example.firstpage.AttractionFragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.R
import com.google.android.gms.maps.*
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import androidx.core.content.ContextCompat.startActivity

import java.util.ArrayList

import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.maps.model.*

class MyRecyclerViewAdapter
//    public View.OnClickListener itemClickListener;


    (//  Context mcontext;
    var hotAttractions: HotAttractions, var spotArrayList: ArrayList<Attraction>

)//        this.itemClickListener = itemClickListener;
    : RecyclerView.Adapter<MyRecyclerViewHolder>(){//, OnMapReadyCallback


    var longitude =0.0
    var latitude = 0.0
    private var tel_number = ""

    private var mydialog: Dialog? = null
    private var imgdialog: Dialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(hotAttractions.baseContext)
        val view = layoutInflater.inflate(R.layout.list_item, parent, false)
        return MyRecyclerViewHolder(view)
    }

    @SuppressLint("ResourceAsColor", "WrongViewCast")
    override fun onBindViewHolder(holder: MyRecyclerViewHolder, position: Int) {
//        val mapFragment = hotAttractions.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment!!.getMapAsync(this.hotAttractions)

        holder.mspotname.text = spotArrayList[position].getspotname()
        Picasso.get().load(spotArrayList[position].getspotpic()).into(holder.mspotpic)

        val db = FirebaseFirestore.getInstance()


        changeCollectionPic(holder, position)

        mydialog = Dialog(this.hotAttractions)
        mydialog!!.setContentView(R.layout.dialog_test)
        mydialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        mydialog!!.window!!.attributes.gravity = Gravity.BOTTOM

        holder.mspotname.setOnClickListener {

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

            longitude =spotArrayList[holder.adapterPosition].getSpotlong().toDouble()
            latitude = spotArrayList[holder.adapterPosition].getSpotlat().toDouble()


            //import google maps in app
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


            var spotID = spotArrayList[holder.adapterPosition].getSpotid()

            db.collection("Attraction").document(spotID).get().addOnSuccessListener {

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

            if(spotArrayList[holder.adapterPosition].getspotAddress()=="NA"){
                address.text ="[目前網路上沒資料歐~]"
                address.setTextColor(Color.LTGRAY)
            }else{
                address.text =  spotArrayList[holder.adapterPosition].getspotAddress()
                address.setTextColor(Color.GRAY)
            }

            tel_number = spotArrayList[holder.adapterPosition].getTel()
            if(tel_number=="NA"){
                tel.text ="[目前網路上沒資料歐~]"
                tel.setTextColor(Color.LTGRAY)
            }else{
                tel.text =  tel_number
                tel.setTextColor(Color.GRAY)
                tel.setTypeface(Typeface.DEFAULT,Typeface.ITALIC)
                tel.paintFlags = tel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                //set phone number can call
                tel.setOnClickListener {

                    val callIntent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+tel_number))

                    holder.itemView.context.startActivity(callIntent)
                }
            }

            AttName.text =  spotArrayList[holder.adapterPosition].getspotname()

            if (!hotAttractions.isFinishing) {
                mydialog!!.show()
            }
        }//mspotname ClickListener

        imgdialog = Dialog(this.hotAttractions)
        imgdialog!!.setContentView(R.layout.img_big)
        imgdialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))

        holder.mspotpic.setOnClickListener {
            val imgShowBig = imgdialog!!.findViewById<ImageView>(R.id.imgShowBig)
            var imgUrl=""
            var spotID = spotArrayList[holder.adapterPosition].getSpotid()

            db.collection("Attraction").document(spotID).get().addOnSuccessListener {
                imgUrl= it.getString("PhotoUrl").toString()

                Picasso.get().load(imgUrl).into(imgShowBig)
            }

            if (!hotAttractions.isFinishing) {
                imgdialog!!.show()
            }

        }//mspotpic ClickListener



        holder.mcollection.setOnClickListener { addSpotToCollection(holder, position) }
    } //onBindViewHolder



    private fun changeCollectionPic(holder: MyRecyclerViewHolder, position: Int) {
        val addSpot = hotAttractions.db.collection("UserInfo")
            .document(hotAttractions.Auth.uid!!)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                var tempcollection=task.result?.get("Collection") as ArrayList<String>
                if(tempcollection.size>=1){

                    val spotcheck = task.result!!.data!!["Collection"] as ArrayList<*>?
                    val spotsize = spotcheck!!.size
                    var state = false
                    for (i in 0 ..spotsize-1) {
                        if (spotArrayList[position].spotid == spotcheck[i].toString()) {
                            holder.mcollection.setImageResource(R.drawable.icon_heart_click)
                            state = true
                            break
                        }
                    }
                    if (!state) {
                        holder.mcollection.setImageResource(R.drawable.icon_heart_unclick)
                    }
                }
            }
        }
    }

    private fun addSpotToCollection(holder: MyRecyclerViewHolder, position: Int) {

        val addSpot = hotAttractions.db.collection("UserInfo")
            .document(hotAttractions.Auth.uid!!)

        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val spotcheck = task.result!!.data!!["Collection"] as ArrayList<*>?
                val spotsize = spotcheck!!.size
                var state = false
                for (i in 0 until spotsize) {
                    if (spotArrayList[position].spotid == spotcheck[i].toString()) {
                        addSpot.update("Collection", FieldValue.arrayRemove(spotArrayList[position].getSpotid()))
                        holder.mcollection.setImageResource(R.drawable.icon_heart_unclick)
                        state = true
                        break
                    }
                }
                if (!state) {
                    addSpot.update("Collection", FieldValue.arrayUnion(spotArrayList[position].getSpotid()))
                    holder.mcollection.setImageResource(R.drawable.icon_heart_click)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return spotArrayList.size

    }

    fun filterList(filteredList: ArrayList<Attraction>) {
        spotArrayList = filteredList
        notifyDataSetChanged()

    }


}

