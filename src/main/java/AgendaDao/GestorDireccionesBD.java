package AgendaDao;

import Datos.Direccion;
import Datos.GestorDirecciones;
import AgendaBD.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GestorDireccionesBD extends GestorDirecciones {

    //Método para buscar una dirección en la base de datos por calle, ciudad y código postal.
    @Override
    public Direccion buscarDireccion(String calle, String ciudad, String codigoPostal) {
        //Consulta SQL para buscar una dirección específica.
        String sql = "SELECT * FROM Direcciones WHERE calle = ? AND ciudad = ? AND codigo_postal = ?";

        try (Connection conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
             PreparedStatement ps = conn.prepareStatement(sql)) { //Prepara la sentencia SQL.

            //Asigna los valores a los parámetros de la consulta.
            ps.setString(1, calle);
            ps.setString(2, ciudad);
            ps.setString(3, codigoPostal);

            ResultSet rs = ps.executeQuery(); //Ejecuta la consulta.
            if (rs.next()) { //Si se encuentra un resultado, crea y retorna el objeto Dirección.
                return new Direccion(
                        rs.getInt("id"), //Obtiene el ID de la dirección.
                        rs.getString("calle"), //Obtiene la calle.
                        rs.getString("ciudad"), //Obtiene la ciudad.
                        rs.getString("codigo_postal") //Obtiene el código postal.
                );
            }
        } catch (SQLException e) { //Captura excepciones de SQL.
            System.err.println("Error al buscar dirección: " + e.getMessage()); //Imprime el error.
        }
        return null; //Retorna null si no se encuentra la dirección.
    }

    //Método para crear una nueva dirección en la base de datos.
    @Override
    public Direccion crearDireccion(String calle, String ciudad, String codigoPostal) {
        //Primero verificar si la dirección ya existe en la base de datos.
        Direccion existente = buscarDireccion(calle, ciudad, codigoPostal);
        if (existente != null) {
            return existente; //Si ya existe, retorna la dirección existente.
        }

        //Si no existe, se crea una nueva dirección.
        String sql = "INSERT INTO Direcciones (calle, ciudad, codigo_postal) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { //Prepara la sentencia SQL.

            //Asigna los valores a los parámetros de la consulta.
            ps.setString(1, calle);
            ps.setString(2, ciudad);
            ps.setString(3, codigoPostal);
            ps.executeUpdate(); //Ejecuta la inserción.

            ResultSet rs = ps.getGeneratedKeys(); //Obtiene las claves generadas (el ID autoincremental).
            if (rs.next()) { //Si se generó una clave, crea la nueva dirección.
                Direccion nueva = new Direccion(calle, ciudad, codigoPostal); //Crea el objeto Dirección.
                nueva.setId(rs.getInt(1)); //Asigna el ID generado a la dirección.
                return nueva; //Retorna la nueva dirección.
            }
        } catch (SQLException e) { //Captura excepciones de SQL.
            System.err.println("Error al crear dirección: " + e.getMessage()); //Imprime el error.
        }
        return null; //Retorna null si hubo un error.
    }

    //Método para obtener todas las direcciones asociadas a una persona específica.
    @Override
    public List<Direccion> obtenerDireccionesPorPersona(int personaId) {
        List<Direccion> direcciones = new ArrayList<>(); //Lista para almacenar las direcciones.

        //Consulta SQL que obtiene direcciones a través de la tabla de relación Persona_Direccion.
        String sql = """
            SELECT d.id, d.calle, d.ciudad, d.codigo_postal 
            FROM Direcciones d
            INNER JOIN Persona_Direccion pd ON d.id = pd.direccion_id
            WHERE pd.persona_id = ?
            """;

        try (Connection conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
             PreparedStatement ps = conn.prepareStatement(sql)) { //Prepara la sentencia SQL.

            ps.setInt(1, personaId); //Asigna el ID de la persona al parámetro de la consulta.
            ResultSet rs = ps.executeQuery(); //Ejecuta la consulta.

            while (rs.next()) { //Recorre todos los resultados.
                Direccion direccion = new Direccion(
                        rs.getInt("id"), //Obtiene el ID de la dirección.
                        rs.getString("calle"), //Obtiene la calle.
                        rs.getString("ciudad"), //Obtiene la ciudad.
                        rs.getString("codigo_postal") //Obtiene el código postal.
                );
                direcciones.add(direccion); //Añade la dirección a la lista.
            }

        } catch (SQLException e) { //Captura excepciones de SQL.
            System.err.println("Error al obtener direcciones para persona " + personaId + ": " + e.getMessage()); //Imprime el error.
        }

        return direcciones; //Retorna la lista de direcciones (puede estar vacía).
    }

    //Método para asignar una dirección existente a una persona en la tabla de relación.
    @Override
    public void asignarDireccionAPersona(int personaId, Direccion direccion) {
        //Consulta SQL para insertar la relación entre persona y dirección.
        String sql = "INSERT INTO Persona_Direccion (persona_id, direccion_id) VALUES (?, ?)";

        try (Connection conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
             PreparedStatement ps = conn.prepareStatement(sql)) { //Prepara la sentencia SQL.

            ps.setInt(1, personaId); //Asigna el ID de la persona.
            ps.setInt(2, direccion.getId()); //Asigna el ID de la dirección.
            ps.executeUpdate(); //Ejecuta la inserción.

        } catch (SQLException e) { //Captura excepciones de SQL.
            System.err.println("Error al asignar dirección a persona: " + e.getMessage()); //Imprime el error.
        }
    }

    //Método para remover la relación entre una persona y una dirección.
    @Override
    public void removerDireccionDePersona(int personaId, Direccion direccion) {
        //Consulta SQL para eliminar la relación de la tabla Persona_Direccion.
        String sql = "DELETE FROM Persona_Direccion WHERE persona_id = ? AND direccion_id = ?";

        try (Connection conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
             PreparedStatement ps = conn.prepareStatement(sql)) { //Prepara la sentencia SQL.

            ps.setInt(1, personaId); //Asigna el ID de la persona.
            ps.setInt(2, direccion.getId()); //Asigna el ID de la dirección.
            ps.executeUpdate(); //Ejecuta la eliminación.

        } catch (SQLException e) { //Captura excepciones de SQL.
            System.err.println("Error al remover dirección de persona: " + e.getMessage()); //Imprime el error.
        }
    }

    //Método para obtener una dirección existente o crear una nueva si no existe.
    @Override
    public Direccion obtenerOCrearDireccion(String calle, String ciudad, String codigoPostal) {
        //Busca si la dirección ya existe.
        Direccion existente = buscarDireccion(calle, ciudad, codigoPostal);
        if (existente != null) {
            return existente; //Retorna la dirección existente si se encuentra.
        }
        //Crea una nueva dirección si no existe.
        return crearDireccion(calle, ciudad, codigoPostal);
    }
}