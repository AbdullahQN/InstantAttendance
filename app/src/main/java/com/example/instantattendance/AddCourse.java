package com.example.instantattendance;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddCourse extends AppCompatActivity {
    private FirebaseAuth uAuth;
    private StorageReference storeRef;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG= "Add Course Activity";
    public Users u;
    private TextInputEditText courseId,courseName,courseCode;
    private MaterialButton datePick,lectDays,courseAdder;
    private ArrayList<String> days;
    String termStartDate,termEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        days = new ArrayList<>();
        courseId = findViewById(R.id.course_id_edit_text);
        courseName = findViewById(R.id.course_name_edit_text);
        courseCode = findViewById(R.id.course_code_edit_text);
        datePick = findViewById(R.id.termDate);
        lectDays = findViewById(R.id.lectDays);
        courseAdder = findViewById(R.id.courseadd);
        MaterialDatePicker.Builder dateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        dateBuilder.setTitleText("Select Term Start & End Dates");
        //dateBuilder.setTheme()
        final MaterialDatePicker datePicker = dateBuilder.build();
        datePick.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // getSupportFragmentManager() to
                        // interact with the fragments
                        // associated with the material design
                        // date picker tag is to get any error
                        // in logcat
                        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                //LocalDate d = LocalDate.parse(datePicker.getSelection().)

                                Pair<Long,Long> i = (Pair<Long, Long>) datePicker.getSelection();
                                Date startdate = new Date(i.first);
                                Date enddate = new Date(i.second);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                termStartDate = simpleDateFormat.format(startdate);
                                termEndDate = simpleDateFormat.format(enddate);
                                //LocalDate e = LocalDate.parse(d);
                                Log.d(TAG, "onPositiveButtonClick: "+termStartDate+" "+ termEndDate);
                            }
                        });
                        //Log.d(TAG, "onClick: "+datePicker.getSelection().toString());datePicker.getSelection().toString();
                    }
                });
        lectDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCourse.this);
                builder.setTitle("Choose Lecture Days");
                days = new ArrayList<>();
                String[] animals = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
                boolean[] checkedItems = {false, false, false, false, false};
                builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            checkedItems[which] = true;
                        }else{
                            checkedItems[which] = false;
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(checkedItems[0]){
                            days.add("6");
                        }
                        if(checkedItems[1]){
                            days.add("0");
                        }
                        if(checkedItems[2]){
                            days.add("1");
                        }
                        if(checkedItems[3]){
                            days.add("2");
                        }
                        if(checkedItems[4]){
                            days.add("3");
                        }
                        Log.d(TAG, "onClick: "+days);
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        uAuth = FirebaseAuth.getInstance();
        //final FirebaseUser user = uAuth.getCurrentUser();
        storeRef = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        u = (Users) intent.getSerializableExtra("u");

        courseAdder.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+courseId.getText()+courseName.getText()+courseCode.getText() +termStartDate+termEndDate+days);
                if(courseId.getText().length() == 0){
                    courseId.setError("Can't be null");
                }else if(courseName.getText().length() == 0){
                    courseName.setError("Can't be null");
                }else if(courseCode.getText().length() == 0){
                    courseCode.setError("Can't be null");
                }else if(termStartDate==null || termEndDate==null){
                    Toast.makeText(AddCourse.this,"Please choose start and end term dates", Toast.LENGTH_LONG).show();
                }else if(days.isEmpty()){
                    Toast.makeText(AddCourse.this,"Please choose lecture days", Toast.LENGTH_LONG).show();
                }else{
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("Course_Code",courseCode.getText().toString());
                    data.put("Course_Name",courseName.getText().toString());
                    data.put("Section_ID",courseId.getText().toString());
                    data.put("Term_End",termEndDate);
                    data.put("Term_Start",termEndDate);
                    //HashMap<String,ArrayList<String>> data2 = new HashMap<>();
                    data.put("Days",days);
                    ArrayList<String> s = new ArrayList<>();
                    /*if(!db.collection("Sections").document("courseId.getText().toString()").get(Source.valueOf("Regesitered_Student")).isSuccessful()){
                        data2.put("Regesitered_Student",s);
                    }*/
                    data.put("Regesitered_Student",s);
                    db.collection("Sections").document(courseId.getText().toString()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseUser x = uAuth.getCurrentUser();
                            u.Sections.add(courseId.getText().toString());

                            db.collection("Users").document(x.getUid()).set(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddCourse.this,"Success", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(AddCourse.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        }
                    });


                    //Toast.makeText(AddCourse.this,"Success", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}