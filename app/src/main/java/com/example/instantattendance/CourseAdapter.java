package com.example.instantattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    private ArrayList personNames;





    //view hoder class
    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        public CardView e;
        public TextView section,courseCode,courseName;
        public CourseViewHolder( CardView e,TextView section,TextView courseCode,TextView courseName) {
            super(e);
            this.courseCode = courseCode;
            this.section = section;
            this.courseName = courseName;

        }
    }



    public CourseAdapter(Context context, ArrayList personNames) {
        this.context = context;
        this.personNames = personNames;
    }
    @NonNull
    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView x = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.course_card,parent,false);
        TextView section = (TextView) x.findViewById(R.id.section_no);
        TextView courseCode = (TextView) x.findViewById(R.id.course_code);
        TextView courseName = (TextView) x.findViewById(R.id.course_name);
        // remmember buttons

        CourseViewHolder i = new CourseViewHolder(x,section,courseCode,courseName);
        return i;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {

        String person = null;
        holder.section.setText(personNames.get(position).toString());
        holder.courseName.setText("Testing The Course Name"+ position);
        holder.courseCode.setText("Testing The Course Code"+ position);



    }

    @Override
    public int getItemCount() {
        return personNames.size();
    }
}
