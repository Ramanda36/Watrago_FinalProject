package com.example.firstpage.AttractionFragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.LoginActivity
import com.example.firstpage.R
import com.example.firstpage.TravelActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso

import java.util.ArrayList
import java.util.Objects

import android.util.Log.d
import com.firebase.ui.auth.AuthUI.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class NewAttractionAdapter
//    public View.OnClickListener itemClickListener;

    (//  Context mcontext;
    var newAttractions: NewAttractions, var NewspotArrayList: ArrayList<Attraction>
)//        this.itemClickListener = itemClickListener;
    : RecyclerView.Adapter<MyRecyclerViewHolder>() {
    private var mydialog: Dialog? = null

    //for google maps
    var longitude =0.0
    var latitude = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(newAttractions.baseContext)
        val view = layoutInflater.inflate(R.layout.new_attraction_list_item, parent, false)


        return MyRecyclerViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyRecyclerViewHolder, position: Int) {
        holder.newspotname.text = NewspotArrayList[position].getspotname()
        Picasso.get().load(NewspotArrayList[position].getspotpic()).into(holder.newspotpic)

        val db = FirebaseFirestore.getInstance()

        changeCollectionPic(holder, position)
        mydialog = Dialog(this.newAttractions)
        mydialog!!.setContentView(R.layout.dialog_test)
        mydialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        mydialog!!.window!!.attributes.gravity =  Gravity.BOTTOM

        holder.newspotpic.setOnClickListener {

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
            longitude =NewspotArrayList[holder.adapterPosition].getSpotlong().toDouble()
            latitude = NewspotArrayList[holder.adapterPosition].getSpotlat().toDouble()

            val mMapView = mydialog!!.findViewById(R.id.mapView) as MapView

            mMapView.onCreate(mydialog!!.onSaveInstanceState())
            mMapView.onResume()

            mMapView.getMapAsync { googleMap ->
                val posisiabsen = LatLng(latitude,longitude) ////your lat lng
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen))
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.addMarker(MarkerOptions().position(posisiabsen))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16f), 2000, null)
            }

            //for attractions information

            var newSpotID = NewspotArrayList[holder.adapterPosition].getSpotid()

            db.collection("Attraction").document(newSpotID).get().addOnSuccessListener {

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

            if(NewspotArrayList[holder.adapterPosition].getspotAddress()=="NA"){
                address.text ="[目前網路上沒資料歐~]"
                address.setTextColor(Color.LTGRAY)
            }else{
                address.text =  NewspotArrayList[holder.adapterPosition].getspotAddress()
                address.setTextColor(Color.GRAY)
            }

            var tel_number = NewspotArrayList!![holder.adapterPosition].getTel()
            if(tel_number=="NA"){
                tel.text ="[目前網路上沒資料歐~]"
                tel.setTextColor(Color.LTGRAY)
            }else{
                tel.text = tel_number
                tel.setTextColor(Color.GRAY)
                tel.setTypeface(Typeface.DEFAULT, Typeface.ITALIC)
                tel.paintFlags = tel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                //set phone number can call
                tel.setOnClickListener {

                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tel_number))

                    holder.itemView.context.startActivity(callIntent)
                }
            }


            AttName.text =  NewspotArrayList[holder.adapterPosition].getspotname()


            if (!newAttractions.isFinishing) {
                mydialog!!.show()
            }
        }

        holder.newcollection.setOnClickListener { addSpotToCollection(holder, position) }
    }


    private fun changeCollectionPic(holder: MyRecyclerViewHolder, position: Int) {

        val addSpot = newAttractions.db.collection("UserInfo")
            .document(newAttractions.Auth.uid!!)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                var tempcollection=task.result?.get("Collection") as ArrayList<String>
                if(tempcollection.size>=1){

                    val spotcheck = task.result!!.data!!["Collection"] as ArrayList<*>?
                    val spotsize = spotcheck!!.size
                    var state = false
                    for (i in 0 until spotsize) {
                        if (NewspotArrayList[position].spotid == spotcheck[i].toString()) {
                            holder.newcollection.setImageResource(R.drawable.icon_heart_click)
                            state = true
                            break
                        }
                    }
                    if (!state) {
                        holder.newcollection.setImageResource(R.drawable.icon_heart_unclick)
                    }
                }
            }
        }

    }

    private fun addSpotToCollection(holder: MyRecyclerViewHolder, position: Int) {

        val addSpot = newAttractions.db.collection("UserInfo")
            .document(newAttractions.Auth.uid!!)

        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val spotcheck = task.result!!.data!!["Collection"] as ArrayList<*>?
                val spotsize = spotcheck!!.size
                var state = false
                for (i in 0 until spotsize) {
                    if (NewspotArrayList[position].spotid == spotcheck[i].toString()) {
                        addSpot.update("Collection", FieldValue.arrayRemove(NewspotArrayList[position].getSpotid()))
                        holder.newcollection.setImageResource(R.drawable.icon_heart_unclick)
                        state = true
                        break
                    }
                }
                if (!state) {
                    addSpot.update("Collection", FieldValue.arrayUnion(NewspotArrayList[position].getSpotid()))
                    holder.newcollection.setImageResource(R.drawable.icon_heart_click)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return NewspotArrayList.size

    }

    fun filterList(filteredList: ArrayList<Attraction>) {
        NewspotArrayList = filteredList
        notifyDataSetChanged()

    }
}

