package com.example.btcontroller.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.btcontroller.R;
import com.example.btcontroller.buetooth.BluetoothStreamsInterface;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class GraphFragment extends Fragment {
    private BluetoothStreamsInterface BSI;
    private View currentView;
    private TextView textView;
    private Thread readThread;
    private boolean readingData;

    public GraphFragment(BluetoothStreamsInterface BSI) {
        super();
        this.BSI = BSI;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        textView = root.findViewById(R.id.message);
        currentView = root;
        readThread = null;
        readingData = false;
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        InputStream inputStream = BSI.getBluetoothInputStream();
        if (inputStream  != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            readingData = true;

            readThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (BSI.isConnected() && readingData) {
                        String line = "";
                        try {
                            line = br.readLine();
                            System.out.println(line);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("FAILED_READ_INPUT","Couldn't read input stream");
                            break;
                        }

                        try {
                            sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Snackbar.make(currentView, "Se ha terminado la conexión", Snackbar.LENGTH_LONG);
                }
            });

            readThread.start();
        }
    }

    @Override
    public void onDetach() {
        if (readThread != null && readingData) {
            readingData = false;
        }
        super.onDetach();
    }
}