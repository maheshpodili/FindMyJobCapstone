package com.example.harish.findmyjobcapstone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.VISIBLE;

public class ForgotActivity extends AppCompatActivity {

    Button forgot;
    EditText Email;
    FirebaseAuth auth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);


        Email = findViewById(R.id.remail);
        auth = FirebaseAuth.getInstance();
        progressBar=new ProgressBar(this);
        forgot= findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String email = Email.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(ForgotActivity.this,"please enter details", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(VISIBLE);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(ForgotActivity.this, "reset sucess", Toast.LENGTH_SHORT).show();
                                } else

                                {
                                    Toast.makeText(ForgotActivity.this, "reset not sucess", Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);

                            }
                        });

            }
        });


    }

}

