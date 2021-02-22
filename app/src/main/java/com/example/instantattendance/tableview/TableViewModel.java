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
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.example.instantattendance.tableview.model.Cell;
import com.example.instantattendance.tableview.model.ColumnHeader;
import com.example.instantattendance.tableview.model.RowHeader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class TableViewModel {
    private static final String TAG = "TableViewModel";
    public TableViewModel() {
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    public List<List<Cell>> getCellList(HashMap<String, ArrayList<String>> map,List<String> days, List<String> students) {
        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < days.size(); j++) {
                String d = days.get(j).substring(0,4)+days.get(j).substring(5,7)+days.get(j).substring(8,10);
                Object text = " ";
                if(map.containsKey(d)){
                    if (map.get(d).contains(students.get(i))){
                        /*Log.d(TAG, "getCellListForSortingTest: "+"VOILAAAAAAAAAAAA"+d+students.get(i));*/
                        text = "1";
                    }else{
                        text = "0";
                    }
                }
                // Create an id.
                String id = j + "-" + i;
                Cell cell;
                cell = new Cell(id, text);
                cellList.add(cell);
            }
            list.add(cellList);
        }
        return list;
    }
    @NonNull
    public List<RowHeader> getRowHeaderList(List<String> regesitered_student) {
        List<RowHeader> list = new ArrayList<>();
        //String s = 437000000;
        for (int i = 0; i < regesitered_student.size(); i++) {
            RowHeader header = new RowHeader(String.valueOf(i), regesitered_student.get(i));
            list.add(header);
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    public List<ColumnHeader> getColumnHeaderList(List<String> days) {
        List<ColumnHeader> list = new ArrayList<>();
        for(int i = 0; i<days.size();i++){
            ColumnHeader header = new ColumnHeader(String.valueOf(i), days.get(i));
            list.add(header);
        }
        return list;
    }
}
