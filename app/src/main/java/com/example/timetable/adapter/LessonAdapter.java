package com.example.timetable.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.timetable.R;
import com.example.timetable.dao.CourseDao;
import com.example.timetable.Entity.Course;

import java.util.List;

/**
 * This class the adapter of add lesson interface
 */
public class LessonAdapter extends BaseAdapter {

    private CourseDao dao;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Course> courseList;
    private View.OnClickListener listener;

    public LessonAdapter(Context context, @NonNull List<Course> courseList, View.OnClickListener listener){
        this.context = context;
        this.courseList = courseList;
        this.layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
        dao = new CourseDao(context);
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return courseList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            convertView = layoutInflater.inflate(R.layout.lession_item, null);
            holder = new ViewHolder();
            holder.ivDel = convertView.findViewById(R.id.ivDel);
            holder.tvDay = convertView.findViewById(R.id.tvDay);
            holder.tvSection = convertView.findViewById(R.id.tvSection);
            holder.tvWeekType = convertView.findViewById(R.id.tvWeekType);
            holder.tvClassroom = convertView.findViewById(R.id.tvClassroom);
            holder.ivDel.setTag(courseList.get(position));
            holder.ivDel.setOnClickListener(listener);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        setContent(position, holder);
        return convertView;
    }

    private void setContent(int i, ViewHolder view){
        view.tvDay.setText(String.format("Week %d", courseList.get(i).getDay()));
        view.tvSection.setText(String.format("Section %s", courseList.get(i).getSection()));
        String weekType = courseList.get(i).getWeekType();
        if("s".equals(weekType)){
            view.tvWeekType.setText("Single");
        }else if("d".equals(weekType)){
            view.tvWeekType.setText("Double");
        }else{
            view.tvWeekType.setText("Normal");
        }
        view.tvClassroom.setText(String.valueOf(courseList.get(i).getClassroom()));
    }

     class ViewHolder{
        TextView tvDay;
        TextView tvSection;
        TextView tvWeekType;
        TextView tvClassroom;
        ImageView ivDel;
    }
}
