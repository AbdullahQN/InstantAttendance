package com.example.instantattendance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.evrencoskun.tableview.TableView;
import com.example.instantattendance.tableview.TableViewAdapter;
import com.example.instantattendance.tableview.TableViewListener;
import com.example.instantattendance.tableview.TableViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Attendance extends AppCompatActivity {
    private TableView mTableView;
    private Sections section;
    List<String> recognizedStudents;
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
        initializeTableView(section);
        if(update){
            recognizedStudents = (List<String>) x.getSerializableExtra("rec");
            Log.d("TAG300", "onCreate: Mohammed");
            //mTableView.getAdapter().changeCellItem();
            /*for(int i = 0 ; i< mTableView.getAdapter().get;i++){

            }*/
        }

        //getSupportActionBar().setTitle(sectionNumber+" Attendance");


        /*DataTable dataTable = findViewById(R.id.datatable);

        DataTableHeader header = new DataTableHeader.Builder()
                .item("ID" , 3)
                .item("Sunday", 2)
                .item("Tuesday", 2)
                .item("Thursday", 2)
                .item("friday",2)
                .item("ttttt",2)
                .build();
        ArrayList<DataTableRow> rows = new ArrayList<>();
        for(int i=0;i<200;i++){
            Random r = new Random();
            int random = r.nextInt(i+1);
            int randomDiscount = r.nextInt(20);
            DataTableRow row = new DataTableRow.Builder()
                    .value("Product #" + i)
                    .value(String.valueOf(random))
                    .value(String.valueOf(random*1000).concat("$"))
                    .value(String.valueOf(randomDiscount).concat("%"))
                    .value("0")
                    .value("1")
                    .build();
            rows.add(row);
        }

        //dataTable.setTypeface(typeface);
        dataTable.setHeader(header);
        dataTable.setRows(rows);
        dataTable.inflate(this);*/







    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeTableView(Sections section) {
        // Create TableView View model class  to group view models of TableView
        TableViewModel tableViewModel = new TableViewModel();

        // Create TableView Adapter
        TableViewAdapter tableViewAdapter = new TableViewAdapter(tableViewModel);

        mTableView.setAdapter(tableViewAdapter);
        mTableView.setTableViewListener(new TableViewListener(mTableView));

        // Create an instance of a Filter and pass the TableView.
        //mTableFilter = new Filter(mTableView);

        // Load the dummy data to the TableView
        tableViewAdapter.setAllItems(tableViewModel.getColumnHeaderList(section.getDays(),section.getTerm_Start(),section.getTerm_End()), tableViewModel
                .getRowHeaderList(section.getRegesitered_Student()), tableViewModel.getCellList(mTableView,section.getSection_ID(),section.getDays(),section.getTerm_Start(),section.getTerm_End(),section.getRegesitered_Student()));

        //mTableView.setHasFixedWidth(true);

        /*for (int i = 0; i < mTableViewModel.getCellList().size(); i++) {
            mTableView.setColumnWidth(i, 200);
        }*)

        //mTableView.setColumnWidth(0, -2);
        //mTableView.setColumnWidth(1, -2);

        /*mTableView.setColumnWidth(2, 200);
        mTableView.setColumnWidth(3, 300);
        mTableView.setColumnWidth(4, 400);
        mTableView.setColumnWidth(5, 500);*/

    }
}