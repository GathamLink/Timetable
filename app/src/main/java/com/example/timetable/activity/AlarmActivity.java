package com.example.timetable.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.timetable.R;

public class AlarmActivity extends AppCompatActivity {

    private TextView title, info;
    private Button back;
    private MediaPlayer mediaPlayer;
    private SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        shp = getSharedPreferences("school", MODE_PRIVATE);
        title = findViewById(R.id.titlemsg);
        info = findViewById(R.id.infomsg);
        back = findViewById(R.id.backbutton);

        String titlemsg = getIntent().getStringExtra("title");
        String infomsg = getIntent().getStringExtra("info");

        title.setText(titlemsg);
        info.setText(infomsg);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int path = shp.getInt("path", R.raw.tracer);
        mediaPlayer = MediaPlayer.create(this, path);
        mediaPlayer.start();

    }
}