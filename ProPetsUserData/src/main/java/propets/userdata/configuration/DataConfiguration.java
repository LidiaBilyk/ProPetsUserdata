package propets.userdata.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
@RefreshScope
public class DataConfiguration {
	
	@Value("${dataAccountUri}")
	String dataAccountUri;
	@Value("${dataLostFoundUri}")
	String dataLostFoundUri;
	@Value("${dataMessageUri}")
	String dataMessageUri;
//	@Value("${dataHotelsUri}")
//	String dataHotelsUri;
//	@Value("${dataWalkingUri}")
//	String dataWalkingUri;
//	@Value("${dataFosteringUri}")
//	String dataFosteringUri;
//	@Value("${dataVetHelpUri}")
//	String dataVetHelpUri;
	@Value("${checkJwtUri}")
	String checkJwtUri;
}
