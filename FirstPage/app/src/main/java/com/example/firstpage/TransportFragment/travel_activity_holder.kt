package com.example.firstpage.TransportFragment

import android.R
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.example.firstpage.R

class travel_activity_holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mschename: TextView
    var mprivatepic: ImageButton

    init {
        mschename = itemView.findViewById(com.example.firstpage.R.id.ScheName)
        mprivatepic = itemView.findViewById(com.example.firstpage.R.id.privateSche)
    }

}
