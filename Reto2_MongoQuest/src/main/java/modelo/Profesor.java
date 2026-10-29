/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.List;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

/**
 *
 * @author songo
 */
public class Profesor {
    
    @BsonId
    private ObjectId id;
    private String nombre;
    private String Apellidos;
    private String email;
    private String contraseña;
    private int sueldo;
    private int horasTrabajo;
    private List<Curso> cursos;

    public Profesor(ObjectId id, String nombre, String Apellidos, String email, String contraseña, int sueldo, int horasTrabajo, List<Curso> cursos) {
        this.id = id;
        this.nombre = nombre;
        this.Apellidos = Apellidos;
        this.email = email;
        this.contraseña = contraseña;
        this.sueldo = sueldo;
        this.horasTrabajo = horasTrabajo;
        this.cursos = cursos;
    }

    public Profesor() {
    }

    public Profesor(String nombre, String Apellidos, String email) {
        this.nombre = nombre;
        this.Apellidos = Apellidos;
        this.email = email;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String Apellidos) {
        this.Apellidos = Apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getSueldo() {
        return sueldo;
    }

    public void setSueldo(int sueldo) {
        this.sueldo = sueldo;
    }

    public int getHorasTrabajo() {
        return horasTrabajo;
    }

    public void setHorasTrabajo(int horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }

    @Override
    public String toString() {
        return "Profesor{" + "id=" + id + ", nombre=" + nombre + ", Apellidos=" + Apellidos + ", email=" + email + ", contrase\u00f1a=" + contraseña + ", sueldo=" + sueldo + ", horasTrabajo=" + horasTrabajo + ", cursos=" + cursos + '}';
    }
    
    
}
