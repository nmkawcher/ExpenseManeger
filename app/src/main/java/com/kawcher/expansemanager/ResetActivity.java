package com.kawcher.expansemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText emailET;
    private Button recoverBtn;

  private   FirebaseAuth firebaseAuth;
  private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        backBtn=findViewById(R.id.backBtn);

        recoverBtn=findViewById(R.id.recoverBtn);
        emailET=findViewById(R.id.emailET);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recoverPassword();
            }
        });
    }

   private String email;

    private void recoverPassword() {

        email=emailET.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            Toast.makeText(this, "Invalid Email...", Toast.LENGTH_SHORT).show();
            return;
        }

progressDialog.setMessage("Sending instruction to reset password...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //instruction sent

                        progressDialog.dismiss();
                        Toast.makeText(ResetActivity.this,"password reset mail send to your email",Toast.LENGTH_SHORT).show();
                        emailET.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //failed sending instructions

                        progressDialog.dismiss();
                        emailET.setText("");
                        Toast.makeText(ResetActivity.this,"error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


}