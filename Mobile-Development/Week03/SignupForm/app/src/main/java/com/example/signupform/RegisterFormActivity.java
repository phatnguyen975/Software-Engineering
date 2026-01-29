package com.example.signupform;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterFormActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword, etRetype, etBirthdate;
    private RadioGroup rgGender;
    private CheckBox cbTennis, cbFutbal, cbOthers;
    private MaterialButton btnSelectDate, btnReset, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);
        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etRetype = findViewById(R.id.et_retype);
        etBirthdate = findViewById(R.id.et_birthdate);
        rgGender = findViewById(R.id.rg_gender);
        cbTennis = findViewById(R.id.cb_tennis);
        cbFutbal = findViewById(R.id.cb_futbal);
        cbOthers = findViewById(R.id.cb_others);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnReset = findViewById(R.id.btn_reset);
        btnSignup = findViewById(R.id.btn_signup);
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnReset.setOnClickListener(v -> resetForm());
        btnSignup.setOnClickListener(v -> attemptSignup());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    etBirthdate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void resetForm() {
        etUsername.setText("");
        etPassword.setText("");
        etRetype.setText("");
        etBirthdate.setText("");
        rgGender.clearCheck();
        cbTennis.setChecked(false);
        cbFutbal.setChecked(false);
        cbOthers.setChecked(false);
        etUsername.requestFocus();
    }

    private void attemptSignup() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String retype = etRetype.getText().toString();
        String birthdate = etBirthdate.getText().toString();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }
        if (TextUtils.isEmpty(retype)) {
            etRetype.setError("Please retype password");
            return;
        }
        if (TextUtils.isEmpty(birthdate)) {
            etBirthdate.setError("Birthdate is required");
            return;
        }
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(retype)) {
            Toast.makeText(this, "Password and Retype do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidDate(birthdate)) {
            Toast.makeText(this, "Invalid date! Please check again!", Toast.LENGTH_SHORT).show();
            etBirthdate.setError("Invalid date");
            return;
        }

        String gender = "";
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_male)
            gender = "Male";
        else if (selectedId == R.id.rb_female)
            gender = "Female";

        ArrayList<String> hobbies = new ArrayList<>();
        if (cbTennis.isChecked())
            hobbies.add("Tennis");
        if (cbFutbal.isChecked())
            hobbies.add("Futbal");
        if (cbOthers.isChecked())
            hobbies.add("Others");

        Intent intent = new Intent(RegisterFormActivity.this, ResultFormActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("PASSWORD", password);
        bundle.putString("BIRTHDATE", birthdate);
        bundle.putString("GENDER", gender);
        bundle.putStringArrayList("HOBBIES", hobbies);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr == null || !dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
