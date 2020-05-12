package com.example.firstpage.TransportFragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.MyRecyclerViewHolder
import com.example.firstpage.AttractionFragment.myAttraction
import com.example.firstpage.R
import com.example.firstpage.collect_travel
import com.example.firstpage.public_travel
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.travel_activity_list_item.*
import java.util.ArrayList

class collect_travel_adapter(var collecttravel: collect_travel, var travelArraylist: ArrayList<myAttraction>)
    : RecyclerView.Adapter<MyRecyclerViewHolder_Transportation>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewHolder_Transportation {
        val layoutInflater = LayoutInflater.from(collecttravel.baseContext)
        val view = layoutInflater.inflate(R.layout.public_travel_list_item, parent, false)
        return MyRecyclerViewHolder_Transportation(view)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        holder.SchelikeImage.isClickable = false
        holder.SchelikeImage.visibility = INVISIBLE

        holder.schename.text = travelArraylist!![position].getmyspotname()

        holder.mcollect.setBackgroundResource(R.drawable.icon_heart_click)
//        changeCollectionPic(holder, position) //1206
//        changeLikePic(holder, position)
        holder.itemView.setOnClickListener {
            var intent= Intent(collecttravel,Schedules::class.java)
            intent.putExtra("publictravel","donotoedit")
            intent.putExtra("traveltempid", travelArraylist?.get(position)!!.mySpotid.toString())
            collecttravel.startActivity(intent)
        }
        if (itemCount <= 0 && position >= itemCount) {
            return
        }
        holder.mcollect.setOnClickListener {  deleteSpotToCollection(holder, holder.adapterPosition)//這邊註解ㄌ
        }

//        holder.SchelikeImage.setOnClickListener { addSpotToLike(holder, position) } // 1206
//        holder.likecount.setText(travelArraylist[position].getlikeCount().toString())
//        invisibleCollect(holder,position)

    }

    private fun deleteSpotToCollection(holder: MyRecyclerViewHolder_Transportation, adapterPosition: Int) {
        travelArraylist!!.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
        notifyDataSetChanged()
        val addSpot = collecttravel.db.collection("Schedule")
            .document(getItemId(adapterPosition).toString())
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //  ArrayList spotcheck = (ArrayList) task.getResult().getData().get("Collection");
                addSpot.update("ScheduleCollect", FieldValue.arrayRemove(collecttravel.Auth.uid.toString()))

            }
        }

    }

    private fun invisibleCollect(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        var addspot= collecttravel.db.collection("Schedule")
            .document(travelArraylist[position].mySpotid)
        addspot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val schcollect = task.result!!.data!!["ScheduleCollect"] as ArrayList<*>?
                val collectsize = schcollect!!.size
                for (i in 0 until collectsize) {
                    if (schcollect[i].toString() == collecttravel.Auth.uid.toString()) {
                        holder.mcollect.isClickable = false
                        holder.mcollect.visibility = INVISIBLE
                    }
                }
            }
        }



    }

    private fun addSpotToCollection(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        val addSpot = collecttravel.db.collection("Schedule")
            .document(travelArraylist[position].mySpotid)

        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val travelcheck = task.result!!.data!!["ScheduleCollect"] as ArrayList<*>?
                val travelsize = travelcheck!!.size
                var state = false
//                val likecountdb = task.result!!.data!!["LikeCount"] as String
//                val like = likecountdb.toInt()
                for (i in 0 until travelsize) {
                    if (collecttravel.Auth.uid == travelcheck[i].toString()) {
                        addSpot.update("ScheduleCollect", FieldValue.arrayRemove(collecttravel.Auth.uid))
//                        addSpot.update("LikeCount", (like-1).toString())
//                        addSpot.update("LikeCount",)
                        holder.mcollect.setImageResource(R.drawable.icon_heart_unclick)
//                        holder.likecount.text = (like-1).toString()
                        state = true
                        break
                    }
                }
                if (!state) {
                    addSpot.update("ScheduleCollect", FieldValue.arrayUnion(collecttravel.Auth.uid))
//                    addSpot.update("LikeCount", (like+1).toString())
//                    holder.likecount.text = (like+1).toString()
                    holder.mcollect.setImageResource(R.drawable.icon_heart_click)
                }
            }
        }



    }

    private fun changeCollectionPic(holder: MyRecyclerViewHolder_Transportation, position: Int) {
        val addSpot = collecttravel.db.collection("Schedule")
            .document(travelArraylist[position].mySpotid)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var tempcollection=task.result?.get("ScheduleCollect") as ArrayList<String>
                if(tempcollection.size>=1){
                    val travelcheck = task.result!!.data!!["ScheduleCollect"] as ArrayList<*>?
                    val travelsize = travelcheck!!.size
                    var state = false
                    for (i in 0 ..travelsize-1) {
                        if (collecttravel.Auth.uid == travelcheck[i].toString()) {
                            holder.mcollect.setImageResource(R.drawable.icon_heart_click)
                            state = true
                            break
                        }
                    }
                    if (!state) {
                        holder.mcollect.setImageResource(R.drawable.icon_heart_unclick)
                    }
                }
            }
        }


    }

    private fun changeLikePic(holder: MyRecyclerViewHolder_Transportation, position: Int) {

        val addSpot = collecttravel.db.collection("Schedule")
            .document(travelArraylist[position].mySpotid)
        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var tempcollection=task.result?.get("Schedulelike") as ArrayList<String>
                if(tempcollection.size>=1){
                    val travelcheck = task.result!!.data!!["Schedulelike"] as ArrayList<*>?
                    val travelsize = travelcheck!!.size
                    var state = false
                    for (i in 0 ..travelsize-1) {
                        if (collecttravel.Auth.uid == travelcheck[i].toString()) {
                            holder.SchelikeImage.setImageResource(R.drawable.hot_range_click)
                            state = true
                            break
                        }
                    }
                    if (!state) {
                        holder.SchelikeImage.setImageResource(R.drawable.hot_range_unclick)
                    }
                }
            }
        }
    }

    private fun addSpotToLike(holder: MyRecyclerViewHolder_Transportation, position: Int) {

        val addSpot = collecttravel.db.collection("Schedule")
            .document(travelArraylist[position].mySpotid)

        addSpot.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val travelcheck = task.result!!.data!!["Schedulelike"] as ArrayList<*>?
                val travelsize = travelcheck!!.size
                var state = false
                val likecountdb = task.result!!.data!!["LikeCount"] as String
                val like = likecountdb.toInt()
                for (i in 0 until travelsize) {
                    if (collecttravel.Auth.uid == travelcheck[i].toString()) {
                        addSpot.update("Schedulelike", FieldValue.arrayRemove(collecttravel.Auth.uid))
                        addSpot.update("LikeCount", (like-1).toString())
//                        addSpot.update("LikeCount",)
                        holder.SchelikeImage.setImageResource(R.drawable.hot_range_unclick)
                        holder.likecount.text = (like-1).toString()
                        state = true
                        break
                    }
                }
                if (!state) {
                    addSpot.update("Schedulelike", FieldValue.arrayUnion(collecttravel.Auth.uid))
                    addSpot.update("LikeCount", (like+1).toString())
                    holder.likecount.text = (like+1).toString()
                    holder.SchelikeImage.setImageResource(R.drawable.hot_range_click)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (travelArraylist == null) {
            0
        } else travelArraylist!!.size
    }

    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.INVISIBLE
//            View.VISIBLE
        }
    }

}