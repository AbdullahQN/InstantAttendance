package com.example.instantattendance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.evrencoskun.tableview.TableView;
import com.example.instantattendance.tableview.TableViewAdapter;
import com.example.instantattendance.tableview.TableViewListener;
import com.example.instantattendance.tableview.TableViewModel;
import com.example.instantattendance.tableview.model.ColumnHeader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Attendance extends AppCompatActivity {
    private TableView mTableView;
    private Sections section;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> recognizedStudents;
    HashMap<String, ArrayList<String>> map;
    Boolean update;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        recognizedStudents = new ArrayList<>();
        Intent x = getIntent();
        update = (Boolean) x.getSerializableExtra("upd");
        section = (Sections) x.getSerializableExtra("sectionN");
        mTableView = findViewById(R.id.tableview);

        if(update){
            recognizedStudents = (ArrayList<String>) x.getSerializableExtra("rec");
            HashMap<String, ArrayList<String>> data = new HashMap<>();
            data.put("Students_Present",recognizedStudents);
            LocalDate d = LocalDate.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
            db.collection("Sections").document(section.getSection_ID().toString())
                    .collection("attendance").document(dtf.format(d).toString()).set(data);


        }
        initializeTableView(section);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeTableView(Sections section) {
        // Create TableView View model class  to group view models of TableView
        TableViewModel tableViewModel = new TableViewModel();
        // Create TableView Adapter
        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);
        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));
        // Load the data to the TableView
        List<String> dayNames = new ArrayList<>();
        List<String> tableDays = new ArrayList<>();
        for(int i=0;i<section.getDays().size();i++){
            String day = section.getDays().get(i);
            if(day.equals("0")){
                dayNames.add("MONDAY");
            }else if(day.equals("1")){
                dayNames.add("TUESDAY");
            }else if(day.equals("2")){
                dayNames.add("WEDNESDAY");
            }else if(day.equals("3")){
                dayNames.add("THURSDAY");
            }else if(day.equals("4")){
                dayNames.add("FRIDAY");
            }else if(day.equals("5")){
                dayNames.add("SATURDAY");
            }else if(day.equals("6")){
                dayNames.add("SUNDAY");
            }
        }
        String s = section.getTerm_Start();
        String e = section.getTerm_End();
        LocalDate start = LocalDate.parse(s);
        LocalDate end = LocalDate.parse(e);
        int i = 0 ;
        while (!start.isAfter(end)) {
            if(dayNames.contains(start.getDayOfWeek().toString())){
                Log.d("TAG", "ColumnHeaderList: "+ start.getDayOfWeek());
                String title = start.toString()+"\n"+start.getDayOfWeek();
                tableDays.add(title);
                i++;
            }
            start = start.plusDays(1);
        }
        map = new HashMap<String, ArrayList<String>>();

        Task<QuerySnapshot> document =
                db.collection("Sections").document(section.getSection_ID().toString()).collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot document = task.getResult();
                            List<DocumentSnapshot> exist = new ArrayList<>();
                            exist =  document.getDocuments();
                            for(DocumentSnapshot d:exist){

                                ArrayList<String> x = new ArrayList<>();
                                x = (ArrayList<String>) d.get("Students_Present");
                                map.put(d.getId(),x);


                            }
                            Log.d("TAG", "1321: "+map.toString());
                            tableViewAdapter.setColumnHeaderItems(tableViewModel.getColumnHeaderList(tableDays));
                            tableViewAdapter.setRowHeaderItems(tableViewModel.getRowHeaderList(section.getRegesitered_Student()));
                            tableViewAdapter.setCellItems( tableViewModel.getCellList(map,tableDays,section.getRegesitered_Student()));


                        }
                    }
                });
    }
}