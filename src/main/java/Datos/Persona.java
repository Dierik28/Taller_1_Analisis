package Datos;
//Se importan las librerias necesarias.
import java.util.ArrayList;
import java.util.List;

public class Persona {
    //Se inicializan los atributos de la clase.
    private int id; //Atributo que se usara para identificar a la persona.
    private String nombre; //Nombre que se le asignara a la persona.
    private String direccion; //Direccion que tendra la persona.
    private List<Telefono> telefonos; //Numeros de telefono asignados a la persona.

    //Constructor que solo inicializa el array de telefonos.
    public Persona(){
        this.telefonos = new ArrayList<>();
    }
    //Segundo constructor que recibe el nombre y direccion de la persona.
    public Persona(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = new ArrayList<>();
    }
    //Tercer constructor que recibe el id, nombre y direccion de la persona.
    public Persona(int id,String nombre, String direccion){
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefonos = new ArrayList<>();
    }

    //Getters y setters de cada atributo
    public int getId() {
        return id;
    }

    public String getNombre(){
        return  nombre;
    }

    public String getDireccion(){
        return direccion;
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
    }
}
