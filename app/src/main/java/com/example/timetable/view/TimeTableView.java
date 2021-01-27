package com.example.timetable.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.example.timetable.activity.InfoActivity;
import com.example.timetable.activity.OptionActivity;
import com.example.timetable.activity.SettingActivity;
import com.example.timetable.activity.TodoActivity;
import com.example.timetable.Entity.Course;
import com.example.timetable.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * this class is used to create the basic ui for timetable
 */
public class TimeTableView extends LinearLayout {

    //Week
    private String[] weekTitle = {"Mon", "Tues", "Wednes", "Thurs", "Fri", "Satur", "Sun"};
    //Section time
    private String[] time = {"\n8:00\n|\n9:00", "\n9:15\n|\n10:15", "\n10:30\n|\n11:30", "\n13:30\n|\n14:30", "\n14:45\n|\n15:45", "\n16:00\n|\n17:00", "\n18:00\n|\n19:00", "\n19:15\n|\n20:15", "\n20:30\n|\n21:30"};
    //Max number of weeks
    private int weeksNum = 5;
    //Max section number
    private int maxSection = 9;

    //radius of angles in textview
    private int radius = 8;
    //line width
    private int tableLineWidth = 1;
    //text size of number
    private int numberSize = 14;
    //text size of title
    private int titleSize = 14;
    //text size of weekday
    private int daySize = 10;
    //text size of course
    private int courseSize = 8;
    //size of button
    private int buttonSize = 12;

    //height of each cell
    private int cellHeight = 100;
    //height if title
    private int titleHeight = 40;
    //width of number frame
    private int numberWidth = 40;

    private Context mContext;
    private List<Course> courseList;
    private Map<String, Integer> colorMap = new HashMap<>();
    private Map<Integer, List<Course>> courseMap = new HashMap<>();

    //start date of college
    private Date startDate;
    private long weekNum;
    private int weekDay;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    //menu
    private ImageView mCategory, previous, next;
    //information of week
    private TextView mWeekTitle;
    private LinearLayout mMainLayout;
    private RelativeLayout mTitleLayout;

    //
    private SharedPreferences shp ;

    //different color for blanks
    private int color[] = {
            R.color.one, R.color.two, R.color.three,
            R.color.four, R.color.five, R.color.six,
            R.color.seven, R.color.eight, R.color.nine,
            R.color.ten, R.color.eleven, R.color.twelve,
            R.color.thirteen, R.color.fourteen, R.color.fifteen
    };


    private int currentX;

    public TimeTableView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public TimeTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void preprocessorParam() {
        tableLineWidth = dip2px(tableLineWidth);
        cellHeight = dip2px(cellHeight);
        titleHeight = dip2px(titleHeight);
        numberWidth = dip2px(numberWidth);
    }

    /**
     * Load data
     * @param courses
     */
    public void loadData(List<Course> courses, Date date) {
        this.courseList = courses;
        this.startDate = date;
        weekNum = calcWeek(startDate);
//        weekDay = calWeekDay(startDate);
        handleData(courseMap, courses, weekNum);
        flushView(courseMap, weekNum);
    }

    /**
     * Deal with the course data
     * @param courseMap
     * @param courseList
     * @param weekNum
     */
    private void handleData(Map<Integer, List<Course>> courseMap, List<Course> courseList, long weekNum) {
        courseMap.clear();
        for (Course c : courseList) {
            if (c.getStartWeek() > weekNum || c.getEndWeek() < weekNum) continue;
            String courseTime = c.getCourseTime();
            if(TextUtils.isEmpty(courseTime))continue;
            String[] courseArray = courseTime.split(";");
            for (int i = 0; i < courseArray.length; i++) {
                Course clone = c.clone();
                String[] info = courseArray[i].split(":");
                if ("n".equals(info[2]) || weekNum % 2 == 0 && "d".equals(info[2]) || weekNum % 2 == 1 && "s".equals(info[2])) {
                    clone.setDay(Integer.parseInt(info[0]));
                    clone.setSection(Integer.parseInt(info[1]));
                    clone.setClassroom(info[3]);

                    List<Course> courses = courseMap.get(clone.getDay());
                    if (null == courses) {
                        courses = new ArrayList<>();
                        courseMap.put(clone.getDay(), courses);
                    }
                    courses.add(clone);
                }
            }
        }
    }

    /**
     * Initialize the view
     */
    private void initView(){
        preprocessorParam();

        addWeekTitle(this);

        addWeekLabel(this);

        flushView(null, weekNum);
    }

    /**
     * refresh the timetable
     * @param courseMap
     * @param weekNum
     */
    private void flushView(Map<Integer, List<Course>> courseMap, long weekNum) {
        if (null != mMainLayout) removeView(mMainLayout);
        mMainLayout = new LinearLayout(mContext);
        mMainLayout.setOrientation(HORIZONTAL);
        mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mMainLayout);

        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        String datestr = simpleDateFormat.format(date);
        int num = calendar.get(Calendar.DAY_OF_WEEK);
        if (num == 1) {
            num = 8;
        }
        String daytitle = weekTitle[num-2];

