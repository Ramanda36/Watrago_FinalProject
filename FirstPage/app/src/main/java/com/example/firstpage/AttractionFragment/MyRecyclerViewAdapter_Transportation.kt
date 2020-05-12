package com.example.firstpage.AttractionFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.R
import com.example.firstpage.TransportFragment.MyRecyclerViewHolder_Transportation
import com.example.firstpage.TransportFragment.Transport_Attraction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_transport__attraction.*

import java.util.ArrayList


//import android.R



class MyRecyclerViewAdapter_Transportation(
    var transport_attraction: Transport_Attraction,
    var spotArrayList: ArrayList<Attraction>

) : RecyclerView.Adapter<MyRecyclerViewHolder_Transportation>() {
//    internal lateinit var selectedspot :ArrayList<String>
//    internal lateinit var checkspot : ArrayList<String>

    var selectedspot = ArrayList<String>()
    var checkspot = ArrayList<String>()
    var tempname=ArrayList<String>()
    var tempid=ArrayList<String>()



    internal lateinit var checkspotname : ArrayList<String>

    var select = ArrayList<String>()
//    lateinit var tempname:ArrayList<String>
//    lateinit var tempid:ArrayList<String>
//    var tempid = ArrayList<String>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder_Transportation {
//        selectedspot = ArrayList()
//        checkspot = ArrayList()
//        tempname = ArrayList()
//        tempid= ArrayList()

        val layoutInflater = LayoutInflater.from(transport_attraction.baseContext)
        val view = layoutInflater.inflate(com.example.firstpage.R.layout.transportation_attraction_listitem, parent, false)

        return MyRecyclerViewHolder_Transportation(view)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        if (itemCount <= 0 && position >= itemCount) {
            return
        }
        holder.mTranSpotname.text = spotArrayList[position].getspotname()
        Picasso.get().load(spotArrayList[position].getspotpic()).into(holder.mpicture)
        changeCollectionPic(holder,position)
//        if( transport_attraction.intent.getStringArrayListExtra("spotname")!=null){
//            selectedspot = transport_attraction.intent.getStringArrayListExtra("spotname")
//            checkspot = transport_attraction.intent.getStringArrayListExtra("spotid")

//        }//1126註解




        if (transport_attraction.schid != "" ) {
//            var tempname:ArrayList<String> = ArrayList()
            changeblockPic(holder, position)
        }

         if( transport_attraction.myschid!=""){
            changemyblockPic(holder, position)
        }

        holder.mpicture.setOnClickListener {

            if (checkspot.contains(spotArrayList[position].getSpotid().toString())) {
                holder.mblock.setBackgroundResource(com.example.firstpage.R.color.clear)
                selectedspot.remove(spotArrayList[position].getspotname())
//                Log.d("selectedspot",spotArrayList[position].getSpotid().toString())
                checkspot.remove(spotArrayList[position].getSpotid().toString())
                transport_attraction.showSelectedSpot(selectedspot, checkspot)
            } else if (!checkspot.contains(spotArrayList[position].getSpotid().toString())) {
                holder.mblock.setBackgroundResource(com.example.firstpage.R.color.block)
                selectedspot.add(spotArrayList[position].getspotname())
//                Log.d("selectedspot",spotArrayList[position].getSpotid().toString())
                checkspot.add(spotArrayList[position].getSpotid().toString())
                transport_attraction.showSelectedSpot(selectedspot, checkspot)
            }
        }
        holder.mcollection.setOnClickListener { addSpotToCollection(holder, position) }
    }

    private fun changemyblockPic(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        val addSpot = transport_attraction.db.collection("Schedule").document(transport_attraction.tempid)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                        tempname = ArrayList()

                val spotcheck = task.result!!.data!!["ScheduleList"] as ArrayList<*>?
                val spotsize = spotcheck!!.size
                var state = false
                for (i in 0 .. transport_attraction.intent.getStringArrayListExtra("spotid").size-1) {
                    if (spotArrayList[position].getSpotid().toString() ==transport_attraction.myspotid[i].toString()) {
//                        tempname.add(spotArrayList[position].spotname)
//                        tempid.add(spotArrayList[position].spotid)

                        holder.mblock.setBackgroundResource(com.example.firstpage.R.color.block)
                        state = true
                        break
                    }
                }
                if (!state) {
                    holder.mblock.setBackgroundResource(com.example.firstpage.R.color.clear)
                }
                transport_attraction.alreadyseleted.text="已選擇:"+transport_attraction.myspotname.toString().replace("[", "").replace("]", "")
                checkspot=transport_attraction.myspotid
                selectedspot=transport_attraction.myspotname
            }
        }


    }

    private fun changeCollectionPic(holder: MyRecyclerViewHolder_Transportation, position: Int) {

        val addSpot = transport_attraction.db.collection("UserInfo")
            .document(transport_attraction.Auth.uid!!)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                var tempcollection=task.result?.get("Collection") as ArrayList<String>
                if(tempcollection.size>=1){

                    val spotcheck = task.result!!.data!!["Collection"] as ArrayList<*>?
                    val spotsize = spotcheck!!.size
                    var state = false
                    for (i in 0 until spotsize) {
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
    private fun changeblockPic(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        val addSpot = transport_attraction.db.collection("Schedule").document(transport_attraction.tempid)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                        tempname = ArrayList()


                val spotcheck = task.result!!.data!!["ScheduleList"] as ArrayList<*>?
                val spotsize = spotcheck!!.size
                var state = false
                for (i in 0 ..spotsize-1) {
                    if (spotArrayList[position].getSpotid().toString() == spotcheck[i].toString()) {
//                        tempname.add(spotArrayList[position].spotname)
//                        tempid.add(spotArrayList[position].spotid)

                        holder.mblock.setBackgroundResource(com.example.firstpage.R.color.block)
                        state = true
                        break
                    }
                }
                if (!state) {
                    holder.mblock.setBackgroundResource(com.example.firstpage.R.color.clear)
                }
                transport_attraction.alreadyseleted.text="已選擇:"+transport_attraction.myspotname.toString().replace("[", "").replace("]", "")
                selectedspot=transport_attraction.myspotname
                checkspot=transport_attraction.myspotid
            }
        }
    }


    private fun addSpotToCollection(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        val addSpot = transport_attraction.db.collection("UserInfo")
            .document(transport_attraction.Auth.uid!!)

        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val spotcheck = task.result!!.data!!["Collection"] as ArrayList<*>?
                val spotsize = spotcheck!!.size
                var state = false
                for (i in 0 until spotsize) {
                    if (spotArrayList[position].getSpotid().toString() == spotcheck[i].toString()) {
                        addSpot.update("Collection", FieldValue.arrayRemove(spotArrayList[position].getSpotid()))
                        holder.mcollection.setImageResource(com.example.firstpage.R.drawable.icon_heart_unclick)
                        state = true
                        break
                    }
                }
                if (!state) {
                    addSpot.update("Collection", FieldValue.arrayUnion(spotArrayList[position].getSpotid().toString()))
                    holder.mcollection.setImageResource(com.example.firstpage.R.drawable.icon_heart_click)
                }
            }
        }
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemCount(): Int {
        return spotArrayList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun filterList(filteredList: ArrayList<Attraction>) {
        spotArrayList = filteredList
        notifyDataSetChanged()

    }
}