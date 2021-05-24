package cl.transbank.possdk.example;

import cl.transbank.pos.POS;
import cl.transbank.pos.exceptions.TransbankLinkException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import org.apache.log4j.BasicConfigurator;

public class App extends Application {

    private static Scene scene;

    static POS pos = null;

    public static POS getPos() {
        return pos;
    }

    @Override
    public void start(Stage stage) throws IOException, TransbankLinkException {
        BasicConfigurator.configure();
        if (pos == null) {
            pos = POS.getInstance();
        }
        scene = new Scene(loadFXML());
        stage.setTitle("Caja POS Integrado");
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinHeight(800);
        stage.show();
        
    }

    @Override
    public void stop() throws Exception {
        pos.getInstance().closePort();
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
