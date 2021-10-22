package com.example.denis.medicine;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

//Sets the columns that will be stored in the database
public class MedicineData {

    private MedicineData() {}

    public static final String PACKAGE_NAME = "com.example.denis.medicine";

    public static final Uri MAIN_URI = Uri.parse("content://" + PACKAGE_NAME);

    public static final String SECOND_URI = "medicinepath"; //Path to the table

    //Sub class that implements the columns
    public static final class MedicineSub implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(MAIN_URI, SECOND_URI);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PACKAGE_NAME + "/" + SECOND_URI;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PACKAGE_NAME + "/" + SECOND_URI;

        public final static String TABLE_NAME = "med"; //Name of the entire table

        public final static String _ID = BaseColumns._ID;

        public static final String NAME = "name";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String REPEAT = "repeat";
        public static final String NO_REPEAT = "no_repeat";
        public static final String REPEAT_TYPE = "repeat_type";
        public static final String ACTIVE = "active";

    }

    //Returns the column in a string format
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }
}
