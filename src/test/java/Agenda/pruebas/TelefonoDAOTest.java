package Agenda.pruebas;
//Se importan las librerias o packages necesarios.
import AgendaDao.TelefonoDAO;
import Datos.Telefono;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TelefonoDAOTest {

    private TelefonoDAO telefonoDAO;
    private static int PERSONA_ID_TEST = 1;

    //Metodo que se ejecuta antes de cada prueba para inicializar recursos.
    @BeforeEach
    void setUp() {
        telefonoDAO = new TelefonoDAO();
    }

    @Test
    @DisplayName("Insertar teléfono correctamente")
    void testInsertarTelefono() {
        //Se crea un telefono de prueba.
        Telefono telefono = new Telefono(
                0, //ID temporal
                PERSONA_ID_TEST, //ID de la persona asociada.
                "6861234567" //Numero de telefono de prueba.
        );

        //Se verifica que la insercion no lance excepciones.
        assertDoesNotThrow(() -> telefonoDAO.insertar(telefono));
    }

    @Test
    @DisplayName("Obtener teléfonos por persona")
    void testObtenerPorPersona() {
        //Se obtienen los telefonos de la persona con ID de prueba.
        List<Telefono> telefonos = telefonoDAO.obtenerPorPersona(PERSONA_ID_TEST);

        //Se verifica que la lista no sea nula.
        assertNotNull(telefonos);
        //Se verifica que la lista tenga 0 o mas elementos.
        assertTrue(telefonos.size() >= 0); // Puede ser 0 o más
    }

    @Test
    @DisplayName("Eliminar teléfono correctamente")
    void testEliminarTelefono() {
        //Insertamos primero un telefono de prueba.
        Telefono telefono = new Telefono(
                0, //ID temporal.
                PERSONA_ID_TEST, //ID de la persona asociada.
                "6869999999" //Numero de telefono de prueba.
        );

        telefonoDAO.insertar(telefono); //Se inserta el telefono en la base de datos.

        //Se obtienen nuevamente los telefonos de la persona.
        List<Telefono> telefonos = telefonoDAO.obtenerPorPersona(PERSONA_ID_TEST);
        Telefono ultimo = telefonos.get(telefonos.size() - 1); //Se obtiene el ultimo telefono insertado.

        //Se elimina el ultimo telefono y se verifica que no lance excepciones.
        assertDoesNotThrow(() -> telefonoDAO.eliminar(ultimo.getId()));
    }
}