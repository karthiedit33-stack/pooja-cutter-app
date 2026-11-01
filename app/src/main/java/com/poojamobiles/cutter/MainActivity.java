package com.poojamobiles.cutter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect, btnCut;
    private EditText etWidth, etHeight, etName;

    // Ungaloda exact Cutter Bluetooth details
    private static final String CUTTER_NAME = "CUTTER_F9BD";
    private static final String CUTTER_MAC = "00:16:A6:04:50:B5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        btnCut = findViewById(R.id.btnCut);
        etWidth = findViewById(R.id.etWidth);
        etHeight = findViewById(R.id.etHeight);
        etName = findViewById(R.id.etName);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Connecting to " + CUTTER_NAME + " (" + CUTTER_MAC + ")...", Toast.LENGTH_SHORT).show();
            }
        });

        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String w = etWidth.getText().toString();
                String h = etHeight.getText().toString();
                if(w.isEmpty() || h.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter Width & Height!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Sending Cut Command: " + w + "x" + h + "mm to " + CUTTER_NAME, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
