package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.models.RegistrationModel;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AppCenter.start(getApplication(), "856d64ed-a118-41ad-ad5c-d7f8a165a99b", Analytics.class, Crashes.class);
        final Context mContext = SplashScreen.this;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    RegistrationModel registrationModel = new Gson().fromJson(Utils.getStringSetting(mContext, Constants.REGISTRATION_MODEL, ""), RegistrationModel.class);
                    Log.e("error" , "registration model " + new Gson().toJson(registrationModel));

                    if (registrationModel == null) {
                        startActivity(new Intent(SplashScreen.this, CheckPhoneActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    }

                    finish();
                } catch (Exception ex) {
                    startActivity(new Intent(SplashScreen.this, CheckPhoneActivity.class));
                } finally {
                    finish();
                }
            }
        }, 3000);


        // Utils.setStringSetting(mContext, Constants.USERS, "[]");

    }

}
