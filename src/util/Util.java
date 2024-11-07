package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import modelo.Naipe;
import modelo.Palo;
import modelo.Valores;

public class Util {
	
	public static final int FILA_1_Y = 65;
	public static final int FILA_2_Y = 257;
	
	public static final int MAZO_X = 39;
	public static final int RESTO_X = 229;
	public static final int[] CIMIENTOS_X = {606, 786, 975, 1169};
	public static final int[] TABLEROS_X = {39, 229, 421, 606, 786, 975, 1169};
	
	private static final int[] LISTA_X = {39, 229, 421, 606, 786, 975, 1169};
	private static final int[] LISTA_Y = {65, 257};

	public static String obtenerIdCarta(Naipe naipe) {

		String id = "";
		Palo palo = naipe.getPalo();
		Valores valor = naipe.getValor();

		switch (palo) {
		case CORAZONES: id += "COR"; break;
		case DIAMANTES: id += "DIA"; break;
		case TREBOLES:  id += "TRE"; break;
		case PICAS: 	id += "PIC"; break;
		default: break;
		}

		switch (valor) {
		case AS: 	 id += "1"; break;
		case DOS: 	 id += "2"; break;
		case TRES: 	 id += "3"; break;
		case CUATRO: id += "4"; break;
		case CINCO:  id += "5"; break;
		case SEIS: 	 id += "6"; break;
		case SIETE:  id += "7"; break;
		case OCHO: 	 id += "8"; break;
		case NUEVE:  id += "9"; break;
		case DIEZ: 	 id += "10"; break;
		case SOTA:   id += "11"; break;
		case REINA:  id += "12"; break;
		case REY:    id += "13"; break;
		default: break;
		}

		return id;
	}
	
	public static double redondearCoordenada(double x, boolean esY) {

		int[] valores = esY ? LISTA_Y : LISTA_X;

		for (int i = 0; i < valores.length-1; i++) {

			if (estaEntre(x, valores[i], valores[i+1])) {
				return valores[i];
			}
		}
		// Última posición
		return esY ? 257 : 1169;
	}

	private static boolean estaEntre(double x, int lower, int upper) {
		return lower <= x && x <= upper;
	}

	public static int getPosicionCarta(double x, boolean esY) {

		int[] valores = esY ? LISTA_Y : LISTA_X;

		for (int i = 0; i < valores.length; i++) {

			if (x == valores[i]) {
				return i;	
			}		
		}

		return -1;
	}
	
	public void sustituirVentana(String rutaFXML, String titulo, String css, Stage stage) {

		if (css == null) {
			
			css = "/vista/css/application.css";
		}
		
		try {
			// Cargar la nueva vista
			Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));

			// Crear una nueva escena con la nueva vista
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource(css).toExternalForm());

			// Configurar la escena en el escenario y mostrarla
			stage.setScene(scene);
			stage.setTitle(titulo);
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Alertas
	public static void alertaBD() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error en la conexión a BD");
		alert.setHeaderText("Ha ocurrido un error al conectarse a la base de datos");
		alert.setContentText("Por favor, vuelva a intentarlo más tarde, contacte con el administrador o inicie sesión como invitado.");
		alert.showAndWait();
	}

	public static void alertaAyuda() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Ayuda");
		alert.setHeaderText("Ten en cuenta:");
		alert.setContentText( "El usuario que introduzcas debe estar en minusculas\r\n"
				+ "Si esta en mayúsculas automáticamente se convertirá a minúsculas.\r\n\n"
				+ "La contraseña puede tener mayúsculas, minúsculas, numeros y/o carácteres especiales.\r\n"
				+ "Además la contraseña debe tener al menos 3 carácteres.");
		alert.showAndWait();
	}

	public static void alertaInicioSesion() {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error al iniciar sesión");
		alert.setHeaderText("Usuario o contraseña incorrecto");
		alert.setContentText("Por favor, vuelva a intentarlo.");
		alert.showAndWait();
	}

	public static void alertaCrearUsuario(boolean bool) {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error al crear el usuario");
		alert.setHeaderText(bool ? "La contraseña introducida no es válida" : "El nombre de usuario introducido ya está en uso.");
		alert.setContentText(bool ? "Por favor, introduzca una contraseña válida." : "Por favor, introduzca otro nombre de usuario.");
		// Añadir botón de ayuda
		ButtonType buttonAyuda = new ButtonType("Ayuda");
		alert.getButtonTypes().add(buttonAyuda);
		alert.showAndWait().ifPresent(response -> { if (response == buttonAyuda) Util.alertaAyuda(); });
	}

	public static void alertaCrearUsuarioVacio() {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error al crear el usuario");
		alert.setHeaderText("El campo del usuario o de la contraseña está vacio");
		alert.setContentText("Por favor, introduzca un usuario y contraseña válida.");
		// Añadir botón de ayuda
		ButtonType buttonAyuda = new ButtonType("Ayuda");
		alert.getButtonTypes().add(buttonAyuda);
		alert.showAndWait().ifPresent(response -> { if (response == buttonAyuda) Util.alertaAyuda(); });
	}
}
