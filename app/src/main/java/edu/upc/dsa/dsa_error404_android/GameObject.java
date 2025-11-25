package edu.upc.dsa.dsa_error404_android;

public class GameObject {
    private String Id;
    private String nombre;
    private int precio;
    public GameObject() {
    }

    public GameObject(String objectId, String nombre, int precio) {
        this.Id = objectId;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
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
}
