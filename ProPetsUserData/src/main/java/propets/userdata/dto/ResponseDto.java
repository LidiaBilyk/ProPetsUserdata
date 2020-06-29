package propets.userdata.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseDto {
	
	Set<PostDto> lostFoundPosts;
	Set<MessageDto> otherPosts;

}
