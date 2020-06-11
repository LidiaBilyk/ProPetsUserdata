package propets.userdata.service;

import propets.userdata.dto.ResponseDto;

public interface DataService {
	
	ResponseDto getData(String login, boolean type);
	

}
