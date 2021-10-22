package com.example.denis.medicine;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Creates the table, uses SQLite database

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicine.db"; //Database name

    private static final int DATABASE_VERSION = 1; //Database version

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates the table with same column names as in the MedicineData
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ALARM_TABLE =  "CREATE TABLE " + MedicineData.MedicineSub.TABLE_NAME + " ("
                + MedicineData.MedicineSub._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MedicineData.MedicineSub.NAME + " TEXT, "
                + MedicineData.MedicineSub.DATE + " TEXT, "
                + MedicineData.MedicineSub.TIME + " TEXT, "
                + MedicineData.MedicineSub.REPEAT + " TEXT, "
                + MedicineData.MedicineSub.NO_REPEAT + " TEXT, "
                + MedicineData.MedicineSub.REPEAT_TYPE + " TEXT, "
                + MedicineData.MedicineSub.ACTIVE + " TEXT " + " );";

        //Executes the SQL command
        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

