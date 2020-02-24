package com.example.harish.findmyjobcapstone;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.harish.findmyjobcapstone.SaveJobsData.AppDatabase;
import com.example.harish.findmyjobcapstone.SaveJobsData.JobViewModel;
import com.example.harish.findmyjobcapstone.SaveJobsData.SaveJobData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String State = "state";
    StatefullRecyclerview recyclerView;
    RequestQueue requestQueue;
    List<SaveJobData> saveJobDataList;
    FirebaseAuth auth;
    AppDatabase db;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor speditor;
    String key = "jobs";
    JobViewModel viewModel;
    List<MyJobs> list;

    String MainUrl = "https://jobs.github.com/positions.json?page=1&search=code";

    private DrawerLayout dl;
    private ActionBarDrawerToggle at;
    NavigationView nv;
    FragmentManager fm;

    private GoogleSignInClient mGoogleSignInClient;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        recyclerView = findViewById(R.id.rec);

        dl=findViewById(R.id.drawer);
        nv = findViewById(R.id.navV);
        nv.setNavigationItemSelectedListener(this);
        at=new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        dl.addDrawerListener(at);
        at.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fm = getSupportFragmentManager();
        saveJobDataList = new ArrayList<>();
        db = Room.databaseBuilder(this, AppDatabase.class, getString(R.string.msg_db))
                .allowMainThreadQueries().build();
        viewModel = ViewModelProviders.of(this).get(JobViewModel.class);
        auth = FirebaseAuth.getInstance();
        requestQueue = Volley.newRequestQueue(this);
        list = new ArrayList<>();
        sharedPreferences = getSharedPreferences(getString(R.string.harish), MODE_PRIVATE);
        String key1 = sharedPreferences.getString(State, null);
        if (key1 != null) {
            if (key1.equalsIgnoreCase(key)) {

                GetJobsInfo();

            } else if (key1.equalsIgnoreCase(getString(R.string.savedjobs))) {

                getdatafromdatabase();
            }
        } else {
            GetJobsInfo();
        }
        isNetworkAvailable();
        Intent i=getIntent();
        String name=i.getStringExtra("n");
        String email=i.getStringExtra("e");
        String imgurl=i.getStringExtra("url");
        View v=nv.getHeaderView(0);
        TextView tv_name=v.findViewById(R.id.names);
        TextView tv_email=v.findViewById(R.id.emails);
        ImageView iv=v.findViewById(R.id.images);
        tv_name.setText(name);
        tv_email.setText(email);
        Picasso.get().load(imgurl).into(iv);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main2Activity.this);
            alertDialog.setMessage(R.string.internet_connction);
            alertDialog.setTitle(R.string.no_internet_connection);
            alertDialog.setPositiveButton(
                    R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }
            );
            alertDialog.show();


        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.signout) {

            if (SaveSharedPreference.getUserName(Main2Activity.this).length() == 0) {
                if (auth.getCurrentUser() != null) {
                    auth.signOut();
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                    finish();
                }

            }

        }
        if (id == R.id.savedjobs) {

            String key = getString(R.string.savedjobs);
            speditor = sharedPreferences.edit();
            speditor.putString(State, key);
            speditor.apply();
            getdatafromdatabase();


        }
        if (id == R.id.homeactivity) {
            String key = getString(R.string.jobs);
            speditor = sharedPreferences.edit();
            speditor.putString(State, key);
            speditor.commit();
            requestQueue = Volley.newRequestQueue(this);
            GetJobsInfo();
        }

        return super.onOptionsItemSelected(item);
    }
*/

    private void GetJobsInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                String id = null;
                String type = null;
                String url = null;
                String created_at = null;
                String company = null;
                String companyurl = null;
                String location = null;
                String company_logo = null;
                String how_to_apply = null;
                String description = null;
                String title = null;

                try {

                    JSONArray root = new JSONArray(response);
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject jsonObject = root.getJSONObject(i);
                        id = jsonObject.getString("id");
                        type = jsonObject.getString("type");
                        url = jsonObject.getString("url");
                        created_at = jsonObject.getString("created_at");
                        company = jsonObject.getString("company");
                        companyurl = jsonObject.getString("company_url");
                        location = jsonObject.getString("location");
                        company_logo = jsonObject.getString("company_logo");
                        how_to_apply = jsonObject.getString("how_to_apply");
                        description = jsonObject.getString("description");
                        title = jsonObject.getString("title");
                        MyJobs myjobs = new MyJobs(id, type, url, created_at, company, companyurl, location, company_logo, how_to_apply, description, title);
                        list.add(myjobs);

                    }
                    MyAdapter adapter = new MyAdapter(Main2Activity.this, list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Main2Activity.this));
                    recyclerView.setAdapter(adapter);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);

    }

    public void getdatafromdatabase() {

        viewModel.getListLiveData().observe(this, new Observer<List<SaveJobData>>() {
            @Override
            public void onChanged(@Nullable List<SaveJobData> saveJobData) {
                saveJobDataList = saveJobData;


                if (saveJobDataList.isEmpty()) {

                    Toast.makeText(Main2Activity.this, R.string.jobsToast, Toast.LENGTH_SHORT).show();
                }
                JobsAdapter adapter = new JobsAdapter(Main2Activity.this, saveJobDataList);
                recyclerView.setLayoutManager(new LinearLayoutManager(Main2Activity.this));
                recyclerView.setAdapter(adapter);



            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(at.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
          case  R.id.settingss:

            dl.closeDrawers();
            break;
            case R.id.signout:
              /*  if (SaveSharedPreference.getUserName(Main2Activity.this).length() == 0) {
                    if (auth.getCurrentUser() != null) {
                        auth.signOut();*/
                        mGoogleSignInClient.signOut();
                        Toast.makeText(Main2Activity.this,"You are Logged Out",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Main2Activity.this, MainActivity.class));
                        /*finish();*/

                  /*  }

               *//* }*/
                break;
            case R.id.savedjobs:
                String key = getString(R.string.savedjobs);
                speditor = sharedPreferences.edit();
                speditor.putString(State, key);
                speditor.apply();
                getdatafromdatabase();
                dl.closeDrawers();
                break;
            case R.id.homeactivity:
                String key1 = getString(R.string.jobs);
                speditor = sharedPreferences.edit();
                speditor.putString(State, key1);
                speditor.commit();
                requestQueue = Volley.newRequestQueue(this);
                GetJobsInfo();
                dl.closeDrawers();
                break;

        }
        return true;
    }
}
