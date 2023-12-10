/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package principalCliente;

/**
 *
 * @author Angel
 */
public class Producto {

    private String nombre;
    private String categoria;
    private double precio;
    private int id;
    private int cantidad = 1;

    public Producto(String nombre, String categoria, double precio, int id) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPrecio() {
        return precio;
    }
    
    public int getId() {
        return id;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void addCantidad() {
        this.cantidad++;
    }

    @Override
    public String toString() {
        return "Producto: " + "Nombre = " + nombre + ", Categoria=" + categoria + ", Precio = " + precio + ",ID: " + id;
    }
    
}
