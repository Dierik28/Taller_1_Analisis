package Datos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Direccion {
    //Propiedades JavaFX para soportar enlace de datos bidireccional (data binding)
    private SimpleIntegerProperty id; //Propiedad para el ID de la dirección
    private SimpleStringProperty calle; //Propiedad para el nombre de la calle
    private SimpleStringProperty ciudad; //Propiedad para el nombre de la ciudad
    private SimpleStringProperty codigoPostal; //Propiedad para el código postal

    //Constructor sin parámetros (constructor por defecto)
    public Direccion() {
        this.id = new SimpleIntegerProperty(0); //Inicializa el ID con valor 0
        this.calle = new SimpleStringProperty(""); //Inicializa la calle como cadena vacía
        this.ciudad = new SimpleStringProperty(""); //Inicializa la ciudad como cadena vacía
        this.codigoPostal = new SimpleStringProperty(""); //Inicializa el código postal como cadena vacía
    }

    //Constructor que recibe calle, ciudad y código postal (sin ID)
    public Direccion(String calle, String ciudad, String codigoPostal) {
        this(); //Llama al constructor por defecto
        this.calle.set(calle); //Establece el valor de la calle
        this.ciudad.set(ciudad); //Establece el valor de la ciudad
        this.codigoPostal.set(codigoPostal); //Establece el valor del código postal
    }

    //Constructor completo que recibe todos los atributos
    public Direccion(int id, String calle, String ciudad, String codigoPostal) {
        this(calle, ciudad, codigoPostal); //Llama al constructor con tres parámetros
        this.id.set(id); //Establece el valor del ID
    }

    // Getters (métodos para obtener los valores)
    public int getId() { return id.get(); } //Obtiene el valor del ID como int
    public String getCalle() { return calle.get(); } //Obtiene el valor de la calle como String
    public String getCiudad() { return ciudad.get(); } //Obtiene el valor de la ciudad como String
    public String getCodigoPostal() { return codigoPostal.get(); } //Obtiene el valor del código postal como String

    // Setters (métodos para modificar los valores)
    public void setId(int id) { this.id.set(id); } //Establece el valor del ID
    public void setCalle(String calle) { this.calle.set(calle); } //Establece el valor de la calle
    public void setCiudad(String ciudad) { this.ciudad.set(ciudad); } //Establece el valor de la ciudad
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal.set(codigoPostal); } //Establece el valor del código postal

    // Properties para JavaFX (métodos que devuelven las propiedades para enlace de datos)
    public SimpleIntegerProperty idProperty() { return id; } //Devuelve la propiedad del ID
    public SimpleStringProperty calleProperty() { return calle; } //Devuelve la propiedad de la calle
    public SimpleStringProperty ciudadProperty() { return ciudad; } //Devuelve la propiedad de la ciudad
    public SimpleStringProperty codigoPostalProperty() { return codigoPostal; } //Devuelve la propiedad del código postal

    //Método para representar la dirección como cadena de texto
    @Override
    public String toString() {
        return calle.get() + ", " + ciudad.get() + " " + codigoPostal.get(); //Formato: "Calle, Ciudad CódigoPostal"
    }

    //Método para comparar si dos direcciones son iguales (basado en calle, ciudad y código postal)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; //Si es el mismo objeto, son iguales
        if (obj == null || getClass() != obj.getClass()) return false; //Si el objeto es nulo o no es de la misma clase

        Direccion direccion = (Direccion) obj; //Convierte el objeto a Direccion

        //Compara calle, ciudad y código postal (ignora el ID)
        return calle.get().equals(direccion.calle.get()) &&
                ciudad.get().equals(direccion.ciudad.get()) &&
                codigoPostal.get().equals(direccion.codigoPostal.get());
    }

    //Método para generar un código hash único basado en calle, ciudad y código postal
    @Override
    public int hashCode() {
        return (calle.get() + ciudad.get() + codigoPostal.get()).hashCode(); //Hash combinado de los tres atributos
    }
}