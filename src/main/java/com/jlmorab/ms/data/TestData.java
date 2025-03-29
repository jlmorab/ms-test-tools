package com.jlmorab.ms.data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {
	
	private static Random random = new Random();

	public static int getRandom(int min, int max) {
		return random.nextInt( max ) + min;
	}//end getRandom()
	
	public static String getRandomText() {
		int random = TestData.getRandom(1, 1000); 
		return getRandomText( random );
	}//end getRandomText()
	
	public static String getRandomText( int random ) { 
		return String.format("text%d", random);
	}//end getRandomText()
	
	public static LocalDate getRandomDate() {
		int random = TestData.getRandom(1, 3650);
		return LocalDate.now().minusDays(random);
	}//end getRandomDate()
	
	public static String getRandomObject() {
		Map<String, Object> object = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		
		int random = TestData.getRandom(1, 1000);
		String text = String.format("data%d", random);
		
		object.put("id", random);
		object.put("data", text);
		
		try { 
			return mapper.writeValueAsString(object);
		} catch( Exception e ) {
			return null;
		}//end try
	}//end getRandomObject()
	
}
