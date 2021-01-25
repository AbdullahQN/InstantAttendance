package com.example.instantattendance;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                Toast.makeText(this,"no user logged in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, signIn.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, uAuth.getCurrentUser().getEmail().toString()+" is signed in", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this,"connection lost", Toast.LENGTH_LONG).show();

        }
    }

}
