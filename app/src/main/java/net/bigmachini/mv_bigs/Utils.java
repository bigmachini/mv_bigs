package net.bigmachini.mv_bigs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.macroyau.blue2serial.BluetoothSerial;

import net.bigmachini.mv_bigs.activities.HomeActivity;
import net.bigmachini.mv_bigs.db.controllers.RecordController;
import net.bigmachini.mv_bigs.db.entities.RecordEntity;
import net.bigmachini.mv_bigs.db.entities.UserEntity;
import net.bigmachini.mv_bigs.structures.RecordStructure;
import net.bigmachini.mv_bigs.structures.UserStructure;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Utils {
    public static void createUser(final Context mContext) {
        new MaterialDialog.Builder(mContext)
                .title("Create User")
                .content("Enter Name")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Enter Name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String userName = input.toString();
                        String message = "";
                        if (userName.isEmpty() || userName.length() < 3) {
                            message = "Invalid name, should not be empty or less than 3 characters";
                        } else {
                            UserEntity userEntity = new UserEntity();
                            userEntity.setDeviceId(Global.gSelectedDevice.getId());
                            userEntity.setName(userName);
                            Global.gSelectedKey = incrementCounter(mContext, 1);
                            if (mContext instanceof HomeActivity) {
                                Global.gSelectedUser = userEntity;
                                Global.gSelectedAction = Constants.ENROLL;
                            }
                            UserStructure userStructure =  new UserStructure();
                            userStructure.name = userName;
                            userStructure.deviceId =  Global.gSelectedDevice.getId();

                            if( mContext instanceof  HomeActivity) {
                                ((HomeActivity)mContext).createUser(mContext, userStructure);
                            }
                        }
                        dialog.dismiss();
                    }
                }).positiveColorRes(R.color.colorGreen)
                .negativeText("Close")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .show();
    }

    /**
     * Create a dialog with an icon
     *
     * @param context
     * @param title
     * @param errorMessage
     * @param drawable
     */
    public static void showDialog(Context context, String title, String errorMessage, Drawable drawable) {
        new MaterialDialog.Builder(context)
                .title(title)
                .icon(drawable)
                .content(errorMessage)
                .positiveText("dismiss")
                .show();
    }

    public static int incrementCounter(Context mContext, int i) {
        int counter = Utils.getIntSetting(mContext, Constants.COUNTER, 0) + i;
        saveCounter(mContext, counter);
        return counter;
    }

    private static void saveCounter(Context mContext, int count) {
        Utils.setIntSetting(mContext, Constants.COUNTER, count);
    }

    /**
     * Create a dialog without an icon
     *
     * @param context
     * @param title
     * @param errorMessage
     */
    public static void showDialog(Context context, String title, String errorMessage) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(errorMessage)
                .positiveText("dismiss")
                .show();
    }

    /**
     * Return current date as a sting
     *
     * @return
     */
    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }


    public static String getDateTimeFromTimeStamp(String time, String dateTimeFormatString) {
        //return null when time is null
        if (time == null || time.isEmpty())
            return null;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time) * 1000);
        String date = DateFormat.format(dateTimeFormatString, cal).toString();
        return date;
    }


    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Verifies phone number, takes in phone, lenght and what it should start with
     *
     * @param phone
     * @param length
     * @param startWith
     * @return true or false
     */
    public static boolean isPhoneNumberValid(String phone, int length, String startWith) {
        return Patterns.PHONE.matcher(phone).matches() && phone.trim().length() == length && phone.startsWith(startWith);
    }


    public static Boolean CheckConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo activeWIFIInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void toastText(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }


    public static int getIntSetting(Context mContext, String key, int defaultValue) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        return pref.getInt(key, defaultValue);
    }

    public static void setIntSetting(Context mContext, String key, int val) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static void setLongSetting(Context mContext, String key, long val) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, val);
        editor.commit();
    }

    public static long getLongSetting(Context mContext, String key, long defaultValue) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        return pref.getLong(key, defaultValue);
    }

    public static String getStringSetting(Context mContext, String key, String defaultValue) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        return pref.getString(key, defaultValue);
    }

    public static void setStringSetting(Context mContext, String key, String val) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public static boolean getBooleanSetting(Context mContext, String key, Boolean defaultValue) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        return pref.getBoolean(key, defaultValue);
    }

    public static void setBooleanSetting(Context mContext, String key, boolean val) {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }


    public static void sendMessage(final BluetoothSerial bluetoothSerial, final String action, final String key) {
        boolean check = bluetoothSerial.isConnected();
        new Thread() {
            public void run() {
                try {
                    switch (action) {
                        case Constants.ENROLL:
                            bluetoothSerial.write(Constants.ENROLL);
                            break;

                        case Constants.DELETE:
                            bluetoothSerial.write(Constants.DELETE);
                            break;

                        case Constants.DELETE_ALL:
                            bluetoothSerial.write(Constants.DELETE_ALL);
                            break;
                    }
                    sleep(3000);
                    bluetoothSerial.write(String.valueOf(key));
                } catch (Exception e) {
                }

            }
        }.start();

    }


    public static  void updateRecordDatabase(List<RecordStructure> data, RecordController mRecordController ) {
        for (RecordStructure recordStructure : data) {
            RecordEntity recordEntity = new RecordEntity();
            recordEntity.setId(recordStructure.id);
            recordEntity.setName(recordStructure.name);
            recordEntity.setUserId(recordStructure.userId);
            mRecordController.createRecord(recordEntity);
        }
    }

}

