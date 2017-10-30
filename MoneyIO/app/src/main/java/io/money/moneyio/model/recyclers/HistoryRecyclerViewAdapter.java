package io.money.moneyio.model.recyclers;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.MoneyFlow;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MyHolder>{

    private Context context;
    private List<MoneyFlow> data;
    private LayoutInflater inflater;

    public HistoryRecyclerViewAdapter(Context context, List<MoneyFlow> data) {
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
    public HistoryRecyclerViewAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_histoy_row, parent, false);
        MyHolder viewHolder = new MyHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        MoneyFlow moneyFlow = data.get(data.size() - position - 1);

        holder.comment.setText(moneyFlow.getComment());
        holder.type.setText(moneyFlow.getType());
        holder.price.setText(Double.toString(moneyFlow.getSum()));
        holder.date.setText(new SimpleDateFormat("d-MMM-yy' / 'HH:mm").format(new Date(moneyFlow.getCalendar())));
        if (moneyFlow.getExpense().equals("true")) {
            holder.image.setImageResource(R.drawable.outcome_icon);//outcome
            holder.price.setTextColor(Color.argb(255,255,85,85));
        } else {
            holder.image.setImageResource(R.drawable.income_icon);//income
            holder.price.setTextColor(Color.argb(255,0,255,64));
        }

    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
