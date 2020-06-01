package telran.propets.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter

public class Location {
	String country;
    String city;
    String street;
    int building;
	double longitude;
	double latitude;
	

}
