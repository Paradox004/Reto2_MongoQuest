package tonguely.Log;

import java.time.LocalDateTime;

public class EntradaLog {
    private LocalDateTime fecha;
    private String usuario;
    private String tipo;
    private String coleccion;
    private String resultado;
    private String detalle;

    public EntradaLog(LocalDateTime fecha, String usuario, 
        String tipo, String coleccion, String resultado, String detalle) {
            this.fecha = fecha;
            this.usuario = usuario;
            this.tipo = tipo;
            this.coleccion = coleccion;
            this.resultado = resultado;
            this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "EntradaLog [ ]";
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getColeccion() {
        return coleccion;
    }

    public String getResultado() {
        return resultado;
    }

    public String getDetalle() {
        return detalle;
    }
    
}
