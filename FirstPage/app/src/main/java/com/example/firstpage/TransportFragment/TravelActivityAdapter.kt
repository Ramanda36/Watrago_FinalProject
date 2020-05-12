package com.example.firstpage.TransportFragment

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstpage.AttractionFragment.*
import com.example.firstpage.R
import com.example.firstpage.TravelActivity

import java.util.ArrayList

class TravelActivityAdapter(var travelActivity: TravelActivity, var travelArraylist: ArrayList<myAttraction>?) :
    RecyclerView.Adapter<travel_activity_holder>() {

    private var removePosition : Int = 0
    private var removedItem : String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): travel_activity_holder {
        val layoutInflater = LayoutInflater.from(travelActivity.baseContext)
        val view = layoutInflater.inflate(R.layout.travel_activity_list_item, parent, false)
        return travel_activity_holder(view)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder){
        removePosition = viewHolder.adapterPosition
        removedItem = travelArraylist?.get(viewHolder.adapterPosition)!!.getmyspotname()
        travelArraylist!!.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        notifyDataSetChanged()
//        notifyItemRemoved(viewHolder.adapterPosition)
//        notifyDataSetChanged()

//        Snackbar.make(viewHolder.itemView, "$removedItem deleted.",Snackbar.LENGTH_LONG).setAction("UNDO"){
//            ScheduleList.add(removePosition,removedItem)
//            notifyItemInserted(removePosition)
//        }.show()
    }

    override fun onBindViewHolder(holder: travel_activity_holder, position: Int) {
//        holder.mprivatepic.setImageResource(R.drawable.ic_locked_travel)

        holder.itemView.setOnClickListener {
            var intent=Intent(travelActivity,Schedules::class.java)
            intent.putExtra("traveltempid", travelArraylist?.get(position)!!.mySpotid.toString())
            travelActivity.startActivity(intent)

        }
        if (itemCount <= 0 && position >= itemCount) {
            return
        }

        holder.mschename.text = travelArraylist!![position].getmyspotname()

        if(travelArraylist!![position].privacy=="True"){
            holder.mprivatepic.setImageResource(R.drawable.ic_locked_travel)
        }else{
            holder.mprivatepic.setImageResource(R.drawable.ic_unlocked_travel)
        }

        //        holder.mprivatepic.setImageResource(R.drawable.btn_privacy_luck);

    }

    override fun getItemCount(): Int {
        return if (travelArraylist == null) {
            0
        } else travelArraylist!!.size
        // setMySpotArray();
    }

}
