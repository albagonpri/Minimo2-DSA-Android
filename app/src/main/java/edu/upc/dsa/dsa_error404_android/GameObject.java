package edu.upc.dsa.dsa_error404_android;
import com.google.gson.annotations.SerializedName;

public class GameObject {

    @SerializedName("id")
    private String id;
    private String nombre;
    private int precio;

    private String descripcion;
    public GameObject() {
    }

    public GameObject(String objectId, String nombre, int precio) {
        this.id = objectId;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
