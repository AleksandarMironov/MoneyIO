package io.money.moneyio.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import io.money.moneyio.R;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.database.DatabaseHelperFirebase;

public class Fragment_AddFriend extends Fragment {

    private View view;
    private EditText email;
    private ImageView add;
    private DatabaseHelperFirebase fdb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addfriend, container, false);
        initialise();
        add();
        return view;
    }

    private void initialise() {
        fdb = DatabaseHelperFirebase.getInstance();
        email = view.findViewById(R.id.addfriend_email);
        add = view.findViewById(R.id.addfriend_add_btn);
    }

   public void add() {
       add.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String mail = email.getText().toString().trim().toString();
               StringBuilder sb = new StringBuilder();
               for (int i = 0; i < mail.length(); i++) {
                   if (mail.charAt(i) == '.') {
                       sb.append("_");
                   } else {
                       sb.append(mail.charAt(i));
                   }
               }
               fdb.addFriend(sb.toString());
               Toast.makeText(view.getContext(), "added", Toast.LENGTH_SHORT).show();
           }
       });
   }
}
