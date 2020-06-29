package propets.userdata.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import propets.userdata.dto.ResponseDto;
import propets.userdata.dto.UserUpdateDto;
import propets.userdata.model.Post;
import propets.userdata.service.DataService;

@RestController
@RequestMapping("/{lang}/v1")
public class UserDataController {
	
	@Autowired
	DataService dataService;
	
	@GetMapping("/{login:.*}")
	public ResponseDto getData(@PathVariable String login, @RequestParam boolean dataType) {
		return dataService.getData(login, dataType);
	}
	
	@PutMapping("/updateuser")
	public ResponseDto updateUserPosts(@RequestBody UserUpdateDto userUpdateDto) {	
		return dataService.updateUserData(userUpdateDto);
	}
}
