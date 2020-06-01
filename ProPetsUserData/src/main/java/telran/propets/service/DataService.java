package telran.propets.service;

import telran.propets.dto.ResponseDto;

public interface DataService {
	
	ResponseDto getData(String login, boolean type);
	

}
