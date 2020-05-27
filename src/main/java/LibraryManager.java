import javafx.collections.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

public class LibraryManager {
	private static EntityManager entityManager;
	private static EntityManagerFactory factory;
	private String loggedUser=null;
	
	public void setup()	{
		factory = Persistence.createEntityManagerFactory("bibliosDB");
	}
	
	public void exit() {
		factory.close();
	}
	
	public List<String> login(String id) {
		if (loggedUser!=null)
			return null;
		String name = null;
		int privilege = 0;
		List<String> result = new ArrayList<String>();
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			User user = entityManager.find(User.class, id);
			entityManager.getTransaction().commit();
			loggedUser = user.getId();
			privilege = user.getPrivilege();
			name = user.getName();
			name = name.concat(" ");
			name = name.concat(user.getSurname());
		}catch (Exception ex) {
    		ex.printStackTrace();
    		System.out.println("A problem occurred with the login.");
    	}
		finally {
			entityManager.close();
		}
		if(loggedUser != null) {
			result.add(name);
			result.add(Integer.toString(privilege));
			return result;	
		}
		return null;
	}
	
	public void logout() {
		loggedUser = null;
	}
	
	public boolean isLogged() {
		if(loggedUser != null)
			return true;
		return false;
	}

//Loan Operations	
	//browses a specific user's loans (reserved to librarians only)
	public ObservableList<Book> browseUserLoans(int status, String userid) {
		User user=null;
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			user = entityManager.find(User.class, userid);
			//to fetch the collection
			Hibernate.initialize(user.getLoans());

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the browseUserLoans()");
		}
		finally {
			entityManager.close();
		}

		ObservableList<Book> bookList = FXCollections.observableArrayList();
		for(Loan l: user.getLoans()) {
			if (l.getStatus() == status) bookList.add(l.getBook());
		}
		return bookList;
	}
	
    public boolean limitUserLoans() { 
        int size=0;
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			User user = entityManager.find(User.class, loggedUser);
            size=user.getLoans().size();
			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the limitUserLoans().");
		}
		finally {
			entityManager.close();
		}
        if (size>=10)
            return true;
        return false;
	}
    
	//response is returned to the caller in order to display a comprehensive output message to the user interface
	public String borrowBook(long bookId) { 
		String response = "";
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			LoanId loanid = new LoanId(loggedUser,bookId);
			Loan loan = entityManager.find(Loan.class, loanid);
			if(loan != null)
				return "You already borrowed this book.";
			else {	
				Book book = entityManager.find(Book.class, bookId);
				User user = entityManager.find(User.class, loggedUser);
				if(available(book) > 0) {
					user.addLoan(book);
					response ="Successfully requested the book: " + book.getTitle() +".";
				}
				else
					response = "This book is not available.";
			}
			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			response = "A problem occurred with the loan request.";
		}
		finally {
			entityManager.close();
		}
        return response;
	}
	
	public void validateBorrow(String userid, long bookId) {
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			LoanId loanid = new LoanId(userid, bookId);
			Loan loan = entityManager.find(Loan.class, loanid);

			if(loan.getStatus() == 0)
				loan.setStatus(1);

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the loan validation!");
		}
		finally {
			entityManager.close();
		}
	}
	
	//for user to request a return
	public void returnBook(long bookId) {
		try {
			entityManager=factory.createEntityManager();
			entityManager.getTransaction().begin();
			LoanId loanid= new LoanId(loggedUser,bookId);
			Loan loan=entityManager.find(Loan.class, loanid);
			if(loan!=null) {
				if(loan.getStatus()==1) {
					loan.setStatus(2);
				}
				else if(loan.getStatus()==0)
				{
					User user = entityManager.find(User.class, loggedUser);
					user.removeLoan(loan.getBook());
				}
			}
			entityManager.getTransaction().commit();
		}catch (Exception ex) {
    		ex.printStackTrace();
    		System.out.println("A problem occurred with the book return request.");
    	}
		finally {
			entityManager.close();
		}
			
	}
	
	public void validateReturn(String userid, long bookId) {
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			LoanId loanid = new LoanId(userid, bookId);
			Loan loan = entityManager.find(Loan.class, loanid);

			if(loan.getStatus() == 2)
				loan.getUser().removeLoan(loan.getBook());

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the loan validation!");
		}
		finally {
			entityManager.close();
		}
	}

