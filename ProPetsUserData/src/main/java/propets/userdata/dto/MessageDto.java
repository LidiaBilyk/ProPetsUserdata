package propets.userdata.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
public class MessageDto {
	String id;
	String userLogin;
    String username; 
    String avatar;
	LocalDateTime datePost;	
	String text;
	@Singular
	List<String> images;
}
