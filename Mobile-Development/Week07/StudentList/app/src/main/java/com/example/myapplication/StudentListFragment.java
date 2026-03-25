package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class StudentListFragment extends Fragment {

    private ListView lvStudents;
    private TextView tvHeaderList;
    private OnStudentSelectedListener callback;
    private List<HocSinh> students;
    private StudentAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (OnStudentSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStudentSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);
        lvStudents = view.findViewById(R.id.lvStudents);
        tvHeaderList = view.findViewById(R.id.tvHeaderList);

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onStudentSelected(position);
            }
        });

        if (students != null) {
            setupAdapter();
        }

        return view;
    }

    public void setStudents(List<HocSinh> students) {
        this.students = students;
        if (lvStudents != null) {
            setupAdapter();
        }
    }

    private void setupAdapter() {
        adapter = new StudentAdapter(getContext(), students);
        lvStudents.setAdapter(adapter);
    }

    public void updateSelection(int position) {
        if (lvStudents != null && students != null && position >= 0 && position < students.size()) {
            lvStudents.clearChoices();
            lvStudents.requestLayout();
            
            lvStudents.setItemChecked(position, true);
            lvStudents.setSelection(position);
            
            tvHeaderList.setText("Mã số: " + students.get(position).getMaHS());
            
            adapter.notifyDataSetChanged();
        }
    }
}
