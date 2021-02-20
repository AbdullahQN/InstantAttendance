/*
 * MIT License
 *
 * Copyright (c) 2021 Evren Coşkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.instantattendance.tableview;

//import androidx.annotation.DrawableRes;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

//import com.evrencoskun.tableviewsample.R;
import com.evrencoskun.tableview.TableView;
import com.example.instantattendance.tableview.model.Cell;
import com.example.instantattendance.tableview.model.ColumnHeader;
import com.example.instantattendance.tableview.model.RowHeader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
//import java.util.Random;

/**
 * Created by evrencoskun on 4.02.2018.
 */

public class TableViewModel {

    // Columns indexes


    // Constant values for icons


    // Constant size for dummy data sets
    private static final int COLUMN_SIZE = 30;
    private static final int ROW_SIZE = 40;
    private static final String TAG = "TableViewModel";
    HashMap<String, ArrayList<String>> map;

    public TableViewModel() {
        // initialize drawables

    }

    @NonNull
    private List<RowHeader> getSimpleRowHeaderList(List<String> regesitered_student) {
        List<RowHeader> list = new ArrayList<>();
        //int s = 437000000;
        for (int i = 0; i < regesitered_student.size(); i++) {

            RowHeader header = new RowHeader(String.valueOf(i), regesitered_student.get(i));
            list.add(header);
        }

        return list;
    }

    /**
     * This is a dummy model list test some cases.
     * @param days
     * @param term_start
     * @param term_end
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private List<ColumnHeader> getRandomColumnHeaderList(List<String> days, String term_start, String term_end) {
        List<ColumnHeader> list = new ArrayList<>();
        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        //Date d = new Date();

        List<String> dayNames = days;
        for(int i=0;i<days.size();i++){
            String day = days.get(i);
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
        String s = term_start;
        String e = term_end;
        LocalDate start = LocalDate.parse(s);
        LocalDate end = LocalDate.parse(e);
        int i = 0 ;
        while (!start.isAfter(end)) {
            if(dayNames.contains(start.getDayOfWeek().toString())){
                Log.d(TAG, "getRandomColumnHeaderList: "+ start.getDayOfWeek());
                String title = start.toString()+"\n"+start.getDayOfWeek();
                ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
                list.add(header);
                i++;
            }

            start = start.plusDays(1);


        }
       /* for (int i = 0; i < COLUMN_SIZE; i++) {
            String title = "2021" + i;
            *//*int nRandom = new Random().nextInt();
            if (nRandom % 4 == 0 || nRandom % 3 == 0 || nRandom == i) {
                title = "large column " + i;
            }*//*

            ColumnHeader header = new ColumnHeader(String.valueOf(i), title);
            list.add(header);
        }*/

        return list;
    }

    /**
     * This is a dummy model list test some cases.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private List<List<Cell>> getCellListForSortingTest(TableView mTableView, String section_id, List<String> days, String term_start, String term_end, List<String> regesitered_student) {
        map = new HashMap<String, ArrayList<String>>();
        List<String> dayNames = days;
        for(int i=0;i<days.size();i++){
            String day = days.get(i);
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
        String s = term_start;
        String e = term_end;
        LocalDate start = LocalDate.parse(s);
        LocalDate end = LocalDate.parse(e);
        int countCol = 0 ;
        while (!start.isAfter(end)) {
            if (dayNames.contains(start.getDayOfWeek().toString())) {

                countCol++;
            }

            start = start.plusDays(1);

        }
        //

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> document =
                db.collection("Sections").document(section_id.toString()).collection("attendance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            Log.d(TAG, "1321: "+map.toString());

                        }
                    }
                });

        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < regesitered_student.size(); i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < countCol; j++) {
                /*String d = mTableView.getAdapter().getColumnHeaderItem(j).toString().substring(0,3)+mTableView.getAdapter().getColumnHeaderItem(j).toString().substring(5,6)+mTableView.getAdapter().getColumnHeaderItem(j).toString().substring(8,9);
                if(map.containsKey(d)){
                    Log.d(TAG, "getCellListForSortingTest: "+"VOILAAAAAAAAAAAA");
                }*/

                Object text = " ";

                // Create dummy id.
                String id = j + "-" + i;

                Cell cell;
                cell = new Cell(id, text);
                /*if (j == 3) {

                } else if (j == 4) {
                    // NOTE female and male keywords for filter will have conflict since "female"
                    // contains "male"
                    cell = new Cell(id, text);
                } else {
                    cell = new Cell(id, text);
                }*/
                cellList.add(cell);
            }
            list.add(cellList);
        }

        return list;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    public List<List<Cell>> getCellList(TableView mTableView, String section_id, List<String> days, String term_start, String term_end, List<String> regesitered_student) {
        return getCellListForSortingTest(mTableView,section_id,days,term_start,term_end,regesitered_student);
    }

    @NonNull
    public List<RowHeader> getRowHeaderList(List<String> regesitered_student) {
        return getSimpleRowHeaderList(regesitered_student);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    public List<ColumnHeader> getColumnHeaderList(List<String> days, String term_start, String term_end) {
        return getRandomColumnHeaderList(days,term_start,term_end);
    }
    @NonNull
    public void updateStatus(){

    }
}
