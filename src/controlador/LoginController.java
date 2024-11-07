package controlador;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import excepciones.UsuarioContrasenyaException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.BaseDeDatos;
import util.Util;

public class LoginController implements Initializable {

	@FXML private Pane panelInicioSesion;
	@FXML private Pane panelRegistro;
	@FXML private TextField CampoInicioUsuario;
	@FXML private PasswordField CampoInicioContrasenya;
	@FXML private TextField CampoRegistroUsuario;
	@FXML private TextField CampoRegistroNombre;
	@FXML private PasswordField CampoRegistroContrasenya;
	@FXML private Button botonIniciarSesion;
	@FXML private Button botonInvitado;
	@FXML private Button botonAyuda;
	@FXML private Button botonRegistro;
	@FXML private Button botonRegistrarse;
	@FXML private Button botonVolver;


	@Override
	public void initialize(URL url, ResourceBundle ResourceBundle) {

		BaseDeDatos bd = new BaseDeDatos();

		this.panelRegistro.setVisible(false);

		// Panel de inicio de sesión
		CampoInicioUsuario.setOnKeyPressed((event) -> {if (event.getCode() == KeyCode.ENTER) CampoInicioContrasenya.requestFocus();});
		CampoInicioContrasenya.setOnKeyPressed((event) -> {if (event.getCode() == KeyCode.ENTER) iniciarSesion(bd);});

		botonRegistro.setOnAction((event) -> cambiarPanel(false, true));
		botonIniciarSesion.setOnMouseClicked((event) -> iniciarSesion(bd));
		botonInvitado.setOnMouseClicked((event) -> iniciarInvitado());

		// Panel de registro
		CampoRegistroUsuario.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) CampoRegistroNombre.requestFocus();
			else if (event.getCode() == KeyCode.ESCAPE) cambiarPanel(true, false);
		});
		CampoRegistroNombre.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) CampoRegistroContrasenya.requestFocus();
			else if (event.getCode() == KeyCode.ESCAPE) cambiarPanel(true, false);
		});
		CampoRegistroContrasenya.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) registrarUsuario(bd);
			else if (event.getCode() == KeyCode.ESCAPE) cambiarPanel(true, false);
		});

		botonRegistrarse.setOnAction((event) -> registrarUsuario(bd));
		botonVolver.setOnAction((event) -> cambiarPanel(true, false));
		botonAyuda.setOnMouseClicked((event) -> Util.alertaAyuda());
	}

	private void iniciarInvitado() {
		// Alerta invitado
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Aviso");
		alert.setHeaderText("Vas a iniciar sesión como invitado");
		alert.setContentText("Si continuas no se guardará la puntuación que consigas al terminar la partida.\r\n"
						   + "¿Estás seguro/a?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			// Si se continua
			mostrarVentana("/vista/VentanaPrincipal.fxml", "Solitario", "/vista/icon/solitario.png", "/vista/css/solitario.css", true);
			salir();
		}
	}

	// Iniciar sesión
	private void iniciarSesion(BaseDeDatos bd) {
		
		try {
			if (this.CampoInicioUsuario.getText().equals("") || this.CampoInicioContrasenya.getText().equals("")) {

				throw new UsuarioContrasenyaException();
			}

			if ( bd.consultarUsuarioContraseña(this.CampoInicioUsuario.getText().toLowerCase(), this.CampoInicioContrasenya.getText()) ) {
				
				// Mostrar la ventana de juego y cerrar la ventana de login
				mostrarVentana("/vista/VentanaPrincipal.fxml", "Solitario", "/vista/icon/solitario.png", "/vista/css/solitario.css", true);
				salir();
			};

		} catch (UsuarioContrasenyaException e) {
			// Mostrar error de inicio de sesión
			Util.alertaInicioSesion();
		}
	}

	// Registrar usuario
	private void registrarUsuario(BaseDeDatos bd) {

		boolean invalido = bd.usuarioExiste(this.CampoRegistroUsuario.getText().toLowerCase()) || (this.CampoRegistroContrasenya.getLength() < 3);

		if (this.CampoRegistroUsuario.getText().equals("") || this.CampoRegistroContrasenya.getText().equals("")) {

			// Mostrar error de creación de usuario vacio
			Util.alertaCrearUsuarioVacio();
		}
		else if (invalido) {

			// Mostrar error de creación de usuario
			Util.alertaCrearUsuario(this.CampoRegistroContrasenya.getLength() < 3);
		}
		else {

			if ( bd.crearUsuario(this.CampoRegistroUsuario.getText().toLowerCase(), this.CampoRegistroContrasenya.getText(), this.CampoRegistroNombre.getText()) ) {
				
				cambiarPanel(true, false);
			};
		}
	}

	// Cambiar panel
	private void cambiarPanel(boolean bool1, boolean bool2) {

		this.panelInicioSesion.setVisible(bool1);
		this.panelRegistro.setVisible(bool2); 
		this.CampoInicioUsuario.setText("");
		this.CampoInicioContrasenya.setText("");
		this.CampoRegistroUsuario.setText("");
		this.CampoRegistroNombre.setText("");
		this.CampoRegistroContrasenya.setText("");
	}

	// Cerrar Ventana
	private void salir() {

		Stage stage = (Stage)botonIniciarSesion.getScene().getWindow();
		stage.close();
	}

	// Mostrar otra ventana
	private void mostrarVentana(String rutaFXML, String titulo, String icon, String css, boolean modal) {

		try{
			//Léeme el source del archivo que te digo fxml y te pongo el path
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaFXML));
			Parent root = (Parent) fxmlLoader.load();
			Scene scene = new Scene(root);

			//Creame un nuevo Stage (una nueva ventana vacía)
			Stage stage = new Stage();

			//Asignar al Stage la escena que anteriormente hemos leído y guardado en root
			stage.setTitle(titulo);
			scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
			stage.setResizable(false);

			if (modal) stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);
			stage.getIcons().add(new Image(getClass().getResource(icon).toExternalForm()));

			//Mostrar el Stage (ventana)
			stage.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
