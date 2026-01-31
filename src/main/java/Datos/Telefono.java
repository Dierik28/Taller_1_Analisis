package Datos;

public class Telefono {
    private int id;
    private String telefono;
    private int personaId;

    public Telefono() {
    }

    public Telefono(String telefono, int personaId) {
        this.telefono = telefono;
        this.personaId = personaId;
    }

    public Telefono(int id, String telefono, int personaId) {
        this.id = id;
        this.telefono = telefono;
        this.personaId = personaId;
    }

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
