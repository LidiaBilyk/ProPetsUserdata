package propets.userdata.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import propets.userdata.configuration.DataConfiguration;
import propets.userdata.dao.MessageRepository;
import propets.userdata.dao.PostRepository;
import propets.userdata.dto.LostFoundResponseDto;
import propets.userdata.dto.MessageDto;
import propets.userdata.dto.OtherResponseDto;
import propets.userdata.dto.PostDto;
import propets.userdata.dto.ResponseDto;
import propets.userdata.dto.UserUpdateDto;
import propets.userdata.exceptions.BadRequestException;
import propets.userdata.exceptions.ConflictException;
import propets.userdata.model.Message;
import propets.userdata.model.Post;

@Service
public class DataServiceImpl implements DataService {

	@Autowired
	DataConfiguration dataConfiguration;
	@Autowired
	PostRepository postRepository;
	@Autowired
	MessageRepository messageRepository;


// true = "Favorites", false = "Activities"
	@Override
	public ResponseDto getData(String login, boolean dataType) {		
		Set<PostDto> lostFoundPosts = null;
		Set<MessageDto> otherPosts = null;
		if (dataType) {
			Map<String, Set<String>> dataFromAccount = getDataFromAccount(login);
			for (String key : dataFromAccount.keySet()) {
				if ("lostfound".equalsIgnoreCase(key)) {
//					lostFoundPosts = responseFromLostFound(dataFromAccount.get(key));
					lostFoundPosts = StreamSupport.stream(postRepository.findAllById(dataFromAccount.get(key)).spliterator(), false)
							.map(p -> postToPostDto(p))		
							.collect(Collectors.toSet());
				} 
				if ("message".equalsIgnoreCase(key)) {
					otherPosts = StreamSupport.stream(messageRepository.findAllById(dataFromAccount.get(key)).spliterator(), false)
							.map(m -> messageToMessageDto(m))		
							.collect(Collectors.toSet());
				}
			} 
		}
		if (!dataType) {
			lostFoundPosts = postRepository.findByUserLogin(login).stream().map(p -> postToPostDto(p)).collect(Collectors.toSet());
			otherPosts = messageRepository.findByUserLogin(login).stream().map(m -> messageToMessageDto(m)).collect(Collectors.toSet());
		}
		return ResponseDto.builder().lostFoundPosts(lostFoundPosts).otherPosts(otherPosts).build();
	}	


	private Map<String, Set<String>> getDataFromAccount(String login) {
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dataConfiguration.getDataAccountUri());
		ResponseEntity<Map<String, Set<String>>> responseEntity = null;
		try {
			RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, builder.buildAndExpand(login).toUri());
			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Map<String, Set<String>>>() {});
		} catch (RestClientException e) {
			throw new ConflictException();
		}
		return responseEntity.getBody();
	}
	
	private PostDto postToPostDto(Post post) {		
		List<String> postList = post.getPhotos();
		List<String> tagList = post.getTags();
		return PostDto.builder()
				.id(post.getId())
				.typePost(post.isTypePost())
				.userLogin(post.getUserLogin())
				.username(post.getUsername())
				.avatar(post.getAvatar())
				.datePost(post.getDatePost())
				.type(post.getType())
				.sex(post.getSex())
				.breed(post.getBreed())
				.address(post.getAddress())	
				.location(post.getLocation())
				.tags(tagList)
				.photos(postList)
				.build();
	}
	
	private MessageDto messageToMessageDto(Message message) {		
		return MessageDto.builder()
				.id(message.getId())
				.userLogin(message.getUserLogin())
				.username(message.getUsername())
				.avatar(message.getAvatar())
				.text(message.getText())
				.datePost(message.getDatePost())
				.images(message.getImages())
				.build();
	}

	@Override
	public ResponseDto updateUserData(UserUpdateDto userUpdateDto) {
		Set<PostDto> lostFoundPosts = postRepository.findByUserLogin(userUpdateDto.getLogin()).stream()
				.map(p -> updateUser(userUpdateDto, p))
				.map(p -> postRepository.save(p))
				.map(p -> postToPostDto(p))
				.collect(Collectors.toSet());
		Set<MessageDto> otherPosts = messageRepository.findByUserLogin(userUpdateDto.getLogin()).stream()
				.map(m -> updateUser(userUpdateDto, m))
				.map(m -> messageRepository.save(m))
				.map(m -> messageToMessageDto(m))
				.collect(Collectors.toSet());
		
		return ResponseDto.builder().lostFoundPosts(lostFoundPosts).otherPosts(otherPosts).build();
	}
	
	private Post updateUser(UserUpdateDto userUpdateDto, Post post) { 		
		post.setUsername(userUpdateDto.getName());		
		post.setAvatar(userUpdateDto.getAvatar());		
	return post;
}
		
	private Message updateUser(UserUpdateDto userUpdateDto, Message message) { 		
		message.setUsername(userUpdateDto.getName());		
		message.setAvatar(userUpdateDto.getAvatar());		
	return message;
}
	

