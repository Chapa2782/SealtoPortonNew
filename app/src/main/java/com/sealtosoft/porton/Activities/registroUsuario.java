package com.sealtosoft.porton.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sealtosoft.porton.sealtoporton.R;

public class registroUsuario extends AppCompatActivity {
    EditText userEmail,userPass;
    Button btnIniciar,btnRegistrar;
    FirebaseAuth auth;
    FirebaseUser user;
    String email,pass;
    DatabaseReference ref;
    FirebaseDatabase database;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){

        }else{
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.userPass);
        btnIniciar = findViewById(R.id.btnIngresar);
        btnRegistrar = findViewById(R.id.btnRegistrarse);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Usuario");
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(registroUsuario.this, registrar.class));
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = userEmail.getText().toString();
                pass = userPass.getText().toString();
                if (email.isEmpty()) {
                    userEmail.setError("Especifique un email");
                    return;
                }
                if (email.length() < 3) {
                    userEmail.setError("El email es ivalido");
                    return;
                }
                if (pass.isEmpty()) {
                    userEmail.setError("Especifique una contraseña");
                    return;
                }
                if (email.length() < 6) {
                    userEmail.setError("La contraseña debe tener al menos 6 caracteres");
                    return;
                }
                email = email.replace(" ","");
                auth.signInWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(registroUsuario.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    user = auth.getCurrentUser();
                                    startActivity(new Intent(registroUsuario.this, MainActivity.class));
                                    finish();
                                }else{
                                    Log.d("Consola","Error no valido");
                                    Toast.makeText(registroUsuario.this,"Usuario no valido !!!",Toast.LENGTH_LONG);
                                    userEmail.setText("");
                                    userPass.setText("");
                                }
                            }
                        });

            }
            });

    }
}
