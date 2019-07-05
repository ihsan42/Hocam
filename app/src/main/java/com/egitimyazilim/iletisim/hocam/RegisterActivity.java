package com.egitimyazilim.iletisim.hocam;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences preferences;
    Dialog dialogPosition;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 9001;
    int positionNo;
    Button buttonRegister, buttonHasAccount;
    EditText editTextManegerName, editTextSchoolName, editTextSchoolCode, editTextEmail, editTextPassword, editTextPasswordAgain;
    String manegerName, schoolName, schoolCode,email,password,passwordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        buttonHasAccount=(Button)findViewById(R.id.buttonHasAccount);
        editTextSchoolName=(EditText)findViewById(R.id.editTextSchoolName);
        editTextSchoolCode=(EditText)findViewById(R.id.editTextSchoolCode);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextPasswordAgain=(EditText)findViewById(R.id.editTextPasswordAgain);
        editTextManegerName=(EditText)findViewById(R.id.editTexRegisterManegerName);

        auth = FirebaseAuth.getInstance();

        preferences=getSharedPreferences("position",MODE_PRIVATE);
        positionNo=preferences.getInt("positionNo",-1);

       if(positionNo==0){
           preferences=getSharedPreferences("hasAccount",MODE_PRIVATE);
           int hasaccount=preferences.getInt("hasaccount",0);

           if(hasaccount==1){
               startActivity(new Intent(getApplicationContext(),LoginActivity.class));
               finish();
           }
        }else if(positionNo==1){
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            intent.putExtra("position",positionNo);
            startActivity(intent);
            finish();
        }

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolName=editTextSchoolName.getText().toString().trim();
                schoolCode=editTextSchoolCode.getText().toString().trim();
                email=editTextEmail.getText().toString().trim();
                password=editTextPassword.getText().toString().trim();
                passwordAgain=editTextPasswordAgain.getText().toString().trim();
                manegerName=editTextManegerName.getText().toString().trim();

                boolean isInternetConnAvaliable=isNetworkAvailable();
                if(isInternetConnAvaliable==false){
                    Toast.makeText(getApplicationContext(),"Kullanılabilir internet bağlantını yok!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(schoolName)){
                    Toast.makeText(getApplicationContext(),"Okul ismi boş bırakılamz",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(schoolCode)){
                    Toast.makeText(getApplicationContext(),"Okul kodu boş bırakılamz",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"E-mail boş bırakılamz",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Şifre boş bırakılamz",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(passwordAgain)){
                    Toast.makeText(getApplicationContext(),"Şifre tekrar bölümü boş bırakılamaz",Toast.LENGTH_SHORT).show();
                }else if(!password.equals(passwordAgain)){
                    Toast.makeText(getApplicationContext(),"Şifreler aynı değil!",Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Yetkilendirme Hatası", Toast.LENGTH_SHORT).show();
                                    }else {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        Map<String, Object> school = new HashMap<>();
                                        school.put("school_name", schoolName);
                                        school.put("school_code", schoolCode);
                                        school.put("maneger_name", manegerName);
                                        school.put("maneger_email", email);

                                        db.collection("schools").document(schoolCode)
                                                .set(school
                                                )
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        preferences = getSharedPreferences("school_infos", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = preferences.edit();
                                                        editor.putString("school_code", schoolCode);
                                                        editor.putString("school_name", schoolName);
                                                        editor.putString("maneger_email", email);
                                                        editor.putString("maneger_name", manegerName);
                                                        editor.commit();

                                                        Toast.makeText(RegisterActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RegisterActivity.this, "Bilgiler kaydedilemedi!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    }
                                }
                            });
                }
            }
        });

        buttonHasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences=getSharedPreferences("hasAccount",MODE_PRIVATE);
                preferences.edit().putInt("hasaccount",1).commit();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
