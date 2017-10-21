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
            instance = new DatabaseHelper(context);
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

    public ArrayList<Type> getUserTypes(String userID){
        SQLiteDatabase db = this.getReadableDatabase();
        String myRawQuery = "SELECT " + T_SETINGS_COL_2  + ", " + T_SETINGS_COL_3  + ", " + T_SETINGS_COL_4
                + " FROM " + TABLE_SETINGS + " WHERE " + T_SETINGS_COL_1 + " = \"" + userID + "\";";
        Cursor c = db.rawQuery(myRawQuery, null);
        c.moveToFirst();
        ArrayList<Type> out = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++){
            c.moveToPosition(i);
            out.add(new Type(c.getString(0), c.getString(1), c.getInt(2)));
            out.add(new Type("TRUE", "type1" + i, R.drawable.sandwich));
            out.add(new Type("FALSE", "type2" + i, R.drawable.sandwich));
            out.add(new Type("TRUE", "type3" + i , R.drawable.sandwich));
            out.add(new Type("TRUE", "type1" + i, R.drawable.sandwich));
            out.add(new Type("FALSE", "type2" + i, R.drawable.sandwich));
            out.add(new Type("TRUE", "type3" + i , R.drawable.sandwich));
            out.add(new Type("TRUE", "type1" + i, R.drawable.sandwich));
            out.add(new Type("FALSE", "type2" + i, R.drawable.sandwich));
            out.add(new Type("TRUE", "type3" + i , R.drawable.sandwich));
            out.add(new Type("TRUE", "type1" + i, R.drawable.sandwich));
            out.add(new Type("FALSE", "type2" + i, R.drawable.sandwich));
            out.add(new Type("TRUE", "type3" + i , R.drawable.sandwich));
            out.add(new Type("TRUE", "type1" + i, R.drawable.sandwich));
            out.add(new Type("FALSE", "type2" + i, R.drawable.sandwich));
            out.add(new Type("TRUE", "type3" + i , R.drawable.sandwich));
        }
        c.close();
        return out;
    }
}
