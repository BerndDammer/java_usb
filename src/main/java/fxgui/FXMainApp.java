package fxgui;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXMainApp extends Application {
	public void start(final Stage stage) throws IOException {
    	final MainframeController mfc = new MainframeController(stage);
    	Scene scene = new Scene( mfc );
        stage.setTitle("USB Device Tree View");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }
}