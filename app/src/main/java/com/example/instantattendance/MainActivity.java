package com.example.instantattendance;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            uAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = uAuth.getCurrentUser();
            if(currentUser==null){
                Toast.makeText(this,"no user logged in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, signIn.class);
                startActivity(intent);
            }
        }catch (Exception e){
            Toast.makeText(this,"connection lost", Toast.LENGTH_LONG).show();

        }
        Toast.makeText(this, uAuth.toString(), Toast.LENGTH_LONG).show();
        //checkLogInState(uAuth);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
    }

   // @Override
    public  void checkLogInState(FirebaseAuth uAuth){

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if (id == R.id.signout) {
            uAuth.signOut();
             Toast.makeText(this,"signed out", Toast.LENGTH_LONG).show();
             Intent intent = new Intent(this, signIn.class);
             startActivity(intent);
             finish();
        }

        return true;
    }
}
