package com.example.timetable.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetable.Entity.ListItem;
import com.example.timetable.R;
import com.example.timetable.activity.detailedListActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TodoAdapter extends BaseAdapter {

    private LayoutInflater inflater;
//    private LinkedList<String> list = getList();
    private List<ListItem> listItems;
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TodoAdapter(Context context, List<ListItem> listItems) {
        this.inflater = LayoutInflater.from(context);
        this.listItems = listItems;
        for (ListItem listItem:listItems) {
            Log.e("test545", listItem.getTitle());
        }
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        date = new Date();
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.Title);
            viewHolder.dateview = convertView.findViewById(R.id.listDate);
            viewHolder.detailed = convertView.findViewById(R.id.detailedinfo);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            String title = listItems.get(position).getTitle();
            String datetime = listItems.get(position).getDate() + " | " + listItems.get(position).getTime();
            Date date1 = simpleDateFormat.parse(listItems.get(position).getDate()+ " " +listItems.get(position).getTime());
            SpannableString strTitle = new SpannableString(title);
            SpannableString strDatetime = new SpannableString(datetime + " (OVER TIME) ");
            SpannableString strTitle1 = new SpannableString(title);
            SpannableString strDatetime1 = new SpannableString(datetime);
            if (date1.getTime() >= date.getTime()) {
                strTitle1.setSpan(new ForegroundColorSpan(Color.parseColor("#76B9ED")),0, strTitle1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                strDatetime1.setSpan(new ForegroundColorSpan(Color.parseColor("#76B9ED")),0, strDatetime1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                Log.e("test5456", date.getTime() + "  5466  " + date1.getTime());//simpleDateFormat.format(date)
                viewHolder.title.setText(strTitle1);
                viewHolder.dateview.setText(strDatetime1);
            } else {
                Log.e("test5456", simpleDateFormat.format(date));
                strTitle.setSpan(new ForegroundColorSpan(Color.RED),0, strTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                strDatetime.setSpan(new ForegroundColorSpan(Color.RED),0, strDatetime.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.title.setText(strTitle);
                viewHolder.dateview.setText(strDatetime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.detailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListItem listItem = listItems.get(position);
                Intent intent = new Intent(inflater.getContext(), detailedListActivity.class);
                intent.putExtra("listItem", listItem);
                inflater.getContext().startActivity(intent);
            }
        });
        return convertView;
    }



    class ViewHolder {
        TextView title;
        TextView dateview;
        TextView detailed;
    }
}
