package com.example.btcontroller.buetooth;

import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;

public interface BluetoothStreamsInterface {
    /**
     * Method for setting Bluetooth socket
     * @param bluetoothSocket bluetooth socket
     */
    abstract void setBluetoothSocket(BluetoothSocket bluetoothSocket);

    /**
     * Method for getting Bluetooth input stream
     * @return bluetooth input stream
     */
    abstract InputStream getBluetoothInputStream();

    /**
     * Method for getting Bluetooth output stream
     * @return bluetooth output stream
     */
    abstract OutputStream getBluetoothOutputStream();

    /**
     * Method for checking bluetooth connection status
     * @return true if connection is active.
     */
    abstract boolean isConnected();

    /**
     * Method for closing bluetooth connection
     */
    abstract void closeConnection();
}