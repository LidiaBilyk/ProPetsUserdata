package propets.userdata.dao;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import propets.userdata.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {
	
	Set<Post> findByUserLogin(String userLogin);

}
