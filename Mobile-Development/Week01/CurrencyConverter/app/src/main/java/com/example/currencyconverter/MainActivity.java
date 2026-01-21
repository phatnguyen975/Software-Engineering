package com.example.currencyconverter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;

public class MainActivity extends Activity {

    // USA money format
    DecimalFormat usaDf = new DecimalFormat("###,###,###,###.##");

    // Conversion rates and currency symbols
    private final double EURO2USD = 1.35;
    private final char EUROSYM = '€';
    private final double COLON2USD = 0.0019;
    private final char COLONSYM = '₡';

    // GUI Widgets
    Button btnConvert, btnClear;
    EditText txtUSDollars, txtEuros, txtColones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Java objects to XML UI components
        txtUSDollars = (EditText) findViewById(R.id.txtUSDollars);
        txtEuros = (EditText) findViewById(R.id.txtEuros);
        txtColones = (EditText) findViewById(R.id.txtColones);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnConvert = (Button) findViewById(R.id.btnConvert);

        // Disable keyboard input for result fields programmatically
        txtEuros.setInputType(EditorInfo.TYPE_NULL);
        txtColones.setInputType(EditorInfo.TYPE_NULL);

        // Set Click Listener for Clear Button
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset all input and output fields
                txtUSDollars.setText("");
                txtEuros.setText("");
                txtColones.setText("");
            }
        });

        // Set Click Listener for Convert Button
        btnConvert.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String usdStr = txtUSDollars.getText().toString();

                    // Proceed only if the input is not empty
                    if (!usdStr.isEmpty()) {
                        double usd = Double.parseDouble(usdStr);

                        // Perform calculations: Input / Rate
                        String euros = EUROSYM + usaDf.format(usd / EURO2USD);
                        String colones = COLONSYM + usaDf.format(usd / COLON2USD);

                        // Update UI with formatted results
                        txtEuros.setText(euros);
                        txtColones.setText(colones);
                    }
                } catch (NumberFormatException e) {
                    // Prevent app crash on invalid numeric input
                }
            }
        });
    }
}
