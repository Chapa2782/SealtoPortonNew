package com.sealtosoft.porton.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sealtosoft.porton.Estructuras.dispositivosEstruc;
import com.sealtosoft.porton.Estructuras.usuarioEstruc;
import com.sealtosoft.porton.sealtoporton.R;

public class registroDispo extends AppCompatActivity {
    EditText IdDispo,PassDispo;
    Button btnRegistrar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference ref,refUsuario;
    SharedPreferences prefs;
    String Email;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_dispo);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Dispositivos");

        prefs = getSharedPreferences("Preferencias",MODE_PRIVATE);
        editor = prefs.edit();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        try {
            Email = user.getEmail();
        }catch (Exception e){}
        IdDispo = findViewById(R.id.IdDispo);
        PassDispo = findViewById(R.id.PassDispo);
        btnRegistrar = findViewById(R.id.btnRegistrarDispo);


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref = database.getReference("Dispositivos/" + IdDispo.getText().toString().toUpperCase());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dispositivosEstruc result = dataSnapshot.getValue(dispositivosEstruc.class);
                        try {
                            if (!result.Clave.equals(PassDispo.getText().toString())) {
                                PassDispo.setError("La clave no es correcta");
                                return;
                            } else {
                                refUsuario = database.getReference("Usuarios/" + user.getUid() + "/Dispositivos/");
                                refUsuario.push().setValue(IdDispo.getText().toString());
                                refUsuario = database.getReference("Usuarios/" + user.getUid());
                                usuarioEstruc usuario = new usuarioEstruc();
                                usuario.Email = user.getEmail();
                                usuario.Pref = IdDispo.getText().toString().toUpperCase();
                                usuario.Propietario = true;
                                refUsuario.setValue(usuario);
                                finish();
                            }
                        }catch (Exception e){
                            Toast.makeText(registroDispo.this,"Error el dispositivo no esta disponible",Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



    }
}
