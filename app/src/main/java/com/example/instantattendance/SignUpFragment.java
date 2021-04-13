package com.example.instantattendance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;/*
import androidx.navigation.fragment.NavHostFragment;*/

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<String> items = new ArrayList<>();
        items.add("Faculty");
        //items.add("Student");
        TextInputEditText email = view.findViewById(R.id.email_edit_text);
        TextInputEditText pass = view.findViewById(R.id.password_edit_text);
        TextInputEditText fname = view.findViewById(R.id.first_name_edit_text);
        TextInputEditText lname = view.findViewById(R.id.last_name_edit_text);
        TextInputEditText sfname = view.findViewById(R.id.student_first_name_edit_text);
        TextInputEditText slname = view.findViewById(R.id.student_last_name_edit_text);
        Button b = view.findViewById(R.id.button_second);


        ArrayAdapter adapter= new ArrayAdapter(requireContext(), R.layout.list_item, items);
        AutoCompleteTextView actv = (AutoCompleteTextView) view.findViewById(R.id.user_type);
        actv.setAdapter(adapter);



        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("here", "onItemSelected: "+position);
                if(position ==0){
                    sfname.setVisibility(View.GONE);
                    slname.setVisibility(View.GONE);
                    email.setVisibility(View.VISIBLE);
                    pass.setVisibility(View.VISIBLE);
                    fname.setVisibility(View.VISIBLE);
                    lname.setVisibility(View.VISIBLE);

                    b.setVisibility(View.VISIBLE);
                    //Users u = new Users(FName,LName,UserType, new List<String>);
                    //Faculty
                    /**/

                }else if(position == 1){
                    email.setVisibility(View.GONE);
                    pass.setVisibility(View.GONE);
                    fname.setVisibility(View.GONE);
                    lname.setVisibility(View.GONE);
                    b.setVisibility(View.GONE);
                    sfname.setVisibility(View.VISIBLE);
                    slname.setVisibility(View.VISIBLE);

                }
            }
        });


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// display a message by using a Toast
                if(email.getText().length() == 0){
                    email.setError("Can not be empty");
                }else if(pass.getText().length() == 0){
                    pass.setError("Can not be empty");
                }else if(fname.getText().length() == 0){
                    fname.setError("Can not be empty");
                }else if(lname.getText().length() == 0){
                    lname.setError("Can not be empty");
                }else{
                    List<String> Sections = new ArrayList<>();
                    Users u = new Users(fname.getText().toString(),lname.getText().toString(),"Faculty",Sections);
                    Log.d("TAG", "onItemClick: "+u.FName+" "+u.LName+" "+"Faculty"+" "+Sections);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        HashMap<String,Object> userdata = new HashMap<>();
                                        userdata.put("FName",u.FName);
                                        userdata.put("LName",u.LName);
                                        userdata.put("UserType",u.UserType);
                                        userdata.put("Sections",u.Sections);
                                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("Users").document(user.getUid()).set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                startActivity(intent);

                                            }
                                        });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getActivity(), task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                //Toast.makeText(getActivity(), "First Fragment", Toast.LENGTH_LONG).show();
            }
        });
    }

}