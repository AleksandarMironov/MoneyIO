package io.money.moneyio.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import io.money.moneyio.R;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import io.money.moneyio.model.utilities.Utilities;

public class Fragment_AddFriend extends Fragment {

    private View view;
    private EditText email;
    private ImageView add, delete, refresh;
    private DatabaseHelperFirebase fdb;
    private FirebaseAuth user;
    private Switch switchNotifications;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addfriend, container, false);
        initialise();
        onAddListener();
        onDeleteListener();
        onRefreshListener();
        setEditText();
        notificationsSwithListener();
        return view;
    }

    private void initialise() {
        preferences = PreferenceManager.getDefaultSharedPreferences(view.getContext().getApplicationContext());
        fdb = DatabaseHelperFirebase.getInstance(view.getContext());
        email = view.findViewById(R.id.addfriend_email);
        add = view.findViewById(R.id.addfriend_add_btn);
        user = FirebaseAuth.getInstance();
        delete = view.findViewById(R.id.addfriend_remove);
        refresh = view.findViewById(R.id.addfriend_refresh);
        switchNotifications = view.findViewById(R.id.addfriend_notifications_switch);
    }

    @Override
    public void onStart() {
        super.onStart();
        switchNotifications.setChecked(preferences.getString(user.getCurrentUser().getEmail()  + "notifications", "EMPTY").equals("ON"));
    }

    public void onAddListener() {
       add.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String mail = email.getText().toString().trim();
               if(!Utilities.isMail(mail)){
                   email.setError("Enter valid email");
                   return;
               }

               SharedPreferences.Editor editor = preferences.edit();
               editor.putString(user.getCurrentUser().getEmail(), mail);
               editor.apply();

               StringBuilder sb = new StringBuilder();
               for (int i = 0; i < mail.length(); i++) {
                   if (mail.charAt(i) == '.') {
                       sb.append("__");
                   } else {
                       sb.append(mail.charAt(i));
                   }
               }

               fdb.addFriend(sb.toString());
               Utilities.setHasFriend(true);
               Toast.makeText(view.getContext(), "added", Toast.LENGTH_SHORT).show();
               setEditText();
           }
       });
   }

   public void onDeleteListener(){
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String frendMail = preferences.getString(user.getCurrentUser().getEmail(), " ");
               SharedPreferences.Editor editor = preferences.edit();
               editor.putString(user.getCurrentUser().getEmail(), "NOFRIEND");
               editor.apply();

               Utilities.setHasFriend(false);

               fdb.deleteFriend(frendMail);

               Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
               setEditText();
           }
       });
   }

   public void onRefreshListener(){
       refresh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               fdb.updateMyFriend();
               Toast.makeText(view.getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
               setEditText();
           }
       });
   }

   private void setEditText(){
       String frendMail = preferences.getString(user.getCurrentUser().getEmail(), " ");
       if(frendMail.equals(" ") || frendMail.equals("NOFRIEND")){
           refresh.setVisibility(View.GONE);
           delete.setVisibility(View.GONE);
           add.setVisibility(View.VISIBLE);
           email.setText("");
       } else {
           refresh.setVisibility(View.VISIBLE);
           delete.setVisibility(View.VISIBLE);
           add.setVisibility(View.GONE);
           email.setText(frendMail);
       }
   }
   
   public void notificationsSwithListener(){
       if(preferences.getString(user.getCurrentUser().getUid()  + "notifications", "EMPTY").equals("EMPTY")){
           SharedPreferences.Editor editor = preferences.edit();
           editor.putString(user.getCurrentUser().getUid()  + "notifications", "ON");
           editor.apply();
       }
       switchNotifications.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(view.getContext(), switchNotifications.isChecked()? "ON" : "OFF", Toast.LENGTH_SHORT).show();

               SharedPreferences.Editor editor = preferences.edit();

               if(switchNotifications.isChecked()){
                   editor.putString(user.getCurrentUser().getUid() + "notifications", "ON");
               } else {
                   editor.putString(user.getCurrentUser().getUid()  + "notifications", "OFF");
               }
               editor.apply();
           }
       });
   }
}

