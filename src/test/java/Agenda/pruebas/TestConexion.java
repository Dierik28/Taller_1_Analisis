package Agenda.pruebas;

//Se importan las librerías necesarias.
import AgendaBD.ConexionBD;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class TestConexion {

    //Método de prueba que verifica la conexión a la base de datos.
    @Test
    void probarConexion() {
        //Se intenta establecer una conexión.
        try (Connection conn = ConexionBD.realizarConexion()) {

            //Se verifica que la conexion no sea nula.
            assertNotNull(conn, "La conexion fallo o es nula.");
            //Se verifica que la conexión esté abierta.
            assertFalse(conn.isClosed(), "La conexión se cerro.");

        } catch (Exception e) {
            //Si ocurre una excepción, la prueba falla mostrando el mensaje de error.
            fail("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}