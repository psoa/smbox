package br.com.psoa.smbox;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.psoa.smbox.model.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
	
	Optional<Post> findById(Long id);
	
	void deleteById(Long id);
}
