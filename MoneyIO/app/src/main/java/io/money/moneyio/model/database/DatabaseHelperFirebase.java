package io.money.moneyio.model.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;

public class DatabaseHelperFirebase {


    private static List<MoneyFlow> data = new ArrayList<>();
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

    public DatabaseHelperFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        base = FirebaseDatabase.getInstance().getReference();
        base.keepSynced(true);
        readDatabase();
    }

    public static synchronized DatabaseHelperFirebase getInstance() {
        if (instance == null) {
            instance = new DatabaseHelperFirebase();
        }
        return instance;
    }

    public void addData(String userId, MoneyFlow moneyFlow){
        this.base.child(userId).push().setValue(moneyFlow);
    }

    private void readDatabase(){
        data = new ArrayList<>();
        DatabaseReference fdbuser = base.child(firebaseAuth.getCurrentUser().getUid());
        fdbuser.addChildEventListener(new ChildEventListener() {
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
    }

    public List<MoneyFlow> getData() {
        return Collections.unmodifiableList(data);
    }
}
