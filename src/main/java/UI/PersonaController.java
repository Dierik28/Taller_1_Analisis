package UI;

//Se importan las librerias y packages necesarios.
import AgendaDao.PersonaDAO;
import AgendaDao.TelefonoDAO;
import Datos.Persona;
import Datos.Telefono;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class PersonaController {

    @FXML private TableView<Persona> tablaPersonas; //Tabla para mostrar las personas.
    @FXML private TableColumn<Persona, String> colNombre; //Columna para el nombre de la persona.
    @FXML private TableColumn<Persona, String> colDireccion; //Columna para la direccion de la persona.
    @FXML private ListView<Telefono> listaTelefonos; //Lista para mostrar los telefonos de una persona.
    @FXML private TextField txtTelefono; //Campo para ingresar un numero de telefono.
    @FXML private TextField txtNombre; //Campo para ingresar el nombre de la persona.
    @FXML private TextField txtDireccion; //Campo para ingresar la direccion de la persona.

    private final PersonaDAO personaDAO = new PersonaDAO(); //Objeto para manejar operaciones de personas en la base de datos.
    private final TelefonoDAO telefonoDAO = new TelefonoDAO(); //Objeto para manejar operaciones de telefonos en la base de datos.

    private ObservableList<Persona> listaPersonas; //Lista observable de personas para la tabla.
    private ObservableList<Telefono> listaTelefonosObs; //Lista observable de telefonos para la lista.


    @FXML
    public void initialize() {
        //Se configuran las columnas de la tabla para mostrar los datos de las personas.
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colDireccion.setCellValueFactory(c -> c.getValue().direccionProperty());

        //Se configura un listener para detectar cuando se selecciona una persona en la tabla.
        tablaPersonas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> mostrarPersona(newSel));

        cargarPersonas(); //Se cargan las personas al iniciar la ventana.
    }

    //Metodo para cargar todas las personas desde la base de datos y mostrarlas en la tabla.
    private void cargarPersonas() {
        try {
            listaPersonas = FXCollections.observableArrayList(personaDAO.listar());
            tablaPersonas.setItems(listaPersonas);
        } catch (SQLException e) {
            mostrarError("Error al cargar personas", e);
        }
    }

    //Metodo para mostrar los datos de una persona seleccionada en los campos de texto.
    private void mostrarPersona(Persona persona) {
        if (persona == null) { //Si no hay persona seleccionada, se limpian los campos.
            limpiarCampos();
            return;
        }

        txtNombre.setText(persona.getNombre()); //Se muestra el nombre de la persona.
        txtDireccion.setText(persona.getDireccion()); //Se muestra la direccion de la persona.

        cargarTelefonos(persona.getId()); //Se cargan los telefonos de la persona.
    }

    //Metodo para agregar una nueva persona a la base de datos.
    @FXML
    private void agregar() {
        if (txtNombre.getText().isBlank()) { //Se valida que el nombre no este vacio.
            mostrarError("El nombre es obligatorio");
            return;
        }

        try {
            Persona p = new Persona(
                    txtNombre.getText(),
                    txtDireccion.getText()
            );
            personaDAO.insertar(p); //Se inserta la persona en la base de datos.
            cargarPersonas(); //Se recarga la lista de personas.
            limpiarCampos(); //Se limpian los campos de texto.
        } catch (SQLException e) {
            mostrarError("Error al agregar persona", e);
        }
    }

    //Metodo para modificar los datos de una persona existente.
    @FXML
    private void modificar() {
        Persona p = tablaPersonas.getSelectionModel().getSelectedItem(); //Se obtiene la persona seleccionada.
        if (p == null) {
            mostrarError("Seleccione una persona");
            return;
        }

        try {
            p.setNombre(txtNombre.getText()); //Se actualiza el nombre.
            p.setDireccion(txtDireccion.getText()); //Se actualiza la direccion.
            personaDAO.actualizar(p); //Se actualiza la persona en la base de datos.
            cargarPersonas(); //Se recarga la lista de personas.
        } catch (SQLException e) {
            mostrarError("Error al modificar persona", e);
        }
    }

    //Metodo para eliminar una persona de la base de datos.
    @FXML
    private void eliminar() {
        Persona p = tablaPersonas.getSelectionModel().getSelectedItem(); //Se obtiene la persona seleccionada.
        if (p == null) {
            mostrarError("Seleccione una persona");
            return;
        }

        //Se muestra un cuadro de confirmacion antes de eliminar.
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar a " + p.getNombre() + "?",
                ButtonType.YES, ButtonType.NO
        );

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                personaDAO.eliminar(p.getId()); //Se elimina la persona de la base de datos.
                cargarPersonas(); //Se recarga la lista de personas.
                limpiarCampos(); //Se limpian los campos de texto.
                listaTelefonos.getItems().clear(); //Se limpia la lista de telefonos.
            } catch (SQLException e) {
                mostrarError("Error al eliminar persona", e);
            }
        }
    }

    // ======================
    // TELEFONOS
    // ======================
    //Metodo para cargar los telefonos de una persona especifica.
    private void cargarTelefonos(int personaId) {
        listaTelefonosObs = FXCollections.observableArrayList(
                telefonoDAO.obtenerPorPersona(personaId) //Se obtienen los telefonos desde la base de datos.
        );
        listaTelefonos.setItems(listaTelefonosObs); //Se muestran los telefonos en la lista.
    }

    //Metodo para agregar un telefono a la persona seleccionada.
    @FXML
    private void agregarTelefono() {
        Persona p = tablaPersonas.getSelectionModel().getSelectedItem(); //Se obtiene la persona seleccionada.
        if (p == null) {
            mostrarError("Seleccione una persona");
            return;
        }

        if (txtTelefono.getText().isBlank()) { //Se valida que el telefono no este vacio.
            mostrarError("Ingrese un número");
            return;
        }

        Telefono t = new Telefono(
                0, //ID temporal (sera generado por la base de datos).
                p.getId(), //ID de la persona a la que pertenece el telefono.
                txtTelefono.getText() //Numero de telefono.
        );

        telefonoDAO.insertar(t); //Se inserta el telefono en la base de datos.
        cargarTelefonos(p.getId()); //Se recargan los telefonos de la persona.
        txtTelefono.clear(); //Se limpia el campo de texto.
    }

    //Metodo para eliminar un telefono seleccionado.
    @FXML
    private void eliminarTelefono() {
        Telefono t = listaTelefonos.getSelectionModel().getSelectedItem(); //Se obtiene el telefono seleccionado.
        if (t == null) {
            mostrarError("Seleccione un teléfono");
            return;
        }

        telefonoDAO.eliminar(t.getId()); //Se elimina el telefono de la base de datos.
        cargarTelefonos(t.getPersonaId()); //Se recargan los telefonos de la persona.
    }

    // ======================
    // UTILIDADES
    // ======================
    //Metodo para limpiar todos los campos y selecciones de la interfaz.
    @FXML
    private void limpiar() {
        limpiarCampos(); //Se limpian los campos de texto.
        tablaPersonas.getSelectionModel().clearSelection(); //Se deselecciona cualquier persona en la tabla.
        listaTelefonos.getItems().clear(); //Se limpia la lista de telefonos.
    }

    //Metodo para limpiar los campos de texto de persona y telefono.
    private void limpiarCampos() {
        txtNombre.clear();
        txtDireccion.clear();
        txtTelefono.clear();
    }

    //Metodo para mostrar un mensaje de error simple.
    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    //Metodo para mostrar un mensaje de error con detalles de la excepcion.
    private void mostrarError(String mensaje, Exception e) {
        e.printStackTrace();
        mostrarError(mensaje + ": " + e.getMessage());
    }
}