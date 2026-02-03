package Datos;
//Se importan las librerias necesarias.
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import java.util.List;

public class Persona {
    //Se inicializan los atributos de la clase.
    private SimpleIntegerProperty id; //Atributo que se usara para identificar a la persona.
    private SimpleStringProperty nombre; //Nombre que se le asignara a la persona.
    private SimpleStringProperty direccion; //Direccion que tendra la persona.
    private List<Telefono> telefonos; //Numeros de telefono asignados a la persona.

    //Constructor que solo inicializa los atributos.
    public Persona() {
        this.id = new SimpleIntegerProperty(0);
        this.nombre = new SimpleStringProperty("");
        this.direccion = new SimpleStringProperty("");
        this.telefonos = new ArrayList<>();
    }
    //Segundo constructor que recibe el nombre y direccion de la persona.
    public Persona(String nombre, String direccion) {
        this();
        this.nombre.set(nombre);
        this.direccion.set(direccion);
    }
    //Tercer constructor que recibe el id, nombre y direccion de la persona.
    public Persona(int id, String nombre, String direccion) {
        this();
        this.id.set(id);
        this.nombre.set(nombre);
        this.direccion.set(direccion);
    }

    //Getters y setters de cada atributo
    public int getId() {
        return id.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getDireccion() {
        return direccion.get();
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setDireccion(String direccion) {
        this.direccion.set(direccion);
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }

    //Metodos para saber las propiedades del objeto Persona esto para que JavaFx pueda reaccionar a sus cambios
    //implementado para actualizar automaticamente el TableView de la interfaz.
    public SimpleIntegerProperty idProperty() {
        return id;
    }
    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public SimpleStringProperty direccionProperty() {
        return direccion;
    }

    //Metodo para agregar un telefono a la lista de telefonos del usuario.
    public void agregarTelefono(Telefono telefono) {
        this.telefonos.add(telefono);
    }
    //Metodo para eliminar un telefono de la lista de telefonos del usuario.
    public void eliminarTelefono(Telefono telefono) {
        this.telefonos.remove(telefono);
    }
    //Metodo toString.
    @Override
    public String toString() {
        return getNombre() + " - " + getDireccion();
    }
}