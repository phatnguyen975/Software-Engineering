package com.example.signupform;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class ResultFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_form);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String username = bundle.getString("USERNAME");
            String password = bundle.getString("PASSWORD");
            String birthdate = bundle.getString("BIRTHDATE");
            String gender = bundle.getString("GENDER");
            ArrayList<String> hobbies = bundle.getStringArrayList("HOBBIES");

            StringBuilder hobbiesStr = new StringBuilder();
            if (hobbies != null) {
                for (int i = 0; i < hobbies.size(); i++) {
                    hobbiesStr.append(hobbies.get(i));
                    if (i < hobbies.size() - 1)
                        hobbiesStr.append(", ");
                }
            }

            TextView tvUsername = findViewById(R.id.tv_res_username);
            TextView tvPassword = findViewById(R.id.tv_res_password);
            TextView tvBirthdate = findViewById(R.id.tv_res_birthdate);
            TextView tvGender = findViewById(R.id.tv_res_gender);
            TextView tvHobbies = findViewById(R.id.tv_res_hobbies);

            tvUsername.setText(createBoldLabel("Username: ", username));
            tvPassword.setText(createBoldLabel("Password: ", password.replaceAll(".", "*")));
            tvBirthdate.setText(createBoldLabel("Birthdate: ", birthdate));
            tvGender.setText(createBoldLabel("Gender: ", gender));
            tvHobbies.setText(createBoldLabel("Hobbies: ", hobbiesStr.toString()));
        }

        MaterialButton btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
    }

    private SpannableStringBuilder createBoldLabel(String label, String value) {
        SpannableStringBuilder sb = new SpannableStringBuilder(label + value);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
