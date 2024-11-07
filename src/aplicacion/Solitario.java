package aplicacion;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Solitario extends Application {
	@Override
	public void start(Stage primaryStage) {

		String fxml = "/vista/Login.fxml";
		String css = "/vista/css/application.css";
		String icon = "/vista/icon/usuario.png";
		String title = "Iniciar Sesión";

		try {
			// Cargar fxml y crear escena
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource(fxml));
			Scene scene = new Scene(root);

			// Cargar estilo, titulo y icono
			scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
			primaryStage.setTitle(title);
			primaryStage.getIcons().add(new Image(getClass().getResource(icon).toExternalForm()));

			// Cargar escena
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
