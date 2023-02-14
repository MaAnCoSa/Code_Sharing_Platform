package platform.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import platform.Model.Code;

import java.util.List;
import java.util.Optional;

// Repository for all the code snippets.
@Repository
public interface CodeRepository extends CrudRepository<Code, String> {
    // To find a specific code snippet by its ID.
    Optional<Code> findById(String id);
    // To find all the code snippets that have no restrictions.
    List<Code> findAllByRestrictedTimeAndRestrictedView(boolean restrictedTime, boolean restrictedView);
}
