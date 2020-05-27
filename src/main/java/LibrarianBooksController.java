import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import java.net.URL;
import java.util.ResourceBundle;

public class LibrarianBooksController extends Controller {
    @FXML
    private TextField isbn_field, category_field, title_field, author_field, copies_field;
    @FXML
    private TableView<Book> book_table;
    @FXML
    private TableColumn<Book, Long> idCol;
    @FXML
    private TableColumn<Book, String> titleCol, authorCol, categoryCol;
    @FXML
    private TableColumn<Book, Integer> copiesCol;
    @FXML
    private Button add_but, remove_but;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome_msg.setText("Welcome " + Controller.getUsername());

        idCol.setResizable(false);
        titleCol.setResizable(false);
        authorCol.setResizable(false);
        categoryCol.setResizable(false);
        copiesCol.setResizable(false);

        //associating the table's column with the corresponding attributes of the book class
        idCol.setCellValueFactory(new PropertyValueFactory<Book, Long>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<Book, String>("category"));
        copiesCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("numCopies"));

        //filling the table with the list returned by the query
        tableOffset = 0;
        book_table.setItems(Main.lm.browseBooks(tableOffset, false));

        previous_but.setDisable(true);
        totalPages = ((Main.lm.getNumBooks() + 9)/10);
        currentPage = 1;

        page_count.setText("Page " + currentPage + " of " + totalPages);
    }

    private void updateTable(ObservableList<Book> list)
    {
        book_table.setItems(list);
    }

    @FXML
    public void nextPage(ActionEvent ev) {
        super.nextPage();
        updateTable(Main.lm.searchBooks(false, 0, search_field.getText(), tableOffset));
    }

    @FXML
    public void previousPage(ActionEvent ev) {
        super.previousPage();
        updateTable(Main.lm.searchBooks(false, 0, search_field.getText(), tableOffset));
    }

    @FXML
    public void search(ActionEvent event) {
        output_field.clear();
        super.resetPageButtons();

        if(menuOption == null || menuOption.equals("Title")) {
            updateTable(Main.lm.searchBooks(false, 0, search_field.getText(), 0));
        }
        else updateTable(Main.lm.searchBooks(false, 1, search_field.getText(), 0));

        search_filter.setText("Title");
    }

    @FXML
    public void addBook(ActionEvent event) {
        copies_field.setStyle("");
        isbn_field.setStyle("");
        title_field.setStyle("");
        author_field.setStyle("");
        category_field.setStyle("");

        output_field.clear();
        if(!validateFields()) return;

        //finally, add book and refresh table
        Main.lm.addBook(Long.parseLong(isbn_field.getText()), title_field.getText(), author_field.getText(), category_field.getText(), Integer.parseInt(copies_field.getText()));
        updateTable(Main.lm.searchBooks(false, 0, "", tableOffset));

        clearFields();
    }

    public boolean validateFields() {
        boolean ret = true;
        if ((isbn_field.getText().isEmpty()) || (title_field.getText().isEmpty()) || (author_field.getText().isEmpty()) || (category_field.getText().isEmpty()) || (copies_field.getText().isEmpty())) {
            //error log
            printErrorMessage("You have an error in the input boxes. Please check for typos and retry.");

            if(title_field.getText().isEmpty())
                title_field.setStyle("-fx-background-color: #ff0000");
            if(author_field.getText().isEmpty())
                author_field.setStyle("-fx-background-color: #ff0000");
            if(category_field.getText().isEmpty())
                category_field.setStyle("-fx-background-color: #ff0000");

            ret = false;
        }
        if (!copies_field.getText().matches("[0-9]+")) {
            copies_field.setStyle("-fx-background-color: #ff0000");
            printErrorMessage("You have an error in the numCopies field, only numbers are allowed. Please check for typos and retry.");
            ret = false;
        }
        if (!isbn_field.getText().matches("[0-9]+")) {
            isbn_field.setStyle("-fx-background-color: #ff0000");
            printErrorMessage("You have an error in the ISBN field, only numbers are allowed. Please check for typos and retry.");
            ret = false;
        }
        return ret;
    }

    public void clearFields() {
        isbn_field.clear();
        title_field.clear();
        author_field.clear();
        category_field.clear();
        copies_field.clear();
    }

    @FXML
    public void removeBook(ActionEvent event) {
        Book selectedBook = book_table.getSelectionModel().getSelectedItem();

        if(selectedBook == null) {
            printErrorMessage("No book was selected!");
        }
        else {
            Main.lm.removeBook(selectedBook.getId());
            updateTable(Main.lm.searchBooks(false, 0, "", tableOffset));
        }
    }

    /* public TextArea getOutputField() così per stampare posso fare printMsg(classe.getOutputField(), String msg); */
    @FXML
    public void removeBookCopy(ActionEvent event) {
        output_field.clear();
        Book selectedBook = book_table.getSelectionModel().getSelectedItem();
        if(selectedBook == null)
            printErrorMessage("No book was selected!");
        else {
            Main.lm.removeCopies(selectedBook.getId(), 1); //1 copy removed
            updateTable(Main.lm.searchBooks(false, 0, "", tableOffset));
        }
    }

    @FXML
    public void addCopy(ActionEvent event) {
        output_field.clear();
        Book selectedBook = book_table.getSelectionModel().getSelectedItem();
        if(selectedBook == null)
            printErrorMessage("No book was selected!");
        else {
            Main.lm.addCopies(selectedBook.getId(), 1); //1 copy added
            updateTable(Main.lm.searchBooks(false, 0, "", tableOffset));
        }
    }
}
