package com.example.instantattendance;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    private EditText mail;
    private EditText pass;
    private Button login,signup;
    private TextView forgottenPass;
    private FirebaseAuth uAuth;

    ProgressBar pB ;
    private static final String TAG = "SignIn";
    public SignIn(){
        super(R.layout.activity_sign_in);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mail = (EditText) findViewById(R.id.signInEmail);
        pass = (EditText) findViewById(R.id.signInPass);
        signup = (Button) findViewById(R.id.signUpButton);
        pB = (ProgressBar) findViewById(R.id.progressBar);
        forgottenPass = (TextView) findViewById(R.id.forgottenPass);
        login = (Button) findViewById(R.id.signInButton);
        logi();
    }

    protected void logi(){
        forgottenPass.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                if(email.length()!=0){
                    try {
                        pB.setVisibility(View.VISIBLE);
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar.make(v, "Reset link was sent to your Email", Snackbar.LENGTH_LONG).show();
                                        } else {
                                            Snackbar.make(v, "This Email is not signed up", Snackbar.LENGTH_LONG).show();
                                        }
                                        pB.setVisibility(View.GONE);
                                    }
                                });
                    }catch (Exception e){
                        Log.d(TAG, "forgottenPass: "+e);
                    }
                }else{
                    Snackbar.make(v,"Please enter a valid email",Snackbar.LENGTH_LONG).show();
                }


            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlaceHolder, (Fragment) new SignUpFragment());
                fragmentTransaction.commit();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                String email = mail.getText().toString();
                //Toast.makeText(signIn.this, email, Toast.LENGTH_LONG).show();
                String password = pass.getText().toString();

                //Toast.makeText(signIn.this, password, Toast.LENGTH_LONG).show();
                //is mail empty
                if (!(email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+"))&&!(email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]+"))) {
                    Toast.makeText(SignIn.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
                    return;
                }

                //is pass empty
                if (password.length() < 6) {
                    Toast.makeText(SignIn.this, "Password should be at least 6 characters long.", Toast.LENGTH_LONG).show();
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
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(SignIn.this, "Email or Password invalid", Toast.LENGTH_LONG).show();
                            logi();
                        } else {
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignIn.this, "going to main success", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    }

                });}
        catch (Exception S){
               System.out.println(S);
               logi();

                }
    }
}