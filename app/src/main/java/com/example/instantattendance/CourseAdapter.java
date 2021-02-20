package com.example.instantattendance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    ArrayList<Sections> sections;
    //ArrayList<String> sectionCodeNames;
    //ArrayList<String> sectionNames;
    //ArrayList<ArrayList<String>> registeredStudents;
    //ArrayList<String> termStart;
    //ArrayList<String> End;







    //view holder class
    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        public CardView e;
        public TextView section,courseCode,courseName;
        public Button takeAttendance;
        public Button viewHistory;
        public CourseViewHolder( CardView e,TextView section,TextView courseCode,TextView courseName,Button takeAttendance, Button viewHistory) {
            super(e);
            this.courseCode = courseCode;
            this.section = section;
            this.courseName = courseName;
            //
            this.takeAttendance=takeAttendance;
            this.viewHistory= viewHistory;

        }
    }



    public CourseAdapter(Context context, ArrayList sectionList) {
        this.context = context;
        this.sections = sectionList;
        /*this.sectionCodeNames = sectionCodeNames;, ArrayList sectionCodeNames, ArrayList sectionNames
        this.sectionNames = sectionNames;*/
    }
    @NonNull
    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView x = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.course_card,parent,false);
        TextView section = (TextView) x.findViewById(R.id.section_no);
        TextView courseCode = (TextView) x.findViewById(R.id.course_code);
        TextView courseName = (TextView) x.findViewById(R.id.course_name);
        Button takeAttendance = (Button) x.findViewById(R.id.take_attendance);
        Button viewHistory = (Button) x.findViewById(R.id.view_history);
        // remmember buttons
        //CourseViewHolder i = new CourseViewHolder(x,section,courseCode,courseName);

        CourseViewHolder i = new CourseViewHolder(x,section,courseCode,courseName,takeAttendance,viewHistory);
        return i;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {

        holder.section.setText(sections.get(position).getSection_ID());
        holder.courseName.setText(sections.get(position).getCourse_Name());
        holder.courseCode.setText(sections.get(position).getCourse_Code());

        holder.takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, CameraActivity.class);
                in.putExtra("sectionN", sections.get(position));
                context.startActivity(in);
            }
        });

        holder.viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, Attendance.class);
                in.putExtra("sectionN", sections.get(position));
                in.putExtra("upd",false);
                context.startActivity(in);
            }
        });



    }

    @Override
    public int getItemCount() {
        return sections.size();
    }
}
