package com.example.timetable.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timetable.R;
import com.example.timetable.dao.CourseDao;
import com.example.timetable.Entity.Course;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OptionActivity extends AppCompatActivity {

    private CourseDao courseDao = new CourseDao(this);

    private ListView lvContent;
    private List<Course> courseList;
    private TextView textView;
    private SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        shp = getSharedPreferences("school", MODE_PRIVATE);
        long date = shp.getLong("date", 0);
        String datetime = "Please Select start date!";
        if (date != 0) {
            Date d = new Date(date);
            datetime = "Start Date: " + DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(d);
        }
        lvContent = findViewById(R.id.lvContent);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = courseList.get(position);
                Intent intent = new Intent(OptionActivity.this, UpdateCourseActivity.class);
                intent.putExtra("course", course);
                startActivity(intent);
            }
        });

        textView = findViewById(R.id.tvAlterDate);
        textView.setText(datetime);

    }

    @Override
    protected void onStart() {
        super.onStart();
        show();
    }

    /**
     * show all the courses
     */
    private void show() {
        courseList = courseDao.listAll();
        int listSize = courseList.size();
        if (listSize > 0) {
            String[] courseNames = new String[listSize];
            for (int i = 0; i < listSize; i++) courseNames[i] = courseList.get(i).getCourseName();
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseNames);
            lvContent.setAdapter(arrayAdapter);
        } else {
            Toast.makeText(this, "No course, Please add course! ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Back to the previous activity
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    /**
     * Add a course
     * @param view
     */
    public void addCourse(View view) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_course_item, null);
        new AlertDialog.Builder(this)
                .setView(inflate)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String courseName = ((EditText) inflate.findViewById(R.id.etCourseName)).getText().toString();
                        String teacherName = ((EditText) inflate.findViewById(R.id.etTeacherName)).getText().toString();
                        String startWeek = ((EditText) inflate.findViewById(R.id.etStartWeek)).getText().toString();
                        String endWeek = ((EditText) inflate.findViewById(R.id.etEndWeek)).getText().toString();
                        if ("".equals(courseName) || "".equals(startWeek) || "".equals(endWeek)) {
                            Toast.makeText(OptionActivity.this, "Course name and week cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Course course = new Course();
                            course.setCourseName(courseName);
                            course.setTeacherName(teacherName);
                            course.setStartWeek(Integer.parseInt(startWeek));
                            course.setEndWeek(Integer.parseInt(endWeek));
                            long insert = courseDao.insert(course);
                            if (insert != -1) {
                                Toast.makeText(OptionActivity.this, "Add Course successfully！", Toast.LENGTH_SHORT).show();
                                show();
                            }else{
                                Toast.makeText(OptionActivity.this, "Fail to edit! ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Edit start date of College
     * @param view
     */
    public void alterDate(View view) {
        final SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        final DatePicker datePicker = new DatePicker(this);
        long date = config.getLong("date", 0);
        Date d = new Date(date);
        String datetime = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(d);

        //Set the title of this textview
        TextView text = findViewById(R.id.tvAlterDate);
        text.setText(datetime);
        if(date != 0){
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
            datetime = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(d);
            text.setText(datetime);
        }

        //select the start date
        new DatePickerDialog.Builder(this)
                .setTitle("Select college start date")
                .setView(datePicker)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int dayOfMonth = datePicker.getDayOfMonth();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth, 0, 0, 0);
                        Date time = calendar.getTime();
                        config.edit().putLong("date", time.getTime()).apply();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
