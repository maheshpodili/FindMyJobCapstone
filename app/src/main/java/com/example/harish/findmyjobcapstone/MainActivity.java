package com.example.harish.findmyjobcapstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button signin;
    EditText e,p;
    ProgressDialog progressDialog;
    TextView tv1,tv2;
    List<User> list;
    private FirebaseAuth mAuthe;

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private  String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    /*private Button btnSignOut;*/
    private int RC_SIGN_IN = 1;

    FirebaseDatabase database;
    DatabaseReference reference;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signin=findViewById(R.id.signin);

        mAuthe = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        tv1=findViewById(R.id.createacc);
        tv2=findViewById(R.id.forgotpassword);
        database= FirebaseDatabase.getInstance();
        reference=database.getReference();

        e=findViewById(R.id.remail);
        p=findViewById(R.id.rpass);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String email = e.getText().toString();
                String password = p.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(MainActivity.this, "enter password", Toast.LENGTH_LONG).show();
                }
                else {
                    if (isConnected()){
                        progressDialog.setMessage("SignInPlz Wait...");
                        progressDialog.show();

                        mAuthe.signInWithEmailAndPassword(email, password)

                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseUser user = mAuthe.getCurrentUser();
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this,"login sucess", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, Main2Activity.class));
                                            //finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                });

                    }
                    else {
                        Toast.makeText(MainActivity.this, "check your connection", Toast.LENGTH_SHORT).show();
                    }


                }



            }
        });

        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        /*   btnSignOut = findViewById(R.id.sign_out_button);*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        preferences=getSharedPreferences("MyPreferences",MODE_PRIVATE);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    public boolean isConnected()
    {
        boolean connected = false;
        try
        {
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e)
        {

        }
        return connected;
    }
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);

        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }

        else{
            Toast.makeText(MainActivity.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser fUser){
        /*btnSignOut.setVisibility(View.VISIBLE);*/
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            SharedPreferences.Editor editor=preferences.edit();
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            String personPhoto = String.valueOf(account.getPhotoUrl());
            editor.putString("name",personName);
            editor.putString("email",personEmail);
            editor.putString("photourl",personPhoto);
            editor.apply();

          /*  Toast.makeText(MainActivity.this,personName + personEmail ,Toast.LENGTH_SHORT).show();*/
            Intent i=new Intent(this,Main2Activity.class);
            i.putExtra("n",personName);
            i.putExtra("e",personEmail);
            i.putExtra("url",personPhoto);
            startActivity(i);
        }

    }




    public void create(View view) {
        Intent i=new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void forgotpass(View view) {
        Intent i=new Intent(this,ForgotActivity.class);
        startActivity(i);
    }



}
