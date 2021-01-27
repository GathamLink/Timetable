package com.example.timetable.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.Entity.ListItem;
import com.example.timetable.R;
import com.example.timetable.dao.ListDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class detailedListActivity extends AppCompatActivity {

    private TextView title, date, time, info, delete, alarm;
    private ListItem listItem;
    private ListDao listDao;
    private int id;
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_list);

        final Date now = new Date(System.currentTimeMillis());
        final String nowstr = sdfDate.format(now);

        listDao = new ListDao(this);
        title = findViewById(R.id.Titledetailed);
        date = findViewById(R.id.datedetailed);
        time = findViewById(R.id.timedetailed);
        info = findViewById(R.id.infodetailed);
        delete = findViewById(R.id.delete);
        alarm = findViewById(R.id.alarm);

        listItem = (ListItem) getIntent().getSerializableExtra("listItem");
        title.setText(listItem.getTitle());
        date.setText(listItem.getDate());
        time.setText(listItem.getTime());
        info.setText(listItem.getInfo());

        id = listItem.getId();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nowstr.equals(listItem.getDate())) {
                    addAlarm();
                } else {
                    Toast.makeText(detailedListActivity.this, "Not today !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void back(View view) {
        this.finish();
    }

    private void deleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delete = listDao.deleteItem(id);
                        if (delete > 0) {
                            Toast.makeText(detailedListActivity.this, "Delete successfully! ", Toast.LENGTH_SHORT).show();
                            detailedListActivity.this.finish();
                        } else {
                            Toast.makeText(detailedListActivity.this, "Fail to delete! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void addAlarm() {
        new AlertDialog.Builder(this)
                .setTitle("Set Alarm")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        title = findViewById(R.id.Titledetailed);
                        info = findViewById(R.id.infodetailed);

                        String[] datetime = listItem.getTime().split(":");

                        Intent intent = new Intent(detailedListActivity.this, AlarmReceiver.class);
                        intent.putExtra("title", title.getText());
                        intent.putExtra("info", info.getText());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(detailedListActivity.this, Integer.parseInt(datetime[0])+Integer.parseInt(datetime[1]), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(datetime[0]));
                        Toast.makeText(detailedListActivity.this, datetime[0], Toast.LENGTH_SHORT).show();
                        calendar.set(Calendar.MINUTE, Integer.parseInt(datetime[1]));
                        Toast.makeText(detailedListActivity.this, datetime[1], Toast.LENGTH_SHORT).show();
                        calendar.set(Calendar.SECOND, 0);
                        Toast.makeText(detailedListActivity.this, calendar.getTimeInMillis() + "", Toast.LENGTH_SHORT).show();
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Toast.makeText(detailedListActivity.this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("cancel", null)
                .create()
                .show();
    }
}