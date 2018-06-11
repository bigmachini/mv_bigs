package net.bigmachini.mv_bigs;

import android.app.Activity;

import me.aflak.bluetooth.Bluetooth;

public class BluetoothSingleton {

    private static volatile BluetoothSingleton sSoleInstance = new BluetoothSingleton();
    public static Bluetooth cBluetooth = null;
    public static Activity activity;
    //private constructor.
    private BluetoothSingleton(){}

    public static BluetoothSingleton getInstance(Activity act) {

        if(cBluetooth == null)
        {
            cBluetooth = new Bluetooth(activity);
        }

        return sSoleInstance;
    }
}