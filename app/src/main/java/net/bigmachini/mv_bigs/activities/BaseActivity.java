package net.bigmachini.mv_bigs.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.models.UserModel;

public class BaseActivity extends AppCompatActivity implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Context mContext;
    private boolean crlf = false;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private MenuItem actionConnect, actionDisconnect;
    public static BluetoothSerial bluetoothSerial;
    ProgressDialog progressDialog;



    public void initializa(Context context) {
        mContext = context;
        new ProgressDialog(context);
        bluetoothSerial = new BluetoothSerial(context, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check Bluetooth availability on the device and set up the Bluetooth adapter
        bluetoothSerial.setup();
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
                if (Global.gAddress != null) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            bluetoothSerial.connect(Global.gAddress);
                        }
                    };

                    thread.start();
                }
            }
        }
    }


    protected void onStop() {
        // Disconnect from the remote device and close the serial port
        bluetoothSerial.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Global.gSelectedUser1 = null;
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
        } else if (id == R.id.action_delete_all) {
            progressDialog.setMessage("Deleting records .. ");
            progressDialog.setCancelable(true);
            progressDialog.show();
            Global.gSelectedAction = Constants.DELETE_ALL;
            deleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        switch (Global.gSelectedAction) {
            case Constants.ENROLL:
            case Constants.DELETE:
                if (Global.gSelectedUser1 != null && Global.gSelectedKey != 0) {

                    Global.gSelectedUser1.addKey(Global.gSelectedKey);
                    UserModel.saveUser(mContext, Global.gSelectedUser1);
                    Utils.toastText(mContext, "ID: " + Global.gSelectedKey + " Added Successfully");
                    Global.gSelectedUser1 = null;
                    Global.gSelectedKey = 0;
                    //mAdapter.clear();
                    //mAdapter.updateList();
                    progressDialog.dismiss();
                }
                break;

            case Constants.DELETE_ALL:
               // mAdapter.updateList(new ArrayList<UserModel>());
                Utils.toastText(mContext, "Deleted Successfully");
                progressDialog.dismiss();
                Global.gSelectedUser1 = null;
                Global.gSelectedKey = 0;
                Utils.setIntSetting(mContext, Constants.COUNTER, 0);
                break;

            default:
                break;
        }
        Global.gSelectedAction = "";
    }

    @Override
    public void onBluetoothSerialWrite(String message) {
        Log.e("bluetooth", "message: " + message);
    }

    /* Implementation of BluetoothDeviceListDialog.OnDeviceSelectedListener */

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        // Connect to the selected remote Bluetooth device
        Global.gDevice = device;
        Global.gAddress = device.getAddress();
        bluetoothSerial.connect(device);
    }
}

