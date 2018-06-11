package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

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
        final Context mContext = SplashScreen.this;
        Utils.setIntSetting(mContext, Constants.COUNTER, 0);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RegistrationModel registrationModel = new Gson().fromJson(Utils.getStringSetting(mContext, Constants.REGISTRATION_MODEL, ""), RegistrationModel.class);

                if (registrationModel == null) {
                    startActivity(new Intent(SplashScreen.this, RegistrationActivity.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }

                finish();
            }
        }, 3000);

       // Utils.setStringSetting(mContext, Constants.USERS, "[]");

    }

}
