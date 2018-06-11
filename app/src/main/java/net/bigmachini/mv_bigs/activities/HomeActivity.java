package net.bigmachini.mv_bigs.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.DeviceAdapter;
import net.bigmachini.mv_bigs.adapters.PairedDeviceAdapter;
import net.bigmachini.mv_bigs.adapters.UserAdapter;
import net.bigmachini.mv_bigs.models.UserModel;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DiscoveryCallback;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserModel> videoModelList = new ArrayList<>();
    private Context mContext;
    private static Bluetooth bluetooth;
    private Button btnDelete;
    private MaterialDialog dialog;
    private PairedDeviceAdapter pAdapter;
    private DeviceAdapter dAdapter;
    List<BluetoothDevice> devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTEst();
                // Utils.createUser(mContext);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = HomeActivity.this;

        bluetooth = new Bluetooth(HomeActivity.this);
        btnDelete = findViewById(R.id.btn_delete);

        mRecyclerView = findViewById(R.id.rv_users);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);

        List<UserModel> users = UserModel.getUsers(mContext);
        // specify an adapter (see also next example)
        mAdapter = new UserAdapter(mContext, users, btnDelete);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetooth.onStart();

        if (!bluetooth.isEnabled())
            bluetooth.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }

    private void showSectionSelectionDialog() {
        if (dialog == null) {
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            pAdapter = new PairedDeviceAdapter(mContext, bluetooth.getPairedDevices());
            recyclerView.setAdapter(pAdapter);
            dialog = new MaterialDialog.Builder(this)
                    .customView(recyclerView, true)
                    .build();
        } else {
            pAdapter.notifyDataSetChanged();
        }
        dialog.show();
    }

    private void dialogTEst() {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.dialog_devices, true)
                .title("Select Device")
                .build();

        View dialogView = dialog.getCustomView();
        final RecyclerView rvPariedDevice = dialogView.findViewById(R.id.rv_paired_devices);
        final RecyclerView rvDevice = dialogView.findViewById(R.id.rv_devices);
        final Button btnScan = dialogView.findViewById(R.id.btn_scan);

        bluetooth.startScanning();
        bluetooth.setDiscoveryCallback(discoveryCallback);
        bluetooth.setBluetoothCallback(bluetoothCallback);

        rvPariedDevice.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        rvPariedDevice.setLayoutManager(mLayoutManager);

        List<BluetoothDevice> pDevices = bluetooth.getPairedDevices();
        // specify an adapter (see also next example)
        pAdapter = new PairedDeviceAdapter(mContext, pDevices);
        mRecyclerView.setAdapter(pAdapter);


        rvDevice.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        rvDevice.setLayoutManager(mLayoutManager);
        devices = new ArrayList<>();
        // specify an adapter (see also next example)
        dAdapter = new DeviceAdapter(mContext, devices);
        rvDevice.setAdapter(dAdapter);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices.clear();
                bluetooth.startScanning();

            }
        });

        dAdapter.notifyDataSetChanged();

        dialog.show();
    }

    private DiscoveryCallback discoveryCallback = new DiscoveryCallback() {
        @Override
        public void onDiscoveryStarted() {
            Utils.toastText(mContext, "Discovery started");
        }

        @Override
        public void onDiscoveryFinished() {
            Utils.toastText(mContext, "Discovery Finished");
        }

        @Override
        public void onDeviceFound(BluetoothDevice device) {
            devices.add(device);
        }

        @Override
        public void onDevicePaired(BluetoothDevice device) {
            Utils.createUser(mContext);
        }

        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {
        }

        @Override
        public void onError(String message) {
            Utils.toastText(mContext, message);
        }
    };

    private BluetoothCallback bluetoothCallback = new BluetoothCallback() {
        @Override
        public void onBluetoothTurningOn()  {
            Utils.toastText(mContext, getString(R.string.bluetooth_turning_on));
        }

        @Override
        public void onBluetoothOn() {
            bluetooth.startScanning();
            dialogTEst();
        }

        @Override
        public void onBluetoothTurningOff() {
            bluetooth.stopScanning();
            Utils.toastText(mContext, "You need to enable your bluetooth...");
        }

        @Override
        public void onBluetoothOff() {
        }

        @Override
        public void onUserDeniedActivation() {
        }
    };
}
