package com.example.denis.medicine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.denis.medicine.MedicineData;

import java.util.Set;

//Uses the CursorAdapter
public class MedicineAdapter extends CursorAdapter {

    private TextView newTitleText, newDateAndTimeText, newRepeatInfoText;
    private ImageView newActiveImage , newThumbnailImage;
    private ColorGenerator newColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable newDrawableBuilder;

    public MedicineAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        newTitleText = (TextView) view.findViewById(R.id.recycle_title);
        newDateAndTimeText = (TextView) view.findViewById(R.id.recycle_date_time);
        newRepeatInfoText = (TextView) view.findViewById(R.id.recycle_repeat_info);
        newActiveImage = (ImageView) view.findViewById(R.id.active_image);
        newThumbnailImage = (ImageView) view.findViewById(R.id.thumbnail_image);

        int titleColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.NAME);
        int dateColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.DATE);
        int timeColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.TIME);
        int repeatColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.REPEAT);
        int repeatNoColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.NO_REPEAT);
        int repeatTypeColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.REPEAT_TYPE);
        int activeColumnIndex = cursor.getColumnIndex(MedicineData.MedicineSub.ACTIVE);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String repeat = cursor.getString(repeatColumnIndex);
        String repeatNo = cursor.getString(repeatNoColumnIndex);
        String repeatType = cursor.getString(repeatTypeColumnIndex);
        String active = cursor.getString(activeColumnIndex);

        setReminderTitle(title);

        if (date != null){
            String dateTime = date + " " + time;
            setReminderDateTime(dateTime);
        }else{
            newDateAndTimeText.setText("Date not set");
        }

        if(repeat != null){
            setReminderRepeatInfo(repeat, repeatNo, repeatType);
        }else{
            newRepeatInfoText.setText("Repeat Not Set");
        }

        if (active != null){
            setActiveImage(active);
        }else{
            newActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
        }


    }


    //Set the title view
    public void setReminderTitle(String title) {
        newTitleText.setText(title);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = newColorGenerator.getRandomColor();

        //Creates an icon with with a the letter of the medicine name and chooses a random background color
        newDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        newThumbnailImage.setImageDrawable(newDrawableBuilder);
    }

    //Set the date and time view
    public void setReminderDateTime(String datetime) {
        newDateAndTimeText.setText(datetime);
    }

    //Set repeat
    public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            newRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            newRepeatInfoText.setText("Repeat Off");
        }
    }

    //Set notification icon
    public void setActiveImage(String active){
        if(active.equals("true")){
            newActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        }else if (active.equals("false")) {
            newActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
        }

    }
}