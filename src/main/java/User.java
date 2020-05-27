import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class User implements Serializable {

	@Id
	//The id variable is linked to the column of the table with the name userId 
	//therefore we must use the following annotation.
	@Column(name="idUser")
	private String id;
	
	private String name;
	private String surname;
	
	@Column(columnDefinition = "tinyint(4) default 0")
	private int privilege;


	@OneToMany( mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Loan> loans = new ArrayList<>();
	
	
	public User() {
	}
	
	public String getId() {
		return id;
	}
	
	public void setUserId(String id) {
		this.id=id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname=surname;
	}
	
	public int getPrivilege() {
		return privilege;
	}
	
	public List<Loan> getLoans()
	{
			return this.loans;
	}
	
	
	//helper method to sync a loan addition
	public void addLoan(Book book) {
		Loan loan = new Loan(this, book);
		loans.add(loan);
		book.getLoans().add(loan);
	}
	
	//helper method to sync a loan removal
	public void removeLoan(Book book) {
		for (Iterator<Loan> iterator = loans.iterator(); iterator.hasNext(); ) 
		{
			Loan loan = iterator.next();
	            if (loan.getUser().equals(this) &&
	                    loan.getBook().equals(book)) {
	            			iterator.remove();
							book.getLoans().remove(loan);
							loan.setUser( null );
							loan.setBook( null );
	            }
		}
	}
	
	@Override
    public boolean equals(Object o) {
        if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
 
        User that = (User) o;
        return id != null && id.equals(that.getId());
    }
 
    @Override
    public int hashCode() {
        return 41;
    }
}
