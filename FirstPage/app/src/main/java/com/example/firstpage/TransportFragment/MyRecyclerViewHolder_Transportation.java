package com.example.firstpage.TransportFragment;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firstpage.AttractionFragment.Attraction;
import com.example.firstpage.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyRecyclerViewHolder_Transportation  extends RecyclerView.ViewHolder  {

    public TextView mTranSpot;
    public TextView mtransmode;
    public TextView mtransalrm;
    public TextView mtranstime;
    public TextView mTranSpotname;
    public ImageButton mpicture;
    public ImageButton mcollection;
    public ImageView mblock;
    public TextView myTranSpotname;
    public ImageButton mytranspicture;
    public ImageButton mytranscollection;
    public ImageView mytransblock;
    public ImageView EDstoptime;
    public TextView stayTime;
    public ImageView mimgTrans ;

    public ImageView SchelikeImage ;
    public TextView likecount;
    public TextView schename;
    public ImageButton mcollect;

//    public ImageView EDclcok ;

    public MyRecyclerViewHolder_Transportation( View itemView) {
        super(itemView);
        mTranSpotname=itemView.findViewById(R.id.myspotname);
        mcollection=itemView.findViewById(R.id.mycollection);
        mpicture=itemView.findViewById(R.id.transportpic);
        mblock=itemView.findViewById(R.id.img_block);

//        tranaportant_list_item
        mTranSpot=itemView.findViewById(R.id.TransportSpot);
        mtransmode=itemView.findViewById(R.id.TransportMode);
//        mtransalrm=itemView.findViewById(R.id.TransportAlarm);
        mtranstime=itemView.findViewById(R.id.TransportTime);
        mimgTrans = itemView.findViewById(R.id.imgTrans);
        stayTime = itemView.findViewById(R.id.stayTime);

        myTranSpotname=itemView.findViewById(R.id.mytransname);
        mytranscollection=itemView.findViewById(R.id.mytranscollection);
        mytranspicture=itemView.findViewById(R.id.mytransspotpic);
        mytransblock=itemView.findViewById(R.id.my_trans_img_block);

        //sche_edit_list_item.xml
        EDstoptime = itemView.findViewById(R.id.StopTime);
//        EDclcok = itemView.findViewById(R.id.TransportPic);

        //public_travel_list_item
        SchelikeImage = itemView.findViewById(R.id.SchelikeImage);
        likecount = itemView.findViewById(R.id.likecount);
        schename = itemView.findViewById(R.id.ScheName);
        mcollect = itemView.findViewById(R.id.collect);
    }

}
