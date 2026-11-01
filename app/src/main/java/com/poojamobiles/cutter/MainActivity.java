package com.poojamobiles.cutter;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private Button btnConnect, btnCut;
    private EditText etWidth, etHeight, etName;

    // Standard Serial Port UUID
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Ungaloda CUTTER_F9BD Machine Address
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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkPermissions();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToCutter();
            }
        });

        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCutCommand();
            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 101);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 101);
        }
    }

    private void connectToCutter() {
        try {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Please turn ON Bluetooth!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                Toast.makeText(this, "Allow Nearby Devices Permission & Try Again!", Toast.LENGTH_LONG).show();
                return;
            }

            bluetoothAdapter.cancelDiscovery();

            // Direct-ah unga CUTTER_F9BD (00:16:A6:04:50:B5) edukkurom
            BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(CUTTER_MAC);

            Toast.makeText(this, "Connecting to " + targetDevice.getName() + "...", Toast.LENGTH_SHORT).show();

            try {
                // Method 1: Insecure RFCOMM (Best for Chinese Plotters)
                bluetoothSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                bluetoothSocket.connect();
            } catch (Exception e1) {
                // Method 2: Fallback Channel 1 Reflection
                Method m = targetDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                bluetoothSocket = (BluetoothSocket) m.invoke(targetDevice, 1);
                bluetoothSocket.connect();
            }

            outputStream = bluetoothSocket.getOutputStream();
            btnConnect.setText("CONNECTED: CUTTER_F9BD");
            Toast.makeText(this, "Super! Cutter Connected Successfully!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendCutCommand() {
        if (outputStream == null) {
            Toast.makeText(this, "First Connect Bluetooth Cutter!", Toast.LENGTH_SHORT).show();
            return;
        }

        String wStr = etWidth.getText().toString().trim();
        String hStr = etHeight.getText().toString().trim();

        if (wStr.isEmpty() || hStr.isEmpty()) {
            Toast.makeText(this, "Enter Width and Height!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int widthUnits = Integer.parseInt(wStr) * 40; // 1mm = 40 plotter units
            int heightUnits = Integer.parseInt(hStr) * 40;

            // Standard HPGL Cutting Command
            String hpgl = "IN;PA;PU0,0;PD" + widthUnits + ",0," + widthUnits + "," + heightUnits + ",0," + heightUnits + ",0,0;PU0,0;";
            outputStream.write(hpgl.getBytes());
            outputStream.flush();

            Toast.makeText(this, "Cut Command Sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Cut Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
