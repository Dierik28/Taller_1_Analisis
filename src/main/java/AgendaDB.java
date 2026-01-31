import java.sql.*;

public class AgendaDB {
    private static final String URL =
            "jdbc:mariadb://localhost:3306/agenda";
    private static final String USER = "user1";
    private static final String PASSWORD = "superpassword";

    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Registrar driver
            Class.forName("org.mariadb.jdbc.Driver");

            // Conectar
            System.out.println("Conectando a la base de datos...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // Consulta
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Personas");

            System.out.println("\n=== LISTADO DE PERSONAS ===");

            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                                ", Nombre: " + rs.getString("nombre") +
                                ", Dirección: " + rs.getString("direccion")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
