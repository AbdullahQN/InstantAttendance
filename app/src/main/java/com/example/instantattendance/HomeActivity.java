package com.example.instantattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth uAuth;
    private StorageReference storeRef;
    private final String TAG= "Home Activity";
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
        //final FirebaseUser user = uAuth.getCurrentUser();
        storeRef = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);


        Intent intent = getIntent();
        u = (Users) intent.getSerializableExtra("u");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddCourse.class);
                i.putExtra("u",u);
                startActivity(i);

                /*Snackbar.make(view, user.getEmail().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
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
            File dir = new File(getFilesDir() + "/Sections/"+forignkey);
            if(!dir.exists()) {
                dir.mkdirs();
            }

            //get student reference pictures
            //gs://instantattenddb.appspot.com/Sections/11111/437100230.png
            storeRef.child("/Sections/"+s.get(i).toString()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult result) {
                    for(StorageReference fileRef : result.getItems()) {
                        // TODO: Download the file using its reference (fileRef)
                        Log.d(TAG, "onSuccess: "+dir+"/"+fileRef.getName().substring(0,9));

                        final File localFile = new File(dir+"/", fileRef.getName());
                        fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle any errors
                }
            });
            db.collection("Sections").document(forignkey).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Log.d("Abdullah100",document.get("Course_Code")+" "+document.get("Course_Name")+document.contains("attendance"));
                            sectionsArrayList.add(Objects.requireNonNull(document).toObject(Sections.class));
                            //sectionNames.add(Objects.requireNonNull(document).get("Course_Name").toString());
                            //sectionCodeNames.add(Objects.requireNonNull(document).get("Course_Code").toString());
                            CourseAdapter customAdapter = new CourseAdapter(HomeActivity.this,sectionsArrayList);
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