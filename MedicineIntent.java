package com.example.denis.medicine;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.denis.medicine.NewMedicineActivity;
import com.example.denis.medicine.R;
import com.example.denis.medicine.MedicineData;

//Uses Notification, NotificationManager, PendingIntent

public class MedicineIntent extends IntentService {


    private static final String TAG = MedicineIntent.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;


    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, MedicineIntent.class);
        action.setData(uri);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public MedicineIntent() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri uri = intent.getData();

        //Display the notification
        Intent action = new Intent(this, NewMedicineActivity.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        String description = "";
        try {
            if (cursor != null && cursor.moveToFirst()) {
                description = MedicineData.getColumnString(cursor, MedicineData.MedicineSub.NAME);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.title))
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentIntent(operation)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .build();

        manager.notify(NOTIFICATION_ID, note);
    }
}