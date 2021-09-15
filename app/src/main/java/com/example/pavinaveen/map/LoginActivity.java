package com.example.pavinaveen.map;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText password, phone_number;
    String passTxt, phoneTxt;

    Button lb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = findViewById(R.id.confirm_pass);
        phone_number = findViewById(R.id.phonenumber);
        lb = findViewById(R.id.loading_btn);
        lb.setTypeface(Typeface.SERIF);

    }

    public void create_account(View view) {
        Toast.makeText(this, "Create new Account", Toast.LENGTH_SHORT).show();
        Intent ac_page = new Intent(this, RegisterPage.class);
        startActivity(ac_page);
    }

    public void forgot(View view) {
//        Intent forgot = new Intent(this,Forgot_password.class);
//        startActivity(forgot);
        Toast.makeText(this, "Forgot Password", Toast.LENGTH_SHORT).show();
    }

    public void login(View view) {

        passTxt = password.getText().toString().trim();
        phoneTxt = phone_number.getText().toString().trim();


        if (TextUtils.isEmpty(phoneTxt)) {
            phone_number.setError("Enter the phone number");
            phone_number.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passTxt)) {
            password.setError("Enter the password");
            password.requestFocus();
            return;
        }

        //Displaying a progress dialog
//        final ProgressDialog loading = ProgressDialog.show(this, "Logging in", "Please wait...", false, false);

        Intent ac_page = new Intent(this, MapsActivity.class);
        startActivity(ac_page);

    }

}
