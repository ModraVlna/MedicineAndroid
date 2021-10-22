package com.example.denis.medicine;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.medicine.MedicineData;
import com.example.denis.medicine.DatabaseHelper;


//Uses LoaderManager - manages longer-running operations
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FloatingActionButton newAddReminderButton;
    private Toolbar newToolbar;
    MedicineAdapter newCursorAdapter;
    DatabaseHelper newDbHelper = new DatabaseHelper(this);
    ListView reminderListView;
    ProgressDialog prgDialog;
    TextView reminderText;

    private String medicineTitle = "";

    private static final int medicineLoader = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        newToolbar = (Toolbar) findViewById(R.id.toolbar);//Initialize toolbar
        setSupportActionBar(newToolbar); //Set actionBar
        newToolbar.setTitle(R.string.app_name);

        reminderListView = (ListView) findViewById(R.id.list);
        reminderText = (TextView) findViewById(R.id.reminderText);


        View emptyView = findViewById(R.id.empty_view);
        reminderListView.setEmptyView(emptyView); //Set empty view

        newCursorAdapter = new MedicineAdapter(this, null); //Binds data
        reminderListView.setAdapter(newCursorAdapter);

        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //Each click triggers the intent
                Intent intent = new Intent(MainActivity.this, NewMedicineActivity.class);

                //Calls to content URI and ID
                Uri currentVehicleUri = ContentUris.withAppendedId(MedicineData.MedicineSub.CONTENT_URI, id);

                intent.setData(currentVehicleUri);

                startActivity(intent);

            }
        });

        //set floatingActionButton
        newAddReminderButton = (FloatingActionButton) findViewById(R.id.fab);

        newAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addReminderTitle();
            }
        });

        getSupportLoaderManager().initLoader(medicineLoader, null, this);


    }

    //Display all of the columns to the list
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                MedicineData.MedicineSub._ID,
                MedicineData.MedicineSub.NAME,
                MedicineData.MedicineSub.DATE,
                MedicineData.MedicineSub.TIME,
                MedicineData.MedicineSub.REPEAT,
                MedicineData.MedicineSub.NO_REPEAT,
                MedicineData.MedicineSub.REPEAT_TYPE,
                MedicineData.MedicineSub.ACTIVE

        };

        return new CursorLoader(this,   // Parent activity context
                MedicineData.MedicineSub.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        newCursorAdapter.swapCursor(cursor);
        if (cursor.getCount() > 0){
            reminderText.setVisibility(View.VISIBLE);
        }else{
            reminderText.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        newCursorAdapter.swapCursor(null);

    }

    public void addReminderTitle(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().isEmpty()){
                    return;
                }

                medicineTitle = input.getText().toString();
                ContentValues values = new ContentValues();

                values.put(MedicineData.MedicineSub.NAME, medicineTitle);

                Uri newUri = getContentResolver().insert(MedicineData.MedicineSub.CONTENT_URI, values);

                restartLoader();


                if (newUri == null) {
                    Toast.makeText(getApplicationContext(), "Could not set title", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Title set", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void restartLoader(){
        getSupportLoaderManager().restartLoader(medicineLoader, null, this);
    }
}
