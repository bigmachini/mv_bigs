package net.bigmachini.mv_bigs.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.DeviceIdAdapter;
import net.bigmachini.mv_bigs.db.controllers.RecordController;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.models.UserModel;
import net.bigmachini.mv_bigs.services.APIListResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.RecordStructure;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceIdActivity extends AppCompatActivity
        implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {

    private static final String TAG = DeviceIdActivity.class.getSimpleName();
    public RecyclerView mRecyclerView;
    public DeviceIdAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    RecordController mRecordController;
    private Button btnDelete;
    private boolean crlf = false;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private MenuItem actionConnect, actionDisconnect;
    public BluetoothSerial bluetoothSerial;
    UserModel userModel;
    private ProgressDialog progressDialog;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_id);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = DeviceIdActivity.this;
        sb = new StringBuilder();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.CheckConnection(mContext)) {
                    if (!bluetoothSerial.isConnected()) {
                        Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                    } else {
                        List<RecordEntity> records;
                        int recordId;
                        do {
                            recordId = new Random().nextInt(127);
                            records = mRecordController.getRecordByUser(String.valueOf(recordId), Global.gSelectedUser.getId());
                        } while (records.size() > 0);
                        Global.gSelectedKey = recordId;
                        Utils.sendMessage(bluetoothSerial, Constants.ENROLL, String.valueOf(recordId));
                        UserModel.saveUser(mContext, userModel);
                    }
                } else {
                    Utils.toastText(mContext, getString(R.string.no_internet));
                }
            }
        });
        fab.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecordController = new

                RecordController(mContext);

        getRecords(mContext);

        btnDelete =

                findViewById(R.id.btn_delete);

        userModel = Global.gSelectedUser1;
        mRecyclerView =

                findViewById(R.id.rv_device_ids);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new

                LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        progressDialog = new

                ProgressDialog(this);
        // specify an adapter (see also next example)
        mAdapter = new

                DeviceIdAdapter(mContext, progressDialog);
        mRecyclerView.setAdapter(mAdapter);
        // Create a new instance of BluetoothSerial
        bluetoothSerial = new

                BluetoothSerial(this, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check Bluetooth availability on the device and set up the Bluetooth adapter
        bluetoothSerial.setup();

        if (mAdapter != null) {
            mAdapter = new DeviceIdAdapter(mContext, progressDialog);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetooth();
    }


    public void checkBluetooth() {
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            if (!bluetoothSerial.isConnected()) {
                bluetoothSerial.start();
                if (Global.gDevice != null) {
                    bluetoothSerial.connect(Global.gDevice);
                    connectDevice();
                }
            }
        }
    }

    public void connectDevice() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothSerial.connect(Global.gDevice);
                invalidateOptionsMenu();
                updateBluetoothState();
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect from the remote device and close the serial port
        bluetoothSerial.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        actionConnect = menu.findItem(R.id.action_connect);
        actionDisconnect = menu.findItem(R.id.action_disconnect);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_connect) {
            showDeviceListDialog();
            return true;
        } else if (id == R.id.action_disconnect) {
            bluetoothSerial.stop();
            Global.gAddress = null;
            return true;
        } else if (id == R.id.action_crlf) {
            crlf = !item.isChecked();
            item.setChecked(crlf);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invalidateOptionsMenu() {
        if (bluetoothSerial == null)
            return;

        // Show or hide the "Connect" and "Disconnect" buttons on the app bar
        if (bluetoothSerial.isConnected()) {
            if (actionConnect != null)
                actionConnect.setVisible(false);
            if (actionDisconnect != null)
                actionDisconnect.setVisible(true);
        } else {
            if (actionConnect != null)
                actionConnect.setVisible(true);
            if (actionDisconnect != null)
                actionDisconnect.setVisible(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                // Set up Bluetooth serial port when Bluetooth adapter is turned on
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothSerial.setup();
                }
                break;
        }
    }

    private void updateBluetoothState() {
// Get the current Bluetooth state
        final int state;
        if (bluetoothSerial != null)
            state = bluetoothSerial.getState();
        else
            state = BluetoothSerial.STATE_DISCONNECTED;

        // Display the current state on the app bar as the subtitle
        String subtitle;
        switch (state) {
            case BluetoothSerial.STATE_CONNECTING:
                subtitle = getString(R.string.status_connecting);
                break;
            case BluetoothSerial.STATE_CONNECTED:
                subtitle = getString(R.string.status_connected, bluetoothSerial.getConnectedDeviceName());
                break;
            default:
                subtitle = getString(R.string.status_disconnected);

                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    private void showDeviceListDialog() {
        // Display dialog for selecting a remote Bluetooth device
        BluetoothDeviceListDialog dialog = new BluetoothDeviceListDialog(this);
        dialog.setOnDeviceSelectedListener(this);
        dialog.setTitle(R.string.paired_devices);
        dialog.setDevices(bluetoothSerial.getPairedDevices());
        dialog.showAddress(true);
        dialog.show();
    }

    /* Implementation of BluetoothSerialListener */

    @Override
    public void onBluetoothNotSupported() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.no_bluetooth)
                .setPositiveButton(R.string.action_quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBluetoothDisabled() {

    }

    public void enableBluetooth() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
    }

    @Override
    public void onBluetoothDeviceDisconnected() {
        invalidateOptionsMenu();
        updateBluetoothState();
    }

    @Override
    public void onConnectingBluetoothDevice() {
        updateBluetoothState();
    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address) {
        invalidateOptionsMenu();
        updateBluetoothState();

    }

    @Override
    public void onBluetoothSerialRead(String message) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        sb.append(message);
        if (sb.toString().contains(",")) {
            Log.e("TAG", "message: " + sb.toString());
            String res = sb.toString().trim().replace("enroll","").replace("empty","");
            sb = new StringBuilder();
            res = res.replace(',', ' ');
            res = res.trim();
            int response = Integer.parseInt(res);
            switch (Global.gSelectedAction) {
                case Constants.DELETE:
                    if (Global.gSelectedUser != null) {
                        deleteRecord(mContext, response);
                    }
                    break;
            }
        }

    }

    @Override
    public void onBluetoothSerialWrite(String message) {

        Log.e("TAG", "message out: " + message);
        // Print the outgoing message on the terminal screen
//        tvTerminal.append(getString(R.string.terminal_message_template,
//                bluetoothSerial.getLocalAdapterName(),
//                message));
//        svTerminal.post(scrollTerminalToBottom);
    }

    /* Implementation of BluetoothDeviceListDialog.OnDeviceSelectedListener */

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        // Connect to the selected remote Bluetooth device
        Global.gDevice = device;
        bluetoothSerial.connect(device);
    }

    /* End of the implementation of listeners */

    private final Runnable scrollTerminalToBottom = new Runnable() {
        @Override
        public void run() {
            // Scroll the terminal screen to the bottom
            // svTerminal.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    public void getRecords(Context context) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("user_id", Global.gSelectedUser.getId());
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<RecordStructure>> call = myAPI.getUserRecords(params);
            call.enqueue(new Callback<APIListResponse<RecordStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<RecordStructure>> call, Response<APIListResponse<RecordStructure>> response) {
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<RecordStructure> records = response.body().data;
                                if (records == null || records.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_record), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                updateDatabase(records);
                                mAdapter = new DeviceIdAdapter(mContext, progressDialog);
                                mRecyclerView.setAdapter(mAdapter);

                                if (records.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_record), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_record), Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(mContext, response.body().strMessage.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIListResponse<RecordStructure>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            if (mAdapter != null) {
                mAdapter = new DeviceIdAdapter(mContext, progressDialog);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateDatabase(List<RecordStructure> data) {
        for (RecordStructure recordStructure : data) {
            RecordEntity recordEntity = new RecordEntity();
            recordEntity.setId(recordStructure.id);
            recordEntity.setName(recordStructure.name);
            recordEntity.setUserId(recordStructure.userId);
            mRecordController.createRecord(recordEntity);
        }
    }


    public void deleteRecord(Context context, final int recordId) {
        if (Utils.CheckConnection(context)) {

            HashMap<String, Object> params = new HashMap<>();
            params.put("id", recordId);
            params.put("user_id", Global.gSelectedUser.getId());
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<RecordStructure>> call = myAPI.deleteRecorod(params);
            call.enqueue(new Callback<APIListResponse<RecordStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<RecordStructure>> call, Response<APIListResponse<RecordStructure>> response) {

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<RecordStructure> records = response.body().data;
                                if (records.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_record), Toast.LENGTH_LONG).show();
                                }
                                mRecordController.deleteByName(String.valueOf(recordId));
                                mAdapter = new DeviceIdAdapter(mContext, progressDialog);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("SUCCESS!")
                                        .setContentText("ID: " + Global.gSelectedKey + " Deleted Successfully")
                                        .show();
                                Global.gSelectedKey = 0;
                                Global.gSelectedAction = "";
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_record), Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(mContext, response.body().strMessage.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIListResponse<RecordStructure>> call, Throwable t) {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    t.printStackTrace();
                }
            });
        } else {
            Utils.toastText(mContext, getString(R.string.no_internet));
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (mAdapter != null) {
                mAdapter = new DeviceIdAdapter(mContext, progressDialog);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
