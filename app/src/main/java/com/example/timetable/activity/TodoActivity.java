package com.example.timetable.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.Entity.ListItem;
import com.example.timetable.R;
import com.example.timetable.adapter.TodoAdapter;
import com.example.timetable.dao.ListDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class TodoActivity extends AppCompatActivity {

    private LinkedList<String> coming;
    private ListDao listDao;
    private List<ListItem> listItems;
    private ListView cominglist;
    private TextView date;
    private ImageView reload, previous, next;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date datetime = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        reload = findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        previous = findViewById(R.id.previousDate);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusDay();
            }
        });

        next = findViewById(R.id.nextDate);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDay();
            }
        });

        listDao = new ListDao(this);
        cominglist = findViewById(R.id.cominglist);
        int in = 0;
        for (String i: getList()) {
            Log.e("test", in + "");
            in++;
        }
        show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
    }

    private void show() {
        List<ListItem> list = new LinkedList<ListItem>();
        listItems = listDao.listAll();
        int size = listItems.size();
        date = findViewById(R.id.Dateview);
        String date1 = simpleDateFormat.format(datetime);
        date.setText(date1);

        for (ListItem listItem:listItems) {
            if (listItem.getDate().equals(date1)) {
                list.add(listItem);
            }
        }

        if (size > 0) {
            for (ListItem listItem:listItems) {
                Log.e("test545", listItem.getTitle());
            }
            TodoAdapter comingAdapter = new TodoAdapter(TodoActivity.this, list);
            Log.e("TEst4654", comingAdapter.getCount() +"");
            cominglist.setAdapter(comingAdapter);
        } else {
            Toast.makeText(this, "No items, Please add items! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void addListitem(View view) {
        final int id = (int)((Math.random()*9+1)*1000);
        final View inflate = getLayoutInflater().inflate(R.layout.add_list_item, null);
        new AlertDialog.Builder(this)
                .setView(inflate)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = ((EditText) inflate.findViewById(R.id.datetext)).getText().toString();
                        String title = ((EditText) inflate.findViewById(R.id.titletext)).getText().toString();
                        String info = ((EditText) inflate.findViewById(R.id.infotext)).getText().toString();
                        String time = ((EditText) inflate.findViewById(R.id.timetext)).getText().toString();

                        try {
                            Date date1 = sdf.parse(date +  " " + time);
                            if (date1.getTime() < datetime.getTime()) {
                                Toast.makeText(TodoActivity.this, "Cannot add a item before now", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (date.equals("") || title.equals("") || info.equals("") || time.equals("")){
                            Toast.makeText(TodoActivity.this, "Blanks can not be empty !", Toast.LENGTH_SHORT).show();
                        } else {
                            ListItem listItem = new ListItem(id, title, date, time, info);
                            long count = listDao.insert(listItem);
                            if (count != -1) {
                                Toast.makeText(TodoActivity.this, "Add item successfully！", Toast.LENGTH_SHORT).show();
                                show();
                            } else {
                                Toast.makeText(TodoActivity.this, "Fail to add! ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void back(View view) {
        this.finish();
    }

    private void addDay() {
        Date datetest = new Date(datetime.getTime() +  1 * 24 * 60 * 60 * 1000);
        datetime = datetest;
        show();
    }

    private void minusDay() {
        Date datetest = new Date(datetime.getTime() -  1 * 24 * 60 * 60 * 1000);
        datetime = datetest;
        show();
    }

    private LinkedList<String> getList() {
        LinkedList<String> list = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add("Hello world");
        }
        return list;
    }

}