package generalassemb.ly.trantalk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by brendan on 8/9/16.
 */
public  class ChatHolder extends RecyclerView.ViewHolder {
    View mView;
    RelativeLayout rl;
    CardView cv;
    public ChatHolder(View itemView) {
        super(itemView);
        mView = itemView;
        cv = (CardView)itemView.findViewById(R.id.cardView);
       rl = (RelativeLayout)itemView.findViewById(R.id.message_layout);

    }
    public void setTime(String time){
        TextView field = (TextView) mView.findViewById(R.id.timestamp);
        field.setText(time);
    }

    public void setName(String name) {
        TextView field = (TextView) mView.findViewById(R.id.user_name);
        field.setText(name);
    }

    public void setText(String text) {
        TextView field = (TextView) mView.findViewById(R.id.message_body);
        field.setText(text);

    }
    public void setGravity(String side){
        if(side.equals("right")){
        rl.setGravity(Gravity.RIGHT);
            cv.setCardBackgroundColor(Color.parseColor("#FF0000"));
    }else{rl.setGravity(Gravity.LEFT);
            cv.setCardBackgroundColor(Color.parseColor("#000000"));}
    }
}