package com.example.timetable.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("title", intent.getStringExtra("title"));
        i.putExtra("info", intent.getStringExtra("info"));
        i.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(context, "12345645", Toast.LENGTH_SHORT).show();
        context.startActivity(i);

    }
}
