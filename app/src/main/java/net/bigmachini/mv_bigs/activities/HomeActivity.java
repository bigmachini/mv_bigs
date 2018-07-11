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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.UserAdapter;
import net.bigmachini.mv_bigs.db.controllers.RecordController;
import net.bigmachini.mv_bigs.db.controllers.UserController;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.db.entities.UserEntity;
import net.bigmachini.mv_bigs.services.APIListResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.RecordStructure;
import net.bigmachini.mv_bigs.structures.UserStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public RecyclerView mRecyclerView;
    public UserAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    private Button btnDelete;
    public List<UserEntity> users;
    private boolean crlf = false;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private MenuItem actionConnect, actionDisconnect;
    public static BluetoothSerial bluetoothSerial;
    ProgressDialog progressDialog;
    private UserController mUserController;
    private RecordController mRecordController;
    private ScrollView svTerminal;
    static StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*    if (bluetoothSerial.checkBluetooth()) {
                    checkBluetooth();
                    if (!bluetoothSerial.isConnected()) {
                        Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.setMessage("Creating User");
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        Utils.createUser(mContext, progressDialog);
                    }
                } else {
                    enableBluetooth();
                }*/


                Utils.createUser(mContext);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = HomeActivity.this;
        btnDelete = findViewById(R.id.btn_delete);
        mUserController = new UserController(mContext);
        mRecordController = new RecordController(mContext);
        mRecyclerView = findViewById(R.id.rv_users);
        svTerminal = findViewById(R.id.sv_terminal);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<UserEntity> dUsers = new ArrayList<>(mAdapter.getUsers());
                StringBuilder sb = new StringBuilder();
                for (int i = dUsers.size() - 1; i >= 0; i--) {
                    if (dUsers.get(i).isSelected()) {
                        UserEntity user = dUsers.get(i);
                        List<RecordEntity> recordEntities = mRecordController.getRecordsByUserId(user.getId());
                        if (recordEntities.size() > 0) {
                            sb.append(user.getName() + "\n");
                        } else {
                            dUsers.remove(i);
                        }
                    }
                }
                if (sb.toString().isEmpty() || sb.toString().length() != 0) {
                    Utils.toastText(mContext, "Please delete all ids for user(s):\n" + sb.toString());
                }

                mAdapter.clear();
                mAdapter.updateList(dUsers);
            }
        });

        // specify an adapter (see also next example)
        progressDialog = new ProgressDialog(this);
        mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
        mRecyclerView.setAdapter(mAdapter);
        // Create a new instance of BluetoothSerial
        bluetoothSerial = new BluetoothSerial(this, this);
        getUser(mContext);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check Bluetooth availability on the device and set up the Bluetooth adapter
        bluetoothSerial.setup();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
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
                    connectDevice();
                }
            }
        }
    }

    public void connectDevice() {
        progressDialog.setTitle("Connecting to device.");
        progressDialog.setMessage("Please wait ... ");
        progressDialog.show();
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


    public boolean checkIfConnected() {
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect from the remote device and close the serial port
        bluetoothSerial.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.gSelectedUser = null;
        Global.gSelectedKey = 0;
        Global.gAddress = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        } else if (id == R.id.action_select_device) {
            startActivity(new Intent(HomeActivity.this, DeviceActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_delete_all) {
            progressDialog.setMessage("Deleting records .. ");
            progressDialog.setCancelable(true);
            Global.gSelectedAction = Constants.DELETE_ALL;
            deleteAll();
            return true;
        }
        return super.

                onOptionsItemSelected(item);

    }

    private void deleteAll() {
        checkBluetooth();
        if (!bluetoothSerial.isConnected()) {
            Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
        } else {
            bluetoothSerial.write(Constants.DELETE_ALL);
        }
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
        if (bluetoothSerial.checkBluetooth()) {
            BluetoothDeviceListDialog dialog = new BluetoothDeviceListDialog(this);
            dialog.setOnDeviceSelectedListener(this);
            dialog.setTitle(R.string.paired_devices);
            dialog.setDevices(bluetoothSerial.getPairedDevices());
            dialog.showAddress(true);

            dialog.show();
        } else {
            enableBluetooth();
        }
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
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
        sb.append(message);
        if (sb.toString().contains(",")) {
            String res = sb.toString().trim();
            res = res.replace(',', ' ');
            res = res.trim();
            int response = Integer.parseInt(res);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            switch (Global.gSelectedAction) {
                case Constants.ENROLL:
                    if (response < 127) {
                        if (Global.gSelectedUser != null) {
                            createRecord(mContext, response);
                        }
                    } else {
                        Global.gSelectedUser = null;
                        Global.gSelectedKey = 0;
                        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Operation failed!")
                                .setContentText("Try again")
                                .show();
                        Global.gSelectedAction = "";
                    }
                    break;

                case Constants.DELETE_ALL:
                    mAdapter.updateList(new ArrayList<UserEntity>());
                    new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("SUCCESS!")
                            .setContentText("Delete Successful")
                            .show();
                    progressDialog.dismiss();
                    Global.gSelectedUser = null;
                    Global.gSelectedKey = 0;
                    Utils.setIntSetting(mContext, Constants.COUNTER, 0);
                    break;

                default:
                    break;
            }
            Global.gSelectedAction = "";
        }
    }

    @Override
    public void onBluetoothSerialWrite(String message) {
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
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        Global.gDevice = device;
        Global.gAddress = device.getAddress();
        bluetoothSerial.connect(device);
    }

    /* End of the implementation of listeners */

    private final Runnable scrollTerminalToBottom = new Runnable() {
        @Override
        public void run() {
            //Scroll the terminal screen to the bottom
            svTerminal.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };


    private void updateDatabase(List<UserStructure> data) {
        for (UserStructure userStructure : data) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(userStructure.id);
            userEntity.setName(userStructure.name);
            userEntity.setDeviceId(userStructure.deviceId);
            mUserController.createUser(userEntity);
        }
    }

    private void updateRecordDatabase(List<RecordStructure> data) {
        for (RecordStructure recordStructure : data) {
            RecordEntity recordEntity = new RecordEntity();
            recordEntity.setId(recordStructure.id);
            recordEntity.setName(recordStructure.name);
            recordEntity.setUserId(recordStructure.userId);
            mRecordController.createRecord(recordEntity);
        }
    }

    public void createUser(Context context, UserStructure userStructure) {
        if (Utils.CheckConnection(context)) {
            progressDialog.setMessage("Creating User");
            progressDialog.setCancelable(true);
            progressDialog.show();
            progressDialog.show();
            HashMap<String, Object> params = new HashMap<>();
            params.put("device_id", userStructure.deviceId);
            params.put("name", userStructure.name);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<UserStructure>> call = myAPI.createDeviceUser(params);
            call.enqueue(new Callback<APIListResponse<UserStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<UserStructure>> call, Response<APIListResponse<UserStructure>> response) {

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<UserStructure> users = response.body().data;
                                updateDatabase(users);
                                mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("SUCCESS!")
                                        .setContentText("User: " + Global.gSelectedUser.getName() + " Added Successfully")
                                        .show();

                                if (users.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_users_found), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_users_found), Toast.LENGTH_LONG).show();

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
                public void onFailure(Call<APIListResponse<UserStructure>> call, Throwable t) {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    t.printStackTrace();
                }
            });
        } else {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (mAdapter != null) {
                mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }


    public void createRecord(Context context, int recordId) {
        if (Utils.CheckConnection(context)) {
            progressDialog.setMessage("Creating Record");
            progressDialog.setCancelable(false);
            progressDialog.show();
            HashMap<String, Object> params = new HashMap<>();
            params.put("name", recordId);
            params.put("user_id", Global.gSelectedUser.getId());
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<RecordStructure>> call = myAPI.createUserRecord(params);
            call.enqueue(new Callback<APIListResponse<RecordStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<RecordStructure>> call, Response<APIListResponse<RecordStructure>> response) {

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<RecordStructure> records = response.body().data;
                                updateRecordDatabase(records);
                                mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("SUCCESS!")
                                        .setContentText("User: " + Global.gSelectedUser.getName() + " Added Successfully")
                                        .show();

                                if (users.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_users_found), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_users_found), Toast.LENGTH_LONG).show();

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

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (mAdapter != null) {
                mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
    public void getUser(Context context) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("device_id", Global.gSelectedDevice.getId());
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<UserStructure>> call = myAPI.getDeviceUser(params);
            call.enqueue(new Callback<APIListResponse<UserStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<UserStructure>> call, Response<APIListResponse<UserStructure>> response) {
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<UserStructure> users = response.body().data;
                                updateDatabase(users);
                                mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
                                mRecyclerView.setAdapter(mAdapter);

                                if (users.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_users_found), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_users_found), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(mContext, response.body().strMessage.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<APIListResponse<UserStructure>> call, Throwable t) {
                }
            });
        } else {
            if (mAdapter != null) {
                mAdapter = new UserAdapter(mContext, btnDelete, progressDialog);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
