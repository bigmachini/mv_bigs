package net.bigmachini.mv_bigs;

import android.bluetooth.BluetoothDevice;

import net.bigmachini.mv_bigs.models.UserModel;

public class Global {
    public static int gSelectedKey = 0;
    public static UserModel gSelectedUser = null;
    public static String gSelectedAction = "";
    public static BluetoothDevice gDevice= null;
    public static BluetoothDevice gDevice2= null;
    public static String gAddress= null;
    public static BluetoothInstance gSerial= null;
}
