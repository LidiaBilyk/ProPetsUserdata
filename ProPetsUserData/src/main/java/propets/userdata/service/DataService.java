package propets.userdata.service;

import propets.userdata.dto.ResponseDto;
import propets.userdata.dto.UserUpdateDto;

public interface DataService {
	
	ResponseDto getData(String login, boolean dataType);
	ResponseDto updateUserData(UserUpdateDto userUpdateDto);	
}
