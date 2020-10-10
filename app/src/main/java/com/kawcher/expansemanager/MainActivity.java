package com.kawcher.expansemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    private EditText emailET,pass;
    private Button login;
    private TextView noAccountTV,forgetPassword;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() !=null){

            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

        progressDialog=new ProgressDialog(this);

        login();
    }

    private void login() {

        emailET=findViewById(R.id.emailET);
        pass=findViewById(R.id.passwordET);

        login=findViewById(R.id.loginBtn);

       noAccountTV=findViewById(R.id.noAccountTV);
        forgetPassword=findViewById(R.id.forgetTV);

        progressDialog.setMessage("Progressing...");

       login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  email= emailET.getText().toString().trim();
                String password=pass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){

                    emailET.setError("Email Required...");
                    return;
                }
                if(TextUtils.isEmpty(password)||password.length()<6){

                    pass.setError("pls  enter at least six characer in password");
                    return;
                }

                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "login successful", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                } else {

                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "failed to login.pls try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });





            }

        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetPassword();
            }


        });

        noAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),RegistationActivity.class));
            }
        });
    }

    private void resetPassword() {

        startActivity(new Intent(getApplicationContext(),ResetActivity.class));
    }
}