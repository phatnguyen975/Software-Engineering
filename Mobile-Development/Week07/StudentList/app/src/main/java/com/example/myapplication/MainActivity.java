package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnStudentSelectedListener {

    private List<HocSinh> students;
    private int currentPosition = 0;
    private StudentListFragment listFragment;
    private StudentDetailFragment detailFragment;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XÓA DATABASE CŨ MỖI KHI CHẠY APP
        // Mục đích: Để dữ liệu luôn mới 100% mỗi khi chấm bài, 
        // không bị lẫn với dữ liệu của các lần chạy trước đó.
        deleteDatabase("SchoolManager.db");

        dbHelper = new DatabaseHelper(this);
        initData();

        listFragment = (StudentListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentList);
        detailFragment = (StudentDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentDetail);

        if (listFragment != null) {
            listFragment.setStudents(students);
        }

        // Khởi tạo sinh viên đầu tiên
        if (!students.isEmpty()) {
            onStudentSelected(0);
        }
    }

    private void initData() {
        students = dbHelper.getAllHocSinh();
        
        // Vì bên trên đã xóa DB, nên students.isEmpty() sẽ luôn đúng ở lần chạy đầu tiên
        if (students.isEmpty()) {
            dbHelper.addLop(new Lop("A1", "Lớp A1"));
            dbHelper.addLop(new Lop("A2", "Lớp A2"));
            dbHelper.addLop(new Lop("A3", "Lớp A3"));
            dbHelper.addLop(new Lop("A4", "Lớp A4"));

            dbHelper.addHocSinh(new HocSinh("A1_9829", "Lê Thị A", 8.0, "A1"));
            dbHelper.addHocSinh(new HocSinh("A1_1809", "Nguyễn Văn B", 7.5, "A1"));
            dbHelper.addHocSinh(new HocSinh("A2_3509", "Trần Thị C", 9.0, "A2"));
            dbHelper.addHocSinh(new HocSinh("A2_3100", "Phạm Văn D", 6.5, "A2"));
            dbHelper.addHocSinh(new HocSinh("A1_1120", "Hoàng Thị E", 8.5, "A1"));
            dbHelper.addHocSinh(new HocSinh("A3_4120", "Bùi Văn F", 7.0, "A3"));
            dbHelper.addHocSinh(new HocSinh("A2_8100", "Võ Thị G", 8.2, "A2"));
            dbHelper.addHocSinh(new HocSinh("A4_1160", "Đặng Văn H", 7.8, "A4"));

            students = dbHelper.getAllHocSinh();
        }
    }

    @Override
    public void onStudentSelected(int position) {
        currentPosition = position;
        HocSinh selectedStudent = students.get(position);
        
        if (detailFragment != null) {
            detailFragment.updateDetails(selectedStudent, currentPosition, students.size());
        }
        
        if (listFragment != null) {
            listFragment.updateSelection(currentPosition);
        }
    }

    @Override
    public void onNavigationClicked(String action) {
        switch (action) {
            case "FIRST":
                onStudentSelected(0);
                break;
            case "PREV":
                if (currentPosition > 0) {
                    onStudentSelected(currentPosition - 1);
                }
                break;
            case "NEXT":
                if (currentPosition < students.size() - 1) {
                    onStudentSelected(currentPosition + 1);
                }
                break;
            case "LAST":
                onStudentSelected(students.size() - 1);
                break;
        }
    }
}
