package com.example.instantattendance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Parcelable;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the splash screen the first class to be initialized when the program is invoked.
 * It will communicate with the database to get an Authorization instance (Auth), through which
 * we can see if a user is logged in or not, if a user is logged in we get the user's data
 * from the database then move on to home Activity. If there is no logged in user we move to login Activity.
 */
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth uAuth;
    public Users u;
    public List<String> sectionCodeNames;
    public List<String> sectionNames;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check for the signed in user
        checkPermissions();



    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    private void initialize() {
        uAuth = FirebaseAuth.getInstance();
        userCheck(uAuth);
    }

    private void userCheck(FirebaseAuth uAuth){
            FirebaseUser currentUser = uAuth.getCurrentUser();
            if(currentUser==null){
                //no user is logged in move to login activity
                Toast.makeText(this,"no user logged in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, SignIn.class);
                startActivity(intent);
            }else{
                // figure out what type of a user is logged in
                FirebaseAuth xAuth = FirebaseAuth.getInstance();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                String forignkey = xAuth.getUid();
                DocumentReference refrenceFK = db.collection("Users").document(forignkey);

                refrenceFK.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                u = document.toObject(Users.class);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.putExtra("u", u);
                                startActivity(intent);
                            }
                        } else {
                            ///task is not Successful

                        }
                    }
                });

            }
    }



     /* TRY TO SAVE FILES LOCALLY
    private void saveFile(String name) {
        File file = new File(getFilesDir(), name + ".json");

       try{
            OutputStream out = new FileOutputStream(file);
            JsonWriter writer = new JsonWriter();
            writer.beginObject()
            out.close();
        }catch (Exception e){
            Log.e("saveState ERROR", "----------------------------------------------------");
        }
    }*/

}
