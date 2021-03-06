package com.example.firstpage.AttractionFragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.FirstPageActivity
import com.example.firstpage.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.ArrayList


class FirstPageRecyclerviewAdapter(var firstpagehotspot: FirstPageActivity , var spotArrayList: ArrayList<Attraction>)
        : RecyclerView.Adapter<MyRecyclerViewHolder>(){

    private var mydialog: Dialog? = null

    var longitude =0.0
    var latitude = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder {
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        val layoutInflater = LayoutInflater.from(firstpagehotspot.baseContext)
        val view = layoutInflater.inflate(R.layout.list_item_firstpage, parent, false)
        return MyRecyclerViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyRecyclerViewHolder, position: Int) {
        holder.mspotname.text = spotArrayList[position].getspotname()
        Picasso.get().load(spotArrayList[position].getspotpic()).into(holder.mspotpic)
//        changeCollectionPic(holder, position)
//        holder.mcollection.setOnClickListener { addSpotToCollection(holder, position) }

        val db = FirebaseFirestore.getInstance()

        mydialog = Dialog(this.firstpagehotspot)
        mydialog!!.setContentView(R.layout.dialog_text_firstpage)
        mydialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
//        mydialog!!.window!!.attributes.gravity = Gravity.BOTTOM

        holder.mspotpic.setOnClickListener {

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


            val mMapView = mydialog!!.findViewById(R.id.mapView) as MapView

            //MapsInitializer.initialize(getActivity());

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
                    txtMonTime.visibility= View.INVISIBLE
                    txtTueTime.visibility= View.INVISIBLE
                    txtWedTime.visibility= View.INVISIBLE
                    txtThuTime.visibility= View.INVISIBLE
                    txtFriTime.visibility= View.INVISIBLE
                    txtSatTime.visibility= View.INVISIBLE

                }else{
                    if(StatusSun=="TRUE"){
                        txtSunTime.setTextColor(Color.GRAY)
                        txtSunTime.setBackgroundResource(R.drawable.att_info_bhour_sun)
                        txtSunTime.visibility= View.VISIBLE
                        txtSunTime.text=SunTimeS+" - "+SunTimeC
                    }else if(StatusSun=="FALSE"){
                        txtSunTime.setTextColor(Color.GRAY)
                        txtSunTime.setBackgroundResource(R.drawable.att_info_bhour_sun)
                        txtSunTime.visibility= View.VISIBLE
                        txtSunTime.text="未開店"
                    }

                    if(StatusMon=="TRUE"){
                        txtMonTime.visibility= View.VISIBLE
                        txtMonTime.text=MonTimeS+" - "+MonTimeC
                    }else if(StatusMon=="FALSE"){
                        txtMonTime.visibility= View.VISIBLE
                        txtMonTime.text="未開店"
                    }

                    if(StatusTue=="TRUE"){
                        txtTueTime.visibility= View.VISIBLE
                        txtTueTime.text=TueTimeS+" - "+TueTimeC
                    }else if(StatusTue=="FALSE"){
                        txtTueTime.visibility= View.VISIBLE
                        txtTueTime.text="未開店"
                    }

                    if(StatusWed=="TRUE"){
                        txtWedTime.visibility= View.VISIBLE
                        txtWedTime.text=WedTimeS+" - "+WedTimeC
                    }else if(StatusWed=="FALSE"){
                        txtWedTime.visibility= View.VISIBLE
                        txtWedTime.text="未開店"
                    }

                    if(StatusThu=="TRUE"){
                        txtThuTime.visibility= View.VISIBLE
                        txtThuTime.text=ThuTimeS+" - "+ThuTimeC
                    }else if(StatusThu=="FALSE"){
                        txtThuTime.visibility= View.VISIBLE
                        txtThuTime.text="未開店"
                    }

                    if(StatusFri=="TRUE"){
                        txtFriTime.visibility= View.VISIBLE
                        txtFriTime.text=FriTimeS+" - "+FriTimeC
                    }else if(StatusFri=="FALSE"){
                        txtFriTime.visibility= View.VISIBLE
                        txtFriTime.text="未開店"
                    }

                    if(StatusSat=="TRUE"){
                        txtSatTime.visibility= View.VISIBLE
                        txtSatTime.text=SatTimeS+" - "+SatTimeC
                    }else if(StatusSat=="FALSE"){
                        txtSatTime.visibility= View.VISIBLE
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

            var tel_number = spotArrayList[holder.adapterPosition].getTel()
            if(tel_number=="NA"){
                tel.text ="[目前網路上沒資料歐~]"
                tel.setTextColor(Color.LTGRAY)
            }else{
                tel.text =  tel_number
                tel.setTextColor(Color.GRAY)
                tel.setTypeface(Typeface.DEFAULT, Typeface.ITALIC)
                tel.paintFlags = tel.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                //set phone number can call
                tel.setOnClickListener {

                    val callIntent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+tel_number))

                    holder.itemView.context.startActivity(callIntent)

                }
            }

            AttName.text =  spotArrayList[holder.adapterPosition].getspotname()

            if (!firstpagehotspot.isFinishing) {
                mydialog!!.show()
            }
        }
    }

    private fun changeCollectionPic(holder: MyRecyclerViewHolder, position: Int) {

        val addSpot = firstpagehotspot.HotFirstDB.collection("UserInfo")
            .document(firstpagehotspot.Auth.uid!!)
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
        val addSpot = firstpagehotspot.HotFirstDB.collection("UserInfo")
            .document(firstpagehotspot.Auth.uid!!)

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
}