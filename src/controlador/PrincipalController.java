package controlador;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.Juego;
import modelo.Naipe;
import modelo.Palo;
import modelo.Valores;
import util.Util;
import vista.Carta;

public class PrincipalController implements Initializable {

	@FXML private AnchorPane Ancla;
	@FXML private MenuBar barraMenu;
	
	// Menú Juego
	@FXML private Menu MenuJuego;
	@FXML private MenuItem MenuJuegoDeshacer;
	@FXML private MenuItem MenuJuegoNuevo;
	@FXML private MenuItem MenuJuegoSalir;
	@FXML private MenuItem MenuJuegoCerrarSesion;
	// Submenú Dificultad
	@FXML private Menu MenuDificultad;
	@FXML private MenuItem MenuDificultadDificil;
	@FXML private MenuItem MenuDificultadFacil;
	@FXML private MenuItem MenuDificultadMedio;
	// Menú Ayuda
	@FXML private Menu MenuAyuda;
	@FXML private MenuItem MenuAyudaAcercaDe;
	
	private Juego solitario;
	private Util util;
	private int y = Util.FILA_2_Y;
	
	
	@Override
	public void initialize(URL url, ResourceBundle ResourceBundle) {
		
		this.util = new Util();
		
		// PROXIMAMENTE
		MenuDificultad.setVisible(false);
		MenuDificultadFacil.setVisible(false);
		MenuDificultadMedio.setVisible(false);
		MenuDificultadDificil.setVisible(false);
		// EOF PROXIMAMENTE

		MenuJuegoSalir.setOnAction((event) -> salir());
		MenuJuegoNuevo.setOnAction((event) -> {
			
			Stage stage = (Stage)MenuJuegoNuevo.getParentPopup().getOwnerWindow();
			
			this.util.sustituirVentana("/vista/VentanaPrincipal.fxml", "Solitario", "/vista/css/solitario.css", stage);
			});
		
		MenuJuegoDeshacer.setOnAction((event) -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deshacer");
			alert.setHeaderText("Vas a deshacer");
			alert.setContentText("¿Estás seguro/a?");
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				
				mostrarEE(this.Ancla, this.barraMenu);
			}
		});
		
		MenuJuegoCerrarSesion.setOnAction((event) -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Cerrar Sesión");
			alert.setHeaderText("Estas a punto de cerrar sesión");
			alert.setContentText("¿Estás seguro/a?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				
				mostrarVentana("/vista/Login.fxml", "Iniciar Sesión", "/vista/icon/usuario.png", "../vista/css/application.css", true);
				salir();
			}
		});
		MenuAyudaAcercaDe.setOnAction((event) -> mostrarVentana("/vista/AcercaDe.fxml", "Acerca de Solitario", "/vista/icon/solitario.png", "/vista/css/application.css", true));

		this.solitario = new Juego();
		dibujarTableros();
		dibujarMazo();
		
	}
	
	/** Dibuja las cartas del mazo. */
	public void dibujarMazo() {
		
		Stack<Naipe> pila;
		Naipe naipe;
		
		pila = solitario.mazo;

		for (int i = 0; i < pila.size(); i++) {

			naipe = pila.get(i);

			naipe.revelar();

			insertarCarta(Util.obtenerIdCarta(naipe), 39, 65, obtenerNombreCarta(naipe), naipe.estaRevelada());
		}
	}
	
	/** Dibuja las cartas de los tableros. */
	public void dibujarTableros() {
		
		Stack<Naipe> pila;
		Naipe naipe;
		
		for (int i = 0; i < 7; i++) {

			pila = solitario.tableros.get(i);
			for (int j = 0; j < pila.size(); j++) {

				naipe = pila.get(j);

				if (j == pila.size() -1) naipe.revelar();

				insertarCarta(Util.obtenerIdCarta(naipe), Util.TABLEROS_X[i], y, obtenerNombreCarta(naipe), naipe.estaRevelada());

				y += 24;
			}
			y = 257;
		}
	}	
	
	/** Obtiene el nombre de los naipes para dibujarlos. 
	 * @param naipe El naipe del que se quiere obtener el nombre. */
	private String obtenerNombreCarta(Naipe naipe) {

		String ruta = "/vista/img/";
		Palo palo = naipe.getPalo();
		Valores valor = naipe.getValor();

		switch (valor) {
		case AS: 	 ruta += "ace"; break;
		case DOS: 	 ruta += "2"; break;
		case TRES: 	 ruta += "3"; break;
		case CUATRO: ruta += "4"; break;
		case CINCO:  ruta += "5"; break;
		case SEIS: 	 ruta += "6"; break;
		case SIETE:  ruta += "7"; break;
		case OCHO: 	 ruta += "8"; break;
		case NUEVE:  ruta += "9"; break;
		case DIEZ: 	 ruta += "10"; break;
		case SOTA:   ruta += "jack"; break;
		case REINA:  ruta += "queen"; break;
		case REY:    ruta += "king"; break;
		default: break;
		}

		ruta += "_of_";

		switch (palo) {
		case CORAZONES: ruta += "hearts"; break;
		case DIAMANTES: ruta += "diamonds"; break;
		case TREBOLES:  ruta += "clubs"; break;
		case PICAS: 	ruta += "spades"; break;
		default: break;
		}

		ruta += ".png";

		return ruta;
	}

	/** Inserta/Dibuja las cartas en la ventana.
	 * @param x Coordenada X donde se dibujará la carta.
	 * @param y Coordenada Y donde se dibujará la carta. 
	 * @param rutaImagen Ruta donde se encuentra la imagen.
	 * @param revelada Indica si la carta está revelada o no.*/
	private void insertarCarta(String id, double x, double y, String rutaImagen, boolean revelada) {

		Carta carta = new Carta(id, x, y, rutaImagen, revelada, solitario);
		
		solitario.listaCartas.add(carta);

		this.Ancla.getChildren().add(carta);
	}
	
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
	
	/** Acciones a realizar cuando se gana la partida. */
	public void partidaGanada() {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("¡Enhorabuena!");
		alert.setHeaderText("¡Has ganado!");
		alert.setContentText("Si quieres iniciar otra partida, ve al menú 'Juego' y selecciona 'Nuevo Juego'");
		alert.showAndWait();
	}
	
	/** Muestra el <i>Easter Egg</i>. */
	public void mostrarEE(AnchorPane ancla, MenuBar barraMenu) {
		
		ImageView elmo = new ImageView("/vista/decoraciones/elmo_nuke_meme.gif");
		elmo.setX(-3);
		elmo.setY(-36);
		elmo.setFitWidth(1315);
		elmo.setFitHeight(736);
		
		ancla.getChildren().add(elmo);
		
		elmo.toFront();
		barraMenu.toFront();
		
		
	}

	private void salir() {

		Stage stage = (Stage)MenuJuegoSalir.getParentPopup().getOwnerWindow();
		stage.close();
	}
}
