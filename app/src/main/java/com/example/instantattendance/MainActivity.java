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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth uAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check for the signed in user
        try {
            uAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = uAuth.getCurrentUser();
            if(currentUser==null){
                //no user is logged in move to login activity
                Toast.makeText(this,"no user logged in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, signIn.class);
                startActivity(intent);
            }else{
                // figure out what type of a user is logged in
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String forignkey = uAuth.getUid();
                DocumentReference refrenceFK = db.collection("Users").document(forignkey);
                refrenceFK.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task){
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                            }
                        }
                    }
                });
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, uAuth.getCurrentUser().getEmail().toString()+" is signed in", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this,"connection lost", Toast.LENGTH_LONG).show();

        }
    }

}
