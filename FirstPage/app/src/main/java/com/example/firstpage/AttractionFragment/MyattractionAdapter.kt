package com.example.firstpage.AttractionFragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.protobuf.Any
import com.squareup.picasso.Picasso

import java.util.ArrayList
import java.util.HashMap

import android.media.CamcorderProfile.get
import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.auth.AuthUI.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MyattractionAdapter(var myAttractions: MyAttractions, var myspotArrayList: ArrayList<myAttraction>?) :
    RecyclerView.Adapter<myAttractionViewHolder>() {


    //    public String xyz=null;
    //    String TAG="Myattraction";
    //    public  ArrayList allspot=new ArrayList();
    var longitude: Double? = 0.0
    var latitude: Double? = 0.0


    private var mydialog: Dialog? = null
    private var imgdialog: Dialog? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myAttractionViewHolder {
        val layoutInflater = LayoutInflater.from(myAttractions.baseContext)
        val view = layoutInflater.inflate(R.layout.my_attraction_list_item, parent, false)
        return myAttractionViewHolder(view)


    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: myAttractionViewHolder, position: Int) {
        //
        if (itemCount <= 0 && position >= itemCount) {
            return
        }

        holder.myspotname.text = myspotArrayList!![position].getmyspotname()
        Picasso.get().load(myspotArrayList!![position].getmyspotpic()).into(holder.myspotpic)
        holder.mycollection.setImageResource(R.drawable.icon_heart_click)
        holder.mycollection.setOnClickListener {
            deleteSpotToCollection(holder, holder.adapterPosition)//這邊註解ㄌ
        }

        val db = FirebaseFirestore.getInstance()

        mydialog = Dialog(this.myAttractions)
        mydialog!!.setContentView(R.layout.dialog_test)
        mydialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))
        mydialog!!.window!!.attributes.gravity = Gravity.BOTTOM

        holder.myspotname.setOnClickListener {

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

            longitude = myspotArrayList!![holder.adapterPosition].getSpotlong().toDouble()
            latitude = myspotArrayList!![holder.adapterPosition].getSpotlat().toDouble()

//            Log.d("longitude",longitude.toString())
//            Log.d("latitude",latitude.toString())


            val mMapView = mydialog!!.findViewById(R.id.mapView) as MapView

            //MapsInitializer.initialize(getActivity());

            mMapView.onCreate(mydialog!!.onSaveInstanceState())
            mMapView.onResume()

            mMapView.getMapAsync { googleMap ->

                val position = LatLng(latitude!!,longitude!!)
//                Log.d("position",position.toString())
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(position))
                googleMap.uiSettings.isZoomControlsEnabled = true
                googleMap.addMarker(MarkerOptions().position(position).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.spot_only
                    )))
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
            }


            var spotID = myspotArrayList!![holder.adapterPosition].getMySpotid()

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

            if(myspotArrayList!![holder.adapterPosition].getAdd()=="NA"){
                address.text ="[目前網路上沒資料歐~]"
                address.setTextColor(Color.LTGRAY)
            }else{
                address.text =  myspotArrayList!![holder.adapterPosition].getAdd()
                address.setTextColor(Color.GRAY)
            }
            var tel_number = myspotArrayList!![holder.adapterPosition].getTel()
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

            AttName.text =  myspotArrayList!![holder.adapterPosition].getmyspotname()
                mydialog!!.show()

        }

        imgdialog = Dialog(this.myAttractions)
        imgdialog!!.setContentView(R.layout.img_big)
        imgdialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.background_dark))

        holder.myspotpic.setOnClickListener {
            val imgShowBig = imgdialog!!.findViewById<ImageView>(R.id.imgShowBig)
            var imgUrl=""
            var spotID = myspotArrayList!![holder.adapterPosition].getMySpotid()

            db.collection("Attraction").document(spotID).get().addOnSuccessListener {
                imgUrl= it.getString("PhotoUrl").toString()

                Picasso.get().load(imgUrl).into(imgShowBig)
            }

            if (!myAttractions.isFinishing) {
                imgdialog!!.show()
            }

        }//myspotpic ClickListener

    }

    private fun deleteSpotToCollection(holder: myAttractionViewHolder, position: Int) {
        myspotArrayList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
        val addSpot = myAttractions.db.collection("UserInfo")
            .document(myAttractions.Auth.uid!!)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //  ArrayList spotcheck = (ArrayList) task.getResult().getData().get("Collection");
                addSpot.update("Collection", FieldValue.arrayRemove(myspotArrayList!![position].mySpotid))

            }
        }

    }

    override fun getItemCount(): Int {
        return if (myspotArrayList == null) {
            0
        } else myspotArrayList!!.size
        // setMySpotArray();

    }

    fun filterList(filteredList: ArrayList<myAttraction>) {
        myspotArrayList = filteredList
        notifyDataSetChanged()
    }
}
