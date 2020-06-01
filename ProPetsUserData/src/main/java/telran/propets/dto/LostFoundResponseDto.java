package telran.propets.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import telran.propets.model.Location;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LostFoundResponseDto {
	String id;
	String userLogin;
	LocalDateTime datePost;
	String type;
	@Singular
	List<String> tags;
	@Singular
	List<String> photos;
	Location location;
	int radius;

}
