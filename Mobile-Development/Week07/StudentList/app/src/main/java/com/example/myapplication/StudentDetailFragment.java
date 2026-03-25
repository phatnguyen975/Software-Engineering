package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StudentDetailFragment extends Fragment {

    private TextView tvIdTitle, tvName, tvClass, tvGpa;
    private Button btnFirst, btnPrev, btnNext, btnLast;
    private OnStudentSelectedListener callback;

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
        View view = inflater.inflate(R.layout.fragment_student_detail, container, false);

        tvIdTitle = view.findViewById(R.id.tvDetailIdTitle);
        tvName = view.findViewById(R.id.tvDetailName);
        tvClass = view.findViewById(R.id.tvDetailClass);
        tvGpa = view.findViewById(R.id.tvDetailGpa);

        btnFirst = view.findViewById(R.id.btnFirst);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);
        btnLast = view.findViewById(R.id.btnLast);

        btnFirst.setOnClickListener(v -> callback.onNavigationClicked("FIRST"));
        btnPrev.setOnClickListener(v -> callback.onNavigationClicked("PREV"));
        btnNext.setOnClickListener(v -> callback.onNavigationClicked("NEXT"));
        btnLast.setOnClickListener(v -> callback.onNavigationClicked("LAST"));

        return view;
    }

    public void updateDetails(HocSinh student, int position, int totalCount) {
        if (student != null) {
            tvIdTitle.setText(student.getMaHS());
            tvName.setText("Họ tên: " + student.getTenHS());
            tvClass.setText("Mã lớp: " + student.getMaLop());
            tvGpa.setText("Điểm trung bình: " + student.getDtb());

            btnFirst.setEnabled(position > 0);
            btnPrev.setEnabled(position > 0);
            btnNext.setEnabled(position < totalCount - 1);
            btnLast.setEnabled(position < totalCount - 1);
        }
    }
}
