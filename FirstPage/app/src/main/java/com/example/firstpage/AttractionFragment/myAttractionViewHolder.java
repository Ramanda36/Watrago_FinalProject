package com.example.firstpage.AttractionFragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firstpage.R;

public class myAttractionViewHolder extends RecyclerView.ViewHolder {

        public TextView myspotname;
        public ImageButton myspotpic;
        public ImageButton mycollection;
//  public ImageView mspotcheck;

        public myAttractionViewHolder( View itemView) {
                super(itemView);

                myspotname=itemView.findViewById(R.id.myspotname);
                myspotpic=itemView.findViewById(R.id.myspotpic);
                mycollection=itemView.findViewById(R.id.mycollection);
                // mspotcheck=itemView.findViewById(R.id.check);
        }

}
