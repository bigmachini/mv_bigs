package net.bigmachini.mv_bigs;

import android.content.Context;

import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;

public class BluetoothInstance extends BluetoothSerial {

    private static volatile BluetoothSerial bluetoothSerial;

    /**
     * Constructor.
     *
     * @param context  The {@link Context} to use.
     * @param listener The {@link BluetoothSerialListener} to use.
     */
    public BluetoothInstance(Context context, BluetoothSerialListener listener) {
        super(context, listener);

        if (bluetoothSerial == null) {
            bluetoothSerial = new BluetoothSerial(context, listener);
        }
    }

    public static BluetoothSerial getInstance(Context context, BluetoothSerialListener listener) {

        if (bluetoothSerial == null) {
            bluetoothSerial = new BluetoothSerial(context, listener);
        }
        return bluetoothSerial;
    }
}
