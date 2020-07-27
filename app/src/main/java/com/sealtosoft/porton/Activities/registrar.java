package com.sealtosoft.porton.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sealtosoft.porton.Estructuras.usuarioEstruc;
import com.sealtosoft.porton.sealtoporton.R;

public class registrar extends AppCompatActivity {
    EditText Email,Pass1,Pass2,IdDispo,PassDispo;
    Button btnRegistrarme;
    String email,pass1,pass2;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference refDispo,refUsuarios;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        Email = findViewById(R.id.regEmail);
        Pass1 = findViewById(R.id.regPass1);
        Pass2 = findViewById(R.id.regPass2);
        btnRegistrarme = findViewById(R.id.btnRegistrarme);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        refDispo = database.getReference("Dispositivos");
        refUsuarios = database.getReference("Usuarios");
        prefs = getSharedPreferences("Preferencias",MODE_PRIVATE);
        editor = prefs.edit();


        btnRegistrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = Email.getText().toString();
                email = email.replace(" ","");
                pass1 = Pass1.getText().toString();
                pass2 = Pass2.getText().toString();
                if(email.isEmpty()){
                    Email.setError("Debe especificar un email");
                    return;
                }
                if(email.length() < 3){
                    Email.setError("El email no es valido");
                    return;
                }
                if(pass1.length() < 6){
                    Pass1.setError("La contraseña debe tener almenos 6 caracteres");
                    return;
                }
                if(!pass1.equals(pass2)){
                    Pass1.setError("Las contraseñas no coinciden");
                    return;
                }
                auth.createUserWithEmailAndPassword(email,pass1)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d("Registro",task.getResult().getUser().getUid());
                                    refUsuarios = database.getReference("Usuarios/" +task.getResult().getUser().getUid() );
                                    usuarioEstruc usuario = new usuarioEstruc();
                                    usuario.Email = task.getResult().getUser().getEmail();
                                    usuario.Pref = "";
                                    refUsuarios.setValue(usuario);
                                    startActivity(new Intent(registrar.this, MainActivity.class));
                                    finish();
                                }else{
                                    Log.d("Registro",task.getException().toString());
                                    Toast.makeText(registrar.this,"Problemas para hacer el registro, revise los datos",Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });
    }
}
