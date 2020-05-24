package com.example.btcontroller;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

import com.example.btcontroller.buetooth.BluetoothStreamsInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.btcontroller.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity implements BluetoothStreamsInterface {

    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private BluetoothSocket bluetoothSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle(), this);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);

        new TabLayoutMediator(tabs, viewPager2, (tab, position)
                -> tab.setText(TAB_TITLES[position])).attach();

        bluetoothSocket = null;
    }

    @Override
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    @Override
    public InputStream getBluetoothInputStream() {
        try {
            return bluetoothSocket.getInputStream();
        } catch (Exception e) {
            Log.e("GET_INPUT_STREAM_FAILED","Couldn't get input stream");
            Snackbar.make(findViewById(R.id.main), "Algo ha salido mal", Snackbar.LENGTH_LONG);
            return null;
        }
    }

    @Override
    public OutputStream getBluetoothOutputStream() {
        try {
            return bluetoothSocket.getOutputStream();
        } catch (Exception e) {
            Log.e("GET_INPUT_STREAM_FAILED","Couldn't get output stream");
            Snackbar.make(findViewById(R.id.main), "Algo ha salido mal", Snackbar.LENGTH_LONG);
            return null;
        }
    }

    @Override
    public boolean isConnected() {
        if (bluetoothSocket != null) {
            return bluetoothSocket.isConnected();
        }

        return false;
    }
}