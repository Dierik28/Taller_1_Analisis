package AgendaDao;
//Se importan las librerias necesarias.
import Datos.Persona;
import Datos.Telefono;
import AgendaBD.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    //Metodo para insertar una nueva persona en la base de datos.
    public void insertar(Persona persona) throws SQLException {
        //Se realiza una consulta SQL para insertar una persona en la tabla Personas.
        String sqlPersona = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";

        try (Connection conexion = ConexionBD.realizarConexion();
             PreparedStatement psPersona = conexion.prepareStatement(
                     sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

            // Se asignan los valores a los parametros de la consulta.
            psPersona.setString(1, persona.getNombre());
            psPersona.setString(2, persona.getDireccion());
            psPersona.executeUpdate(); //Se insertan los datos de la persona.

            // Se obtiene el ID generado automaticamente por la base de datos.
            ResultSet rs = psPersona.getGeneratedKeys();
            if (rs.next()) {
                persona.setId(rs.getInt(1)); //Se asigna el ID generado a la persona.
            }

            // Se insertan los telefonos asociados a la persona.
            insertarTelefonos(conexion, persona);
        }
    }
    //Metodo para actualizar los datos de una persona existente.
    public void actualizar(Persona persona) throws SQLException {
        //Se realiza una consulta SQL para actualizar una persona en la tabla Personas.
        String sql = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.realizarConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            //Se asignan los nuevos valores a los parametros.
            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getDireccion());
            ps.setInt(3, persona.getId());

            ps.executeUpdate(); //Se realizan los cambios en las columnas.

            //Se reemplazan los telefonos de la persona.
            eliminarTelefonosPorPersona(conexion, persona.getId());
            insertarTelefonos(conexion, persona);
        }
    }

    //Metodo para eliminar una persona de la base de datos por su ID.
    public void eliminar(int idPersona) throws SQLException {
        //Se realiza una consulta SQL para eliminar una persona.
        String sql = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = ConexionBD.realizarConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPersona); //Se asigna el ID de la persona a eliminar.
            ps.executeUpdate(); //Se realiza la eliminacion de la persona.
        }
    }

    //Metodo para obtener todas las personas de la base de datos.
    public List<Persona> listar() throws SQLException {
        //Lista para que se almacenen las personas de la base de datos.
        List<Persona> personas = new ArrayList<>();

        //Se realiza una consulta SQL para seleccionar todas las personas.
        String sql = "SELECT * FROM Personas";

        try (Connection conn = ConexionBD.realizarConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            //Se recorre cada fila del resultado de la consulta.
            while (rs.next()) {
                //Se crea un objeto Persona con los datos de la fila actual.
                Persona p = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion")
                );

                //Se obtienen y asignan los telefonos de la persona.
                p.setTelefonos(obtenerTelefonos(conn, p.getId()));
                personas.add(p); //Se agrega la persona a la lista.
            }
        }

        return personas; //Se retorna la lista de personas.
    }

    //Metodo para insertar los telefonos de una persona en la base de datos.
    private void insertarTelefonos(Connection conn, Persona persona) throws SQLException {
        //Si la persona no tiene telefonos no se hace nada.
        if (persona.getTelefonos() == null) return;

        //Se realiza una consulta SQL para insertar un telefono.
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            //Se recorren todos los telefonos de la persona.
            for (Telefono t : persona.getTelefonos()) {
                ps.setInt(1, persona.getId()); //ID de la persona.
                ps.setString(2, t.getTelefono()); //Numero de telefono.
                ps.executeUpdate(); //Se inserta cada telefono.
            }
        }
    }

    //Metodo para obtener los telefonos de una persona por su ID.
    private List<Telefono> obtenerTelefonos(Connection conn, int personaId) throws SQLException {
        //Lista que almacenara los telefonos de la persona.
        List<Telefono> telefonos = new ArrayList<>();

        //Se realiza una consulta SQL para seleccionar los telefonos de una persona.
        String sql = "SELECT * FROM Telefonos WHERE personaId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId); //Se asigna el ID de la persona.
            ResultSet rs = ps.executeQuery(); //Se ejecuta la consulta.

            //Se recorren los resultados y se crean objetos Telefono.
            while (rs.next()) {
                telefonos.add(new Telefono(
                        rs.getInt("id"),
                        personaId,
                        rs.getString("telefono")
                ));
            }
        }
        return telefonos; //Se retorna la lista de telefonos.
    }

    //Metodo para eliminar todos los telefonos de una persona por su ID.
    private void eliminarTelefonosPorPersona(Connection conn, int personaId) throws SQLException {
        //Se realiza una consulta SQL para eliminar telefonos.
        String sql = "DELETE FROM Telefonos WHERE personaId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId); //Se asigna el ID de la persona.
            ps.executeUpdate(); //Se ejecuta la eliminacion.
        }
    }
}