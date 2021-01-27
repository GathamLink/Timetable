package com.example.timetable.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences ;
    EditText school;
    TextView startDate;
    Spinner music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        school = findViewById(R.id.schoolName);
        startDate = findViewById(R.id.startDate);
        music = findViewById(R.id.music);
        music.setSelection(sharedPreferences.getInt("music", 0));

        long date = sharedPreferences.getLong("date", 0);
        String datetime= "";
        if (date != 0) {
            Date d = new Date(date);
            datetime = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(d);
        }else {
            datetime = "Please Select Start Date";
        }


        final SharedPreferences.Editor editor = sharedPreferences.edit();
        school.setText(sharedPreferences.getString("school", ""));
        startDate.setText(datetime);

        school.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("school", school.getText().toString());
                editor.apply();
            }
        });

        music.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("music", position);
                String item = music.getSelectedItem().toString();
                int path = 0;
                if (item.equals("Ana")) {
                    path = R.raw.ana;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else if (item.equals("Bastion")) {
                    path = R.raw.bastion;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else if (item.equals("Brigitte")) {
                    path = R.raw.brigitte;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else if (item.equals("Doomfits")) {
                    path = R.raw.doomfits;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else if (item.equals("Phara")) {
                    path = R.raw.phara;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else if (item.equals("Soilder")) {
                    path = R.raw.soilder;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else if (item.equals("Tracer")) {
                    path = R.raw.tracer;
                    editor.putInt("path", path);
                    Toast.makeText(SettingActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void back(View view) {
        this.finish();
    }

    public void addDate(View view) {
        sharedPreferences = getSharedPreferences("school", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final DatePicker datePicker = new DatePicker(this);

        new AlertDialog.Builder(this)
                .setView(datePicker)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, 0, 0, 0);
                        Date time = calendar.getTime();
                        editor.putLong("date", time.getTime()).apply();
                        long date = sharedPreferences.getLong("date", 0);
                        Date d = new Date(date);
                        String datetime = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(d);
                        startDate = findViewById(R.id.startDate);
                        startDate.setText(datetime);
                    }
                })
                .setNegativeButton("Cancle", null)
                .create()
                .show();
    }
}