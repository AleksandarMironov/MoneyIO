package io.money.moneyio.model.database;

import android.content.Context;
import android.os.SystemClock;
import android.util.FloatProperty;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
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

    public static void resetFirebaseDatabase(){
        data = new ArrayList<>();
    }

    public static ArrayList<MoneyFlow> filterData(long start, long end){
        ArrayList<MoneyFlow> filteredArr = new ArrayList<>();
        for (MoneyFlow f: data) {
            if(start <= f.getCalendar() && f.getCalendar() <= end){
                filteredArr.add(f);
            } else if(f.getCalendar() > end){
                break;
            }
        }
        return filteredArr;
    }


    private static DatabaseHelperFirebase instance;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference base;

    public DatabaseHelperFirebase(Context con) {
        firebaseAuth = FirebaseAuth.getInstance();
        base = FirebaseDatabase.getInstance().getReference();
        base.keepSynced(true);
        context = con;
        updateContext(con);
        updateMyFriend();
        readDatabase();
    }

    private void updateContext(Context con){
        this.context = con;
    }

    public static synchronized DatabaseHelperFirebase getInstance(Context con) {
        if (instance == null) {
            instance = new DatabaseHelperFirebase(con);
        }
        return instance;
    }

    private void updateMyFriend(){
        this.myFriend = "W5FTdpvvH9cP0Qyoz7fOI6B4pfr1";
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

    private void readDatabase(){
        data = new ArrayList<>();
        DatabaseReference fdbuser = base;
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

        fdbuser.child(myFriend).addChildEventListener(new ChildEventListener() {
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
