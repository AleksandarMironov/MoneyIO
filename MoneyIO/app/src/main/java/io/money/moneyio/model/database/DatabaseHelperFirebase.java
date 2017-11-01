package io.money.moneyio.model.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.FloatProperty;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.money.moneyio.activities.HomeActivity;
import io.money.moneyio.model.AddFriend;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.AlarmUtilities;
import io.money.moneyio.model.utilities.Utilities;

public class DatabaseHelperFirebase {


    private static List<MoneyFlow> data = new ArrayList<>();
    private Context context;
    private String myFriend;
    private long elapsedTime;
    private static DatabaseHelperFirebase instance;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference base;

    public static void resetFirebaseDatabase(){
        data = new ArrayList<>();
    }

    public static ArrayList<MoneyFlow> filterData(long start, long end){
        ArrayList<MoneyFlow> filteredArr = new ArrayList<>();
        for (MoneyFlow f: data) {
            if(start <= f.getCalendar() && f.getCalendar() <= end){
                filteredArr.add(f);
            }
        }

        Collections.sort(filteredArr, new Comparator<MoneyFlow>() {
            @Override
            public int compare(MoneyFlow o1, MoneyFlow o2) {
                return (o1.getCalendar() > o2.getCalendar())? -1 : 1;
            }
        });
        return filteredArr;
    }

    public DatabaseHelperFirebase(Context con) {
        firebaseAuth = FirebaseAuth.getInstance();
        base = FirebaseDatabase.getInstance().getReference();
        //base.keepSynced(true);
        context = con;
        readDatabase(" ");
        getMyFriend();
        updateContext(con);
        updateMyFriend();
    }

    private void updateContext(Context con){
        this.context = con;
    }

    public static synchronized DatabaseHelperFirebase getInstance(Context con) {
        if (instance == null) {
            instance = new DatabaseHelperFirebase(con);
        }
        instance.updateContext(con);
        return instance;
    }

    public void updateMyFriend(){

        getMyFriend();


        checkForFriend();

    }

    private void getMyFriend(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        myFriend = preferences.getString(getUid(), " ");
    }

    private void checkForFriend(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        final String[] friendID = {""};
        String addedFriend = preferences.getString(getEmail(), " ");

        String userMail =  firebaseAuth.getCurrentUser().getEmail();
        StringBuilder userMailSb = new StringBuilder();
        for (int i = 0; i < userMail.length(); i++) {
            if (userMail.charAt(i) == '.') {
                userMailSb.append("__");
            } else {
                userMailSb.append(userMail.charAt(i));
            }
        }
        userMail = userMailSb.toString();
        StringBuilder addedFriendSb = new StringBuilder();
        for (int i = 0; i < addedFriend.length(); i++) {
            if (addedFriend.charAt(i) == '.') {
                addedFriendSb.append("__");
            } else {
                addedFriendSb.append(addedFriend.charAt(i));
            }
        }
        addedFriend = addedFriendSb.toString();

        final String addedFr = addedFriend;
        base.child("friends").child(userMail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AddFriend t = dataSnapshot.getValue(AddFriend.class);
                if(t != null && t.getMyEmail() != null && t.getMyEmail().equals(addedFr)){
                    friendID[0] = t.getMyUid();
                    Utilities.setHasFriend(true);
                    readDatabase(friendID[0]);
                } else {
                    readDatabase(" ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getUid(), friendID[0]);
        editor.apply();
    }

    public void addFriend(String friendMail) {
        String mail = getEmail();
        StringBuilder userMail = new StringBuilder();
        StringBuilder friendMailSb = new StringBuilder();

        for (int i = 0; i < mail.length(); i++) {
            if (mail.charAt(i) == '.') {
                userMail.append("__");
            } else {
                userMail.append(mail.charAt(i));
            }
        }

        for (int i = 0; i < friendMail.length(); i++) {
            if (friendMail.charAt(i) == '.') {
                friendMailSb.append("__");
            } else {
                friendMailSb.append(friendMail.charAt(i));
            }
        }

        AddFriend friend = new AddFriend(getUid(), userMail.toString());
        this.base.child("friends").child(friendMailSb.toString()).setValue(friend);
        checkForFriend();
    }

    public void deleteFriend(String friendMail) {
        StringBuilder friendMailSb = new StringBuilder();

        for (int i = 0; i < friendMail.length(); i++) {
            if (friendMail.charAt(i) == '.') {
                friendMailSb.append("__");
            } else {
                friendMailSb.append(friendMail.charAt(i));
            }
        }

        AddFriend friend = new AddFriend("NOFRIEND", "NOFRIEND");
        this.base.child("friends").child(friendMailSb.toString()).setValue(friend);
        checkForFriend();
    }

    public String getUid() {
        String id = firebaseAuth.getCurrentUser().getUid();
        return id;
    }

    public String getEmail() {
        String email = firebaseAuth.getCurrentUser().getEmail();
        return email;
    }

    public void addData(String userId, MoneyFlow moneyFlow){
        this.base.child(userId).push().setValue(moneyFlow);
    }

    private void readDatabase(String fr){
        data = new ArrayList<>();
        DatabaseReference fdbuser = FirebaseDatabase.getInstance().getReference(); // base;
        elapsedTime = SystemClock.elapsedRealtime();
        fdbuser.child(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MoneyFlow t = dataSnapshot.getValue(MoneyFlow.class);
                data.add(t);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fdbuser.child(fr).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MoneyFlow t = dataSnapshot.getValue(MoneyFlow.class);
                data.add(t);
                if(SystemClock.elapsedRealtime() - elapsedTime > 3000){
                    AlarmUtilities.notifyMe(context, Float.toString(t.getSum()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<MoneyFlow> getData() {
        return Collections.unmodifiableList(data);
    }
}
