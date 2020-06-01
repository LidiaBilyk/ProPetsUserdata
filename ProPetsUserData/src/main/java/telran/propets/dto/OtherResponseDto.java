package telran.propets.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OtherResponseDto {
	String id;
	String userLogin;
    String username; 
    String avatar;
	LocalDateTime datePost;	
	String text;
	@Singular
	List<String> images;

}
