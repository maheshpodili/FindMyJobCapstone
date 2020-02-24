package com.example.harish.findmyjobcapstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    Button reg;
    EditText name, pass;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuthe;

    /*Button signin;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg=findViewById(R.id.reg);
        name=findViewById(R.id.et1);
        pass=findViewById(R.id.et2);
        mAuthe = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);



        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = name.getText().toString();
                String password = pass.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "enter a mail id", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.setMessage("SigningUp...");
                    progressDialog.show();
                    mAuthe.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuthe.getCurrentUser();
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, "sucess", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));



                                    } else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    public void login(View view) {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }
}
