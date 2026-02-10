package UI;

import AgendaDao.GestorDireccionesBD;
import AgendaDao.PersonaDAO;
import AgendaDao.TelefonoDAO;
import Datos.Direccion;
import Datos.GestorDirecciones;
import Datos.Persona;
import Datos.Telefono;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class PersonaController {

    //Componentes de la interfaz gráfica
    @FXML private TableView<Persona> tablaPersonas; //Tabla para mostrar la lista de personas
    @FXML private TableColumn<Persona, String> colNombre; //Columna para mostrar el nombre de la persona
    @FXML private ListView<Direccion> listaDirecciones; //Lista para mostrar direcciones de la persona seleccionada
    @FXML private ListView<Telefono> listaTelefonos; //Lista para mostrar teléfonos de la persona seleccionada
    @FXML private TextField txtTelefono; //Campo de texto para ingresar un nuevo teléfono
    @FXML private TextField txtNombre; //Campo de texto para ingresar/modificar el nombre de persona
    @FXML private TextField txtCalle; //Campo de texto para ingresar la calle de una dirección
    @FXML private TextField txtCiudad; //Campo de texto para ingresar la ciudad de una dirección
    @FXML private TextField txtCodigoPostal; //Campo de texto para ingresar el código postal
    @FXML private Label lblIdPersona; //Etiqueta para mostrar el ID de la persona seleccionada
    @FXML private Label lblEstado; //Etiqueta para mostrar mensajes de estado
    @FXML private Label lblContador; //Etiqueta para mostrar el contador de personas
    @FXML private Button btnAgregarDireccion; //Botón para agregar/actualizar direcciones

    //Variables de estado
    private Direccion direccionSeleccionadaParaEditar = null; //Almacena la dirección que se está editando
    private final PersonaDAO personaDAO = new PersonaDAO(); //DAO para operaciones con personas
    private final TelefonoDAO telefonoDAO = new TelefonoDAO(); //DAO para operaciones con teléfonos
    private GestorDirecciones gestorDirecciones; //Gestor para operaciones con direcciones

    //Persona actualmente seleccionada en la tabla
    private Persona personaSeleccionada = null;

    //Lista observable para enlazar con la tabla
    private ObservableList<Persona> listaPersonas;

    //Método de inicialización que se ejecuta cuando se carga la interfaz
    @FXML
    public void initialize() {
        try {
            gestorDirecciones = new GestorDireccionesBD(); //Inicializa el gestor de direcciones

            //Configura la columna de nombre para obtener el valor de la propiedad nombre de Persona
            colNombre.setCellValueFactory(cellData ->
                    cellData.getValue().nombreProperty());

            //Agrega un listener para detectar cambios en la selección de la tabla
            tablaPersonas.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, oldSel, newSel) -> mostrarPersona(newSel));

            cargarPersonas(); //Carga las personas desde la base de datos
            actualizarEstado("Aplicación iniciada"); //Actualiza el estado de la aplicación

        } catch (Exception e) {
            mostrarError("Error al inicializar aplicación", e); //Muestra error si hay problemas
        }
    }

    // ======================
    // MÉTODOS PARA TELÉFONOS (GUARDADO INMEDIATO)
    // ======================

    //Método para agregar un nuevo teléfono a la persona seleccionada
    @FXML
    private void agregarTelefono() {
        //Validar que haya una persona seleccionada
        if (personaSeleccionada == null) {
            mostrarError("Debe seleccionar una persona primero");
            return;
        }

        if (txtTelefono.getText().isBlank()) { //Valida que el campo no esté vacío
            mostrarError("Ingrese un número de teléfono");
            txtTelefono.requestFocus(); //Pone el foco en el campo
            return;
        }

        String telefono = txtTelefono.getText().trim(); //Obtiene y limpia el texto

        try {
            //Crear y guardar el teléfono en la base de datos INMEDIATAMENTE
            Telefono nuevoTelefono = new Telefono(
                    telefono,
                    personaSeleccionada.getId()  //Asigna el ID de la persona
            );

            telefonoDAO.insertar(nuevoTelefono); //Inserta el teléfono en la BD

            //Actualizar la lista de teléfonos desde la base de datos
            cargarTelefonosDesdeBD(personaSeleccionada);

            txtTelefono.clear(); //Limpia el campo de texto
            txtTelefono.requestFocus(); //Pone el foco en el campo
            actualizarEstado("Teléfono guardado exitosamente");

        } catch (Exception e) {
            mostrarError("Error al guardar teléfono", e);
            actualizarEstado("Error al guardar teléfono");
        }
    }

    //Método para eliminar un teléfono seleccionado
    @FXML
    private void eliminarTelefono() {
        Telefono telefonoSeleccionado = listaTelefonos.getSelectionModel().getSelectedItem();
        if (telefonoSeleccionado == null) { //Valida que haya un teléfono seleccionado
            mostrarError("Seleccione un teléfono para eliminar");
            return;
        }

        try {
            //Eliminar de la base de datos INMEDIATAMENTE
            telefonoDAO.eliminar(telefonoSeleccionado.getId());

            //Actualizar la lista desde BD
            cargarTelefonosDesdeBD(personaSeleccionada);

            actualizarEstado("Teléfono eliminado exitosamente");

        } catch (Exception e) {
            mostrarError("Error al eliminar teléfono", e);
            actualizarEstado("Error al eliminar teléfono");
        }
    }

    //Método para cargar los teléfonos de una persona desde la base de datos
    private void cargarTelefonosDesdeBD(Persona persona) {
        if (persona == null) return; //Si no hay persona, no hace nada

        try {
            //Obtiene los teléfonos de la persona y los convierte a lista observable
            ObservableList<Telefono> telefonos = FXCollections.observableArrayList(
                    telefonoDAO.obtenerPorPersona(persona.getId())
            );
            listaTelefonos.setItems(telefonos); //Establece la lista en el ListView
        } catch (Exception e) {
            System.err.println("Error al cargar teléfonos: " + e.getMessage());
            listaTelefonos.setItems(FXCollections.observableArrayList()); //Lista vacía en caso de error
        }
    }

    // ======================
    // MÉTODOS PARA DIRECCIONES (GUARDADO INMEDIATO)
    // ======================

    //Método para agregar una nueva dirección a la persona seleccionada
    @FXML
    private void agregarDireccion() {
        //Validar que haya una persona seleccionada
        if (personaSeleccionada == null) {
            mostrarError("Debe seleccionar una persona primero");
            return;
        }

        if (!validarCamposDireccion()) { //Valida los campos de dirección
            return;
        }

        try {
            //Buscar o crear la dirección (compartida)
            Direccion direccion = gestorDirecciones.obtenerOCrearDireccion(
                    txtCalle.getText().trim(),
                    txtCiudad.getText().trim(),
                    txtCodigoPostal.getText().trim()
            );

            //Asignar la dirección a la persona INMEDIATAMENTE
            gestorDirecciones.asignarDireccionAPersona(personaSeleccionada.getId(), direccion);

            //Actualizar la lista desde BD
            cargarDireccionesDesdeBD(personaSeleccionada);

            limpiarCamposDireccion(); //Limpia los campos
            actualizarEstado("Dirección guardada exitosamente");

        } catch (Exception e) {
            mostrarError("Error al guardar dirección", e);
            actualizarEstado("Error al guardar dirección");
        }
    }

    //Método para preparar la edición de una dirección seleccionada
    @FXML
    private void editarDireccion() {
        Direccion seleccionada = listaDirecciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) { //Valida que haya una dirección seleccionada
            mostrarError("Seleccione una dirección para editar");
            return;
        }

        direccionSeleccionadaParaEditar = seleccionada; //Almacena la dirección a editar
        //Carga los datos de la dirección en los campos de texto
        txtCalle.setText(seleccionada.getCalle());
        txtCiudad.setText(seleccionada.getCiudad());
        txtCodigoPostal.setText(seleccionada.getCodigoPostal());

        if (btnAgregarDireccion != null) {
            btnAgregarDireccion.setText("Actualizar Dirección"); //Cambia el texto del botón
        }

        actualizarEstado("Editando dirección...");
    }

    //Método para actualizar una dirección editada
    @FXML
    private void actualizarDireccion() {
        if (direccionSeleccionadaParaEditar == null) { //Valida que haya una dirección en edición
            mostrarError("No hay dirección seleccionada para editar");
            return;
        }

        if (!validarCamposDireccion()) { //Valida los campos
            return;
        }

        try {
            //Primero eliminar la relación antigua
            gestorDirecciones.removerDireccionDePersona(personaSeleccionada.getId(), direccionSeleccionadaParaEditar);

            //Buscar o crear la nueva dirección (puede ser la misma o diferente)
            Direccion nuevaDireccion = gestorDirecciones.obtenerOCrearDireccion(
                    txtCalle.getText().trim(),
                    txtCiudad.getText().trim(),
                    txtCodigoPostal.getText().trim()
            );

            //Asignar la nueva dirección
            gestorDirecciones.asignarDireccionAPersona(personaSeleccionada.getId(), nuevaDireccion);

            //Actualizar la lista desde BD
            cargarDireccionesDesdeBD(personaSeleccionada);

            //Restaurar botón a su estado original
            if (btnAgregarDireccion != null) {
                btnAgregarDireccion.setText("Agregar Dirección");
            }

            limpiarCamposDireccion(); //Limpia los campos
            direccionSeleccionadaParaEditar = null; //Resetea la variable de edición
            actualizarEstado("Dirección actualizada exitosamente");

        } catch (Exception e) {
            mostrarError("Error al actualizar dirección", e);
            actualizarEstado("Error al actualizar dirección");
        }
    }

    //Método para eliminar una dirección seleccionada
    @FXML
    private void eliminarDireccion() {
        Direccion seleccionada = listaDirecciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) { //Valida que haya una dirección seleccionada
            mostrarError("Seleccione una dirección para eliminar");
            return;
        }

        try {
            //Eliminar la relación INMEDIATAMENTE (NO la dirección, solo la relación)
            gestorDirecciones.removerDireccionDePersona(personaSeleccionada.getId(), seleccionada);

            //Actualizar la lista desde BD
            cargarDireccionesDesdeBD(personaSeleccionada);

            actualizarEstado("Dirección eliminada de la persona");

        } catch (Exception e) {
            mostrarError("Error al eliminar dirección", e);
            actualizarEstado("Error al eliminar dirección");
        }
    }

    //Método para cargar las direcciones de una persona desde la base de datos
    private void cargarDireccionesDesdeBD(Persona persona) {
        if (persona == null) return; //Si no hay persona, no hace nada

        try {
            //Obtiene las direcciones de la persona y las convierte a lista observable
            ObservableList<Direccion> direcciones = FXCollections.observableArrayList(
                    gestorDirecciones.obtenerDireccionesPorPersona(persona.getId())
            );
            listaDirecciones.setItems(direcciones); //Establece la lista en el ListView
        } catch (Exception e) {
            System.err.println("Error al cargar direcciones: " + e.getMessage());
            listaDirecciones.setItems(FXCollections.observableArrayList()); //Lista vacía en caso de error
        }
    }

    //Método para limpiar los campos de dirección en la interfaz
    @FXML
    private void limpiarCamposDireccionUI() {
        limpiarCamposDireccion(); //Llama al método que limpia los campos

        //Restaura el botón si estaba en modo edición
        if (btnAgregarDireccion != null &&
                btnAgregarDireccion.getText().equals("Actualizar Dirección")) {
            btnAgregarDireccion.setText("Agregar Dirección");
            direccionSeleccionadaParaEditar = null;
        }

        actualizarEstado("Campos de dirección limpiados");
    }

    // ======================
    // MÉTODOS PARA PERSONAS
    // ======================

    //Método para agregar una nueva persona
    @FXML
    private void agregar() {
        if (txtNombre.getText().isBlank()) { //Valida que el nombre no esté vacío
            mostrarError("El nombre es obligatorio");
            return;
        }

        try {
            Persona nuevaPersona = new Persona(txtNombre.getText()); //Crea nueva persona
            personaDAO.insertar(nuevaPersona); //Inserta en la base de datos

            cargarPersonas(); //Recarga la lista de personas
            limpiarCampos(); //Limpia todos los campos
            actualizarEstado("Persona agregada exitosamente");

        } catch (SQLException e) {
            mostrarError("Error al agregar persona", e);
            actualizarEstado("Error al agregar");
        }
    }

    //Método para modificar la persona seleccionada
    @FXML
    private void modificar() {
        if (personaSeleccionada == null) { //Valida que haya una persona seleccionada
            mostrarError("Seleccione una persona para modificar");
            return;
        }

        try {
            //Solo actualizar el nombre (teléfonos y direcciones ya se guardaron inmediatamente)
            personaSeleccionada.setNombre(txtNombre.getText());
            personaDAO.actualizar(personaSeleccionada); //Actualiza en la base de datos

            cargarPersonas(); //Recarga la lista
            actualizarEstado("Nombre de persona actualizado");

        } catch (SQLException e) {
            mostrarError("Error al modificar persona", e);
            actualizarEstado("Error al modificar");
        }
    }

    //Método para eliminar la persona seleccionada
    @FXML
    private void eliminar() {
        if (personaSeleccionada == null) { //Valida que haya una persona seleccionada
            mostrarError("Seleccione una persona");
            return;
        }

        //Muestra diálogo de confirmación
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar a " + personaSeleccionada.getNombre() + "?",
                ButtonType.YES, ButtonType.NO
        );

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                personaDAO.eliminar(personaSeleccionada.getId()); //Elimina de la base de datos
                cargarPersonas(); //Recarga la lista
                limpiarCampos(); //Limpia los campos
                actualizarEstado("Persona eliminada");
            } catch (SQLException e) {
                mostrarError("Error al eliminar persona", e);
                actualizarEstado("Error al eliminar");
            }
        }
    }

    // ======================
    // MÉTODOS AUXILIARES
    // ======================

    //Método para mostrar los datos de una persona en la interfaz
    private void mostrarPersona(Persona persona) {
        personaSeleccionada = persona; //Almacena la persona seleccionada

        if (persona == null) { //Si no hay persona seleccionada, limpia los campos
            txtNombre.clear();
            if (lblIdPersona != null) lblIdPersona.setText("ID: --");
            listaTelefonos.setItems(FXCollections.observableArrayList());
            listaDirecciones.setItems(FXCollections.observableArrayList());
            actualizarEstado("Seleccione una persona");
            return;
        }

        txtNombre.setText(persona.getNombre()); //Muestra el nombre
        if (lblIdPersona != null) lblIdPersona.setText("ID: " + persona.getId()); //Muestra el ID

        //Carga teléfonos y direcciones desde BD
        cargarTelefonosDesdeBD(persona);
        cargarDireccionesDesdeBD(persona);

        actualizarEstado("Persona seleccionada: " + persona.getNombre());
    }

    //Método para cargar todas las personas desde la base de datos
    private void cargarPersonas() {
        try {
            //Obtiene la lista de personas y la convierte a observable
            listaPersonas = FXCollections.observableArrayList(personaDAO.listar());
            tablaPersonas.setItems(listaPersonas); //Establece los datos en la tabla
            actualizarContador(); //Actualiza el contador
        } catch (SQLException e) {
            mostrarError("Error al cargar personas", e);
            actualizarEstado("Error al cargar datos");
        }
    }

    //Método para limpiar todos los campos de la interfaz
    @FXML
    private void limpiar() {
        limpiarCampos(); //Limpia campos de texto
        tablaPersonas.getSelectionModel().clearSelection(); //Desselecciona la tabla
        personaSeleccionada = null; //Resetea la persona seleccionada
        listaTelefonos.setItems(FXCollections.observableArrayList()); //Limpia lista de teléfonos
        listaDirecciones.setItems(FXCollections.observableArrayList()); //Limpia lista de direcciones
        actualizarEstado("Campos limpiados");
    }

    //Método para recargar todos los datos desde la base de datos
    @FXML
    private void recargarDatos() {
        try {
            cargarPersonas(); //Recarga personas
            //Si hay persona seleccionada, recarga sus teléfonos y direcciones
            if (personaSeleccionada != null) {
                cargarTelefonosDesdeBD(personaSeleccionada);
                cargarDireccionesDesdeBD(personaSeleccionada);
            }
            actualizarEstado("Datos recargados");
        } catch (Exception e) {
            mostrarError("Error al recargar datos", e);
            actualizarEstado("Error al recargar");
        }
    }

    //Método para limpiar los campos de texto principales
    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        limpiarCamposDireccion();
    }

    //Método para limpiar los campos específicos de dirección
    private void limpiarCamposDireccion() {
        txtCalle.clear();
        txtCiudad.clear();
        txtCodigoPostal.clear();
        txtCalle.requestFocus(); //Pone el foco en el campo calle
    }

    //Método para validar los campos de dirección
    private boolean validarCamposDireccion() {
        if (txtCalle.getText().isBlank()) {
            mostrarError("La calle es obligatoria");
            txtCalle.requestFocus();
            return false;
        }
        if (txtCiudad.getText().isBlank()) {
            mostrarError("La ciudad es obligatoria");
            txtCiudad.requestFocus();
            return false;
        }
        return true; //Todos los campos válidos
    }

    //Método para actualizar el contador de personas
    private void actualizarContador() {
        if (lblContador != null) {
            int total = listaPersonas != null ? listaPersonas.size() : 0;
            lblContador.setText(total + " persona(s)");
        }
    }

    //Método para actualizar el mensaje de estado
    private void actualizarEstado(String mensaje) {
        if (lblEstado != null) {
            lblEstado.setText(mensaje);
        }
    }

    //Método para mostrar un mensaje de error simple
    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    //Método para mostrar un mensaje de error con excepción
    private void mostrarError(String mensaje, Exception e) {
        e.printStackTrace();
        mostrarError(mensaje + ": " + e.getMessage());
    }

    //Método para manejar la selección de persona (enlace con FXML)
    @FXML
    private void seleccionarPersona() {
        //Este método simplemente asegura que la persona seleccionada esté cargada
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mostrarPersona(seleccionada);
        }
    }

    //Método para mostrar un mensaje informativo
    private void mostrarMensaje(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}