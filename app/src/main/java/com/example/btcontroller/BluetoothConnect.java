package com.example.btcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothConnect extends Thread {
    private static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final View view;

    public BluetoothConnect(BluetoothAdapter adapter, BluetoothDevice device, View v) {
        BluetoothSocket tmp = null;
        bluetoothAdapter = adapter;
        view = v;

        try {
            tmp = device.createRfcommSocketToServiceRecord(SSP_UUID);
        } catch (IOException e) {
            Snackbar.make(view, "La conexión ha fallado", Snackbar.LENGTH_SHORT)
                    .show();
            Log.e("CONNECTION_FAILED","Couldn't create socket");
        }

        bluetoothSocket = tmp;
    }

    @Override
    public void run() {
        bluetoothAdapter.cancelDiscovery();

        try {
            bluetoothSocket.connect();
            Snackbar.make(view, "Conectado", Snackbar.LENGTH_LONG)
                    .show();
        } catch (IOException e) {
            String deviceName = bluetoothSocket.getRemoteDevice().getName();
            Snackbar.make(view, String.format("No se pudo conectar a %s", deviceName) , Snackbar.LENGTH_SHORT)
                    .show();
            Log.e("CONNECTION_FAILED","Couldn't connect to SSP");
            cancel();
        }
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("ERROR_CLOSING_SOCKET", "Couldn't close client socket");
        }
    }
}
