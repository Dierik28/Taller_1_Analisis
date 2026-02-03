package AgendaBD;

//se importan las librerias necesarias para realizar la conexion.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    //Se crean los datos para la conexion con la base de datos.
    private static final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private static final String USUARIO = "user1";
    private static final String PASSWORD = "superpassword";
    private static final String DRIVER = "org.mariadb.jdbc.Driver";

    //Metodo para poder realizar la conexion a la base de datos
    public static Connection realizarConexion() throws SQLException {
        Connection conexion;
        try {
            //Se intenta cargar el driver.
            Class.forName(DRIVER);
            System.out.println("Se ha cargado el driver correctamente");
        } catch (ClassNotFoundException e) {
            System.out.println("No se pudo cargar el driver");
        }
        try {
            //Se intenta establecer la conexion con la base de datos
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Se realizo la conexion con la base de datos");

        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos");
            throw e;
        }
        return conexion;
    }
}