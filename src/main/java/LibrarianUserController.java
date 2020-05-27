import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LibrarianUserController extends Controller {
    @FXML
    private TextField userid_field, surname_field, name_field;
    @FXML
    private TableView<User> user_table;
    @FXML
    private TableColumn<User, String> idCol, nameCol, surnameCol;
    @FXML
    private Button add_button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome_msg.setText("Welcome " + Controller.getUsername());

        idCol.setResizable(false);
        nameCol.setResizable(false);
        surnameCol.setResizable(false);

        //associating the table's column with the corresponding attributes of the user class
        idCol.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<User, String>("surname"));

        //filling the table with the list returned by the query
        tableOffset = 0;
        user_table.setItems(Main.lm.browseUsers(tableOffset));

        previous_but.setDisable(true);
        totalPages = ((Main.lm.getNumUsers() + 9)/10);
        currentPage = 1;

        page_count.setText("Page " + currentPage + " of " + totalPages);
        if(totalPages == 1)
            next_but.setDisable(true);
    }

    private void updateTable(ObservableList<User> list)
    {
        user_table.setItems(list);
    }

    @FXML
    public void nextPage(ActionEvent ev) {
        super.nextPage();
        updateTable(Main.lm.searchUsers(0, search_field.getText(), tableOffset));
    }

    @FXML
    public void previousPage(ActionEvent ev) {
        super.previousPage();
        updateTable(Main.lm.searchUsers(0, search_field.getText(), tableOffset));
    }

    @FXML
    public void searchUs(ActionEvent event) {
        output_field.clear();
        super.resetPageButtons();

        if(menuOption == null || menuOption.equals("Name")) {
            updateTable(Main.lm.searchUsers(0, search_field.getText(), 0));
        }
        else updateTable(Main.lm.searchUsers(1, search_field.getText(), 0));

        search_filter.setText("Name");
    }

    @FXML
    public void addUser(ActionEvent event) {
        userid_field.setStyle("");
        name_field.setStyle("");
        surname_field.setStyle("");

        output_field.clear();
        if(!validateFields()) return;

        //finally, add book and refresh table
        Main.lm.addUser(userid_field.getText(), name_field.getText(), surname_field.getText());
        updateTable(Main.lm.searchUsers(0, "", tableOffset));

        clearFields();
    }

    public boolean validateFields() {
        boolean ret = true;
        if ((userid_field.getText().isEmpty()) || (name_field.getText().isEmpty()) || (surname_field.getText().isEmpty())) {
            //error log
            printErrorMessage("You have an error in the input boxes. Please check for typos and retry.");

            if(userid_field.getText().isEmpty())
                userid_field.setStyle("-fx-background-color: #ff0000");
            if(name_field.getText().isEmpty())
                name_field.setStyle("-fx-background-color: #ff0000");
            if(surname_field.getText().isEmpty())
                surname_field.setStyle("-fx-background-color: #ff0000");

            ret = false;
        }
        return ret;
    }

    public void clearFields() {
        userid_field.clear();
        name_field.clear();
        surname_field.clear();
    }

}
