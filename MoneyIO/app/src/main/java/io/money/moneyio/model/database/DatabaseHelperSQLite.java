package io.money.moneyio.model.database;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.PlannedFlow;
import io.money.moneyio.model.Type;

public class DatabaseHelperSQLite extends SQLiteOpenHelper {

    private static DatabaseHelperSQLite instance;

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

    private Context context;

    private DatabaseHelperSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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

    //Singleton for SQLite database helper
    public static synchronized DatabaseHelperSQLite getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelperSQLite(context.getApplicationContext());
        }
        return instance;
    }

    //add planned expense (for notifications)
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

    //delete planned expense (for notifications)
    public void deletePlanned(String userID, int date, String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "DELETE FROM " + TABLE_PLANED
                + " WHERE " +
                T_PLANNED_COL_1 + " = \"" + userID + "\" AND " +
                T_PLANNED_COL_2 + " = \"" + date + "\" AND " +
                T_PLANNED_COL_3 + " = \"" + type + "\" AND " +
                T_PLANNED_COL_4 + " = \"" + amount + "\";";
        db.execSQL(myRawQuery);
    }

    //returns all planed expenses for current user
    public PlannedFlow getUserPlanned(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "SELECT * FROM " + TABLE_PLANED + " WHERE " + T_PLANNED_COL_1 + " = \"" + userID + "\";";
        Cursor c = db.rawQuery(myRawQuery, null);
        if(c.getCount() > 0){
            c.moveToFirst();
            PlannedFlow out = new PlannedFlow(c.getString(0), c.getInt(1), c.getString(2), c.getInt(3));
            c.close();
            return out;
        } else {
            c.close();
            return null;
        }
    }

    //returns all planed expenses
    public List<PlannedFlow> getAllPlaned() {
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
        return Collections.unmodifiableList(out);
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

    public List<Alarm> getUserAlarms(String userID) {
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
        return Collections.unmodifiableList(out);
    }

    public List<Alarm> getAllAlarms() {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "SELECT " +
                T_ALARMS_COL_2 + ", " + T_ALARMS_COL_3 + ", " + T_ALARMS_COL_4 + ", " + T_ALARMS_COL_5
                + " FROM " + TABLE_ALARMS + ";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<Alarm> out = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new Alarm(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3)));
        }
        c.close();
        return Collections.unmodifiableList(out);
    }

    //add custom income/expense type
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

    //delete custom income/expense type
    public void deleteType(String userID, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "DELETE FROM " + TABLE_SETINGS
                + " WHERE " + T_SETTINGS_COL_1 + " = \"" + userID + "\" AND " + T_SETTINGS_COL_2 + " = \"" + category + "\" AND " +
                T_SETTINGS_COL_3 + " = \"" + type + "\";";
        db.execSQL(myRawQuery);
    }

    //terurns all custom income/expense type for current user
    public List<Type> getUserTypes(String userID){
        SQLiteDatabase db = this.getWritableDatabase();
        String myRawQuery = "SELECT " + T_SETTINGS_COL_2 + ", " + T_SETTINGS_COL_3 + ", " + T_SETTINGS_COL_4
                + " FROM " + TABLE_SETINGS + " WHERE " + T_SETTINGS_COL_1 + " = \"" + userID + "\";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<Type> out = new ArrayList<>();

        //read all custom added types and put them in front of the grid
        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new Type(c.getString(0), c.getString(1), c.getInt(2)));
        }

        Resources t = context.getResources();

        String [] names = {t.getString(R.string.Alcohol), t.getString(R.string.Bar), t.getString(R.string.Balling),
                t.getString(R.string.Bike), t.getString(R.string.Books), t.getString(R.string.Boy), t.getString(R.string.Burger),
                t.getString(R.string.Business), t.getString(R.string.Car), t.getString(R.string.CarRepair),
                t.getString(R.string.Cinema), t.getString(R.string.Clothes), t.getString(R.string.Coffee),
                t.getString(R.string.Concert), t.getString(R.string.Couple), t.getString(R.string.Bitcoin),
                t.getString(R.string.Dividends), t.getString(R.string.Codding), t.getString(R.string.Electricity),
                t.getString(R.string.FirstAid), t.getString(R.string.Food), t.getString(R.string.Fuel), t.getString(R.string.Games),
                t.getString(R.string.Gift), t.getString(R.string.Girl), t.getString(R.string.HairStyle),
                t.getString(R.string.Holiday), t.getString(R.string.Home), t.getString(R.string.Love),
                t.getString(R.string.Lunch), t.getString(R.string.Makeup), t.getString(R.string.Music),
                t.getString(R.string.Pet), t.getString(R.string.Phone), t.getString(R.string.PhoneApps),
                t.getString(R.string.Race), t.getString(R.string.Repair), t.getString(R.string.Services),
                t.getString(R.string.Sallary), t.getString(R.string.Savings), t.getString(R.string.Shoes),
                t.getString(R.string.Shopping), t.getString(R.string.Skiing), t.getString(R.string.Sport),
                t.getString(R.string.Theatre), t.getString(R.string.Trip), t.getString(R.string.TV),
                t.getString(R.string.Wedding), t.getString(R.string.Tabacco)};

        // true = outcome, false = income
        out.add(new Type("true", names[0], R.drawable.alcohol));
        out.add(new Type("true", names[1], R.drawable.bar));
        out.add(new Type("true", names[2], R.drawable.balling));
        out.add(new Type("true", names[3], R.drawable.bike));
        out.add(new Type("true", names[4], R.drawable.books));
        out.add(new Type("true", names[5], R.drawable.boy));
        out.add(new Type("true", names[6], R.drawable.burger));
        out.add(new Type("true", names[7], R.drawable.bussiness));
        out.add(new Type("false", names[7], R.drawable.bussiness));
        out.add(new Type("true", names[8], R.drawable.car2));
        out.add(new Type("true", names[9], R.drawable.car_repear));
        out.add(new Type("true", names[10], R.drawable.cinema));
        out.add(new Type("true", names[11], R.drawable.clothes));
        out.add(new Type("true", names[12], R.drawable.coffee));
        out.add(new Type("true", names[13], R.drawable.concert));
        out.add(new Type("true", names[14], R.drawable.couple));
        out.add(new Type("false", names[15], R.drawable.bitcoint));
        out.add(new Type("false", names[16], R.drawable.dividents));
        out.add(new Type("false", names[17], R.drawable.work));
        out.add(new Type("true", names[18], R.drawable.electricity));
        out.add(new Type("true", names[19], R.drawable.first_aid));
        out.add(new Type("true", names[20], R.drawable.food));
        out.add(new Type("true", names[21], R.drawable.fuel));
        out.add(new Type("true", names[22], R.drawable.games));
        out.add(new Type("false", names[22], R.drawable.games));
        out.add(new Type("true", names[23], R.drawable.gift));
        out.add(new Type("false", names[23], R.drawable.gift));
        out.add(new Type("true", names[24], R.drawable.girl));
        out.add(new Type("true", names[25], R.drawable.hairdresser));
        out.add(new Type("true", names[26], R.drawable.holiday));
        out.add(new Type("true", names[27], R.drawable.home));
        out.add(new Type("true", names[28], R.drawable.love));
        out.add(new Type("true", names[29], R.drawable.lunch));
        out.add(new Type("true", names[30], R.drawable.makeup));
        out.add(new Type("true", names[31], R.drawable.music));
        out.add(new Type("true", names[32], R.drawable.pet));
        out.add(new Type("true", names[32], R.drawable.pet2));
        out.add(new Type("true", names[33], R.drawable.phone));
        out.add(new Type("false", names[33], R.drawable.phone));
        out.add(new Type("true", names[35], R.drawable.race));
        out.add(new Type("false", names[35], R.drawable.race));
        out.add(new Type("true", names[36], R.drawable.repear));
        out.add(new Type("false", names[37], R.drawable.self_employment));
        out.add(new Type("false", names[38], R.drawable.salary));
        out.add(new Type("false", names[39], R.drawable.saved_money));
        out.add(new Type("true", names[40], R.drawable.shoes));
        out.add(new Type("true", names[41], R.drawable.shopping2));
        out.add(new Type("true", names[42], R.drawable.skiing));
        out.add(new Type("true", names[43], R.drawable.sport));
        out.add(new Type("true", names[44], R.drawable.theatre));
        out.add(new Type("true", names[45], R.drawable.trip));
        out.add(new Type("true", names[46], R.drawable.tv));
        out.add(new Type("true", names[47], R.drawable.wedding));
        out.add(new Type("true", names[48], R.drawable.weed));

        c.close();
        return Collections.unmodifiableList(out);
    }
}
