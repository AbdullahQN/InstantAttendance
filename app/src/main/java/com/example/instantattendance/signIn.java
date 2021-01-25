package com.example.instantattendance;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signIn extends AppCompatActivity {
    private EditText mail;
    private EditText pass;
    private Button login;
    private FirebaseAuth uAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mail = (EditText) findViewById(R.id.signInEmail);
        pass = (EditText) findViewById(R.id.signInPass);
        //Button reset = (Button) findViewById(R.id.btn_reset_password);
        login = (Button) findViewById(R.id.signInButton);
        logi();
    }

    protected void logi(){
        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                String email = mail.getText().toString();
                //Toast.makeText(signIn.this, email, Toast.LENGTH_LONG).show();
                String password = pass.getText().toString();

                //Toast.makeText(signIn.this, password, Toast.LENGTH_LONG).show();
                //is mail empty
                if (!(email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))&&!(email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]+"))) {
                    Toast.makeText(signIn.this, "الرجاء كتابة الايميل", Toast.LENGTH_LONG).show();
                    return;
                }

                //is pass empty
                if (password.length() < 6) {
                    Toast.makeText(signIn.this, "Password should be at least 6 characters long.", Toast.LENGTH_LONG).show();
                    return;
                }

                authuser(email,password);
            }


        });
    }

    protected void authuser(String e, String s){
        try{

            uAuth = FirebaseAuth.getInstance();
        uAuth.signInWithEmailAndPassword(e, s)
                .addOnCompleteListener(signIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(signIn.this, "Email or Password invalid", Toast.LENGTH_LONG).show();
                            logi();
                                    /*if (true) {
                                        //password.length() < 6
                                        pass.setError(getString(R.string.invalid_password));
                                    } else {
                                        //Toast.makeText(this, "الرجاء كتابة pass", Toast.LENGTH_LONG).show();
                                    }*/
                        } else {
                            Intent intent = new Intent(signIn.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(signIn.this, "going to main sucsses", Toast.LENGTH_LONG).show();
                            finish();
                                    /*if (uAuth.getCurrentUser().isEmailVerified()) {
                                        //go to mainActivity clas
                                        Intent intent = new Intent(signIn.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //Toasty.error(getApplicationContext(),"الرجاء تأكيد حسابك").show();
                                    }*/
                        }

                    }

                });}
        catch (Exception S){
               System.out.println(S);
               logi();

                }
    }
}