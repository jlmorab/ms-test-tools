package com.jlmorab.ms.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.fasterxml.jackson.databind.ObjectMapper;

class TestDataTest {

	@Test
	void getRandom() {
		int actual = TestData.getRandom(1, 1000);
		assertNotEquals( 0, actual );
	}//end getRandom()
	
	@Test
	void getRandomText_withoutReference() {
		String actual = TestData.getRandomText();
		
		assertNotNull( actual );
		assertFalse( actual.isBlank() );
		assertThat( actual )
			.isNotNull()
			.isNotEmpty()
			.contains("text");
	}//end getRandomText_withoutReference
	
	@Test
	void getRandomText_withReference() {
		int number = 16;
		String expected = String.format("text%d", number);
		String actual = TestData.getRandomText(number);
		
		assertEquals( expected, actual );
	}//end getRandomText_withReference()
	
	@Test
	void getRandomDate() {
		LocalDate actual = TestData.getRandomDate();
		
		assertNotNull( actual );
		assertTrue( actual.isBefore( LocalDate.now() ) );
	}//end getRandomDate()
	
	@Test
	void getRandomJsonObject() {
		String actual = TestData.getRandomJsonObject();
		
		assertNotNull( actual );
		assertNotEquals("{}", actual);
	}//end getRandomJsonObject()
	
	@Test
	void getRandomJsonObject_whenThrowException() {
		try (MockedConstruction<ObjectMapper> mockedMapper = mockConstruction(ObjectMapper.class, (mock, context) -> {
				when(mock.writeValueAsString(any())).thenThrow(new RuntimeException("Test exception"));
			});
        ) {
			String actual = TestData.getRandomJsonObject();
			assertEquals("{}", actual);
        }//end try()
	}//end getRandomJsonObject_whenThrowException()

}
