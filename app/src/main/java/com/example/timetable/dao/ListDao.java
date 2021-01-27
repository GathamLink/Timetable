package com.example.timetable.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.timetable.Entity.Course;
import com.example.timetable.Entity.ListItem;

import java.util.ArrayList;
import java.util.List;

public class ListDao extends SQLiteOpenHelper {

    private String table = "todolist";

    public ListDao(Context context) {
        super(context, "todolist.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table todolist(" +
                "id integer primary key autoincrement," +
                "title varchar(100)," +
                "date DATE," +
                "time TIME," +
                "info varchar(800))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insert(ListItem listItem) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(listItem.getTitle())) {
            values.put("title", listItem.getTitle());
        }
        if (!TextUtils.isEmpty(listItem.getDate())) {
            values.put("date", listItem.getDate());
        }
        if (!TextUtils.isEmpty(listItem.getTime())) {
            values.put("time", listItem.getTime());
        }
        if (!TextUtils.isEmpty(listItem.getInfo())) {
            values.put("info", listItem.getInfo());
        }
        long count = database.insert(table, null, values);
        database.close();
        return count;
    }

    public int deleteItem(int id) {
        SQLiteDatabase database = getWritableDatabase();
        int count = database.delete(table, "id=?", new String[]{String.valueOf(id)});
        database.close();
        return count;
    }

    public List<ListItem> listAll() {
        List<ListItem> list = new ArrayList<>();
        SQLiteDatabase database = getWritableDatabase();
        Cursor data = database.query(table, null, null, null, null, null, "date desc, time desc");
        if (data.getCount() > 0) {
            while (data.moveToNext()) {
                int id = data.getInt(0);
                String title = data.getString(1);
                String date = data.getString(2);
                String time = data.getString(3);
                String info = data.getString(4);
                ListItem listItem = new ListItem(id, title, date, time, info);
                list.add(listItem);
            }
        }
        database.close();
        return list;
    }

}
