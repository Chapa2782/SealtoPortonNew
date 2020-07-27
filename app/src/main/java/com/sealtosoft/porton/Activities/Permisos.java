package com.sealtosoft.porton.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sealtosoft.porton.Estructuras.EstructuraPermisos;
import com.sealtosoft.porton.Estructuras.PermisoEstruc;
import com.sealtosoft.porton.Estructuras.usuarioEstruc;
import com.sealtosoft.porton.Adaptadores.PermisosAdapter;
import com.sealtosoft.porton.sealtoporton.R;

import java.util.ArrayList;

public class Permisos extends AppCompatActivity {
    Button btnLimpiar,btnAceptar;
    EditText txtIdPermiso,txtPassPermiso;
    FirebaseAuth auth;
    DatabaseReference ref,refDatos, refPermisos;
    FirebaseDatabase database;
    String Dispo, Propietario;
    ListView listado;
    PermisosAdapter adaptador;
    EstructuraPermisos permiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permisos);
        btnAceptar = findViewById(R.id.btnEnviarPermiso);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        txtIdPermiso = findViewById(R.id.IdPermiso);
        txtPassPermiso = findViewById(R.id.ClavePermiso);

        adaptador = new PermisosAdapter(getBaseContext(),new ArrayList<PermisoEstruc>());
        listado = findViewById(R.id.listadoPermisos);
        listado.setAdapter(adaptador);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("/Permisos");
        Propietario = auth.getUid();
        String path = "/Usuarios/" + Propietario;
        refDatos = database.getReference("/Usuarios/" + auth.getUid());
        refDatos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarioEstruc dispo = dataSnapshot.getValue(usuarioEstruc.class);
                Dispo = dispo.Pref;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        refPermisos = database.getReference("/Usuarios/" + Propietario + "/Permisos/" + txtIdPermiso.getText().toString());
        refPermisos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                EstructuraPermisos permisos = dataSnapshot.getValue(EstructuraPermisos.class);
                try{

                   adaptador.add(new PermisoEstruc(permisos.ID,permisos.Pass,permisos.Dispo,permisos.Disponible,permisos.Propietario));
                }catch (Exception e){
                    Log.d("Consola", e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                finish();
                startActivity(getIntent());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtIdPermiso.setText("");
                txtPassPermiso.setText("");
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Dispo.equals("")){
                    Toast.makeText(Permisos.this,"No hay dispositivo asociado", Toast.LENGTH_LONG).show();
                    txtIdPermiso.setText("");
                    txtPassPermiso.setText("");
                    finish();
                    return;
                }
                if(txtPassPermiso.getText().length() < 4){
                    Toast.makeText(Permisos.this,"La contraseña debe tener minimo 6 caracteres",Toast.LENGTH_LONG).show();
                    return;
                }
                permiso  = new EstructuraPermisos();
                permiso.ID = txtIdPermiso.getText().toString();
                permiso.Pass = txtPassPermiso.getText().toString();
                permiso.Dispo = Dispo;
                permiso.Disponible = true;
                permiso.Propietario = Propietario;
                ref = database.getReference("/Usuarios/" + Propietario + "/Permisos/" + txtIdPermiso.getText().toString());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        EstructuraPermisos p = dataSnapshot.getValue(EstructuraPermisos.class);
                        try {
                            Log.d("Consola", p.toString());
                        }catch (Exception e){
                            Log.d("Consola",e.toString());
                        }
                        if (p != null) {
                            Toast.makeText(Permisos.this,
                                    "Este nombre de permiso ya existe",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }else{
                            ref.setValue(permiso);
                            ref = database.getReference("/Permisos/" + txtIdPermiso.getText().toString());
                            ref.setValue(permiso);
                            txtIdPermiso.setText("");
                            txtPassPermiso.setText("");
                            Toast.makeText(Permisos.this,"Permiso emitido",Toast.LENGTH_LONG).show();
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
