import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController extends Controller {
    @FXML
    private Button borrow_but, tableChanger_but, available_check, viewRequests_but;
    @FXML
    private TableView<Book> list_table;
    @FXML
    private TableColumn idCol, titleCol,authorCol, categoryCol;

    private int tableIndicator = 0; //0: catalogue, 1: myLoans table
    private boolean filterAvailable = false, filterRequests = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome_msg.setText("Welcome, " + Controller.getUsername());

        //blocking table column resize
        idCol.setResizable(false);
        titleCol.setResizable(false);
        authorCol.setResizable(false);
        categoryCol.setResizable(false);

        //associating the table's column with the corresponding attributes of the book class
        idCol.setCellValueFactory(new PropertyValueFactory<Book, Long>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<Book, String>("category"));

        //filling the table with the list returned by the query
        tableOffset = 0;
        updateTable(Main.lm.browseBooks(tableOffset, filterAvailable));

        previous_but.setDisable(true);
        totalPages = ((Main.lm.getNumBooks() + 9)/10);
        currentPage = 1;

        page_count.setText("Page " + currentPage + " of " + totalPages);
    }

    public void disableBorrowBut() {
        //disable loans if user has already 10 loans
        if(Main.lm.limitUserLoans()) {
            borrow_but.setDisable(true);
            output_field.setText("You already have 10 Loans!");
        }
        else {
            borrow_but.setDisable(false);
            output_field.clear();
        }
    }
    @FXML
    public void search(ActionEvent event) {
        output_field.clear();
        super.resetPageButtons();

        if(menuOption == null || menuOption.equals("Title")) updateTable(Main.lm.searchBooks(filterAvailable, 0, search_field.getText(), 0));
        else updateTable(Main.lm.searchBooks(filterAvailable, 1, search_field.getText(), 0));

        menuOption = "Title";
        search_filter.setText("Title");
    }

    public void updateTable(ObservableList<Book> list) {
        list_table.setItems(list);
        disableBorrowBut();
    }

    @FXML
    public void borrowSelected(ActionEvent ev) {
        Book selectedBook = list_table.getSelectionModel().getSelectedItem();

        if(selectedBook == null) {
            printErrorMessage("No book was selected. Please select a book a retry.");
        }
        else {
            output_field.clear();
            output_field.setText(Main.lm.borrowBook(selectedBook.getId()));
            disableBorrowBut();
        }
    }

    @FXML
    public void nextPage(ActionEvent ev) {
        super.nextPage();
        updateTable(Main.lm.searchBooks(filterAvailable, 0, search_field.getText(), tableOffset));
    }

    @FXML
    public void previousPage(ActionEvent ev) {
        super.previousPage();
        updateTable(Main.lm.searchBooks(filterAvailable, 0, search_field.getText(), tableOffset));
    }

    @FXML
    public void changeTable(ActionEvent event) {
        output_field.clear();

        if(tableIndicator == 0) { //move to MyLoans
            super.resetPageButtons();

            next_but.setDisable(true); //only 1 page allowed for my loans
            viewRequests_but.setDisable(true);
            search_but.setDisable(true);
            search_filter.setDisable(true);
            search_field.setDisable(true);
            available_check.setDisable(true);

            filterAvailable = false;
            available_check.setText("Only show Availables");

            tableChanger_but.setText("View Catalogue");

            updateTable(Main.lm.browseUserLoans(1, Controller.userId));
            borrow_but.setDisable(false);

            borrow_but.setText("Return Selected");
            borrow_but.setOnAction((ActionEvent ev) -> {
                Book selectedBook = list_table.getSelectionModel().getSelectedItem();

                if(selectedBook == null) {
                    printErrorMessage("No book was selected. Please select a book a retry.");
                }
                else {
                    output_field.clear();
                    Main.lm.returnBook(selectedBook.getId());
                    output_field.setText("Return request forwarded correctly.");
                    updateTable(Main.lm.browseUserLoans(1, Controller.userId));
                }
            });
            tableIndicator = 1;
        }
        else {   //move to Catalogue
            super.resetPageButtons();
            search_but.setDisable(false);
            search_filter.setDisable(false);
            search_field.setDisable(false);
            viewRequests_but.setDisable(false);
            available_check.setDisable(false);
            available_check.setText("Only show Availables");
            filterAvailable = false;

            borrow_but.setText("Borrow Selected");
            borrow_but.setOnAction(this::borrowSelected);

            tableChanger_but.setText("View My Loans");

            updateTable(Main.lm.searchBooks(filterAvailable, 1, search_field.getText(), 0));
            tableIndicator = 0;
        }

    }

    @FXML
    public void filterAvailables(ActionEvent event) {
        output_field.clear();
        filterAvailable = !filterAvailable;

        if(filterAvailable) available_check.setText("Show all");
        else available_check.setText("Only show Availables");

        updateTable(Main.lm.searchBooks(filterAvailable, 1, search_field.getText(), 0));
        super.resetPageButtons();
    }

    @FXML
    public void viewRequests(ActionEvent event) {
        output_field.clear();
        filterRequests = !filterRequests;

        if(filterRequests) {
            viewRequests_but.setText("View Catalogue");

            updateTable(Main.lm.browseUserLoans(0, super.userId)); //0 = pending loan requests
            super.resetPageButtons();

            next_but.setDisable(true); //only 1 page allowed for my requests
            borrow_but.setDisable(false);
            search_but.setDisable(true);
            search_filter.setDisable(true);
            search_field.setDisable(true);
            available_check.setDisable(true);

            filterAvailable = false;
            available_check.setText("Only show Availables");

            tableChanger_but.setDisable(true);

            borrow_but.setText("Return Selected");
            borrow_but.setOnAction((ActionEvent ev) -> {
                Book selectedBook = list_table.getSelectionModel().getSelectedItem();

                if(selectedBook == null) {
                    printErrorMessage("No book was selected. Please select a book a retry.");
                }
                else {
                    output_field.clear();
                    Main.lm.returnBook(selectedBook.getId());
                    output_field.setText("Return request forwarded correctly.");
                    updateTable(Main.lm.browseUserLoans(0, super.userId)); //0 = pending loan requests
                    borrow_but.setDisable(false);
                }
            });
        }
        else {
            viewRequests_but.setText("View My Requests");

            updateTable(Main.lm.searchBooks(filterAvailable, 1, search_field.getText(), 0));
            super.resetPageButtons();

            next_but.setDisable(false); //only 1 page allowed for my requests

            search_but.setDisable(false);
            search_filter.setDisable(false);
            search_field.setDisable(false);
            available_check.setDisable(false);

            filterAvailable = true;
            available_check.setText("Only show Availables");

            borrow_but.setText("Borrow Selected");
            borrow_but.setOnAction(this::borrowSelected);

            tableChanger_but.setDisable(false);
        }
    }
}
