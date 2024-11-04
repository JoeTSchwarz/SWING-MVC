// JFX
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
//
import javafx.fxml.FXML;
// Joe T. Schwarz (c) 
public class fxmlFrameController  {
    @FXML 
    Button But1;
    @FXML 
    TextArea TxtA1;  
    @FXML 
    TextField TxtF1; 
    //
    private boolean on = false;
    
    @FXML 
    private void click( ) {
      But1.setBackground(new Background(new BackgroundFill(on? Color.YELLOW:Color.LIME,
                         CornerRadii.EMPTY, Insets.EMPTY)));
      TxtA1.appendText("\nButton was clicked. Color changed to:"+(on?"YELLOW":"GREEN"));
      on = !on;
    }
    @FXML 
    public void read() {
      TxtA1.appendText("\nName is:"+TxtF1.getText());
    }
    @FXML
    private void initialize() {
      But1.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, 
                      Insets.EMPTY)));
      /*
      But1.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, 
                      Insets.EMPTY)));
      TxtF1.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, 
                      Insets.EMPTY)));
      */
    }
}
