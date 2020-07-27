package com.sealtosoft.porton.Servicios;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sealtosoft.porton.Activities.MainActivity;
import com.sealtosoft.porton.sealtoporton.R;

public class FloatingButtonService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private ImageView btnClose,btnAction, btnExpandir;
    public DatabaseReference ref,refControl,refEstado;
    public FirebaseDatabase database;
    public FirebaseAuth auth;
    public int Estado;
    public Boolean enUso,seMovio;

    public FloatingButtonService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        enUso = false;
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_button, null);
        //Este arreglo sirve para que funcione en diferentes versiones
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
       try {
           mWindowManager.addView(mFloatingView, params);
       }catch (Exception e){Log.d("Consola",e.toString());}

        btnClose = mFloatingView.findViewById(R.id.buttonClose);
       btnAction = mFloatingView.findViewById(R.id.floatingButton);
       btnExpandir = mFloatingView.findViewById(R.id.buttonExpandir);
       database = FirebaseDatabase.getInstance();
       auth = FirebaseAuth.getInstance();
       String path = "/Usuarios/" + auth.getUid() + "/Pref";
       ref = database.getReference(path);
       ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.getValue() != null){
                   String pathControl = "/Dispositivos/" + dataSnapshot.getValue() + "/Comando";
                   String pathEstado = "/Dispositivos/" + dataSnapshot.getValue() + "/Estado";
                   enUso = true;
                   refControl = database.getReference(pathControl);
                   refEstado = database.getReference(pathEstado);
                   refEstado.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           String estado = "";
                           try {
                               estado = dataSnapshot.getValue().toString();
                           }catch (Exception e){
                               Intent dialogIntent = new Intent(FloatingButtonService.this, MainActivity.class);
                               dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(dialogIntent);
                               Toast.makeText(FloatingButtonService.this,"Error!!!",Toast.LENGTH_LONG).show();
                               stopSelf();
                           }
                           switch (estado){
                               case "0":
                                   btnAction.setBackgroundResource(R.drawable.floating_button_abrir);
                                   Estado = 0;
                                   break;
                               case "1":
                                   btnAction.setBackgroundResource(R.drawable.floating_button_parar);
                                   Estado = 1;
                                   break;
                               case "2":
                                   btnAction.setBackgroundResource(R.drawable.floating_button_cerrar);
                                   Estado = 2;
                                   break;
                               case "3":
                                   btnAction.setBackgroundResource(R.drawable.floating_button_parar);
                                   Estado = 3;
                                   break;
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

               }else{
                   enUso = false;
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        btnExpandir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialogIntent = new Intent(FloatingButtonService.this, MainActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
                stopSelf();
            }
        });

        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        switch (Estado){
                            case 0:
                                btnAction.setBackgroundResource(R.drawable.floating_button_abrir_push);
                                break;
                            case 1:
                                btnAction.setBackgroundResource(R.drawable.floating_button_parar_push);
                                break;
                            case 2:
                                btnAction.setBackgroundResource(R.drawable.floating_button_cerrar_push);
                                break;
                            case 3:
                                btnAction.setBackgroundResource(R.drawable.floating_button_parar_push);
                                break;
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if(seMovio){
                            switch (Estado){
                                case 0:
                                    btnAction.setBackgroundResource(R.drawable.floating_button_abrir);
                                    break;
                                case 1:
                                    btnAction.setBackgroundResource(R.drawable.floating_button_parar);
                                    break;
                                case 2:
                                    btnAction.setBackgroundResource(R.drawable.floating_button_cerrar);
                                    break;
                                case 3:
                                    btnAction.setBackgroundResource(R.drawable.floating_button_parar);
                                    break;
                            }
                            seMovio = false;
                            return true;
                        }
                        if(enUso){
                            switch (Estado){
                                case 0:
                                    refControl.setValue(1);
                                    break;
                                case 1:
                                    refEstado.setValue(0);
                                    break;
                                case 2:
                                    refControl.setValue(2);
                                    break;
                                case 3:
                                    refControl.setValue(0);
                                    break;
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        seMovio = true;
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
