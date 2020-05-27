import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.*;

import java.io.IOException;

public class Main extends Application {
    private static Stage stage;
    private static Parent root;
    public static LibraryManager lm;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        lm = new LibraryManager();
        lm.setup();
        stage = primaryStage;
        stage.setTitle("BibliOS");
        changeScene(0); //0: login, 1: user, 2: librarian_base, 3:librarian_book, 4:librarian_user, 5:librarian_loans
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void changeScene(int type) throws IOException {
        switch(type) {
            case 0:
                root = FXMLLoader.load(Main.class.getResource("/BibliosLogin.fxml"));
                stage.setScene(new Scene(root, 640, 400));
                break;
            case 1:
                root = FXMLLoader.load(Main.class.getResource("/UserInterface.fxml"));
                stage.setScene(new Scene(root, 800, 640));
                break;
            case 2:
            	root = FXMLLoader.load(Main.class.getResource("/LibrarianUI.fxml"));
                stage.setScene(new Scene(root, 230, 360));
            	break;
            case 3:
            	root = FXMLLoader.load(Main.class.getResource("/LibrarianBooks.fxml"));
                stage.setScene(new Scene(root, 960, 650));
            	break;
            case 4:
            	root = FXMLLoader.load(Main.class.getResource("/LibrarianUser.fxml"));
                stage.setScene(new Scene(root, 1000, 640));
            	break;
            case 5:
            	root = FXMLLoader.load(Main.class.getResource("/LibrarianLoans.fxml"));
                stage.setScene(new Scene(root, 800, 640));
            	break;
            default:
                break;
        }

        stage.show();
        centerStage(stage);
    }

    public static void exit() { Platform.exit(); }

    public static void centerStage(Stage s) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        s.setX((primScreenBounds.getWidth() - s.getWidth()) / 2);
        s.setY((primScreenBounds.getHeight() - s.getHeight()) / 2);
    }
}
