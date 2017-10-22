package io.money.moneyio.model;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.money.moneyio.R;

public class HistoryRecyslerViewAdapter extends RecyclerView.Adapter<HistoryRecyslerViewAdapter.MyHolder>{

    private Context context;
    private ArrayList<MoneyFlow> data;
    private LayoutInflater inflater;

    public HistoryRecyslerViewAdapter(Context context, ArrayList<MoneyFlow> data) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView date, type, comment, price;

        MyHolder(View row) {
            super(row);
            type = (TextView) row.findViewById(R.id.row_history_type);
            date = (TextView) row.findViewById(R.id.row_history_date);
            comment = (TextView) row.findViewById(R.id.row_history_comment);
            price = (TextView) row.findViewById(R.id.row_history_price);
            image = (ImageView) row.findViewById(R.id.row_history_img);
        }


    }

    @Override
    public HistoryRecyslerViewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_histoy_row, parent, false);
        MyHolder viewHolder = new MyHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        MoneyFlow moneyFlow = data.get(position);
        holder.comment.setText(moneyFlow.getComment());
        holder.type.setText(moneyFlow.getType());
        holder.price.setText("" + moneyFlow.getSum()); ///TODO edit string, fix calendar
        holder.date.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(moneyFlow.getCalendar())));
        holder.image.setImageResource(R.drawable.statistics_icon);
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
