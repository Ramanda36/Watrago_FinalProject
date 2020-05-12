package com.example.firstpage.AttractionFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.R
import com.example.firstpage.TransportFragment.MyRecyclerViewHolder_Transportation
import com.example.firstpage.TransportFragment.My_Transport_Attraction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_transport__attraction.*

import java.util.ArrayList


//import android.R



class My_Transport_Attraction_adpter(
    var my_Transport_Attraction: My_Transport_Attraction,
    var spotArrayList: ArrayList<Attraction>

) : RecyclerView.Adapter<MyRecyclerViewHolder_Transportation>() {

    var selectedspot = ArrayList<String>()
    var checkspot = ArrayList<String>()
    var tempname=ArrayList<String>()
    var tempid=ArrayList<String>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder_Transportation {
//        selectedspot = ArrayList()//1125註解ㄉ
//        checkspot = ArrayList()//1125註解ㄉ
//        tempname = ArrayList()
//        tempid= ArrayList()

        val layoutInflater = LayoutInflater.from(my_Transport_Attraction.baseContext)
        val view = layoutInflater.inflate(com.example.firstpage.R.layout.my_transport_attraction_listitem, parent, false)

        return MyRecyclerViewHolder_Transportation(view)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        if (itemCount <= 0 && position >= itemCount) {
            return
        }
//        Log.d("transspotid",spotArrayList[position].getSpotid())
//        Log.d("transspotname",spotArrayList[position].getspotname())
        holder.myTranSpotname.text = spotArrayList[position].getspotname()
        Picasso.get().load(spotArrayList[position].getspotpic()).into(holder.mytranspicture)
        holder.mytranscollection.setImageResource(R.drawable.icon_heart_click)
        holder.mytranscollection.setOnClickListener {
            deleteSpotToCollection(holder, holder.adapterPosition)
        }

//        Log.d("transtrans",transport_attraction.getid()+" z``2e")
        if (my_Transport_Attraction.tempid != "") {
//            var tempname:ArrayList<String> = ArrayList()
            changeblockPic(holder, position)
//            transport_attraction.db.collection("Schedule").document(transport_attraction.tempid)
//            .get().addOnSuccessListener {
//                select=it.get("ScheduleList") as ArrayList<String>
//                    for(i in 0 until spotArrayList.size-1){
//                        if(select.contains(spotArrayList[i].spotid)){
////                            tempid.add(spotArrayList[i].spotid)
//                            tempname.add(spotArrayList[i].spotname)
//                          holder.mblock.setBackgroundResource(com.example.firstpage.R.color.block)
//                        }
//                        else{
//                            holder.mblock.setBackgroundResource(com.example.firstpage.R.color.clear)
//                        }
//                    }
//            }
//            transport_attraction.alreadyseleted.text="已選擇:"+tempname.toString().replace("[", "").replace("]", "")
//
//            checkspot.clear()
//            checkspot=select
//            checkspotname=tempname
        }

        holder.mytranspicture.setOnClickListener {
            //            var checkspot : ArrayList<String>
//            checkspot.clear()
//            if(tempid!=""){
//                Log.d("tte",tempid[0])
//                checkspot=tempid
//            }
//            checkspot = ArrayList()
//            if(transport_attraction.tempid != ""){
//                Log.d("tempidid",checkspot[1])
//            }

            if (checkspot.contains(spotArrayList[position].spotid)) {
                holder.mytransblock.setBackgroundResource(com.example.firstpage.R.color.clear)
                checkspot.remove(spotArrayList[position].spotid)
                selectedspot.remove(spotArrayList[position].getspotname())
                my_Transport_Attraction.showSelectedSpot(selectedspot, checkspot)
            } else if (!checkspot.contains(spotArrayList[position].spotid)) {
                holder.mytransblock.setBackgroundResource(com.example.firstpage.R.color.block)
                checkspot.add(spotArrayList[position].spotid)
                selectedspot.add(spotArrayList[position].getspotname())
                my_Transport_Attraction.showSelectedSpot(selectedspot, checkspot)
            }
        }
    }

    private fun deleteSpotToCollection(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        spotArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
        val addSpot = my_Transport_Attraction.db.collection("UserInfo")
            .document(my_Transport_Attraction.Auth.getUid()!!)
        addSpot.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
            if (task.isSuccessful) {
                //  ArrayList spotcheck = (ArrayList) task.getResult().getData().get("Collection");
                addSpot.update("Collection", FieldValue.arrayRemove(spotArrayList.get(position).getSpotid()))

            }
        })
    }

    private fun changeblockPic(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        val addSpot = my_Transport_Attraction.db.collection("Schedule")
            .document(my_Transport_Attraction.tempid)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                        tempname = ArrayList()
                val spotcheck = task.result!!.data!!["ScheduleList"] as ArrayList<*>?
                val spotsize = spotcheck!!.size
                var state = false
                for (i in 0 ..checkspot.size-1) {
                    if (spotArrayList[position].getSpotid().toString() == my_Transport_Attraction.intent.getStringArrayListExtra("spotid")[i].toString()) {
//                        tempname.add(spotArrayList[position].spotname)
//                        tempid.add(spotArrayList[position].spotid)

                        holder.mytransblock.setBackgroundResource(com.example.firstpage.R.color.block)
                        state = true
                        break
                    }
                }
                if (!state) {
                    holder.mytransblock.setBackgroundResource(com.example.firstpage.R.color.clear)
                }
                if(my_Transport_Attraction.intent.getStringArrayListExtra("spotid")!=null){
                    checkspot=my_Transport_Attraction.intent.getStringArrayListExtra("spotid")
                    selectedspot=my_Transport_Attraction.intent.getStringArrayListExtra("spotname")
                    my_Transport_Attraction.alreadyseleted.text="已選擇:"+selectedspot.toString().replace("[", "").replace("]", "")

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