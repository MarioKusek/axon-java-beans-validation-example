package hr.fer.axon.users;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class UsersPage extends PageImpl<UserDto> {

  private List<UserDto> content;
  private Pageable pageable;
  private long total;

  public UsersPage(List<UserDto> content, Pageable pageable, long total) {
    super(content, pageable, total);
    this.content = content;
    this.pageable = pageable;
    this.total = total;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UsersPage [content=" + content + ", pageable=" + pageable + ", total=" + total + "]";
  }

}
