package io.money.moneyio.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.money.moneyio.R;
import io.money.moneyio.model.Utilities;


public class Fragment_Alarm extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
       ((Button)view.findViewById(R.id.test_alert)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.notifyMe(view.getContext(), "Working :)");
            }
        });
        return view;
    }
}
