package Agenda.pruebas;
//Se importan las librerias o packages necesarios.
import AgendaDao.PersonaDAO;
import Datos.Persona;
import Datos.Telefono;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

//Se especifica el orden en el que se ejecutaran las pruebas.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonaDAOTest {
    //Atributos de la clase
    private static PersonaDAO personaDAO;
    private static Persona personaPrueba;

    //Metodo que se ejecuta antes de todas las pruebas para inicializar los atributos.
    @BeforeAll
    static void setUp() {
        personaDAO = new PersonaDAO();
    }

    @Test
    @Order(1)
    void insertarPersona() throws SQLException {
        //Se crea una nueva persona para la prueba.
        personaPrueba = new Persona("Persona Test", "Dirección Test");

        //Se agregan telefonos a la persona.
        personaPrueba.agregarTelefono(new Telefono("555-1111111"));
        personaPrueba.agregarTelefono(new Telefono("555-2222222"));

        //Se inserta la persona en la base de datos.
        personaDAO.insertar(personaPrueba);

        //Se verifica que se haya generado un ID para la persona.
        assertTrue(personaPrueba.getId() > 0,
                "Se debio generarse un ID al insertar una persona");
    }

    @Test
    @Order(2)
    void listarPersonas() throws SQLException {
        //Se obtiene la lista de personas de la base de datos.
        List<Persona> personas = personaDAO.listar();

        //Se verifica que la lista no sea nula.
        assertNotNull(personas);
        //Se verifica que la lista no este vacia.
        assertFalse(personas.isEmpty(),
                "La lista de personas no debe estar vacia");
    }

    @Test
    @Order(3)
    void actualizarPersona() throws SQLException {
        //Se modifican los datos de la persona de prueba.
        personaPrueba.setNombre("Persona Modificada");
        personaPrueba.setDireccion("Dirección Modificada");

        //Se actualiza la persona en la base de datos.
        personaDAO.actualizar(personaPrueba);

        //Se obtiene la lista actualizada de personas.
        List<Persona> personas = personaDAO.listar();

        //Se verifica si la persona modificada existe en la lista.
        boolean encontrada = personas.stream()
                .anyMatch(p -> p.getNombre().equals("Persona Modificada"));

        //Se comprueba que la persona se haya actualizado correctamente.
        assertTrue(encontrada,
                "La persona debe haberse actualizado");
    }

    @Test
    @Order(4)
    void eliminarPersona() throws SQLException {
        //Se elimina la persona de prueba de la base de datos.
        personaDAO.eliminar(personaPrueba.getId());

        //Se obtiene la lista actualizada de personas.
        List<Persona> personas = personaDAO.listar();

        //Se verifica si la persona eliminada aun existe en la lista.
        boolean existe = personas.stream()
                .anyMatch(p -> p.getId() == personaPrueba.getId());

        //Se comprueba que la persona haya sido eliminada correctamente.
        assertFalse(existe,
                "La persona eliminada no debe existir");
    }
}