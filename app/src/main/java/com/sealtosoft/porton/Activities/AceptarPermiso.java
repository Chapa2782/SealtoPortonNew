package com.sealtosoft.porton.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sealtosoft.porton.Estructuras.EstructuraPermisos;
import com.sealtosoft.porton.Estructuras.usuarioEstruc;
import com.sealtosoft.porton.sealtoporton.R;

public class AceptarPermiso extends AppCompatActivity {
    EditText ID,PASS;
    Button btnAceptar;
    FirebaseDatabase database;
    DatabaseReference refUsuario, refPermiso;
    FirebaseAuth auth;
    String idUsuario, pathUsuario, pathPermiso, eMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptar_permiso);
        ID = findViewById(R.id.idPermiso);
        PASS = findViewById(R.id.passPermiso);
        btnAceptar = findViewById(R.id.btnAceptarPermiso);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        idUsuario = auth.getUid();
        eMail = auth.getCurrentUser().getEmail();

        pathUsuario = "/Usuarios/" + idUsuario;
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathPermiso = "/Permisos/" + ID.getText().toString();
                refPermiso = database.getReference(pathPermiso);
                refPermiso.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        EstructuraPermisos permisos = dataSnapshot.getValue(EstructuraPermisos.class);
                        if(permisos == null){
                            Toast.makeText(AceptarPermiso.this,"El permiso no existe",Toast.LENGTH_LONG).show();
                            return;
                        }
                        String pass = PASS.getText().toString();
                        String dispo = permisos.Dispo;
                        if(!pass.equals(permisos.Pass)){
                            Toast.makeText(AceptarPermiso.this,"Contraseña incorrecta",Toast.LENGTH_LONG).show();
                            return;
                        }
                        refUsuario = database.getReference(pathUsuario);
                        usuarioEstruc usuario = new usuarioEstruc();
                        usuario.Propietario = false;
                        usuario.Email = eMail;
                        usuario.Permiso = ID.getText().toString();
                        usuario.Pref = dispo;
                        refUsuario.setValue(usuario);
                        Toast.makeText(AceptarPermiso.this,"Permiso Aceptado",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
