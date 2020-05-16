package com.example.btcontroller.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.btcontroller.buetooth.BluetoothCommunicationInterface;
import com.example.btcontroller.buetooth.BluetoothConnection;
import com.example.btcontroller.R;

import java.util.ArrayList;

public class ClientFragment extends Fragment {
    private final int PERMISSION_REQUEST = 101;
    private final int ENABLE_REQUEST = 102;

    private ArrayList<BluetoothDevice> deviceList;
    private BluetoothAdapter bluetoothAdapter;
    private DeviceListAdapter deviceListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity parent;

    /**
     * Thread for update bluetooth device list
     */
    private Thread discoverDevicesThread;

    /**
     * Thread for starting a bluetooth connection
     */
    private BluetoothConnection btConnect;

    /**
     * Handler for handling bluetooth discovered devices event
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceList.add(device);
                    deviceListAdapter.notifyDataSetChanged();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parent = getActivity();
        View root = inflater.inflate(R.layout.fragment_client, container, false);

        deviceList = new ArrayList<>();
        deviceListAdapter = new DeviceListAdapter();
        ListView listDevices = root.findViewById(R.id.list_devices);
        listDevices.setOnItemClickListener((parent, view, position, id) ->
                connect(deviceListAdapter.getItem(position), view)
        );
        listDevices.setAdapter(deviceListAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        parent.registerReceiver(broadcastReceiver, filter);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_devices);
        swipeRefreshLayout.setOnRefreshListener(this::startDevicesDiscovering);

        startDevicesDiscovering();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        parent.unregisterReceiver(broadcastReceiver);
    }

    /**
     * Start a thread for discovering bluetooth devices
     */
    private void startDevicesDiscovering() {
        boolean discoveringDevices = discoverDevicesThread != null && discoverDevicesThread.getState() != Thread.State.TERMINATED;
        boolean connecting = btConnect != null && btConnect.getState() != Thread.State.TERMINATED;
        if (!discoveringDevices && !connecting) {
            discoverDevicesThread = new Thread(this::discoverBluetoothDevices);
            discoverDevicesThread.start();
        } else if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Discover bluetooth devices around
     */
    private void discoverBluetoothDevices() {
        boolean bluetoothAccess = ContextCompat.checkSelfPermission(parent.getApplicationContext(),
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        boolean bluetoothAdminAccess = ContextCompat.checkSelfPermission(parent.getApplicationContext(),
                Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        boolean coarseLocationAccess = ContextCompat.checkSelfPermission(parent.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if ( bluetoothAccess && bluetoothAdminAccess && coarseLocationAccess) {
            if (bluetoothAdapter == null) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }

            deviceList.clear();
            deviceListAdapter.notifyDataSetChanged();

            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.isEnabled()) {
                    if (!swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                    bluetoothAdapter.startDiscovery();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, ENABLE_REQUEST);
                }
            }
        }  else {
            ActivityCompat.requestPermissions(parent, new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMISSION_REQUEST);
        }
    }

    /**
     * Start a thread for starting a bluetooth connection
     */
    private void connect(BluetoothDevice device, View v) {
        if (btConnect == null || btConnect.getState() == Thread.State.TERMINATED) {
            btConnect = new BluetoothConnection(bluetoothAdapter, device, v);
            btConnect.start();
        }
    }

    class DeviceListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return deviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
            }

            BluetoothDevice device = getItem(position);

            ((TextView) convertView.findViewById(R.id.text_line)).setText(device.getName());
            ((TextView) convertView.findViewById(R.id.text_sub_line)).setText(device.getAddress());
            return convertView;
        }
    }
}