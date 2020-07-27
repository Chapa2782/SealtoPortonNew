package com.sealtosoft.porton.Estructuras;

public class PermisoEstruc {
    public String Id;
    public String Pass;
    public String Dispo;
    public Boolean Disponible;
    public String Propietario;
    public PermisoEstruc(){}
    public PermisoEstruc(String Id,String Pass,String Dispo,Boolean Disponible,String Propietario){
        this.Id = Id;
        this.Pass = Pass;
        this.Dispo = Dispo;
        this.Disponible = Disponible;
        this.Propietario = Propietario;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setPass(String pass) {
        Pass = pass;
    }

    public void setDispo(String dispo) {
        Dispo = dispo;
    }

    public void setDisponible(Boolean disponible) {
        Disponible = disponible;
    }

    public void setPropietario(String propietario) {
        Propietario = propietario;
    }

    public String getPass() {
        return Pass;
    }

    public String getDispo() {
        return Dispo;
    }

    public Boolean getDisponible() {
        return Disponible;
    }

    public String getPropietario() {
        return Propietario;
    }
}
