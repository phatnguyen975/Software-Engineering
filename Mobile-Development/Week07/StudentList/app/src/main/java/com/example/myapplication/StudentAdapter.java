package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private Context context;
    private List<HocSinh> studentList;

    public StudentAdapter(Context context, List<HocSinh> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_student, parent, false);
        }

        HocSinh student = studentList.get(position);
        ImageView img = convertView.findViewById(R.id.imgStudent);
        TextView tvId = convertView.findViewById(R.id.tvStudentId);

        // Sử dụng icon mặc định vì trong DB không lưu resource ảnh
        img.setImageResource(android.R.drawable.ic_menu_myplaces);
        tvId.setText(student.getMaHS());

        return convertView;
    }
}