//	private Set<OtherResponseDto> responseFromOtherServices(Map<String, Set<String>> dataFromAccount) {
//		Set<OtherResponseDto> result = new TreeSet<>((e1, e2) -> e2.getDatePost().compareTo(e1.getDatePost()));
//		for (String key : dataFromAccount.keySet()) {
//			if ("message".equalsIgnoreCase(key)) {
//				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataMessageUri()));
//			}
////			if ("hotels".equalsIgnoreCase(key)) {
////				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataHotelsUri()));
////			}
////			if ("walking".equalsIgnoreCase(key)) {
////				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataWalkingUri()));
////			}
////			if ("fostering".equalsIgnoreCase(key)) {
////				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataFosteringUri()));
////			}
////			if ("vethelp".equalsIgnoreCase(key)) {
////				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataVetHelpUri()));
////			}
//		}
//		return result;
//	}

//	private Set<OtherResponseDto> responseFromService(Set<String> postId, String dataServiceUri) {
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<Set<OtherResponseDto>> responseEntity = null;
//		try {
//			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(postId, HttpMethod.POST,
//					new URI(dataServiceUri));
//			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<OtherResponseDto>>() {});
//		} catch (RestClientException e) {
//			throw new ConflictException();
//		} catch (URISyntaxException e) {
//			throw new BadRequestException();
//		}
//		return responseEntity.getBody();
//	}
	
//	private Set<OtherResponseDto> responseFromService(String login, String dataServiceUri) {
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<Set<OtherResponseDto>> responseEntity = null;
//		try {
//			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(HttpMethod.GET,
//					new URI(dataServiceUri.concat("/").concat(login)));
//			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<OtherResponseDto>>() {});
//		} catch (RestClientException e) {
//			throw new ConflictException();
//		} catch (URISyntaxException e) {
//			throw new BadRequestException();
//		}
//		return responseEntity.getBody();
//	}

//	private Set<LostFoundResponseDto> responseFromLostFound(Set<String> postId) {
//		Set<LostFoundResponseDto> result = new TreeSet<>((e1, e2) -> e2.getDatePost().compareTo(e1.getDatePost()));
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<Set<LostFoundResponseDto>> responseEntity = null;
//		try {
//			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(postId, HttpMethod.POST,
//					new URI(dataConfiguration.getDataLostFoundUri()));
//			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<LostFoundResponseDto>>() {});
//		} catch (RestClientException e) {
//			throw new ConflictException();
//		} catch (URISyntaxException e) {
//			throw new BadRequestException();
//		}
//		result.addAll(responseEntity.getBody());
//		return result;
//	}
	
//	private Set<LostFoundResponseDto> responseFromLostFound(String login) {
//		Set<LostFoundResponseDto> result = new TreeSet<>((e1, e2) -> e2.getDatePost().compareTo(e1.getDatePost()));
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<Set<LostFoundResponseDto>> responseEntity = null;
//		try {
//			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(HttpMethod.GET,
//					new URI(dataConfiguration.getDataLostFoundUri().concat("/").concat(login)));
//			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<LostFoundResponseDto>>() {});
//		} catch (RestClientException e) {
//			throw new ConflictException();
//		} catch (URISyntaxException e) {
//			throw new BadRequestException();
//		}
//		result.addAll(responseEntity.getBody());
//		return result;
//	}

}
