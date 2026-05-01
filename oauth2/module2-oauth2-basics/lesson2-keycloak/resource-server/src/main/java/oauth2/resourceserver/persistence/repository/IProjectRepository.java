package oauth2.resourceserver.persistence.repository;

import oauth2.resourceserver.persistence.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IProjectRepository extends PagingAndSortingRepository<Project, Long>, CrudRepository<Project, Long> {
}
