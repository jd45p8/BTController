package com.example.btcontroller.buetooth;

public interface BluetoothCommunicationInterface {
    /**
     * Method for handling bytes received
     * @param bytes
     */
    abstract void bytesReceived(byte[] bytes);

    /**
     * Method for handling connection stopped event
     */
    abstract void onConnectionStopped();
}