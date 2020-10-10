package com.kawcher.expansemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class RegistationActivity extends AppCompatActivity {

    private EditText emailET,pass;
    private Button registerBtn;
    private TextView signInTV;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);

        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        registation();
    }

    private void registation() {

        emailET=findViewById(R.id.emailET);
        pass=findViewById(R.id.passwordET);

        registerBtn=findViewById(R.id.registerBtn);

        signInTV=findViewById(R.id.alreadyHaveAccount);

        registerBtn.setOnClickListener(new View.OnClickListener() {
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

                progressDialog.setMessage("Progressing....");
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(RegistationActivity.this, "Registation Successful", Toast.LENGTH_SHORT).show();


                                }else {

                                    progressDialog.dismiss();
                                    Toast.makeText(RegistationActivity.this, "registation not successful.pls  try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                }

        });
    }
}