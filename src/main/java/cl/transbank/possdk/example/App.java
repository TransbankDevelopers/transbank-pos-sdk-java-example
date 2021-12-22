package cl.transbank.possdk.example;

import cl.transbank.pos.POSIntegrado;
import cl.transbank.pos.exceptions.common.TransbankException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application{

	private static Scene scene;

	static POSIntegrado pos = null;

	public static POSIntegrado getPos() {
		return pos;
	}

	@Override
	public void start(Stage stage) throws IOException, TransbankException {
		if (pos == null) {
			pos = new POSIntegrado();
		}
		scene = new Scene(loadFXML());
		stage.setTitle("Caja POS Integrado");
		scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		pos.closePort();
	}

	private static Parent loadFXML() throws IOException {
		URL resource = App.class.getClassLoader().getResource("cl/transbank/possdk/example/POS_App.fxml");
		System.out.println("resource: " + resource);
		FXMLLoader fxmlLoader = new FXMLLoader(resource);
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		launch(args);
	}


}
