package br.com.psoa.smbox;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.com.psoa.smbox.model.Category;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findById(Long id);

	void deleteById(Long id);
}
