package com.sealtosoft.porton.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sealtosoft.porton.Estructuras.EstructuraPermisos;
import com.sealtosoft.porton.Estructuras.usuarioEstruc;
import com.sealtosoft.porton.Servicios.FloatingButtonService;
import com.sealtosoft.porton.sealtoporton.R;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference rComando,rEstado, rUsuario, rPermisos;
    Button btnAccion,btnEstado;
    int Estado = 0;
    String dispoPref = "";
    FirebaseAuth auth;
    String PathComando,PathEstado,pathPermisos;
    Vibrator v;
    Boolean disponible, modoPropietario;
    TextView txtAbierto;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try{
            Log.d("Consola",intent.toString());
        }catch (Exception e){}
    }
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Consola","Entro en pause");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Maneja item seleccion
        switch (item.getItemId()){
            case R.id.mnuRegistrar:
                startActivity(new Intent(MainActivity.this, registroDispo.class));
                //finish();
                break;
            case R.id.mnuPermisos:
                if(!modoPropietario){
                    Toast.makeText(MainActivity.this,"No puede gestionar permiso por que no es propietario",Toast.LENGTH_LONG).show();
                    return true;
                }
                if(dispoPref.equals("")){
                    Toast.makeText(MainActivity.this,"No puede gestionar permiso por que no tiene dispositivo asociado",Toast.LENGTH_LONG).show();
                    return true;
                }
                startActivity(new Intent(MainActivity.this, Permisos.class));
                break;
            case R.id.mnuAceptarPermiso:
                if(modoPropietario){
                    Toast.makeText(MainActivity.this,"No puede aceptar permiso, porque es propietario",Toast.LENGTH_LONG).show();
                    return true;
                }
                startActivity(new Intent(MainActivity.this, AceptarPermiso.class));
                break;
            case R.id.mnuCerrarSession:
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(MainActivity.this, registroUsuario.class));
                finish();
                break;
            case R.id.mnuEliminar:
                rUsuario = database.getReference("Usuarios/" + auth.getCurrentUser().getUid());
                rUsuario.child("Pref").setValue("");
                rUsuario.child("Propietario").setValue(false);
                rUsuario.child("Permiso").removeValue();
                dispoPref = "";
                break;
            case R.id.mnuFloatingButton:
                if(dispoPref.equals("")){
                    Toast.makeText(MainActivity.this,"No puede abrir floating button por que no tiene dispositivo asociado",Toast.LENGTH_LONG).show();
                    return true;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.d("Consola","Ingreso en build");
                    startService(new Intent(MainActivity.this, FloatingButtonService.class));
                    finish();
                } else if (Settings.canDrawOverlays(this)) {
                    Log.d("Consola","Ingreso en draw");
                    startService(new Intent(MainActivity.this, FloatingButtonService.class));
                    finish();
                } else {
                    askPermission();
                    Toast.makeText(this, "necesitas permiso para esto", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser == null){
            Intent registro = new Intent(MainActivity.this,registroUsuario.class);
            startActivity(registro);
        }else {
            rUsuario = database.getReference("Usuarios/" + currentUser.getUid());
            rUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usuarioEstruc resul = dataSnapshot.getValue(usuarioEstruc.class);
                    try {
                        dispoPref = resul.Pref;
                    }catch (Exception e){ }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        txtAbierto = findViewById(R.id.txtAbierto);
        disponible = false;
        modoPropietario = false;
        dispoPref = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }


        auth = FirebaseAuth.getInstance();
        try {
            rUsuario = database.getReference("Usuarios/" + auth.getCurrentUser().getUid());
            rUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usuarioEstruc resul = dataSnapshot.getValue(usuarioEstruc.class);
                    try {
                        if(resul.Propietario) {
                            dispoPref = resul.Pref;
                            modoPropietario = true;
                            PathComando = "/Dispositivos/" + dispoPref + "/Comando/";
                            PathEstado = "/Dispositivos/" + dispoPref + "/Estado/";
                            rComando = database.getReference(PathComando);
                            rEstado = database.getReference(PathEstado);
                            rEstado.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    try {
                                        int val = Integer.valueOf(dataSnapshot.getValue().toString());
                                        Estado = val;
                                        switch (val) {
                                            case 0:
                                                btnAccion.setBackgroundResource(R.drawable.boton_abrir);
                                                btnEstado.setBackgroundResource(R.drawable.estados_cerrado);
                                                txtAbierto.setVisibility(View.GONE);
                                                break;
                                            case 1:
                                                btnAccion.setBackgroundResource(R.drawable.boton_parar);
                                                btnEstado.setBackgroundResource(R.drawable.estados_abriendo);
                                                break;
                                            case 2:
                                                btnAccion.setBackgroundResource(R.drawable.boton_cerrar);
                                                btnEstado.setBackgroundResource(R.drawable.estados_abierto);
                                                txtAbierto.setVisibility(View.VISIBLE);
                                                break;
                                            case 3:
                                                btnAccion.setBackgroundResource(R.drawable.boton_parar);
                                                btnEstado.setBackgroundResource(R.drawable.estados_cerrando);
                                                break;
                                        }
                                    } catch (Exception e) {

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            //if(dispoPref.equals(""))return;
                            pathPermisos = "/Permisos/" + resul.Permiso;
                            rPermisos = database.getReference(pathPermisos);
                            modoPropietario = false;
                            rPermisos.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    EstructuraPermisos permiso = dataSnapshot.getValue(EstructuraPermisos.class);
                                    if(permiso == null){
                                        Log.d("Consola","permiso null");
                                        dispoPref = "";
                                        return;
                                    }
                                    if(permiso.Dispo == ""){
                                        //el permiso ha sido eliminado o no existe
                                        Log.d("Consola","permiso eliminado");
                                        usuarioEstruc usuario = new usuarioEstruc();
                                        usuario.Email = auth.getCurrentUser().getEmail();
                                        Log.d("Consola","ingreso en null");
                                        usuario.Permiso = "";
                                        usuario.Pref = "";
                                        usuario.Propietario = false;
                                        dispoPref = "";
                                        rUsuario.setValue(usuario);
                                        return;
                                    }
                                    dispoPref = permiso.Dispo;
                                    disponible = permiso.Disponible;
                                    PathComando = "/Dispositivos/" + dispoPref + "/Comando/";
                                    PathEstado = "/Dispositivos/" + dispoPref + "/Estado/";
                                    rComando = database.getReference(PathComando);
                                    rEstado = database.getReference(PathEstado);
                                    rEstado.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            try {
                                                int val = Integer.valueOf(dataSnapshot.getValue().toString());
                                                Estado = val;
                                                switch (val) {
                                                    case 0:
                                                        btnAccion.setBackgroundResource(R.drawable.boton_abrir);
                                                        btnEstado.setBackgroundResource(R.drawable.estados_cerrado);
                                                        txtAbierto.setVisibility(View.GONE);
                                                        break;
                                                    case 1:
                                                        btnAccion.setBackgroundResource(R.drawable.boton_parar);
                                                        btnEstado.setBackgroundResource(R.drawable.estados_abriendo);
                                                        break;
                                                    case 2:
                                                        btnAccion.setBackgroundResource(R.drawable.boton_cerrar);
                                                        btnEstado.setBackgroundResource(R.drawable.estados_abierto);
                                                        txtAbierto.setVisibility(View.VISIBLE);
                                                        break;
                                                    case 3:
                                                        btnAccion.setBackgroundResource(R.drawable.boton_parar);
                                                        btnEstado.setBackgroundResource(R.drawable.estados_cerrando);
                                                        break;
                                                }
                                            } catch (Exception e) {

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch(Exception e){}






        btnAccion = findViewById(R.id.btnAccion);
        btnEstado = findViewById(R.id.btnEstado);




        btnAccion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(dispoPref.equals("")){
                    Toast.makeText(MainActivity.this,"No hay dispositivo asociado",Toast.LENGTH_LONG).show();
                    return true;
                }
                if(!modoPropietario && !disponible){
                    Toast.makeText(MainActivity.this,"El permiso no esta disponible",Toast.LENGTH_LONG).show();
                    return true;
                }
                if(Estado == 0){
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            btnAccion.setBackgroundResource(R.drawable.boton_abrir_push);
                            v.vibrate(500);
                            break;
                        case MotionEvent.ACTION_UP:
                            btnAccion.setBackgroundResource(R.drawable.boton_abrir);
                            try {
                                rComando.setValue(1);
                            }catch (Exception e){
                                Toast.makeText(MainActivity.this,"Error no se puede ejecutar",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            break;

                    }
                }else if(Estado == 2){
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            btnAccion.setBackgroundResource(R.drawable.boton_cerrar_push);
                            v.vibrate(500);
                            break;
                        case MotionEvent.ACTION_UP:
                            btnAccion.setBackgroundResource(R.drawable.boton_cerrar);
                            try {
                                rComando.setValue(2);
                            }catch (Exception e){
                                Toast.makeText(MainActivity.this,"Error no se puede ejecutar",Toast.LENGTH_LONG).show();
                                finish();
                            }
                            break;

                    }
                }else if(Estado == 1 || Estado == 3){
                    switch (motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            btnAccion.setBackgroundResource(R.drawable.boton_parar_push);
                            break;
                        case MotionEvent.ACTION_UP:
                            btnAccion.setBackgroundResource(R.drawable.boton_parar);
                            rComando.setValue(0);
                            break;

                    }
                }
                return true;
            }
        });


        btnEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dispoPref.equals("")){
                    Toast.makeText(MainActivity.this,"No hay dispositivo asociado",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!modoPropietario && !disponible){
                    Toast.makeText(MainActivity.this,"El permiso no esta disponible",Toast.LENGTH_LONG).show();
                    return;
                }
                switch(Estado){
                    case 0:
                        try {
                            rEstado.setValue(2);
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this,"Error no se puede ejecutar",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        Estado = 2;
                        break;
                    case 2:
                        try {
                            rEstado.setValue(0);
                        }catch (Exception e){
                            Toast.makeText(MainActivity.this,"Error no se puede ejecutar",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        Estado = 0;
                        break;
                }
            }
        });
    }
}
