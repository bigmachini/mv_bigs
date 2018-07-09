package net.bigmachini.mv_bigs;

import android.bluetooth.BluetoothDevice;

import net.bigmachini.mv_bigs.db.entities.DeviceEntity;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.models.UserModel;
import net.bigmachini.mv_bigs.structures.LoginStructure;

public class Global {
    public static int gSelectedKey = 0;
    public static UserModel gSelectedUser = null;
    public static String gSelectedAction = "";
    public static BluetoothDevice gDevice = null;
    public static BluetoothDevice gDevice2 = null;
    public static String gAddress = null;
    public static BluetoothInstance gSerial = null;

    public static DeviceEntity gSelectedDevice = null;
    public static RecordEntity gSelectedRecord = null;
    public static LoginStructure gLoginStructure = null;
}
