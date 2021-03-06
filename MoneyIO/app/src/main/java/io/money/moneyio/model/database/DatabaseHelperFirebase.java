package io.money.moneyio.model.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

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

import io.money.moneyio.model.AddFriend;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;

public class DatabaseHelperFirebase {


    private static List<MoneyFlow> data = new ArrayList<>();
    private static final String usersIE = "usersie";
    private Context context;
    private String myFriend;
    private long elapsedTime;
    private static DatabaseHelperFirebase instance;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference base;
    private String friendUid;

    //spinnerPos -> 2 = my stats, 1 = friends stats, 0 = combined stats
    public static List<MoneyFlow> filterData(long start, long end, int spinnerPos){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<MoneyFlow> filteredArr = new ArrayList<>();

        for (MoneyFlow f: data) {
            switch (spinnerPos) {
                case 2:
                        if(start <= f.getCalendar() && f.getCalendar() <= end && f.getUid().equals(uid)){
                        filteredArr.add(f);
                    }
                    break;
                case 1:
                    if(start <= f.getCalendar() && f.getCalendar() <= end && !f.getUid().equals(uid)){
                    filteredArr.add(f);
                    }
                    break;
                case 0:
                    if(start <= f.getCalendar() && f.getCalendar() <= end){
                    filteredArr.add(f);
                    }
                    break;
            }
        }

        Collections.sort(filteredArr, new Comparator<MoneyFlow>() {
            @Override
            public int compare(MoneyFlow o1, MoneyFlow o2) {
                return (o1.getCalendar() > o2.getCalendar())? -1 : 1;
            }
        });
        return Collections.unmodifiableList(filteredArr);
    }


    public static List<MoneyFlow> filterData(long start, long end) {

        return filterData(start, end, 0);
    }

    public DatabaseHelperFirebase(Context con) {
        firebaseAuth = FirebaseAuth.getInstance();
        base = FirebaseDatabase.getInstance().getReference();
        //base.keepSynced(true);  //TODO !!
        context = con;
        friendUid = " ";
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
        String addedFriend = Utilities.filterMail(preferences.getString(getEmail(), " "));

        String userMail = Utilities.filterMail(getEmail());

        final String addedFr = addedFriend;
        base.child("friends").child(addedFriend + userMail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AddFriend t = dataSnapshot.getValue(AddFriend.class);
                if(t != null && t.getMyEmail() != null && t.getMyEmail().equals(addedFr)){
                    friendID[0] = t.getMyUid();
                    Utilities.setHasFriend(true);
                    friendUid = friendID[0];
                    readDatabase(friendID[0]);
                } else {
                    friendUid = " ";
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
        if (friendMail.equals(Utilities.filterMail(getEmail()))) {
            Toast.makeText(context, "You cannot add youself as friend.", Toast.LENGTH_SHORT).show();
        } else {
            String mail = Utilities.filterMail(getEmail());
            String filteredFriendMail = Utilities.filterMail(friendMail);
            AddFriend friend = new AddFriend(getUid(), mail);
            this.base.child("friends").child(mail + filteredFriendMail).setValue(friend);
            checkForFriend();
        }
    }

    public void deleteFriend(String friendMail) {
        base.child(usersIE).child(Utilities.filterMail(friendMail)).removeEventListener(friendEventListener);
        AddFriend friend = new AddFriend("NOFRIEND", "NOFRIEND");
        this.base.child("friends").child(Utilities.filterMail(getEmail()) + Utilities.filterMail(friendMail)).setValue(friend);
        base.child(friendUid).removeEventListener(friendEventListener);
        friendUid = " ";
        checkForFriend();
    }

    private String getUid() {
        String id = firebaseAuth.getCurrentUser().getUid();
        return id;
    }

    private String getEmail() {
        String email = firebaseAuth.getCurrentUser().getEmail();
        return email;
    }

    public void addData(String userId, MoneyFlow moneyFlow){
        this.base.child(usersIE).child(userId).push().setValue(moneyFlow);
    }

    private ChildEventListener userEvent = new ChildEventListener() {
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
    };

    private ChildEventListener friendEventListener =  new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            MoneyFlow t = dataSnapshot.getValue(MoneyFlow.class);
            data.add(t);
            if(SystemClock.elapsedRealtime() - elapsedTime > 3000){
                Utilities.notifyFriend(context, t.getType(), Double.toString(t.getSum()));
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
    };

    private void readDatabase(String friendId) {
        data = new ArrayList<>();

        elapsedTime = SystemClock.elapsedRealtime();

        base.child(usersIE).child(firebaseAuth.getCurrentUser().getUid()).removeEventListener(userEvent);

        base.child(usersIE).child(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(userEvent);

        base.child(usersIE).child(friendId).removeEventListener(friendEventListener);

        base.child(usersIE).child(friendId).addChildEventListener(friendEventListener);
    }

    public void resetData(){
        data = new ArrayList<MoneyFlow>();
    }
    public List<MoneyFlow> getData() {
        return Collections.unmodifiableList(data);
    }
}
