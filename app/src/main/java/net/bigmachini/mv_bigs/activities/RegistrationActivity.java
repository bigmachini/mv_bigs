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

public class RegistrationActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etPhoneNumber,etPin, etConfirmPin;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);

        btnRegister = findViewById(R.id.btn_register);
        etConfirmPin = findViewById(R.id.et_confirm_pin);
        etPin = findViewById(R.id.et_pin);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        mContext = RegistrationActivity.this;

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber  = etPhoneNumber.getText().toString().trim();
                boolean proceed = true;

                if(phoneNumber.isEmpty() || phoneNumber.length() == 0 || phoneNumber == null)
                {
                    etPhoneNumber.setError("Invalid Phone Number");
                    proceed = false;

                }

                String pin  = etPin.getText().toString().trim();

                if(pin.isEmpty() || pin.length() == 0 || pin == null)
                {
                    etPin.setError("Invalid Pin format");
                    proceed = false;
                }

                String confirmPin  = etConfirmPin.getText().toString().trim();

                if(confirmPin.isEmpty() || confirmPin.length() == 0 || confirmPin == null)
                {
                    etConfirmPin.setError("Invalid pin format");
                    proceed = false;
                }

                if(proceed == false)
                    return;

                if (!new String(pin).equals(new String(confirmPin)))
                {
                    Utils.toastText(mContext, "The pin did not match try again");
                    etConfirmPin.setText("");
                    return;
                }

                RegistrationModel registrationModel = new RegistrationModel();
                registrationModel.phoneNumber = phoneNumber;
                registrationModel.pin = pin;

                Utils.setStringSetting(mContext,  Constants.REGISTRATION_MODEL, new Gson().toJson(registrationModel).toString());
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

}
