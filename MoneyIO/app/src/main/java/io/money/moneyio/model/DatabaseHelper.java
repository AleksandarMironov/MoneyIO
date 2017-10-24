package io.money.moneyio.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import io.money.moneyio.R;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "MoneyIO.db";
    private static int DATABASE_VERSION = 1;

    private static final String TABLE_SETINGS = "user_setings";

    private static final String T_SETINGS_COL_1 = "user";
    private static final String T_SETINGS_COL_2 = "category";
    private static final String T_SETINGS_COL_3 = "type";
    private static final String T_SETINGS_COL_4 = "imageid";

    private static final String USERS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_SETINGS +
            " (" + T_SETINGS_COL_1 + " TEXT, " +
            T_SETINGS_COL_2 + " TEXT, " +
            T_SETINGS_COL_3 + " TEXT, " +
            T_SETINGS_COL_4 + " INTEGER)";


    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
            //instance.create();
        }
        return instance;
    }

    //////////////////////////
    //public void create(){
    // SQLiteDatabase db = this.getWritableDatabase(); ///need this to create base ?! stupid!
    // }
    //////////////////////////

    public boolean addType(String user, String category, String type, Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_SETINGS_COL_1, user);
        contentValues.put(T_SETINGS_COL_2, category);
        contentValues.put(T_SETINGS_COL_3, type);
        contentValues.put(T_SETINGS_COL_4, id);

        long b = db.insert(TABLE_SETINGS, null, contentValues);
        return (b != -1);
    }

    public void deleteType(String userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(USERS_TABLE_CREATE, "user=?", new String[] {userID});
    }

    public ArrayList<Type> getUserTypes(String userID){
        SQLiteDatabase db = this.getReadableDatabase();
        String myRawQuery = "SELECT " + T_SETINGS_COL_2  + ", " + T_SETINGS_COL_3  + ", " + T_SETINGS_COL_4
                + " FROM " + TABLE_SETINGS + " WHERE " + T_SETINGS_COL_1 + " = \"" + userID + "\";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<Type> out = new ArrayList<>();
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

        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new Type(c.getString(0), c.getString(1), c.getInt(2)));
        }
        c.close();
        return out;
    }
}
