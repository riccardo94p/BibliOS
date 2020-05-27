import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;
@Entity
public class Loan implements Serializable {
	@EmbeddedId
	private LoanId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	private User user;

	@ManyToOne
	@MapsId("bookId")
	private Book book;
	
	@Column(columnDefinition = "int default 0")
	private int status;
	
	public Loan() {
	}
	
	public Loan(User user, Book book) {
		this.id=new LoanId(user.getId(),book.getId());
		this.user=user;
		this.book=book;
	}
	public void setId(LoanId id){
		this.id=id;
	}
	public User getUser() {
		return user;
	}
	
	public Book getBook() {
		return book;
	}
	
	public void setUser(User user) {
		this.user=user;
	}
	
	public void setBook(Book book) {
		this.book=book;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status=status;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		Loan that = (Loan) o;
		return Objects.equals( user, that.user ) &&
				Objects.equals( book, that.book );
	}

	@Override
	public int hashCode() {
		return Objects.hash( user, book );
	}
}
