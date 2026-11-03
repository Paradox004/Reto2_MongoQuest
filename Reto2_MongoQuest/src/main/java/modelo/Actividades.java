/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import org.bson.types.ObjectId;

/**
 *
 * @author songo
 */
public class Actividades {
    
    private ObjectId id;
    private ObjectId idCurso;
    private String tipo;
    private String pregunta;
    private ArrayList<String> opciones;
    private String respuesta_correcta;

    public Actividades(ObjectId id, ObjectId idCurso, String tipo, String pregunta, ArrayList<String> opciones, String respuesta_correcta) {
        this.id = id;
        this.idCurso = idCurso;
        this.tipo = tipo;
        this.pregunta = pregunta;
        this.opciones = opciones;
        this.respuesta_correcta = respuesta_correcta;
    }

    public Actividades() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(ObjectId idCurso) {
        this.idCurso = idCurso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public ArrayList<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(ArrayList<String> opciones) {
        this.opciones = opciones;
    }

    public String getRespuesta_correcta() {
        return respuesta_correcta;
    }

    public void setRespuesta_correcta(String respuesta_correcta) {
        this.respuesta_correcta = respuesta_correcta;
    }

    @Override
    public String toString() {
        return "Actividades{" + "id=" + id + ", idCurso=" + idCurso + ", tipo=" + tipo + ", pregunta=" + pregunta + ", opciones=" + opciones + ", respuesta_correcta=" + respuesta_correcta + '}';
    }
    
    
}
