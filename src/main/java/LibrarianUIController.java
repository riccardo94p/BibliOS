import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LibrarianUIController extends Controller {

    @FXML
    private Button loan_but, logout_but, book_but, user_but;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome_msg.setText("Welcome, " + Controller.getUsername());
    }
    @FXML
    public void books(ActionEvent event) throws IOException {
    	Main.changeScene(3);
    }

    @FXML
    public void users(ActionEvent event) throws IOException {
    	Main.changeScene(4);
    }

    @FXML
    public void loans(ActionEvent event) throws IOException {
    	Main.changeScene(5);
    }

    @FXML
    private void shutdown() {
        Main.lm.logout();
        Main.exit();
    }
}