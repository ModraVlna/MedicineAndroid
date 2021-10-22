package com.example.denis.medicine;

import android.app.AlarmManager;
import android.content.Context;

//Uses the AlarmManager

public class MedicineManager {

    private static final String TAG = MedicineManager.class.getSimpleName();
    private static AlarmManager newAlarmManager;
    public static synchronized void injectAlarmManager(AlarmManager alarmManager) {
        if (newAlarmManager != null) {
            throw new IllegalStateException("Manager already exists");
        }
        newAlarmManager = alarmManager;
    }
    //Alarm service, which needs to be registered in the manifest
    /*package*/ static synchronized AlarmManager getAlarmManager(Context context) {
        if (newAlarmManager == null) {
            newAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return newAlarmManager;
    }
}
