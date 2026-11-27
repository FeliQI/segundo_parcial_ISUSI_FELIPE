/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistemadeventasparcial;


import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import modelo.DetalleDeCarrito;
import modelo.Producto;


public class SysDeVentasController implements Initializable {

    @FXML
    private TableView<Producto> tblProductos;
    
    private ObservableList<Producto> productos = FXCollections.observableArrayList();
    
    @FXML
    private TextField txtCantidad;
    
    @FXML
    private Button btnAgregar;
    
    @FXML
    private ListView<DetalleDeCarrito> tblCarrito;
    
    private ObservableList<DetalleDeCarrito> productosCarrito = FXCollections.observableArrayList();
    
    @FXML
    private Button btnConfirmar;
    
    @FXML
    private TableColumn<Producto, String> colNombre;
    
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    
    @FXML
    private TableColumn<Producto, Integer> colStock;
    
    
    @FXML
    private void agregarACarrito(){
        Producto producto = tblProductos.getSelectionModel().getSelectedItem();
        Integer cantidad = obtenerCantidad();
        
        if (sePuedeAñadir(producto, cantidad)){
            añadirDetalleACarrito(producto, cantidad);
            reducirStock(producto, cantidad);
            tblProductos.refresh();
        }
       
    }
    
    private Boolean sePuedeAñadir(Producto producto, Integer cantidad){
        
        if(producto == null){
            mostrarError("No se selecciono ningun producto");
            return false;
        }
        if(!(cantidad > 0)){
            mostrarError("La cantidad debe ser mayor a cero");
            return false;
        }
        if(cantidad > producto.getStock()){
            mostrarError("La cantidad no puede superar el stock");
            return false;
        }
        
        return true;
    }
    
    private void añadirDetalleACarrito(Producto producto, Integer cantidad){
        productosCarrito.add(new DetalleDeCarrito(producto, cantidad));
    }
    
    private void mostrarError(String mensaje){
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de validacion");
        alerta.setHeaderText("No se pudo realizar la operacion");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    private Integer obtenerCantidad(){
        
        try {
            return Integer.parseInt(txtCantidad.getText());
        }
        catch (NumberFormatException e) {
            mostrarError("Ingrese una cantidad valida");
            return -1;
        }
    }
    
    private void reducirStock(Producto producto, Integer cantidad){
        Integer stock = producto.getStock();
        
        if(stock > 1){
           producto.setStock(stock - cantidad); 
        }
        else {
           productos.remove(producto);
        }
    }
    
    @FXML
    private void confirmarCompra(){
        
        if(!productosCarrito.isEmpty()){
            if(generarTicket()){
                productosCarrito.clear();
                actualizarStock();
            }
        }
    }
    
    private Boolean generarTicket(){
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("Ticket.txt"))) {
            
            for(DetalleDeCarrito detalle : productosCarrito) {
                escritor.write(detalle.getNombre() + " - " +
                               detalle.getCantidad() + " - $" +
                               detalle.getSubTotal());
                escritor.newLine();
            }
            
            escritor.newLine();
            escritor.write("Total: " + " - $" + calcularTotal());
            return true;
        }
        catch (IOException e) {
            System.out.println("Error al guardar las tareas: " + e.getMessage());
            return false;
        }
    }
    
    private Double calcularTotal(){
        Double total = 0.0;
        
        for(DetalleDeCarrito detalle : productosCarrito){
            total += detalle.getSubTotal();
        }
        
        return total;
    }
    
    private void actualizarStock(){
        
        //System.out.println("Directorio: " + System.getProperty("user.dir"));
        //System.out.println("Archivo " + new File("build/classes/modelo/prodcutos.dat").getAbsolutePath());
        
        try(ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("build/classes/modelo/productos.dat"))){
            
            salida.writeObject(new ArrayList<>(productos));
            System.out.println("Stock guardado correctamente");
        }
        catch(IOException e){
            System.out.println("Error al actualizar stock: " + e.getMessage());
        }
    }
    
    
    private void cargarStock(){
        
        File archivo = new File("build/classes/modelo/productos.dat");
        
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))){
            
            ArrayList<Producto> productosGuardados = (ArrayList<Producto>) entrada.readObject();
            
            productos.setAll(productosGuardados);
        }
        catch (FileNotFoundException e) {
            System.out.println("Error al cargar archivo: " + e.getMessage());
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al cargar stock: " + e.getMessage());
        }
    }
    
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       cargarStock();
       tblProductos.setItems(productos);
       tblCarrito.setItems(productosCarrito);
       
       colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getNombre()));
       colStock.setCellValueFactory(celda -> new SimpleIntegerProperty(celda.getValue().getStock()).asObject());
       colPrecio.setCellValueFactory(celda -> new SimpleDoubleProperty(celda.getValue().getPrecio()).asObject());
    }    

    
}