        //Title of week
        shp = mContext.getSharedPreferences("school", MODE_PRIVATE);
        Date start = new Date(shp.getLong("date", new Date().getTime()));
        mWeekTitle.setText("WEEK " + weekNum + "\n Now: " + datestr + " | " + daytitle + " | Week " + calcWeek(start));

        //Left number frame
        addLeftNumber(mMainLayout);


        //course data
        if (null == courseMap || courseMap.isEmpty()) {
            TextView emptyLayoutTextView = createTextView("The courses are all end\nOr please add courses", titleSize, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0, getResources().getColor(R.color.textColor), Color.WHITE);
            mMainLayout.addView(emptyLayoutTextView);
        } else {
            for (int i = 1; i <= weeksNum; i++) {
                addDayCourse(mMainLayout, courseMap, i);
            }
        }
        invalidate();
    }

    /**
     * Add week title
     * @param pViewGroup
     */
    private void addWeekTitle(ViewGroup pViewGroup) {
        mTitleLayout = new RelativeLayout(mContext);
        mTitleLayout.setPadding(8, 15, 8, 15);
        mTitleLayout.setBackgroundColor(getResources().getColor(R.color.titleColor));

        //information of a week
        mWeekTitle = new TextView(mContext);
        mWeekTitle.setTextSize(titleSize);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mWeekTitle.setLayoutParams(lp);
        mWeekTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        mWeekTitle.setId(R.id.weekTitle);
        mTitleLayout.addView(mWeekTitle);

        //left menu button
        mCategory = new ImageView(mContext);
        mCategory.setImageResource(R.drawable.category);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(new LayoutParams(dip2px(30), dip2px(30)));
        lp2.addRule(RelativeLayout.CENTER_VERTICAL);
        mCategory.setLayoutParams(lp2);
        mCategory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        mTitleLayout.addView(mCategory);

        previous = new ImageView(mContext);
        previous.setImageResource(R.drawable.arrow_back);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        lp1.addRule(RelativeLayout.LEFT_OF, mWeekTitle.getId());
        previous.setLayoutParams(lp1);
        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWeek(-1);
            }
        });
        mTitleLayout.addView(previous);

        next = new ImageView(mContext);
        next.setImageResource(R.drawable.arrow_next);
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(dip2px(30), dip2px(30));
        lp3.addRule(RelativeLayout.RIGHT_OF, mWeekTitle.getId());
        next.setLayoutParams(lp3);
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleWeek(1);
            }
        });
        mTitleLayout.addView(next);

        pViewGroup.addView(mTitleLayout);
    }

    private void showMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.inflate(R.menu.func_list);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.TDlist:
                        Toast.makeText(mContext, "TO_DO List", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(mContext, TodoActivity.class);
                        mContext.startActivity(intent2);
                        return true;
                    case R.id.Set:
                        Toast.makeText(mContext, "Setting", Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(mContext, SettingActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mContext.startActivity(intent1);
                        return true;
                    case R.id.option:
                        Intent intent = new Intent(mContext, OptionActivity.class);
                        mContext.startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /**
     * Add week day label
     */
    private void addWeekLabel(ViewGroup pViewGroup) {
        LinearLayout mTitleLayout = new LinearLayout(mContext);
        mTitleLayout.setOrientation(HORIZONTAL);
        mTitleLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, titleHeight));
        addView(mTitleLayout);

        // empty blank at left
        TextView space = new TextView(mContext);
        space.setLayoutParams(new ViewGroup.LayoutParams(numberWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        space.setBackgroundColor(getResources().getColor(R.color.titleColor));
        mTitleLayout.addView(space);

        //Week day
        for (int i = 0; i < weeksNum; i++) {
            TextView title = createTextView(weekTitle[i], daySize, 0, ViewGroup.LayoutParams.MATCH_PARENT, 1, getResources().getColor(R.color.textColor), getResources().getColor(R.color.titleColor));
            mTitleLayout.addView(title);
        }
    }

    /**
     * Add left section number
     */
    private void addLeftNumber(ViewGroup pViewGroup) {
        LinearLayout leftLayout = new LinearLayout(mContext);
        leftLayout.setOrientation(VERTICAL);
        leftLayout.setLayoutParams(new LayoutParams(numberWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 1; i <= maxSection; i++) {
            TextView number = createTextView(String.valueOf(i) + time[i-1], numberSize, ViewGroup.LayoutParams.MATCH_PARENT, cellHeight, 1, getResources().getColor(R.color.textColor), Color.WHITE);
            leftLayout.addView(number);
        }
        pViewGroup.addView(leftLayout);
    }

    /**
     * Add courses blanks with their information on the screen
     * @param pViewGroup
     * @param day
     */
    private void addDayCourse(ViewGroup pViewGroup, Map<Integer, List<Course>> courseMap, int day) {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        linearLayout.setOrientation(VERTICAL);
        List<Course> courses = getCourses(courseMap, day);
        if (null != courses) {
            for (int i = 0, size = courses.size(); i < size; i++) {
                Course course = courses.get(i);
                int section = course.getSection();
                if (i == 0) addBlankCell(linearLayout, section - 1);
                else addBlankCell(linearLayout, course.getSection() - courses.get(i - 1).getSection() - 2);
                    addCourseCell(linearLayout, course);
                if (i == size - 1) addBlankCell(linearLayout, maxSection - section - 1);
            }
        } else {
            addBlankCell(linearLayout, maxSection);
        }
        pViewGroup.addView(linearLayout);
    }

    /**
     * Access the data of courses
     * @param day
     * @return
     */
    public List<Course> getCourses(Map<Integer, List<Course>> courseMap, int day) {
        final List<Course> courses = courseMap.get(day);
        if (null != courses) {
            Collections.sort(courses, new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    return o1.getSection() - o2.getSection();
                }
            });
        }
        return courses;
    }

    /**
     * Add the course blank function
     * @param pViewGroup
     * @param course
     */
    private void addCourseCell(ViewGroup pViewGroup, Course course) {
        shp = mContext.getSharedPreferences("school", MODE_PRIVATE);
        String school = shp.getString("school", "北京工业大学");
        String room = course.getClassroom();
        String []array = room.split("#");
        String classroom = array[0]+array[1];
        RoundTextView textView = new RoundTextView(mContext, radius, getColor(colorMap, course.getCourseName()));
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, cellHeight + tableLineWidth/2));
        textView.setTextSize(courseSize);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setText(String.format("%s\n%s\n%d~%d week\n"+ school +"%s", course.getCourseName(), course.getTeacherName(), course.getStartWeek(), course.getEndWeek(), classroom));
        final String s = school+array[0];
        final Poi end = new Poi(s, null,"B000A9PHV0");
        final String info = course.toString();

        textView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, end, s, info);
                return false;
            }
        });
        pViewGroup.addView(textView);
    }

    private void showPopupMenu(View view, final Poi poi, final String s, final String info) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_navi:
                        Toast.makeText(mContext, "Navigation to "+s, Toast.LENGTH_SHORT).show();
                        AmapNaviParams params = new AmapNaviParams(null, null, poi, AmapNaviType.WALK, AmapPageType.ROUTE);
                        AmapNaviPage.getInstance().showRouteActivity(mContext, params, null);
                        return true;
                    case R.id.menu_info:
                        Toast.makeText(mContext, "information", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, InfoActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence("info", info);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    /**
     * Add the empty blank
     * @param pViewGroup
     * @param num number of empty blanks
     */
    private void addBlankCell(ViewGroup pViewGroup, int num) {
        for (int i = 0; i < num; i++) {
            TextView blank = new TextView(mContext);
            blank.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, cellHeight));
            pViewGroup.addView(blank);
        }
    }

    /**
     * Create a textview
     * @param content
     * @param color
     * @param size
     * @param width
     * @param height
     * @param weight
     * @return
     */
    private TextView createTextView(String content, int size, int width, int height, int weight, int color, int bkColor) {
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LayoutParams(width, height, weight));
        if(bkColor != -1)textView.setBackgroundColor(bkColor);
        textView.setTextColor(color);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(size);
        textView.setText(content);
        return textView;
    }

    /**
     * calculate whether the week number has overed the limit
     * @param flag
     */
    private void toggleWeek(int flag){
        if(flag < 0){
            weekNum = weekNum - 1 <= 0 ? weekNum : weekNum - 1;
        }else{
            weekNum = weekNum + 1 > 19  ? weekNum : weekNum + 1;
        }
        handleData(courseMap, courseList, weekNum);
        flushView(courseMap, weekNum);
    }

    /**
     * location current week
     * @param date
     * @return current week number
     */
    private long calcWeek(Date date) {
        return (new Date().getTime() - date.getTime()) / (1000 * 3600 * 24 * 7) + 1;
    }

    /**
     *
     * @param map
     * @param name
     * @return the color for different not empty blanks
     */
    private int getColor(Map<String, Integer> map, String name) {
        Integer tip = map.get(name);
        if (null != tip) {
            return tip;
        } else {
            int i = getResources().getColor(color[map.size() % color.length]);
            map.put(name, i);
            return i;
        }
    }

    /**
     * scale certain number
     * @param dpValue
     * @return the number after scaling
     */
    private int dip2px(float dpValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    /**
     * here will detect whether user have slided the screen. If user slides left, go to next week; else, return to previous week
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                int i = (int) event.getX() - currentX;
                if(i > 20){
                    toggleWeek(-1);
                }else if(i < -20){
                    toggleWeek(1);
                }
                break;
        }
        return true;
    }

}
