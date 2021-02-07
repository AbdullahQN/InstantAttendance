package com.example.instantattendance;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth uAuth;
    public Users u;
     ArrayList<Sections> sectionsArrayList;
     ArrayList<String> sectionCodeNames;
     ArrayList<String> sectionNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        uAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = uAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, user.getEmail().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        u = (Users) intent.getSerializableExtra("u");
        //view sections
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.CourseRecycler);
        // set a LinearLayoutManager with default vertical orientaion
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        // call the constructor of CustomAdapter to send the reference and data to Adapter


        final ArrayList<String> s = new ArrayList<>(u.Sections);
        Log.d("Home",s.toString());
        //ArrayList sx = s;
        //Iterator e = sx.iterator();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        sectionNames = new ArrayList<>();
        sectionCodeNames = new ArrayList<>();
        sectionsArrayList = new ArrayList<>();
        for(int i = 0;i<s.size();i++){
            Log.d("Home",s.get(i).toString());
            String forignkey = s.get(i).toString();
            db.collection("Sections").document(forignkey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Home",document.get("Course_Code")+" "+document.get("Course_Name"));
                            sectionsArrayList.add(Objects.requireNonNull(document).toObject(Sections.class));
                            sectionNames.add(Objects.requireNonNull(document).get("Course_Name").toString());
                            sectionCodeNames.add(Objects.requireNonNull(document).get("Course_Code").toString());
                            CourseAdapter customAdapter = new CourseAdapter(HomeActivity.this, s,sectionCodeNames,sectionNames);
                            recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
                        }
                    } else {
                        ///task is not Successful

                    }
                }
            });
        }







    }

    @Override
    public void onBackPressed(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "signed out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}