//User table operations
	
	public String findUser(String userid) {
		String resultStr=null;
		User user=null;
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			user = entityManager.find(User.class, userid);
			if(user.getPrivilege() == 0) resultStr = user.getName() + " " + user.getSurname();

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the LibraryManager.findUser().");
		}
		finally {
			entityManager.close();
		}
		return resultStr;
	}

	//browse user list
	public ObservableList<User> browseUsers(int offset) {
		List<User> tmpUsers = null;
		try {
			entityManager=factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query q = entityManager.createNativeQuery("SELECT u.idUser, u.name, u.surname, u.privilege FROM User u WHERE u.privilege=0 ORDER BY u.idUser LIMIT 10 OFFSET ? ", User.class);
			q.setParameter(1, offset);

			tmpUsers = q.getResultList();

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the login!");
		}
		finally {
			entityManager.close();
		}
		ObservableList<User> users = FXCollections.observableArrayList();
		for(User u: tmpUsers)
			users.add(u);
		return users;
	}

	public ObservableList<User> searchUsers(int option, String name, int offset) { //option 0: name, 1:surname
		List<User> tmpUsers = null;
		name = "%"+name+"%";
		offset = offset*10;

		try {
			entityManager=factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query q;

			String s="SELECT u.idUser, u.name, u.surname, u.privilege FROM User u WHERE u.privilege = 0 and";
			if(option == 0)
				q = entityManager.createNativeQuery( s + " u.name LIKE ? ORDER BY u.name LIMIT 10 OFFSET ? ", User.class);
			else
				q = entityManager.createNativeQuery( s + " u.surname LIKE ? ORDER BY u.name LIMIT 10 OFFSET ? ", User.class);

			q.setParameter(1, name);
			q.setParameter(2, offset);

			tmpUsers = q.getResultList();

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the search by name!");
		}
		finally {
			entityManager.close();
		}
		ObservableList<User> users = FXCollections.observableArrayList();
		for(User u: tmpUsers)
			users.add(u);

		return users;
	}
//returns the number of users
	public int getNumUsers() {
		int result = 0;

		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM User;");
			result = ((Number)q.getSingleResult()).intValue();

			entityManager.getTransaction().commit();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			entityManager.close();
		}
		return result;
	}
	
	public void addUser(String id,String name, String surname) {
		User user=new User();
		user.setUserId(id);
		user.setName(name);
		user.setSurname(surname);
		try {
			entityManager=factory.createEntityManager();
			entityManager.getTransaction().begin();
			User exists=entityManager.find(User.class, id);
			if(exists!=null)
				System.out.println("Book already registered.");
			else
				entityManager.persist(user);
			entityManager.getTransaction().commit();

		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the user registration.");
		}
		finally {
			entityManager.close();
		}
	}

//book table operations

	public ObservableList<Book> browseBooks(int offset, boolean onlyAvailable) {
		List<Book> tmpBooks = null;
		try {
			entityManager=factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query q = entityManager.createNativeQuery("SELECT b.ISBN, b.title, b.author, b.numCopies, b.category FROM Book b ORDER BY b.title LIMIT 10 OFFSET ? ", Book.class);

			q.setParameter(1, offset);

			tmpBooks = q.getResultList();

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the browse books!");
		}
		finally {
			entityManager.close();
		}
		ObservableList<Book> books = FXCollections.observableArrayList();
		for(Book b: tmpBooks){
			if (onlyAvailable){
				if(isAvailable(b.getId()))
					books.add(b);
			}
			else
				books.add(b);
		}
		return books;
	}

