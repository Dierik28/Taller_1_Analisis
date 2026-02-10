package AgendaDao;

//Se importan las librerías necesarias.
import Datos.Direccion;
import Datos.GestorDirecciones;
import Datos.Persona;
import Datos.Telefono;
import AgendaBD.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    //Atributo para gestionar las direcciones usando el patrón DAO.
    private GestorDirecciones gestorDirecciones;

    //Constructor que inicializa el gestor de direcciones con la implementación para base de datos.
    public PersonaDAO() {
        this.gestorDirecciones = new GestorDireccionesBD();
    }

    //Método para insertar una nueva persona en la base de datos.
    public void insertar(Persona persona) throws SQLException {
        //Consulta SQL para insertar una persona en la tabla Personas.
        String sqlPersona = "INSERT INTO Personas (nombre) VALUES (?)";

        try (Connection conexion = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
             PreparedStatement psPersona = conexion.prepareStatement(
                     sqlPersona, Statement.RETURN_GENERATED_KEYS)) { //Prepara la sentencia SQL.

            psPersona.setString(1, persona.getNombre()); //Asigna el nombre de la persona.
            psPersona.executeUpdate(); //Ejecuta la inserción.

            ResultSet rs = psPersona.getGeneratedKeys(); //Obtiene las claves generadas (ID autoincremental).
            if (rs.next()) { //Si se generó una clave, asigna el ID a la persona.
                persona.setId(rs.getInt(1)); //Asigna el ID generado a la persona.
            }

            //Insertar teléfonos asociados a la persona.
            insertarTelefonos(conexion, persona);

            //Insertar direcciones usando el gestor de direcciones.
            if (persona.getDirecciones() != null) { //Verifica si la persona tiene direcciones.
                for (Direccion direccion : persona.getDirecciones()) { //Recorre cada dirección.
                    //Buscar o crear la dirección usando el gestor.
                    Direccion dirExistente = gestorDirecciones.obtenerOCrearDireccion(
                            direccion.getCalle(), //Calle de la dirección.
                            direccion.getCiudad(), //Ciudad de la dirección.
                            direccion.getCodigoPostal() //Código postal de la dirección.
                    );
                    //Asignar la dirección a la persona en la tabla de relación.
                    gestorDirecciones.asignarDireccionAPersona(persona.getId(), dirExistente);
                }
            }
        }
    }

    //Método para actualizar los datos de una persona existente.
    public void actualizar(Persona persona) throws SQLException {
        Connection conn = null; //Variable para la conexión.
        try {
            conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
            conn.setAutoCommit(false); //Inicia transacción (agrupa múltiples operaciones).

            //1. Actualizar nombre de la persona.
            String sqlPersona = "UPDATE Personas SET nombre = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPersona)) {
                ps.setString(1, persona.getNombre()); //Nuevo nombre.
                ps.setInt(2, persona.getId()); //ID de la persona a actualizar.
                ps.executeUpdate(); //Ejecuta la actualización.
            }

            //2. Eliminar y recrear teléfonos (actualización completa).
            eliminarTelefonosPorPersona(conn, persona.getId()); //Elimina teléfonos antiguos.
            insertarTelefonos(conn, persona); //Inserta los nuevos teléfonos.

            //3. Eliminar relaciones de direcciones existentes.
            eliminarDireccionesPorPersona(conn, persona.getId()); //Elimina relaciones antiguas.

            //4. Insertar nuevas direcciones (compartiendo si ya existen).
            for (Direccion direccion : persona.getDirecciones()) { //Recorre cada dirección.
                //Buscar o crear la dirección (compartida) usando el gestor.
                Direccion dirExistente = gestorDirecciones.obtenerOCrearDireccion(
                        direccion.getCalle(), //Calle de la dirección.
                        direccion.getCiudad(), //Ciudad de la dirección.
                        direccion.getCodigoPostal() //Código postal de la dirección.
                );

                //Asignar la dirección a la persona.
                gestorDirecciones.asignarDireccionAPersona(persona.getId(), dirExistente);
            }

            conn.commit(); //Confirma todos los cambios de la transacción.

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); //Revierte todos los cambios en caso de error.
            }
            throw e; //Propaga la excepción.
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); //Restaura el modo auto-commit.
                conn.close(); //Cierra la conexión.
            }
        }
    }

    //Método privado para eliminar las relaciones de direcciones de una persona.
    private void eliminarDireccionesPorPersona(Connection conn, int personaId) throws SQLException {
        String sql = "DELETE FROM Persona_Direccion WHERE persona_id = ?"; //Consulta SQL.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId); //Asigna el ID de la persona.
            ps.executeUpdate(); //Ejecuta la eliminación.
        }
    }

    //Método para eliminar una persona de la base de datos por su ID.
    public void eliminar(int idPersona) throws SQLException {
        Connection conn = null; //Variable para la conexión.
        try {
            conn = ConexionBD.realizarConexion(); //Establece conexión con la base de datos.
            conn.setAutoCommit(false); //Inicia transacción.

            //Eliminar persona (las relaciones se eliminan automáticamente por CASCADE si está configurado).
            String sql = "DELETE FROM Personas WHERE id = ?"; //Consulta SQL.
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idPersona); //Asigna el ID de la persona a eliminar.
                ps.executeUpdate(); //Ejecuta la eliminación.
            }

            conn.commit(); //Confirma la eliminación.

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); //Revierte la eliminación en caso de error.
            }
            throw e; //Propaga la excepción.
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); //Restaura el modo auto-commit.
                conn.close(); //Cierra la conexión.
            }
        }
    }

    //Método para obtener todas las personas de la base de datos.
    public List<Persona> listar() throws SQLException {
        List<Persona> personas = new ArrayList<>(); //Lista para almacenar las personas.
        String sql = "SELECT * FROM Personas"; //Consulta SQL.

        try (Connection conn = ConexionBD.realizarConexion(); //Establece conexión.
             PreparedStatement ps = conn.prepareStatement(sql); //Prepara la sentencia.
             ResultSet rs = ps.executeQuery()) { //Ejecuta la consulta.

            while (rs.next()) { //Recorre todos los resultados.
                //Crea un objeto Persona con los datos obtenidos.
                Persona p = new Persona(
                        rs.getInt("id"), //ID de la persona.
                        rs.getString("nombre") //Nombre de la persona.
                );

                //Obtiene y asigna los teléfonos de la persona.
                p.setTelefonos(obtenerTelefonos(conn, p.getId()));
                //Obtiene y asigna las direcciones de la persona usando el gestor.
                p.setDirecciones(gestorDirecciones.obtenerDireccionesPorPersona(p.getId()));
                personas.add(p); //Añade la persona a la lista.
            }
        }
        return personas; //Retorna la lista de personas.
    }

    //Método privado para obtener las direcciones de una persona (actualmente no usado en listar()).
    private List<Direccion> obtenerDirecciones(Connection conn, int personaId) throws SQLException {
        List<Direccion> direcciones = new ArrayList<>(); //Lista para almacenar direcciones.
        String sql = """
        SELECT d.* FROM Direcciones d
        JOIN Persona_Direccion pd ON d.id = pd.direccion_id
        WHERE pd.persona_id = ?
        """; //Consulta SQL con JOIN.

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId); //Asigna el ID de la persona.
            ResultSet rs = ps.executeQuery(); //Ejecuta la consulta.

            while (rs.next()) { //Recorre los resultados.
                //Crea y añade objetos Dirección.
                direcciones.add(new Direccion(
                        rs.getInt("id"), //ID de la dirección.
                        rs.getString("calle"), //Calle.
                        rs.getString("ciudad"), //Ciudad.
                        rs.getString("codigo_postal") //Código postal.
                ));
            }
        }
        return direcciones; //Retorna la lista de direcciones.
    }

    //Método para insertar los teléfonos de una persona en la base de datos.
    private void insertarTelefonos(Connection conn, Persona persona) throws SQLException {
        //Si la persona no tiene teléfonos no se hace nada.
        if (persona.getTelefonos() == null) return;

        //Consulta SQL para insertar un teléfono.
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            //Se recorren todos los teléfonos de la persona.
            for (Telefono t : persona.getTelefonos()) {
                ps.setInt(1, persona.getId()); //ID de la persona.
                ps.setString(2, t.getTelefono()); //Número de teléfono.
                ps.executeUpdate(); //Se inserta cada teléfono.
            }
        }
    }

    //Método para obtener los teléfonos de una persona por su ID.
    private List<Telefono> obtenerTelefonos(Connection conn, int personaId) throws SQLException {
        //Lista que almacenará los teléfonos de la persona.
        List<Telefono> telefonos = new ArrayList<>();

        //Consulta SQL para seleccionar los teléfonos de una persona.
        String sql = "SELECT * FROM Telefonos WHERE personaId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId); //Se asigna el ID de la persona.
            ResultSet rs = ps.executeQuery(); //Se ejecuta la consulta.

            //Se recorren los resultados y se crean objetos Telefono.
            while (rs.next()) {
                telefonos.add(new Telefono(
                        rs.getInt("id"), //ID del teléfono.
                        personaId, //ID de la persona.
                        rs.getString("telefono") //Número de teléfono.
                ));
            }
        }
        return telefonos; //Se retorna la lista de teléfonos.
    }

    //Método para eliminar todos los teléfonos de una persona por su ID.
    private void eliminarTelefonosPorPersona(Connection conn, int personaId) throws SQLException {
        //Consulta SQL para eliminar teléfonos.
        String sql = "DELETE FROM Telefonos WHERE personaId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId); //Se asigna el ID de la persona.
            ps.executeUpdate(); //Se ejecuta la eliminación.
        }
    }
}