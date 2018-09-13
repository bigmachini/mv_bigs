package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.services.APIResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckPhoneActivity extends AppCompatActivity {

    // EditText edtPhoneNumber;
    // Button btnCheckNumber;
    private Context mContext;
    TextInputLayout phoneNumberWrapper;
    TextInputEditText phoneNumberEditText;
    AppCompatButton checkNumberButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = CheckPhoneActivity.this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);

        phoneNumberWrapper = (TextInputLayout) findViewById(R.id.client_phone_number_input_layout);
        phoneNumberEditText = (TextInputEditText) findViewById(R.id.edt_phone_number);
        checkNumberButton = (AppCompatButton) findViewById(R.id.btn_check_account);

        checkNumberButton.setOnClickListener(view -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.length() == 0) {
                phoneNumberWrapper.setError(getString(R.string.invalid_phone_number));
                return;
            }

            checkPhone(mContext, phoneNumber);
            Utils.setStringSetting(mContext, Constants.PHONE_NUMBER, phoneNumber);
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumberWrapper.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void checkPhone(Context context, final String phoneNumber) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("phone_number", phoneNumber);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIResponse<Boolean>> call = myAPI.checkAccount(params);
            call.enqueue(new Callback<APIResponse<Boolean>>() {
                @Override
                public void onResponse(Call<APIResponse<Boolean>> call, Response<APIResponse<Boolean>> response) {
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                if (response.body().data) {

                                    startActivity(new Intent(CheckPhoneActivity.this, LoginActivity.class));
                                } else {
                                    startActivity(new Intent(CheckPhoneActivity.this, RegistrationActivity.class));
                                }
                            }
                        } else {
                            Toast.makeText(mContext, getString(R.string.some_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<APIResponse<Boolean>> call, Throwable t) {
                    Toast.makeText(mContext, "Something went wrong, check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mContext, "Please connect your data first", Toast.LENGTH_SHORT).show();
        }
    }

}
