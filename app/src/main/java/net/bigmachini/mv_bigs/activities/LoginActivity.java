package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.models.RegistrationModel;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText etPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);

        btnLogin = findViewById(R.id.btn_login);
        etPin = findViewById(R.id.et_pin);
        final Context mContext = LoginActivity.this;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = etPin.getText().toString().trim();
                if (pin.isEmpty() || pin.length() == 0) {
                    Utils.toastText(mContext, "Invalid pin");
                    return;
                }
                RegistrationModel registrationModel = new Gson().fromJson(Utils.getStringSetting(mContext, Constants.REGISTRATION_MODEL, ""), RegistrationModel.class);
                if (registrationModel.verifyPin(pin)) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {

                    Utils.toastText(mContext, "Invalid login");
                }
            }
        });
    }

}
