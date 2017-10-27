package io.money.moneyio.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.money.moneyio.R;

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener mClickListener;
    private ArrayList<Alarm> alarms;

    public AlarmsRecyclerViewAdapter(Context context, ArrayList<Alarm> alarms){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row;
        final AlarmsRecyclerViewAdapter.MyViewHolder viewHolder;
        row = inflater.inflate(R.layout.recycler_alarm_row, parent, false);
        viewHolder = new AlarmsRecyclerViewAdapter.MyViewHolder(row,
                (TextView)row.findViewById(R.id.alarm_datetime_text),
                (TextView)row.findViewById(R.id.alarm_massage_text),
                (ImageView)row.findViewById(R.id.alarm_remove));
        row.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);
        holder.massage.setText(alarm.getMassage());
        holder.dateTime.setText("Date: " + alarm.getDate() + "   Time: " + alarm.getHour() + ":" + alarm.getMinutes());
    }

    @Override
    public int getItemCount() {
        return this.alarms.size();
    }

    public Alarm getItem(int id) {
        return alarms.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(AlarmsRecyclerViewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dateTime, massage;
        ImageView delete;

        public MyViewHolder(View itemView, TextView dateTime, TextView massage, ImageView delete) {
            super(itemView);
            this.delete = delete;
            this.dateTime = dateTime;
            this.massage = massage;

            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
