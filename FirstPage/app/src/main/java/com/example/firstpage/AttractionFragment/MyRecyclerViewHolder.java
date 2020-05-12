package com.example.firstpage.AttractionFragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firstpage.R;

public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView mspotname;
    public ImageButton mspotpic;
    public ImageButton mcollection;
    public TextView mschename;
    public ImageButton mprivatepic;
    public TextView newspotname;
    public ImageButton newspotpic;
    public ImageButton newcollection;



    public MyRecyclerViewHolder( View itemView) {
        super(itemView);
        mspotname=itemView.findViewById(R.id.spotname);
        mspotpic=itemView.findViewById(R.id.spotpic);
        mcollection=itemView.findViewById(R.id.collection);
        mschename=itemView.findViewById(R.id.ScheName);
        mprivatepic=itemView.findViewById(R.id.privateSche);

        newspotname=itemView.findViewById(R.id.newspotname);
        newspotpic=itemView.findViewById(R.id.newspotpic);
        newcollection=itemView.findViewById(R.id.new_spot_collection);

    }

}
