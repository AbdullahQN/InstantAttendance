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
    public Users u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check for the signed in user
        uAuth = FirebaseAuth.getInstance();
        userCheck(uAuth);
    }

    private void userCheck(FirebaseAuth uAuth){
            FirebaseUser currentUser = uAuth.getCurrentUser();
            if(currentUser==null){
                //no user is logged in move to login activity
                Toast.makeText(this,"no user logged in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, signIn.class);
                startActivity(intent);
            }else{
                // figure out what type of a user is logged in
                FirebaseAuth xAuth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String forignkey = xAuth.getUid();
                DocumentReference refrenceFK = db.collection("Users").document(forignkey);
                refrenceFK.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                u = document.toObject(Users.class);
                                System.out.println("Hey it exists!");
                                Toast.makeText(getApplicationContext(), u.FName, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.putExtra("u", u);
                                startActivity(intent);
                                          /*  startActivity(intent);
                                            finish();*/
                            }
                        } else {
                            ///task is not succsesful

                        }
                    }
                });


                /*refrenceFK.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task){
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                System.out.println("Hey it exists!");
                                Toast.makeText(getApplicationContext(), document.get("FName").toString(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);

                            }
                        } else {
                            System.out.println("Task failed here 0");
                        }
                    }
                });*/
                //Toast.makeText(this, uAuth.getCurrentUser().getEmail().toString()+" is signed in", Toast.LENGTH_LONG).show();
            }
    }

}
