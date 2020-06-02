package telran.propets.service;

import java.net.URI;
import java.net.URISyntaxException;
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

import telran.propets.configuration.DataConfiguration;
import telran.propets.dto.LostFoundResponseDto;
import telran.propets.dto.OtherResponseDto;
import telran.propets.dto.ResponseDto;
import telran.propets.exceptions.BadRequestException;
import telran.propets.exceptions.ConflictException;

@Service
public class DataServiceImpl implements DataService {

	@Autowired
	DataConfiguration dataConfiguration;

// true = "Favorites", false = "Activities"
	@Override
	public ResponseDto getData(String login, boolean dataType) {
		Map<String, Set<String>> dataFromAccount = getDataFromAccount(login, dataType);
		Set<LostFoundResponseDto> lostFoundPosts = null;
		Set<OtherResponseDto> otherPosts = null;
		for (String key : dataFromAccount.keySet()) {
			if ("lostfound".equalsIgnoreCase(key)) {
				lostFoundPosts = responseFromLostFound(dataFromAccount.get(key));
				dataFromAccount.remove(key);
			} else {
				otherPosts = responseFromOtherServices(dataFromAccount);
			}
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
			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(postId, HttpMethod.POST, new URI(dataServiceUri));
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
			RequestEntity<Set<String>> requestEntity = new RequestEntity<Set<String>>(postId, HttpMethod.POST,new URI(dataConfiguration.getDataLostFoundUri()));
			responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<LostFoundResponseDto>>() {});
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {
			throw new BadRequestException();
		}
		result.addAll(responseEntity.getBody());
		return result;
	}

	private Map<String, Set<String>> getDataFromAccount(String login, boolean dataType) {
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dataConfiguration.getDataAccountUri()).queryParam("dataType", dataType);
		ResponseEntity<Map<String, Set<String>>> responseEntity = null;
		try {
			RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
					builder.buildAndExpand(login).toUri());
			responseEntity = restTemplate.exchange(requestEntity,
					new ParameterizedTypeReference<Map<String, Set<String>>>() {
					});
		} catch (RestClientException e) {
			throw new ConflictException();
		}
		return responseEntity.getBody();
	}
}
