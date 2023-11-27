package com.controlproyecto.controlapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CRUCETA DE BOTONES PARA MOVIMIENTO
        Button buttonForward = findViewById(R.id.buttonForward);
        Button buttonBackward = findViewById(R.id.buttonBackward);
        Button buttonLeft = findViewById(R.id.buttonLeft);
        Button buttonRight = findViewById(R.id.buttonRight);
        Button buttonStop = findViewById(R.id.buttonStop);

        // Check if Bluetooth is supported and enabled
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            showToast("Bluetooth no esta habilitado o disponible");
            return;
        }

// Check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 100);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
        } else {
            // Detectar la conexión Bluetooth al abrir la aplicación
            if (bluetoothSocket == null) {
                BluetoothDevice device = null;
                for (BluetoothDevice pairedDevice : bluetoothAdapter.getBondedDevices()) {
                    if (pairedDevice.getName().equals("BT-MCQUEEN")) {
                        device = pairedDevice;
                        break;
                    }
                }

                if (device != null) {
                    connectToDevice(device);
                }
            }
        }

        // Comandos a enviar por botones
        buttonForward.setOnClickListener(v -> {
            sendCommand("0"); // Avanzar
        });

        buttonBackward.setOnClickListener(v -> {
            sendCommand("1"); // Retroceder
        });

        buttonLeft.setOnClickListener(v -> {
            sendCommand("3"); // Izquierda
        });

        buttonRight.setOnClickListener(v -> {
            sendCommand("2"); // Derecha
        });

        buttonStop.setOnClickListener(v -> {
            sendCommand("4"); // Detener
        });
    }

    private void connectToDevice(BluetoothDevice device) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (Exception e) {
            showToast("Error conectando al BT-MCQUEEN: " + e.getMessage());
        }
    }

    private void sendCommand(String command) {
        try {
            if (outputStream != null) {
                outputStream.write(command.getBytes());
            }
        } catch (Exception e) {
            showToast("Error enviando el comando: " + e.getMessage());
        }
    }

    private void showToast(final String message) {
        handler.post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