//onlyAvailable specifies wether I want a list on only available books or not
	public ObservableList<Book> searchBooks(boolean onlyAvailable, int option, String title, int offset) { //option 0: title, 1:author
		List<Book> tmpBooks = null;
		title = "%"+title+"%";
		offset = offset*10;

		try {
			entityManager=factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query q;
			
			String s="SELECT b.ISBN, b.title, b.author, b.category, b.numCopies FROM Book b WHERE";
			if(option == 0) 
				q = entityManager.createNativeQuery( s + " b.title LIKE ? ORDER BY b.title LIMIT 10 OFFSET ? ", Book.class);
			else 
				q = entityManager.createNativeQuery( s + " b.author LIKE ? ORDER BY b.title LIMIT 10 OFFSET ? ", Book.class);

			q.setParameter(1, title);
			q.setParameter(2, offset);

			tmpBooks = q.getResultList();

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the search by title!");
		}
		finally {
			entityManager.close();
		}
		ObservableList<Book> books = FXCollections.observableArrayList();
		for(Book b: tmpBooks){
			if (onlyAvailable){
				if(isAvailable(b.getId()))
					books.add(b);
			}
			else
				books.add(b);
		}
		return books;
	}
	
//returns the number of books in the catalogue
	public int getNumBooks() { 
		int result = 0;

		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM Book;");
			result = ((Number)q.getSingleResult()).intValue();

			entityManager.getTransaction().commit();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			entityManager.close();
		}
		return result;
	}

	public String addBook(long isbn, String title, String author, String category, int numCopies) {
		String result = "";
		Book book=new Book();
		book.setId(isbn);
		book.setAuthor(author);
		book.setTitle(title);
		book.setCategory(category);
		book.setNumCopies(numCopies);
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Book exists = entityManager.find(Book.class, isbn);
			if(exists != null)
				result ="Book already registered.";
			else {
				entityManager.persist(book);
				result = "Book successfully added.";
			}
			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			result = "A problem occurred with the book addition.";
		}
		finally {
			entityManager.close();
		}
		return result;
	}

	public int available(Book book) {
		int copies = book.getNumCopies();
		int available = copies - book.getLoans().size();
		return available;
	}
	
	public boolean isAvailable(long bookId){
		int available=0;
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			Book book = entityManager.find(Book.class, bookId);
			available = available(book);
			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the login!");
		}
		finally {
			entityManager.close();
		}
		if (available>0) 
			return true;
		else 
			return false;
	}
	
//returns a comprehensive message to the user interface
	public String removeBook(long bookId) {
		String result = ""; 
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Book book = entityManager.find(Book.class, bookId);

			if(book != null) {
				result = "Successfully removed book.";
				entityManager.remove(book);
			}
			else result = "Selected book doesn't exist.";

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			result = "A problem occurred with the LibraryManager.removeBook().";
		}
		finally {
			entityManager.close();
		}
		return result;
	}

	public void removeCopies(long bookId, int numCopies) {
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Book book = entityManager.find(Book.class, bookId);
			int available = available(book);

			if(numCopies <= available){
				if(numCopies == book.getNumCopies()) {
					entityManager.getTransaction().commit();
					entityManager.close();
					removeBook(bookId);
					return;
				}
				else
					book.setNumCopies(book.getNumCopies()-numCopies);
			}
			else
				System.out.println("ERROR: There are not enough copies to be removed");
			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred with the copyremoval.");
		}
		finally {
			entityManager.close();
		}
	}
	
	public void addCopies(long bookId, int numCopies) {
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();

			Book book = entityManager.find(Book.class, bookId);
			int newNumCopies = book.getNumCopies() + numCopies;
			book.setNumCopies(newNumCopies);

			entityManager.getTransaction().commit();
		}catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("A problem occurred in the LibraryManager.addCopies().");
		}
		finally {
			entityManager.close();
		}
	}
}
