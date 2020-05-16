package com.example.btcontroller.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btcontroller.R;
import com.example.btcontroller.buetooth.BluetoothCommunicationInterface;

public class GraphFragment extends Fragment implements BluetoothCommunicationInterface {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void bytesReceived(byte[] bytes) {

    }

    @Override
    public void onConnectionStopped() {

    }
}
