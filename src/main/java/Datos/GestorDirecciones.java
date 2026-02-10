package Datos;

import java.util.List;

public abstract class GestorDirecciones {
    // Método para buscar una dirección existente
    public abstract Direccion buscarDireccion(String calle, String ciudad, String codigoPostal);

    // Método para crear una nueva dirección
    public abstract Direccion crearDireccion(String calle, String ciudad, String codigoPostal);

    // Método para obtener direcciones de una persona
    public abstract List<Direccion> obtenerDireccionesPorPersona(int personaId);

    // Método para asignar una dirección a una persona
    public abstract void asignarDireccionAPersona(int personaId, Direccion direccion);

    // Método para remover una dirección de una persona
    public abstract void removerDireccionDePersona(int personaId, Direccion direccion);

    // Método template que busca primero, si no existe, crea una nueva
    public Direccion obtenerOCrearDireccion(String calle, String ciudad, String codigoPostal) {
        Direccion existente = buscarDireccion(calle, ciudad, codigoPostal);
        if (existente != null) {
            return existente;
        }
        return crearDireccion(calle, ciudad, codigoPostal);
    }
}