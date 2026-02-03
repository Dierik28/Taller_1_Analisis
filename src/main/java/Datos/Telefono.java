package Datos;

public class Telefono {
    //Se inicializan los atributos de la clase.
    private int id; //Atributo que se usará para identificar el teléfono.
    private String telefono; //Número de teléfono que se asignará.
    private int personaId; //ID de la persona a la que pertenece el teléfono.

    //Constructor que no recibe parámetros.
    public Telefono() {
    }

    //Constructor que recibe el número de teléfono y el ID de la persona.
    public Telefono(String telefono, int personaId) {
        this.telefono = telefono;
        this.personaId = personaId;
    }

    //Constructor que recibe el ID del teléfono, el número y el ID de la persona.
    public Telefono(int id, String telefono, int personaId) {
        this.id = id;
        this.telefono = telefono;
        this.personaId = personaId;
    }

    //Getters y setters de cada atributo.
    public int getId() {
        return id;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getPersonaId() {
        return personaId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setPersonaId(int personaId) {
        this.personaId = personaId;
    }
}