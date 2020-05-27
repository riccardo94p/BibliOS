import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@Embeddable
public class LoanId implements Serializable {
 
    @Column(name = "user")
    private String userId;
 
    @Column(name = "book")
    private Long bookId;
 
    private LoanId() {}
 
    public LoanId(String userId,Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        LoanId that = (LoanId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(bookId, that.bookId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId);
    }
}

