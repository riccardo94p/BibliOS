import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.event.*;
import javafx.scene.input.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    protected Button back;
    @FXML
    private TextField login_code;
    @FXML
    private Button login_but;
    @FXML
    protected Button logout_but;
    @FXML
    protected Label output_text;
    @FXML
    protected Label welcome_msg;
    @FXML
    protected TextArea output_field;
    @FXML
    protected TextField search_field;
    @FXML
    protected Button next_but;
    @FXML
    protected Button previous_but;
    @FXML
    protected Label page_count;
    @FXML
    protected MenuButton search_filter;
    @FXML
    protected Button search_but;

    private static String username;
    private static int privilege;

    protected String menuOption;
    protected static String userId;
    protected int tableOffset, currentPage, totalPages;

    @FXML
    public void logout(ActionEvent event) throws IOException {
        Main.lm.logout();
        Main.changeScene(0);
    }

    @FXML
    public void loginEvent(ActionEvent event) throws IOException { login(); }

    protected void login() throws IOException
    {
        List<String> result = new ArrayList<String>();
        result = Main.lm.login(login_code.getText());

        if (result == null) {
            output_text.setTextFill(Color.RED);
            output_text.setText("ERROR: wrong id. Please check your personal code and retry.");
            Main.lm.logout();
        }
        else {
            username = result.get(0);
            userId = login_code.getText();
            privilege = Integer.parseInt(result.get(1));

            System.out.println(username +", " + privilege);

            if (privilege == 0) {
                Main.changeScene(1);
            } else if (privilege == 1)
                Main.changeScene(2);
        }
    }

    @FXML
    protected void setMenuOption(ActionEvent event) {
        menuOption = ((MenuItem) event.getSource()).getText();
        search_filter.setText(menuOption);
    }

    @FXML
    protected void back(ActionEvent event) throws IOException {
        Main.changeScene(2);
    }

    protected void resetPageButtons() {
        tableOffset = 0;
        currentPage = 1;
        previous_but.setDisable(true);

        if(totalPages == 1) next_but.setDisable(true);
        else next_but.setDisable(false);

        page_count.setText("Page " + currentPage + " of " + totalPages);
    }

    public static String getUsername() { return username; }

    protected void nextPage() {
        tableOffset++;
        currentPage++;
        page_count.setText("Page " + currentPage + " of " + totalPages);

        if(currentPage == totalPages) next_but.setDisable(true);
        if(previous_but.isDisabled()) previous_but.setDisable(false);
    }

    protected void previousPage() {
        tableOffset--;
        currentPage--;
        page_count.setText("Page " + currentPage + " of " + totalPages);

        if(currentPage <= 1) previous_but.setDisable(true);
        if(next_but.isDisabled()) next_but.setDisable(false);
    }

    protected void printErrorMessage(String msg) {
        output_field.clear();
        output_field.setStyle("-fx-text-fill: red ;");
        output_field.setText("ERROR: " + msg);
    }

    protected void printMessage(String msg) {
        output_field.clear();
        output_field.setStyle("-fx-text-fill: green ;");
        output_field.setText(msg);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_code.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER)
                try { login(); } catch (IOException e) { e.printStackTrace(); }
        });
    }
}
