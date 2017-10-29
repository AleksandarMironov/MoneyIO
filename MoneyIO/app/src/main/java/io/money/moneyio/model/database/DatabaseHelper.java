package io.money.moneyio.model.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.Alarm;
import io.money.moneyio.model.utilities.PlannedFlow;
import io.money.moneyio.model.utilities.Type;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "MoneyIO.db";
    private static int DATABASE_VERSION = 1;

    private static final String TABLE_SETINGS = "user_setings";
    private static final String TABLE_ALARMS = "user_alarms";
    private static final String TABLE_PLANED = "planned_events";

    private static final String T_SETTINGS_COL_1 = "user";
    private static final String T_SETTINGS_COL_2 = "category";
    private static final String T_SETTINGS_COL_3 = "type";
    private static final String T_SETTINGS_COL_4 = "imageid";

    private static final String T_ALARMS_COL_1 = "user";
    private static final String T_ALARMS_COL_2 = "date";
    private static final String T_ALARMS_COL_3 = "hour";
    private static final String T_ALARMS_COL_4 = "minutes";
    private static final String T_ALARMS_COL_5 = "massage";

    private static final String T_PLANNED_COL_1 = "user";
    private static final String T_PLANNED_COL_2 = "date";
    private static final String T_PLANNED_COL_3 = "type";
    private static final String T_PLANNED_COL_4 = "amount";

    private static final String PLANNED_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_PLANED +
                                        " (" + T_PLANNED_COL_1 + " TEXT, " +
                                        T_PLANNED_COL_2 + " INTEGER, " +
                                        T_PLANNED_COL_3 + " TEXT, " +
                                        T_PLANNED_COL_4 + " INTEGER," +
                                        " PRIMARY KEY (" + T_PLANNED_COL_1 + "));";

    private static final String SETTINGS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_SETINGS +
                                        " (" + T_SETTINGS_COL_1 + " TEXT, " +
                                        T_SETTINGS_COL_2 + " TEXT, " +
                                        T_SETTINGS_COL_3 + " TEXT, " +
                                        T_SETTINGS_COL_4 + " INTEGER," +
                                        " PRIMARY KEY (" +
                                        T_SETTINGS_COL_1 + ", " + T_SETTINGS_COL_2 + ", " + T_SETTINGS_COL_3 +
                                        "));";

    private static final String ALARMS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_ALARMS +
                                        " (" + T_ALARMS_COL_1 + " TEXT, " +
                                        T_ALARMS_COL_2 + " INTEGER, " +
                                        T_ALARMS_COL_3 + " INTEGER, " +
                                        T_ALARMS_COL_4 + " INTEGER," +
                                        T_ALARMS_COL_5 + " TEXT," +
                                        " PRIMARY KEY (" +
                                        T_ALARMS_COL_1 + ", " + T_ALARMS_COL_2 + ", " + T_ALARMS_COL_3 +  ", "
                                        + T_ALARMS_COL_4 +  ", " + T_ALARMS_COL_4 +
                                        "));";


    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SETTINGS_TABLE_CREATE);
        db.execSQL(ALARMS_TABLE_CREATE);
        db.execSQL(PLANNED_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public boolean addPlanned(String userID, int date, String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_PLANNED_COL_1, userID);
        contentValues.put(T_PLANNED_COL_2, date);
        contentValues.put(T_PLANNED_COL_3, type);
        contentValues.put(T_PLANNED_COL_4, amount);

        long b = db.insert(TABLE_PLANED, null, contentValues);
        return (b != -1);
    }

    public ArrayList<PlannedFlow> getAllPlaned() {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "SELECT * FROM " + TABLE_PLANED + ";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<PlannedFlow> out = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new PlannedFlow(c.getString(0), c.getInt(1), c.getString(2), c.getInt(3)));
        }
        c.close();
        return out;
    }

    public boolean addAlarm(String user, Integer date, Integer hour, Integer minutes, String massage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_ALARMS_COL_1, user);
        contentValues.put(T_ALARMS_COL_2, date);
        contentValues.put(T_ALARMS_COL_3, hour);
        contentValues.put(T_ALARMS_COL_4, minutes);
        contentValues.put(T_ALARMS_COL_5, massage);

        long b = db.insert(TABLE_ALARMS, null, contentValues);
        return (b != -1);
    }

    public void deleteAlarm(String user, Integer date, Integer hour, Integer minutes, String massage) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "DELETE FROM " + TABLE_ALARMS
                + " WHERE " +
                T_ALARMS_COL_1 + " = \"" + user + "\" AND " +
                T_ALARMS_COL_2 + " = \"" + date + "\" AND " +
                T_ALARMS_COL_3 + " = \"" + hour + "\" AND " +
                T_ALARMS_COL_4 + " = \"" + minutes + "\" AND " +
                T_ALARMS_COL_5 + " = \"" + massage + "\";";
        db.execSQL(myRawQuery);
    }

    public ArrayList<Alarm> getUserAlarms(String userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "SELECT " +
                T_ALARMS_COL_2 + ", " + T_ALARMS_COL_3 + ", " + T_ALARMS_COL_4 + ", " + T_ALARMS_COL_5
                + " FROM " + TABLE_ALARMS + " WHERE " + T_ALARMS_COL_1 + " = \"" + userID + "\";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<Alarm> out = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new Alarm(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3)));
        }
        c.close();
        return out;
    }

    public boolean addType(String user, String category, String type, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_SETTINGS_COL_1, user);
        contentValues.put(T_SETTINGS_COL_2, category);
        contentValues.put(T_SETTINGS_COL_3, type);
        contentValues.put(T_SETTINGS_COL_4, id);

        long b = db.insert(TABLE_SETINGS, null, contentValues);
        return (b != -1);
    }

    public void deleteType(String userID, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "DELETE FROM " + TABLE_SETINGS
                + " WHERE " + T_SETTINGS_COL_1 + " = \"" + userID + "\" AND " + T_SETTINGS_COL_2 + " = \"" + category + "\" AND " +
                T_SETTINGS_COL_3 + " = \"" + type + "\";";
        db.execSQL(myRawQuery);
    }

    public ArrayList<Type> getUserTypes(String userID){
        SQLiteDatabase db = this.getReadableDatabase();
        String myRawQuery = "SELECT " + T_SETTINGS_COL_2 + ", " + T_SETTINGS_COL_3 + ", " + T_SETTINGS_COL_4
                + " FROM " + TABLE_SETINGS + " WHERE " + T_SETTINGS_COL_1 + " = \"" + userID + "\";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<Type> out = new ArrayList<>();

        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new Type(c.getString(0), c.getString(1), c.getInt(2)));
        }

        // true = outcome, false = income
        out.add(new Type("true", "Alcohol", R.drawable.alcohol));
        out.add(new Type("true", "Bar", R.drawable.bar));
        out.add(new Type("true", "Balling", R.drawable.balling));
        out.add(new Type("true", "Bike", R.drawable.bike));
        out.add(new Type("true", "Books", R.drawable.books));
        out.add(new Type("true", "Boy", R.drawable.boy));
        out.add(new Type("true", "Burger", R.drawable.burger));
        out.add(new Type("true", "Business", R.drawable.bussiness));
        out.add(new Type("false", "Business", R.drawable.bussiness));
        out.add(new Type("true", "Car", R.drawable.car2));
        out.add(new Type("true", "Car Repair", R.drawable.car_repear));
        out.add(new Type("true", "Cinema", R.drawable.cinema));
        out.add(new Type("true", "Clothes", R.drawable.clothes));
        out.add(new Type("true", "Coffee", R.drawable.coffee));
        out.add(new Type("true", "Concert", R.drawable.concert));
        out.add(new Type("true", "Couple", R.drawable.couple));
        out.add(new Type("false", "Dividends", R.drawable.dividents));
        out.add(new Type("true", "Electricity", R.drawable.electricity));
        out.add(new Type("true", "First Aid", R.drawable.first_aid));
        out.add(new Type("true", "Food", R.drawable.food));
        out.add(new Type("true", "Fuel", R.drawable.fuel));
        out.add(new Type("true", "Games", R.drawable.games));
        out.add(new Type("false", "Games", R.drawable.games));
        out.add(new Type("true", "Gift", R.drawable.gift));
        out.add(new Type("false", "Gift", R.drawable.gift));
        out.add(new Type("true", "Girl", R.drawable.girl));
        out.add(new Type("true", "Hair Style", R.drawable.hairdresser));
        out.add(new Type("true", "Holiday", R.drawable.holiday));
        out.add(new Type("true", "Home", R.drawable.home));
        out.add(new Type("true", "Love", R.drawable.love));
        out.add(new Type("true", "Lunch", R.drawable.lunch));
        out.add(new Type("true", "Makeup", R.drawable.makeup));
        out.add(new Type("true", "Music", R.drawable.music));
        out.add(new Type("true", "Pet", R.drawable.pet));
        out.add(new Type("true", "Pet", R.drawable.pet2));
        out.add(new Type("true", "Phone", R.drawable.phone));
        out.add(new Type("false", "Phone apps", R.drawable.phone));
        out.add(new Type("true", "Race", R.drawable.race));
        out.add(new Type("false", "Race", R.drawable.race));
        out.add(new Type("true", "Repair", R.drawable.repear));
        out.add(new Type("false", "Services", R.drawable.self_employment));
        out.add(new Type("false", "Salary", R.drawable.salary));
        out.add(new Type("false", "Savings", R.drawable.saved_money));
        out.add(new Type("true", "Shoes", R.drawable.shoes));
        out.add(new Type("true", "Shopping", R.drawable.shopping2));
        out.add(new Type("true", "Skiing", R.drawable.skiing));
        out.add(new Type("true", "Sport", R.drawable.sport));
        out.add(new Type("true", "Theatre", R.drawable.theatre));
        out.add(new Type("true", "Trip", R.drawable.trip));
        out.add(new Type("true", "TV", R.drawable.tv));
        out.add(new Type("true", "Wedding", R.drawable.wedding));
        out.add(new Type("true", "Tabacco", R.drawable.weed));
        out.add(new Type("false", "Codding", R.drawable.work));

        c.close();
        return out;
    }
}
