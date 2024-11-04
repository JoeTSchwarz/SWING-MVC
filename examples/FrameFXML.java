import java.io.File;
import javafx.scene.layout.*;
import javafx.scene.image.*;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Joe T. Schwarz (C)
public class FrameFXML extends Application {
  public void start(Stage stage) throws Exception {
    FXMLLoader fxml = new FXMLLoader(getClass().getResource("frame.fxml"));
    AnchorPane root = fxml.load();
    /*
    try { // create the background Image
      BackgroundSize bgSize = new BackgroundSize(280, 240, false, false, false, false);
      Image img = new Image(new File("AIman.jpg").toURI().toString());
      root.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, 
                      BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize)));
    } catch (Exception ex) { }
    */
    stage.setScene(new Scene(root));
    stage.setX(200);
    stage.setY(0);
    stage.show();
  }
}
