package com.example.timetable.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timetable.R;
import com.example.timetable.dao.CourseDao;
import com.example.timetable.Entity.Course;
import com.example.timetable.dao.ListDao;
import com.example.timetable.view.TimeTableView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {

    private CourseDao courseDao = new CourseDao(this);
    private ListDao listDao = new ListDao(this);
    private TimeTableView timeTable;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("school", MODE_PRIVATE);
        timeTable = findViewById(R.id.timeTable);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }


    private List<Course> acquireData() {
        List<Course> courses = new ArrayList<>();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        if (sp.getBoolean("isFirstUse", true)) { //the firsttime to user
            sp.edit().putBoolean("isFirstUse", false).apply();
        }else {
            courses = courseDao.listAll();
        }
        return courses;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences("school", MODE_PRIVATE);
        long date = sp.getLong("date", new Date().getTime());
        timeTable.loadData(acquireData(), new Date(date));
    }
}
