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
import io.money.moneyio.R;
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
        switchNotifications.setChecked(!(preferences.getString(user.getCurrentUser().getEmail()  + "notifications", "EMPTY").equals("OFF")));
    }

    //on add friend listener
    public void onAddListener() {
       add.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String mail = email.getText().toString().trim();
               if(!Utilities.isMail(mail)){
                   email.setError(getString(R.string.enter_valid_mail));
                   return;
               }

               if (mail.equals((user.getCurrentUser().getEmail()))) {
                   Toast.makeText(view.getContext(), "You cannot add youself as friend.", Toast.LENGTH_SHORT).show();
                   return;
               }
               SharedPreferences.Editor editor = preferences.edit();
               editor.putString(user.getCurrentUser().getEmail(), mail);
               editor.apply();

               //add friend in firebase
               fdb.addFriend(Utilities.filterMail(mail));
               Utilities.setHasFriend(true);
               Toast.makeText(view.getContext(), R.string.added, Toast.LENGTH_SHORT).show();
               setEditText();
           }
       });
   }

   //on delete friend listener
   public void onDeleteListener(){
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String frendMail = preferences.getString(user.getCurrentUser().getEmail(), " ");
               SharedPreferences.Editor editor = preferences.edit();
               editor.putString(user.getCurrentUser().getEmail(), "NOFRIEND");
               editor.apply();

               Utilities.setHasFriend(false);

               //delete friend from firebase
               fdb.deleteFriend(frendMail);

               Toast.makeText(view.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
               setEditText();
           }
       });
   }

   public void onRefreshListener(){
       refresh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               fdb.updateMyFriend();
               Toast.makeText(view.getContext(), R.string.refreshing, Toast.LENGTH_SHORT).show();
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

   //on notifications on end off listener
   public void notificationsSwithListener(){
       if(preferences.getString(user.getCurrentUser().getUid()  + "notifications", "EMPTY").equals("EMPTY")){
           SharedPreferences.Editor editor = preferences.edit();
           editor.putString(user.getCurrentUser().getUid()  + "notifications" , "ON");
           editor.apply();
       }
       switchNotifications.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(view.getContext(), switchNotifications.isChecked()? "ON": "OFF", Toast.LENGTH_SHORT).show();

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
