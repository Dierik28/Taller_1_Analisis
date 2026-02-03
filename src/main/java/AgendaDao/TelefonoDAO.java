package AgendaDao;
//Se importan las librerias necesarias.
import AgendaBD.ConexionBD;
import Datos.Telefono;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelefonoDAO {

    //Metodo para obtener todos los telefonos de una persona por su ID.
    public List<Telefono> obtenerPorPersona(int idPersona) {
        //Lista para almacenar los telefonos de la persona.
        List<Telefono> lista = new ArrayList<>();
        //Se realiza una consulta SQL para seleccionar los telefonos de una persona.
        String sql = "SELECT * FROM telefonos WHERE personaId = ?";

        try (Connection con = ConexionBD.realizarConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPersona); //Se asigna el ID de la persona.
            ResultSet rs = ps.executeQuery(); //Se ejecuta la consulta.

            //Se recorren los resultados y se crean objetos Telefono.
            while (rs.next()) {
                lista.add(new Telefono(
                        rs.getInt("id"),
                        rs.getInt("personaId"),
                        rs.getString("telefono")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); //Se maneja la excepcion en caso de error.
        }

        return lista; //Se retorna la lista de telefonos.
    }

    //Metodo para insertar un nuevo telefono en la base de datos.
    public void insertar(Telefono telefono) {
        //Se realiza una consulta SQL para insertar un telefono.
        String sql = "INSERT INTO telefonos (telefono, personaId) VALUES (?, ?)";

        try (Connection con = ConexionBD.realizarConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, telefono.getTelefono()); //Se asigna el numero de telefono.
            ps.setInt(2, telefono.getPersonaId()); //Se asigna el ID de la persona.
            ps.executeUpdate(); //Se realiza la insercion del telefono.

        } catch (SQLException e) {
            e.printStackTrace(); //Se maneja la excepcion en caso de error.
        }
    }

    //Metodo para eliminar un telefono de la base de datos por su ID.
    public void eliminar(int idTelefono) {
        //Se realiza una consulta SQL para eliminar un telefono.
        String sql = "DELETE FROM telefonos WHERE id = ?";

        try (Connection con = ConexionBD.realizarConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTelefono); //Se asigna el ID del telefono a eliminar.
            ps.executeUpdate(); //Se realiza la eliminacion del telefono.

        } catch (SQLException e) {
            e.printStackTrace(); //Se maneja la excepcion en caso de error.
        }
    }
}