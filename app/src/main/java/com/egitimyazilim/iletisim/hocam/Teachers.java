package com.egitimyazilim.iletisim.hocam;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class Teachers extends Fragment {

    FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.teachers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.teachers);

        auth=FirebaseAuth.getInstance();

        FloatingActionButton fabButtonAdd = (FloatingActionButton) view.findViewById(R.id.fabButtonTeacherAdd);
        fabButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences=getActivity().getSharedPreferences("school_infos", Context.MODE_PRIVATE);
                String schoolCode=preferences.getString("school_code","");

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                final String teacherName="Mehmet Yılmaz";
                String teacherUserName="mehmet_yilmaz";
                String teacherPassword="123mehmet";

                Map<String, Object> teacher = new HashMap<>();
                teacher.put("teacher_name", teacherName);
                teacher.put("teacher_username",teacherUserName);
                teacher.put("teacher_password",teacherPassword);
                teacher.put("teachers_school_code",schoolCode);

                db.collection("schools").document(schoolCode).collection("teachers").document(teacherUserName)
                        .set(teacher
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(),teacherName+" eklendi",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(),teacherName+" eklenirken hata oluştu!",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
