import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.cell.*;

public class LibrarianLoansController extends Controller {
    @FXML
    private Label currentUser, loanTable_text;
    @FXML
    private TableView<Book> loan_table, return_table;
    @FXML
    private TableColumn loanTitleCol, loanBookIdCol;
    @FXML
    private TableColumn returnTitleCol, returnBookIdCol;
    @FXML
    private Button loanChanger_but;

    private String userId = "";

    private boolean userVerified = false; //indicates if a valid user has been selected
    private int loanTableIndicator = 0; //0 pending loans, 1 active loans, keeps track of which Loan table to show

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome_msg.setText("Welcome " + getUsername());
        currentUser.setText("Current User: < ? >");

        loanTitleCol.setResizable(false);
        loanBookIdCol.setResizable(false);
        returnTitleCol.setResizable(false);
        returnBookIdCol.setResizable(false);

        returnBookIdCol.setCellValueFactory(new PropertyValueFactory<Book, Long>("id"));
        loanBookIdCol.setCellValueFactory(new PropertyValueFactory<Book, Long>("id"));
        returnTitleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        loanTitleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
    }

    public void resetFields() {
        userId = "";
        currentUser.setText("Current User: < ? >");
        loan_table.getItems().clear();
        return_table.getItems().clear();
    }

    @FXML
    public void searchUser() {
        userVerified = false;

        if (search_field.getText().isEmpty()) {
            printErrorMessage("empty User ID field. Please check the input field and retry.");
            resetFields();
        } else {
            output_field.clear();
            userId = search_field.getText();

            if (Main.lm.findUser(userId) == null) {
                printErrorMessage("specified User ID doesn't match any existing user.");
                resetFields();
            } else {
                userVerified = true;
                currentUser.setText("Current User: <" + Main.lm.findUser(userId) + ">");
                loan_table.setItems(Main.lm.browseUserLoans(0, userId)); //0 = pending loan requests
                return_table.setItems(Main.lm.browseUserLoans(2, userId)); //2 = pending return requests)
            }
        }
    }

    @FXML
    public void confirmLoan(ActionEvent event) {
        output_field.clear();
        if (!userVerified)
            printErrorMessage("You must select a valid User before performing operations.");
        else {
            Book selectedBook = loan_table.getSelectionModel().getSelectedItem();
            Main.lm.validateBorrow(userId, selectedBook.getId());

            //refresh the table
            loan_table.setItems(Main.lm.browseUserLoans(0, userId)); //0 = pending loan requests
        }
    }

    @FXML
    public void confirmReturn(ActionEvent event) {
        output_field.clear();
        if (!userVerified)
            printErrorMessage("You must select a valid User before performing operations.");
        else {
            Book selectedBook = return_table.getSelectionModel().getSelectedItem();
            Main.lm.validateReturn(userId, selectedBook.getId());

            //refresh the table
            return_table.setItems(Main.lm.browseUserLoans(2, userId)); //2 = pending return requests
        }
    }

    @FXML
    void manageToggleButton(ActionEvent event) {
        if (!userVerified)
            printErrorMessage("You must select a valid User before performing operations.");
        else {
            if (loanTableIndicator == 1) {
                loanTable_text.setText("Loan Requests");
                loan_table.setItems(Main.lm.browseUserLoans(0, userId)); //0 = pending loan requests

                loanTableIndicator = 0;
                loanChanger_but.setText("View Active Loans");
            } else {
                loanTable_text.setText("Active Loans");
                loan_table.setItems(Main.lm.browseUserLoans(1, userId)); //1 = active loans

                loanTableIndicator = 1;
                loanChanger_but.setText("View Loan Requests");
            }
        }
    }
}
