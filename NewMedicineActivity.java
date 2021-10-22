package com.example.denis.medicine;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.denis.medicine.MedicineData;
import com.example.denis.medicine.MedicineSchedule;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
/*
For adding a new medicine or updating already existing medicine
Implements the TimePickerDialog for choosing a time
Implements the DatePickerDialog for choosing a date and the LoaderManager
*/

public class NewMedicineActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int loadMedicine = 0;

    //All of the values when editing the medicine
    private Toolbar newToolbar;
    private EditText newTitleText;
    private TextView newDateText, newTimeText, newRepeatText, newRepeatNoText, newRepeatTypeText;
    private FloatingActionButton newFAB1;
    private FloatingActionButton newFAB2;
    private Calendar newCalendar;
    private int newYear, newMonth, newHour, newMinute, newDay;
    private long newRepeatTime;
    private Switch newRepeatSwitch;
    private String newTitle;
    private String newTime;
    private String newDate;
    private String newRepeat;
    private String newRepeatNo;
    private String newRepeatType;
    private String newActive;

    private Uri newMedicineUri; //Distinguish if the the data was already saved in the database
    private boolean updatedMedicine = false;

    // Values for orientation change
    private static final String NAME = "name";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String REPEAT = "repeat";
    private static final String NO_REPEAT = "no_repeat";
    private static final String REPEAT_TYPE = "repeat_type";
    private static final String ACTIVE = "active";


    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            updatedMedicine = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medicine);

        Intent intent = getIntent();
        newMedicineUri = intent.getData(); //Gets the correct Uri

        //Sets the title if the uri is null, values are not filled
        if (newMedicineUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_reminder));

            invalidateOptionsMenu();
         //Sets the title if the uri is not empty
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_reminder));

            //Editing mode, gets the data, which was in the medicine before
            getLoaderManager().initLoader(loadMedicine, null, this);
        }


        // Initialize Views
        newToolbar = (Toolbar) findViewById(R.id.toolbar);
        newTitleText = (EditText) findViewById(R.id.reminder_title);
        newDateText = (TextView) findViewById(R.id.set_date);
        newTimeText = (TextView) findViewById(R.id.set_time);
        newRepeatText = (TextView) findViewById(R.id.set_repeat);
        newRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        newRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        newRepeatSwitch = (Switch) findViewById(R.id.repeat_switch);
        newFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        newFAB2 = (FloatingActionButton) findViewById(R.id.starred2);

        //Initialize default values
        newActive = "true";
        newRepeat = "true";
        newRepeatNo = Integer.toString(1);
        newRepeatType = "Hour";

        //Initialize the calander
        newCalendar = Calendar.getInstance();
        newHour = newCalendar.get(Calendar.HOUR_OF_DAY); //Gets the hour
        newMinute = newCalendar.get(Calendar.MINUTE); //Gets the minute
        newYear = newCalendar.get(Calendar.YEAR); //Gets the year
        newMonth = newCalendar.get(Calendar.MONTH) + 1; //Gets the month
        newDay = newCalendar.get(Calendar.DATE); //Gets the day

        //Converts into one string
        newDate = newDay + "/" + newMonth + "/" + newYear;
        newTime = newHour + ":" + newMinute;

        //Set for changing text
        newTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newTitle = s.toString().trim();
                newTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set TextViews
        newDateText.setText(newDate);
        newTimeText.setText(newTime);
        newRepeatNoText.setText(newRepeatNo); //Number of repeats
        newRepeatTypeText.setText(newRepeatType);
        newRepeatText.setText("Every " + newRepeatNo + " " + newRepeatType + "(s)");

        //Save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(NAME);
            newTitleText.setText(savedTitle);
            newTitle = savedTitle;

            String savedTime = savedInstanceState.getString(TIME);
            newTimeText.setText(savedTime);
            newTime = savedTime;

            String savedDate = savedInstanceState.getString(DATE);
            newDateText.setText(savedDate);
            newDate = savedDate;

            String saveRepeat = savedInstanceState.getString(REPEAT);
            newRepeatText.setText(saveRepeat);
            newRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(NO_REPEAT);
            newRepeatNoText.setText(savedRepeatNo);
            newRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(REPEAT_TYPE);
            newRepeatTypeText.setText(savedRepeatType);
            newRepeatType = savedRepeatType;

            newActive = savedInstanceState.getString(ACTIVE);
        }

        //Set up active buttons, bell icon
        if (newActive.equals("false")) {
            newFAB1.setVisibility(View.VISIBLE);
            newFAB2.setVisibility(View.GONE);

        } else if (newActive.equals("true")) {
            newFAB1.setVisibility(View.GONE);
            newFAB2.setVisibility(View.VISIBLE);
        }

        setSupportActionBar(newToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(NAME, newTitleText.getText());
        outState.putCharSequence(TIME, newTimeText.getText());
        outState.putCharSequence(DATE, newDateText.getText());
        outState.putCharSequence(REPEAT, newRepeatText.getText());
        outState.putCharSequence(NO_REPEAT, newRepeatNoText.getText());
        outState.putCharSequence(REPEAT_TYPE, newRepeatTypeText.getText());
        outState.putCharSequence(ACTIVE, newActive);
    }

    //On clicking Time picker
    public void setTime(View v){
        if(newMedicineUri == null){
            Toast.makeText(this, "Click to set the time alarm", Toast.LENGTH_LONG).show();
            return;
        }
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    //On clicking Date picker
    public void setDate(View v){
        if(newMedicineUri == null){
            Toast.makeText(this, "Click to set date alarm", Toast.LENGTH_LONG).show();
            return;
        }
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    //Obtain time from time picker
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        newHour = hourOfDay;
        newMinute = minute;
        if (minute < 10) {
            newTime = hourOfDay + ":" + "0" + minute;
        } else {
            newTime = hourOfDay + ":" + minute;
        }
        newTimeText.setText(newTime);
    }

    //Obtain date from date picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        newDay = dayOfMonth;
        newMonth = monthOfYear;
        newYear = year;
        newDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        newDateText.setText(newDate);
    }

    //On clicking the active button
    public void selectFab1(View v) {
        newFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        newFAB1.setVisibility(View.GONE);
        newFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        newFAB2.setVisibility(View.VISIBLE);
        newActive = "true";
    }

    //On clicking the inactive button
    public void selectFab2(View v) {
        newFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        newFAB2.setVisibility(View.GONE);
        newFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        newFAB1.setVisibility(View.VISIBLE);
        newActive = "false";
    }

    //On clicking the repeat switch
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            newRepeat = "true";
            newRepeatText.setText("Every " + newRepeatNo + " " + newRepeatType + "(s)");
        } else {
            newRepeat = "false";
            newRepeatText.setText(R.string.repeat_off);
        }
    }

    //On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        //Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                newRepeatType = items[item];
                newRepeatTypeText.setText(newRepeatType);
                newRepeatText.setText("Every " + newRepeatNo + " " + newRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //On clicking repeat interval button
    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        //Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            newRepeatNo = Integer.toString(1);
                            newRepeatNoText.setText(newRepeatNo);
                            newRepeatText.setText("Every " + newRepeatNo + " " + newRepeatType + "(s)");
                        }
                        else {
                            newRepeatNo = input.getText().toString().trim();
                            newRepeatNoText.setText(newRepeatNo);
                            newRepeatText.setText("Every " + newRepeatNo + " " + newRepeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    //For updating menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //if empty sets visibility to null
        if (newMedicineUri == null) {
            MenuItem menuItem = menu.findItem(R.id.discard_reminder);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_reminder:

                //To save the medicine a title is mandatory
                if (newTitleText.getText().toString().length() == 0){
                    newTitleText.setError("Title cannot be empty!");
                }

                else {
                    saveReminder();
                    finish();
                }
                return true;

             //When clicked on delete medicine
            case R.id.discard_reminder:
                //Show a dialog when the medicine is about to be deleted
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                //Go to mainactivity if no update was made
                if (!updatedMedicine) {
                    NavUtils.navigateUpFromSameTask(NewMedicineActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(NewMedicineActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Show a dialog when user decides to enter mainacitivty without saving
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Show a dialog of conformation when deleteing a medicin
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteReminder();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //If the user decides to delete an existing medicine
    private void deleteReminder() {

        if (newMedicineUri != null) {

            int rowsDeleted = getContentResolver().delete(newMedicineUri, null, null);


            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_reminder_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_reminder_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }


        finish();
    }

    //Save all of the values
    public void saveReminder(){

        ContentValues values = new ContentValues();

        //Database
        values.put(MedicineData.MedicineSub.NAME, newTitle);
        values.put(MedicineData.MedicineSub.DATE, newDate);
        values.put(MedicineData.MedicineSub.TIME, newTime);
        values.put(MedicineData.MedicineSub.REPEAT, newRepeat);
        values.put(MedicineData.MedicineSub.NO_REPEAT, newRepeatNo);
        values.put(MedicineData.MedicineSub.REPEAT_TYPE, newRepeatType);
        values.put(MedicineData.MedicineSub.ACTIVE, newActive);

        //Calender
        newCalendar.set(Calendar.MONTH, --newMonth);
        newCalendar.set(Calendar.YEAR, newYear);
        newCalendar.set(Calendar.DAY_OF_MONTH, newDay);
        newCalendar.set(Calendar.HOUR_OF_DAY, newHour);
        newCalendar.set(Calendar.MINUTE, newMinute);
        newCalendar.set(Calendar.SECOND, 0);

        //Converting calender object
        long selectedTimestamp =  newCalendar.getTimeInMillis();


        if (newRepeatType.equals("Minute")) {
            newRepeatTime = Integer.parseInt(newRepeatNo) * milMinute;
        } else if (newRepeatType.equals("Hour")) {
            newRepeatTime = Integer.parseInt(newRepeatNo) * milHour;
        } else if (newRepeatType.equals("Day")) {
            newRepeatTime = Integer.parseInt(newRepeatNo) * milDay;
        } else if (newRepeatType.equals("Week")) {
            newRepeatTime = Integer.parseInt(newRepeatNo) * milWeek;
        } else if (newRepeatType.equals("Month")) {
            newRepeatTime = Integer.parseInt(newRepeatNo) * milMonth;
        }

        if (newMedicineUri == null) {
            //New medicine, insert it into the list
            Uri newUri = getContentResolver().insert(MedicineData.MedicineSub.CONTENT_URI, values);

            //Display information whether the save was successful or not
            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_reminder_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_reminder_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(newMedicineUri, values, null, null);


            if (rowsAffected == 0) {

                Toast.makeText(this, getString(R.string.editor_update_reminder_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_update_reminder_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        //Create notification
        if (newActive.equals("true")) {
            if (newRepeat.equals("true")) {
                new MedicineSchedule().setRepeatAlarm(getApplicationContext(), selectedTimestamp, newMedicineUri, newRepeatTime);
            } else if (newRepeat.equals("false")) {
                new MedicineSchedule().setAlarm(getApplicationContext(), selectedTimestamp, newMedicineUri);
            }

            Toast.makeText(this, "Alarm time is " + selectedTimestamp,
                    Toast.LENGTH_LONG).show();
        }

        Toast.makeText(getApplicationContext(), "Saved",
                Toast.LENGTH_SHORT).show();

    }

    //The return (back) button clicked
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


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
                MedicineData.MedicineSub.ACTIVE,
        };


        return new CursorLoader(this,
                newMedicineUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //Moving the cursor and reading data
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.NAME);
            int dateColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.DATE);
            int timeColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.TIME);
            int repeatColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.REPEAT);
            int repeatNoColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.NO_REPEAT);
            int repeatTypeColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.REPEAT_TYPE);
            int activeColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.ACTIVE);

            //Extract the data from the cursor
            String title = cursor.getString(titleColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            String repeat = cursor.getString(repeatColumnIndex);
            String repeatNo = cursor.getString(repeatNoColumnIndex);
            String repeatType = cursor.getString(repeatTypeColumnIndex);
            String active = cursor.getString(activeColumnIndex);

            //Update the screen with the new values
            newTitleText.setText(title);
            newDateText.setText(date);
            newTimeText.setText(time);
            newRepeatNoText.setText(repeatNo);
            newRepeatTypeText.setText(repeatType);
            newRepeatText.setText("Every " + repeatNo + " " + repeatType + "(s)");

            if (repeat == null){
                newRepeatSwitch.setChecked(false);
                newRepeatText.setText(R.string.repeat_off);
            }
            else if (repeat.equals("false")) {
                newRepeatSwitch.setChecked(false);
                newRepeatText.setText(R.string.repeat_off);

            } else if (repeat.equals("true")) {
                newRepeatSwitch.setChecked(true);
            }

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
