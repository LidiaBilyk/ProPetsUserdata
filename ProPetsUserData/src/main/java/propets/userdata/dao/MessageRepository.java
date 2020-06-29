package propets.userdata.dao;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import propets.userdata.model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
	
	Set<Message> findByUserLogin(String userLogin);

}
