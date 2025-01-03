package hr.fer.axon.users.queries;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public record SearchUsers(Pageable page, String searchText) {
  public static final Sort SORT = Sort.by(Direction.ASC, "lastName")
      .and(Sort.by(Direction.ASC, "firstName"))
      .and(Sort.by(Direction.ASC, "jmbag"));

  public static SearchUsers sorted(Pageable page, String searchText) {
    return new SearchUsers(PageRequest.of(page.getPageNumber(), page.getPageSize(), page.getSortOr(SORT)), searchText);
  }
}
