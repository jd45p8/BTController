package com.example.btcontroller.buetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnection extends Thread {
    private static final UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] buffer;
    private final View view;

    public BluetoothConnection(BluetoothAdapter adapter, BluetoothDevice device, View v) {
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
        if (connectToDevice() == 0) {
            buffer = new byte[1024];
            int numBytes;

            while (bluetoothSocket.isConnected()) {
                try {
                    numBytes = inputStream.read(buffer);
                } catch (IOException e) {
                    Log.e("FAILED_READ_INPUT","Couldn't read input stream");
                }
            }
            Snackbar.make(view, "Se ha terminado la conexión", Snackbar.LENGTH_LONG);
        }
    }

    /**
     * Starts bluetooth connection and gets input and output streams
     * @return 0 for success.
     */
    private int connectToDevice() {
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
            return 1;
        }

        if (bluetoothSocket.isConnected()) {
            InputStream tmpIn;
            OutputStream tmpOut;

            try {
                inputStream = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                Log.e("GET_INPUT_STREAM_FAILED","Couldn't get input stream");
                Snackbar.make(view, "Algo ha salido mal", Snackbar.LENGTH_LONG);
                return 1;
            }

            try {
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e("GET_INPUT_STREAM_FAILED","Couldn't get output stream");
                Snackbar.make(view, "Algo ha salido mal", Snackbar.LENGTH_LONG);
                return 1;
            }
        }
        return 0;
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("ERROR_CLOSING_SOCKET", "Couldn't close client socket");
        }
    }
}
