package com.example.denis.medicine;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


//Accesses data from the DatabaseHelper, uses ContentProvider

public class DatabaseOperations extends ContentProvider {

    private DatabaseHelper dbObj; //An object of class DatabaseHelper

    private static final int MEDS = 100;

    private static final int MED = 101;

    public static final String LOG_TAG = DatabaseOperations.class.getSimpleName();

    private static final UriMatcher UriMatch = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        UriMatch.addURI(MedicineData.PACKAGE_NAME, MedicineData.SECOND_URI, MEDS);

        UriMatch.addURI(MedicineData.PACKAGE_NAME, MedicineData.SECOND_URI + "/#", MED);

    }


    @Override
    public boolean onCreate() {
        dbObj = new DatabaseHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = dbObj.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor = null;

        int match = UriMatch.match(uri);
        switch (match) {
            case MEDS:
                cursor = database.query(MedicineData.MedicineSub.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MED:
                selection = MedicineData.MedicineSub._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(MedicineData.MedicineSub.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI not found" + uri);
        }

        //Displays when the query is done
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = UriMatch.match(uri);
        switch (match) {
            case MEDS:
                return MedicineData.MedicineSub.CONTENT_LIST_TYPE;
            case MED:
                return MedicineData.MedicineSub.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("URI not found" + uri + " with " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = UriMatch.match(uri);
        switch (match) {
            case MEDS:
                return insertReminder(uri, contentValues);

            default:
                throw new IllegalArgumentException("Could not insert " + uri);
        }
    }


    //Inserts into the database the table and its colums with values
    private Uri insertReminder(Uri uri, ContentValues values) {

        SQLiteDatabase database = dbObj.getWritableDatabase();

        long id = database.insert(MedicineData.MedicineSub.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Could not insert row " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }



    //Deletes the medicine from the list
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = dbObj.getWritableDatabase();

        int rowsDeleted;

        final int match = UriMatch.match(uri);
        switch (match) {
            case MEDS:
                rowsDeleted = database.delete(MedicineData.MedicineSub.TABLE_NAME, selection, selectionArgs);
                break;
            case MED:
                selection = MedicineData.MedicineSub._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(MedicineData.MedicineSub.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Could not delete " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }



    //Updates the data within the medicine
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = UriMatch.match(uri);
        switch (match) {
            case MEDS:
                return updateReminder(uri, contentValues, selection, selectionArgs);
            case MED:
                selection = MedicineData.MedicineSub._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateReminder(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Could not update " + uri);
        }
    }

    private int updateReminder(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = dbObj.getWritableDatabase();

        int rowsUpdated = database.update(MedicineData.MedicineSub.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

}
