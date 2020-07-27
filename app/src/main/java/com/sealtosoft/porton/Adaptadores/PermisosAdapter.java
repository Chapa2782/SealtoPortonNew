package com.sealtosoft.porton.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sealtosoft.porton.Estructuras.PermisoEstruc;
import com.sealtosoft.porton.sealtoporton.R;

import java.util.List;

public class PermisosAdapter extends ArrayAdapter<PermisoEstruc> {
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseAuth auth;
    String Prop,ID;
    public PermisosAdapter(Context context, List<PermisoEstruc> objects){
        super(context,0,objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.lista_permisos,
                    parent,
                    false);
        }
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        Prop = auth.getUid();

        // Referencias UI.
        TextView id = convertView.findViewById(R.id.txtIdListado);
        TextView dispo = convertView.findViewById(R.id.txtDispositivoListado);
        TextView pass = convertView.findViewById(R.id.txtPassListado);
        final Switch estado = convertView.findViewById(R.id.switchActivo);

        // Lead actual.
        final PermisoEstruc permisos = getItem(position);

        // Setup.
        id.setText(permisos.getId());
        dispo.setText(permisos.getDispo());
        pass.setText(permisos.getPass());
        estado.setChecked(permisos.Disponible);
        ID = id.getText().toString();
        estado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String path = "/Usuarios/" + Prop + "/Permisos/" + ID;
                ref = database.getReference(path);
                ref.child("Disponible").setValue(estado.isChecked());
                path = "/Permisos/" + ID;
                ref = database.getReference(path);
                ref.child("Disponible").setValue(estado.isChecked());
            }
        });
        return convertView;
    }
}
