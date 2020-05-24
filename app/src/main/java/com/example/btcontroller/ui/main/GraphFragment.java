package com.example.btcontroller.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btcontroller.R;
import com.example.btcontroller.buetooth.BluetoothStreamsInterface;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class GraphFragment extends Fragment {
    private BluetoothStreamsInterface BSI;
    private View currentView;
    private JoystickCanvas joystickCanvas;
    private Thread readThread;
    private boolean readingData;

    /**
     * Method for creating new class instances
     * @param BSI interface to communicate with the main activity
     * @return the fragment object
     */
    public static GraphFragment newInstance(BluetoothStreamsInterface BSI) {
        Bundle args = new Bundle();
        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        fragment.setBSI(BSI);
        return fragment;
    }

    /**
     * Assign the interface to communicate with main activity
     * @param BSI Interface
     */
    private void setBSI(BluetoothStreamsInterface BSI) {
        this.BSI = BSI;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        currentView = root;
        joystickCanvas = root.findViewById(R.id.canvas);
        readThread = null;
        readingData = false;
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BSI == null) {
            Log.e("NULL_BSI_REFERENCE","The BluetoothStreamsInterface instance reference is null");
            return;
        }

        InputStream inputStream = BSI.getBluetoothInputStream();

        if (inputStream == null) {
            return;
        }

        try {
            inputStream.skip(inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        readingData = true;

        readThread = new Thread(() -> {
            while (BSI.isConnected() && readingData) {
                String line;
                JSONObject json;
                int x, y;

                try {
                    line = br.readLine();
                    json = new JSONObject(line);
                    x = json.getInt("x") - 512;
                    y = json.getInt("y") - 512;
                    joystickCanvas.setCoordinates(x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("FAILED_READ_INPUT","Couldn't read parse input stream");
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("FAILED_READ_INPUT","Couldn't read parse input stream");
                }

                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Snackbar.make(currentView, "Se ha terminado la conexión", Snackbar.LENGTH_LONG);
        });

        readThread.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (readThread != null && readingData) {
            readingData = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (readThread != null && readingData) {
            readingData = false;
        }
    }
}