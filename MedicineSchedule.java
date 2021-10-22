package com.example.denis.medicine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

//Schedule a ring at a certain time, which is given from the medicine
//Uses AlarmManager, PendingIntent
public class MedicineSchedule {

    public void setAlarm(Context context, long alarmTime, Uri reminderTask) {
        AlarmManager manager = MedicineManager.getAlarmManager(context);


        PendingIntent operation =
                MedicineIntent.getReminderPendingIntent(context, reminderTask);

        //Updated notifications in diffrent versions of SDK
        if (Build.VERSION.SDK_INT >= 23) {

            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else if (Build.VERSION.SDK_INT >= 19) {

            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else {

            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        }

    }

    //Repeating the alarm at a certain time
    public void setRepeatAlarm(Context context, long alarmTime, Uri reminderTask, long RepeatTime) {
        AlarmManager manager = MedicineManager.getAlarmManager(context);

        PendingIntent operation =
                MedicineIntent.getReminderPendingIntent(context, reminderTask);

        manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, RepeatTime, operation);


    }

    //Disabling the alarm
    public void cancelAlarm(Context context, Uri reminderTask) {
        AlarmManager manager = MedicineManager.getAlarmManager(context);


        PendingIntent operation =
                MedicineIntent.getReminderPendingIntent(context, reminderTask);

        manager.cancel(operation);


    }
}
