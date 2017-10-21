package io.money.moneyio.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import io.money.moneyio.R;

public class TypeRecyclerViewAdapter extends RecyclerView.Adapter<TypeRecyclerViewAdapter.MyViewHolder>{

    Context context;
    ArrayList<Type> types;
    LayoutInflater inflater;

    public TypeRecyclerViewAdapter(Context context, ArrayList<Type> types) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.types = types;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row;
        final TypeRecyclerViewAdapter.MyViewHolder viewHolder;
        row = inflater.inflate(R.layout.recycler_row, parent, false);
        viewHolder = new TypeRecyclerViewAdapter.MyViewHolder(row,
                (ImageView)row.findViewById(R.id.row_image),
                (TextView)row.findViewById(R.id.row_text));
        row.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Type type = types.get(position);
        holder.image.setImageResource(type.getPictureId());
        holder.text.setText(type.getType());
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView text;

        public MyViewHolder(View row, ImageView image, TextView text) {
            super(row);
            this.image = image;
            this.text = text;

        }
    }
}
