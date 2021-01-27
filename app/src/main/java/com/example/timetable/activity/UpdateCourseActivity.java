package com.example.timetable.activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timetable.R;
import com.example.timetable.adapter.LessonAdapter;
import com.example.timetable.dao.CourseDao;
import com.example.timetable.Entity.Course;

import java.util.Iterator;
import java.util.List;

public class UpdateCourseActivity extends AppCompatActivity {

    private CourseDao courseDao = new CourseDao(this);
    private ListView mListView;
    private Course course;
    private SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_course);
        mListView = findViewById(R.id.lvLesson);

        course = (Course) getIntent().getSerializableExtra("course");
        showLessonInfo();
        showCourseInfo();
    }

    /**
     * show the information of course
     */
    private void showCourseInfo() {
//        ((TextView) findViewById(R.id.tvCourseName)).setText(course.getCourseName());
        ((EditText) findViewById(R.id.etCourseName)).setText(course.getCourseName());
        ((EditText) findViewById(R.id.etTeacherName)).setText(course.getTeacherName());
        ((EditText) findViewById(R.id.etStartWeek)).setText(String.valueOf(course.getStartWeek()));
        ((EditText) findViewById(R.id.etEndWeek)).setText(String.valueOf(course.getEndWeek()));
    }

    /**
     * show the information of lesson
     */
    private void showLessonInfo() {
        List<Course> courseList = course.toDetail();
        if (courseList == null) return;
        LessonAdapter adapter = new LessonAdapter(this, courseList, new DeleteListener(courseList));
        mListView.setAdapter(adapter);
        setListViewHeight(mListView);
    }

    private class DeleteListener implements View.OnClickListener {

        private List<Course> courseList;
        public DeleteListener(List<Course> courseList){
            this.courseList = courseList;
        }

        @Override
        public void onClick(final View v) {
            new AlertDialog.Builder(UpdateCourseActivity.this)
                    .setTitle("Delete Course")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteLesson(v, courseList);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();

        }
    }

    /**
     * Delete lesson
     * @param v
     * @param courseList
     */
    private void deleteLesson(View v, List<Course> courseList){
        Course course = (Course) v.getTag();
        Iterator<Course> iterator = courseList.iterator();
        while (iterator.hasNext()) {
            Course c = iterator.next();
            if (c.getDay() == course.getDay() &&
                    c.getSection() == course.getSection() &&
                    c.getWeekType().equals(course.getWeekType()) &&
                    c.getClassroom().equals(course.getClassroom())) {
                iterator.remove();
                break;
            }
        }
        Course toCourse = Course.toCourse(courseList, UpdateCourseActivity.this.course.getId());
        if (null != toCourse) {
            int update = courseDao.update(toCourse);
            if (update > 0) {
                String time = toCourse.getCourseTime();
                UpdateCourseActivity.this.course.setCourseTime(time);
                Toast.makeText(UpdateCourseActivity.this, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                showLessonInfo();
                return;
            }
        }
        Toast.makeText(UpdateCourseActivity.this, "Fail to delete", Toast.LENGTH_SHORT).show();
    }

    /**
     * Set the height of listview
     * @param listView
     */
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        //total height variable
        int totalHeight = 0;

        // calculate the height
        int count = listAdapter.getCount();
        if (count > 0) {
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight() * count;
        }

        //Reset the height of the listview
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }

    /**
     * back to course list
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    /**
     * add lesson
     * @param view
     */
    public void addLesson(View view) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_lesson_item, null);
        new AlertDialog.Builder(this)
                .setTitle("Add Lesson")
                .setView(inflate)
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Course course = new Course();
                        shp = getSharedPreferences("school",MODE_PRIVATE);

                        if (shp.getString("school", "").equals("")) {
                            Toast.makeText(UpdateCourseActivity.this, "Please Add school first!", Toast.LENGTH_LONG).show();
                            return;
                        }else {
                            //Week information
                            Spinner weekdays = inflate.findViewById(R.id.Weekdays);
                            int day = weekdays.getSelectedItemPosition();
                            if (day >= 0) {
                                course.setDay(day+1);
                            } else {
                                Toast.makeText(UpdateCourseActivity.this, "Week cannot be empty! ", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //Section information
                            Spinner Section = inflate.findViewById(R.id.sections);
                            int section = Section.getSelectedItemPosition();
                            if (section >= 0) {
                                course.setSection(section+1);
                            } else {
                                Toast.makeText(UpdateCourseActivity.this, "Section cannot be empty! ", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //Single & Double week information
                            int rbId = ((RadioGroup) inflate.findViewById(R.id.rgWeekType)).getCheckedRadioButtonId();
                            if (R.id.rbSingleWeek == rbId) {
                                course.setWeekType("s");
                            } else if (R.id.rbNormalWeek == rbId) {
                                course.setWeekType("n");
                            } else if (R.id.rbDoubleWeek == rbId) {
                                course.setWeekType("d");
                            } else {
                                Toast.makeText(UpdateCourseActivity.this, "Please Select the type of week! ", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //Classroom information
                            Spinner spinner = inflate.findViewById(R.id.techBuilding);
                            String building = spinner.getSelectedItem().toString();
                            String classroom = building + "#" + ((EditText) inflate.findViewById(R.id.tvClassroom)).getText().toString();
                            if (!TextUtils.isEmpty(classroom)) {
                                course.setClassroom(classroom);
                            } else {
                                Toast.makeText(UpdateCourseActivity.this, "Classroom cannot be empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //Pack up the time of class
                            String courseTime = UpdateCourseActivity.this.course.getCourseTime();
                            int id = UpdateCourseActivity.this.course.getId();
                            if (TextUtils.isEmpty(courseTime)) {
                                course.setCourseTime(course.toTime());
                            } else {
                                course.setCourseTime(courseTime + ";" + course.toTime());
                            }
                            course.setId(id);
                        }
                        
                        //Edit
                        int update = courseDao.update(course);
                        if (update > 0) {
                            UpdateCourseActivity.this.course.setCourseTime(course.getCourseTime());
                            Toast.makeText(UpdateCourseActivity.this, "Edit successfully! ", Toast.LENGTH_SHORT).show();
                            showLessonInfo();
                        } else {
                            Toast.makeText(UpdateCourseActivity.this, "Fail to edit! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Delete Course
     * @param view
     */
    public void delCourse(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int delete = courseDao.delete(course.getId());
                        if (delete > 0) {
                            Toast.makeText(UpdateCourseActivity.this, "Delete successfully! ", Toast.LENGTH_SHORT).show();
                            UpdateCourseActivity.this.finish();
                        } else {
                            Toast.makeText(UpdateCourseActivity.this, "Fail to delete! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Save edit
     * @param view
     */
    public void save(View view) {
        Course course = new Course();
        course.setId(this.course.getId());

        //Get the name of a course
        String courseName = ((EditText) findViewById(R.id.etCourseName)).getText().toString();
        if (!TextUtils.isEmpty(courseName)) {
            course.setCourseName(courseName);
        }

        //Get the teacher's name of a course
        String teacherName = ((EditText) findViewById(R.id.etTeacherName)).getText().toString();
        if (!TextUtils.isEmpty(teacherName)) {
            course.setTeacherName(teacherName);
        }

        course.setStartWeek(Integer.parseInt(((EditText) findViewById(R.id.etStartWeek)).getText().toString()));
        course.setEndWeek(Integer.parseInt(((EditText) findViewById(R.id.etEndWeek)).getText().toString()));
        if (this.course.equals(course)) {
            Toast.makeText(this, "No edit\nNo need to save!", Toast.LENGTH_SHORT).show();
        } else {
            int update = courseDao.update(course);
            if (update > 0) {
                Toast.makeText(this, "Edit successfully! ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fail to edit! ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
