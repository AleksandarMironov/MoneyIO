package io.money.moneyio.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.money.moneyio.R;
import io.money.moneyio.model.HistoryRecyslerViewAdapter;
import io.money.moneyio.model.Utilities;

public class Fragment_DataHistory extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_datahistory, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.history_recycler_view);
        HistoryRecyslerViewAdapter adapter = new HistoryRecyslerViewAdapter(view.getContext(), Utilities.data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}
