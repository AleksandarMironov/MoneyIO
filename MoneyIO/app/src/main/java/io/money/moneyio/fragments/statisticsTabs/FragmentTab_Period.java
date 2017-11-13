package io.money.moneyio.fragments.statisticsTabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import io.money.moneyio.R;

public class FragmentTab_Period extends Fragment {

    private View view;
    private EditText calendarTo;
    private TextView from, to;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_datahistory, container, false);
        initialise();
        return view;
    }

    private void initialise() {
        calendarTo = view.findViewById(R.id.history_date_edit1);
        from = view.findViewById(R.id.history_text_from);
        to = view.findViewById(R.id.history_text_to);
        setVisibility();
    }

    private void setVisibility() {
        calendarTo.setVisibility(View.VISIBLE);
        to.setVisibility(View.VISIBLE);
        from.setVisibility(View.VISIBLE);
    }
}
