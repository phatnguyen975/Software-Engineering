package com.group4.lifecycle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    // UI Components
    private EditText txtMsg;
    private Button btnExit;
    private TextView txtSpy;
    private LinearLayout myScreen;

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_MEMBER_NAME = "memberName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        txtMsg = findViewById(R.id.txtMsg);
        btnExit = findViewById(R.id.btnExit);
        txtSpy = findViewById(R.id.txtSpy);
        myScreen = findViewById(R.id.myScreen);

        // 2. Load saved data (State Restoration)
        loadSavedData();

        // 3. Set up Button Listener
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Destroys the activity
            }
        });

        // 4. Set up TextWatcher to detect typing changes in real-time
        txtMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this logic
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this logic
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Update background color based on the current text
                String inputName = s.toString();
                updateBackgroundColor(inputName);
            }
        });

        showToastAndLog("onCreate");
    }

    /**
     * Helper method to map names to background colors.
     * Case-insensitive comparison.
     */
    private void updateBackgroundColor(String name) {
        // Convert to lowercase to make it case-insensitive
        String cleanName = name.trim().toLowerCase();

        switch (cleanName) {
            case "phat":
                myScreen.setBackgroundColor(Color.parseColor("#FFCDD2")); // Light Red
                break;
            case "tien":
                myScreen.setBackgroundColor(Color.parseColor("#C8E6C9")); // Light Green
                break;
            case "loc":
                myScreen.setBackgroundColor(Color.parseColor("#BBDEFB")); // Light Blue
                break;
            case "bao":
                myScreen.setBackgroundColor(Color.parseColor("#FFF9C4")); // Light Yellow
                break;
            case "khoa":
                myScreen.setBackgroundColor(Color.parseColor("#E1BEE7")); // Light Purple
                break;
            default:
                myScreen.setBackgroundColor(Color.WHITE); // Default
                break;
        }
    }

    /**
     * Saves the current text in EditText to SharedPreferences.
     * Called in onPause() to ensure data is saved when app goes to background.
     */
    private void saveData() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        
        String currentText = txtMsg.getText().toString();
        editor.putString(KEY_MEMBER_NAME, currentText);

        // OPTIMIZATION: Use apply() instead of commit() for asynchronous saving
        editor.apply(); 
        
        Log.d("LifeCycleCheck", "Data Saved: " + currentText);
    }

    /**
     * Loads the saved name from SharedPreferences and updates UI.
     */
    private void loadSavedData() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // "white" is the default value if no key is found
        String savedName = settings.getString(KEY_MEMBER_NAME, ""); 
        
        // Update the EditText (this will trigger the TextWatcher automatically!)
        txtMsg.setText(savedName);
        
        // Manually update color just to be sure (though TextWatcher handles it)
        updateBackgroundColor(savedName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showToastAndLog("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showToastAndLog("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData(); // Crucial: Save data here!
        showToastAndLog("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        showToastAndLog("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showToastAndLog("onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showToastAndLog("onDestroy");
    }

    // Helper to show Toast and Log at the same time
    private void showToastAndLog(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d("LifeCycleCheck", "Current state: " + message);
    }
}