package propets.userdata.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import propets.userdata.dto.LostFoundResponseDto;
import propets.userdata.dto.OtherResponseDto;
import propets.userdata.dto.ResponseDto;
import propets.userdata.exceptions.BadRequestException;
import propets.userdata.exceptions.ConflictException;

@Service
public class DataServiceImpl implements DataService {

	@Autowired
	DataConfiguration dataConfiguration;

// true = "Favorites", false = "Activities"
	@Override
	public ResponseDto getData(String login, boolean dataType) {		
		Set<LostFoundResponseDto> lostFoundPosts = null;
		Set<OtherResponseDto> otherPosts = null;
		if (dataType) {
			Map<String, Set<String>> dataFromAccount = getDataFromAccount(login);
			for (String key : dataFromAccount.keySet()) {
				if ("lostfound".equalsIgnoreCase(key)) {
					lostFoundPosts = responseFromLostFound(dataFromAccount.get(key));
					dataFromAccount.remove(key);
				} else {
					otherPosts = responseFromOtherServices(dataFromAccount);
				}
			} 
		}
		if (!dataType) {
			lostFoundPosts = responseFromLostFound(login);
			otherPosts = responseFromService(login, dataConfiguration.getDataMessageUri());
		}
		return ResponseDto.builder().lostFoundPosts(lostFoundPosts).otherPosts(otherPosts).build();
	}	


	private Set<OtherResponseDto> responseFromOtherServices(Map<String, Set<String>> dataFromAccount) {
		Set<OtherResponseDto> result = new TreeSet<>((e1, e2) -> e2.getDatePost().compareTo(e1.getDatePost()));
		for (String key : dataFromAccount.keySet()) {
			if ("message".equalsIgnoreCase(key)) {
				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataMessageUri()));
			}
//			if ("hotels".equalsIgnoreCase(key)) {
//				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataHotelsUri()));
//			}
//			if ("walking".equalsIgnoreCase(key)) {
//				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataWalkingUri()));
//			}
//			if ("fostering".equalsIgnoreCase(key)) {
//				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataFosteringUri()));
//			}
//			if ("vethelp".equalsIgnoreCase(key)) {
//				result.addAll(responseFromService(dataFromAccount.get(key), dataConfiguration.getDataVetHelpUri()));
//			}
		}
		return result;
	}

	private Set<OtherResponseDto> responseFromService(Set<String> postId, String dataServiceUri) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Set<OtherResponseDto>> responseEntity = null;
		try {
			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(postId, HttpMethod.POST,
					new URI(dataServiceUri));
			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<OtherResponseDto>>() {});
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {
			throw new BadRequestException();
		}
		return responseEntity.getBody();
	}
	
	private Set<OtherResponseDto> responseFromService(String login, String dataServiceUri) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Set<OtherResponseDto>> responseEntity = null;
		try {
			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(HttpMethod.POST,
					new URI(dataServiceUri.concat(login)));
			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<OtherResponseDto>>() {});
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {
			throw new BadRequestException();
		}
		return responseEntity.getBody();
	}

	private Set<LostFoundResponseDto> responseFromLostFound(Set<String> postId) {
		Set<LostFoundResponseDto> result = new TreeSet<>((e1, e2) -> e2.getDatePost().compareTo(e1.getDatePost()));
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Set<LostFoundResponseDto>> responseEntity = null;
		try {
			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(postId, HttpMethod.POST,
					new URI(dataConfiguration.getDataLostFoundUri()));
			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<LostFoundResponseDto>>() {});
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {
			throw new BadRequestException();
		}
		result.addAll(responseEntity.getBody());
		return result;
	}
	
	private Set<LostFoundResponseDto> responseFromLostFound(String login) {
		Set<LostFoundResponseDto> result = new TreeSet<>((e1, e2) -> e2.getDatePost().compareTo(e1.getDatePost()));
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Set<LostFoundResponseDto>> responseEntity = null;
		try {
			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(HttpMethod.POST,
					new URI(dataConfiguration.getDataLostFoundUri().concat(login)));
			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<LostFoundResponseDto>>() {});
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {
			throw new BadRequestException();
		}
		result.addAll(responseEntity.getBody());
		return result;
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
}
