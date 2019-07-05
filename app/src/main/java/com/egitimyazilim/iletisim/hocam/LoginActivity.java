package com.egitimyazilim.iletisim.hocam;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;
    private FirebaseAuth auth;
    int positionNo;
    Button buttonSignIn, buttonRegister;
    CheckBox checkBoxRememberMe;
    EditText editTextEmail, editTextPassword;
    TextView textViewEmail, textViewPassword;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences=getSharedPreferences("position",MODE_PRIVATE);
        positionNo=preferences.getInt("positionNo",-1);

        buttonSignIn = (Button) findViewById(R.id.buttonLoginSignIn);
        buttonRegister = (Button) findViewById(R.id.buttonLoginRegister);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        editTextEmail = (EditText) findViewById(R.id.editTextloginEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextloginPassword);
        textViewEmail = (TextView) findViewById(R.id.textViewLoginEmail);
        textViewPassword = (TextView) findViewById(R.id.textViewLoginPassword);

        auth=FirebaseAuth.getInstance();

        if (positionNo == 1) {
            textViewEmail.setText("Kullanıcı Adı");
            editTextEmail.setHint("Kullanıcı adınızı giriniz...");
            buttonRegister.setVisibility(View.INVISIBLE);
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                final String[] manegerName = {""};

                boolean isInternetConnAvaliable=isNetworkAvailable();
                if(isInternetConnAvaliable==false){
                    Toast.makeText(getApplicationContext(),"Kullanılabilir internet bağlantınız yok!",Toast.LENGTH_SHORT).show();
                }else {
                    if (positionNo == 0) {
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Şifre boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Yetkilendirme Hatası", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    } else if (positionNo == 1) {
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Şifre boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                        } else {
                            String schoolCode = "753938";

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef2 = db.collection("schools").document(schoolCode);
                            final DocumentReference docRef = db.collection("schools").document(schoolCode).collection("teachers").document(email);
                            final Source source = Source.DEFAULT;

                            docRef2.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document2 = task.getResult();
                                        if (document2.getData() == null) {
                                            Toast.makeText(getApplicationContext(), "Okulunuz kayıtlarda bulunamadı. Lütfen yöneticinizle görüşünüz ve okul adına hesap açınız.", Toast.LENGTH_LONG).show();
                                        } else {
                                            manegerName[0] = document2.getData().get("maneger_name").toString();

                                            docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        // Document found in the offline cache
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.getData() == null) {
                                                            Toast.makeText(getApplicationContext(), "Kullanıcı kayıtlarda bulunamadı. Lütfen yöneticiniz " + manegerName[0] + " ile görüşünüz. Sizi eklemesi gerekir.", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            String usermaneTeacher = document.getData().get("teacher_username").toString();
                                                            String passwordTeacher = document.getData().get("teacher_password").toString();

                                                            if (!email.equals(usermaneTeacher) || !password.equals(passwordTeacher)) {
                                                                Toast.makeText(getApplicationContext(), "Kullanıcı adı veya şifre hatalı!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Giriş başarılı", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), ActivityMain.class));
                                                                finish();
                                                            }
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Sunucuya bağlanılamadı!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Sunucuya bağlanılamadı!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences=getSharedPreferences("hasAccount",MODE_PRIVATE);
                preferences.edit().putInt("hasaccount",0).commit();
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
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
