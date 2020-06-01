package telran.propets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.propets.dto.ResponseDto;
import telran.propets.service.DataServiceImpl;

@RestController
@RequestMapping("/{lang}/v1")
public class DataServiceController {
	
	@Autowired
	DataServiceImpl dataServiceImpl;
	
	@GetMapping("/{login:.*}")
	public ResponseDto getData(@PathVariable String login, @RequestParam boolean dataType, @RequestHeader("X-token") String token) {
		return dataServiceImpl.getData(login, dataType);
	}

}
