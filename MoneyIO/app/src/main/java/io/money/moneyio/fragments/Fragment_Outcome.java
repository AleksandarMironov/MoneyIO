package io.money.moneyio.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.money.moneyio.R;
import io.money.moneyio.model.MoneyFlow;

public class Fragment_Outcome extends Fragment {

    private View view;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_outcome, container, false);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //keep data sync offline
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        myRef = myRef.child(firebaseAuth.getCurrentUser().getUid());
        final DatabaseReference finalMyRef = myRef;
        Button b = (Button)view.findViewById(R.id.out_test_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalMyRef.push().setValue(new MoneyFlow(true, "test", "no comment", 125));
            }
        });
        return view;
    }
}
