package hr.fer.axon.users.queries;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public record FindAllUsers(Pageable page) {
  public static final Sort SORT = Sort.by(Direction.ASC, "lastName")
      .and(Sort.by(Direction.ASC, "firstName"))
      .and(Sort.by(Direction.ASC, "jmbag"));

  public static FindAllUsers max_10_000() {
    return new FindAllUsers(PageRequest.of(0, 10_000, SORT));
  }

  public static FindAllUsers sorted(Pageable pageable) {
    return new FindAllUsers(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(SORT)));
  }
}
