package br.com.psoa.smbox;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.com.psoa.smbox.model.Post;

@RepositoryRestResource(collectionResourceRel = "api", path = "api")
public interface PostRepository extends JpaRepository<Post, Integer> {
	
	Optional<Post> findById(Long id);
	
	void deleteById(Long id);
}
