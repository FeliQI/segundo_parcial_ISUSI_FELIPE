/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;


public class DetalleDeCarrito {
    private Producto producto;
    private Integer cantidad;
    
    public DetalleDeCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    public String getNombre(){
        return producto.getNombre();
    }
    
    public Producto getProducto(){
        return producto;
    }
    
    public Integer getCantidad(){
        return cantidad;
    }
    
    public Double getSubTotal(){
        return cantidad * producto.getPrecio();
    }
    
    @Override
    public String toString() {
        return getNombre() + "------- $" + getSubTotal();
    }
}
