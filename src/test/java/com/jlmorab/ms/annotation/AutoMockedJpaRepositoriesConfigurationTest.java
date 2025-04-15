package com.jlmorab.ms.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
class AutoMockedJpaRepositoriesConfigurationTest {

	static final String 	TEST_PACKAGE 	= "com.jlmorab.ms";
	
	ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	AutoMockedJpaRepositoriesConfiguration config;
	static String defaultValue;
	
	@Mock
	AnnotationMetadata metadata;
	
	@Mock
	BeanDefinitionRegistry registry;
	
	static interface FakeRepository extends JpaRepository<Object, Integer> {}
	
	@BeforeAll
	static void beforeAll() throws Exception {
		Method valueMethod = EnableAutoMockedJpaRepositories.class.getMethod("value");
    	defaultValue = (String) valueMethod.getDefaultValue();
	}//end beforeAll()
	
	@BeforeEach
	void setUp() {
		reset( metadata, registry );
		config = new AutoMockedJpaRepositoriesConfiguration();
		System.setOut(new PrintStream(outContent));
	}//end setUp()
	
	@AfterEach
	void tearDown() {
		System.setOut(null);
	}//end tearDown()
	
	@Test
	void registerBeanDefinitions_whenMetadataDontHaveAnnotation() {
		when( metadata.getAnnotationAttributes( any() ) ).thenReturn( null );
		
		assertThrows( IllegalStateException.class, () -> {
			config.registerBeanDefinitions( metadata, registry );
		});
	}//end registerBeanDefinitions_whenMetadataDontHaveAnnotation()
	
	@Test
	void registerBeanDefinitions_whenValueIsDefault() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("value", "");
        attributes.put("basePackages", new String[0]);
        attributes.put("basePackageClasses", new Class[0]);
		
        when( metadata.getAnnotationAttributes( any() ) ).thenReturn( attributes );
			
		try( MockedConstruction<Reflections> mockedConstructor = Mockito.mockConstruction(Reflections.class, (mock, context) -> {
			Set<Class<?>> repositories = Set.of(FakeRepository.class);
			when( mock.getSubTypesOf( any()) ).thenReturn( repositories );
			when(mock.getConfiguration()).thenReturn( new ConfigurationBuilder() );
		})) {
			config.registerBeanDefinitions( metadata, registry );
			
			assertThat( outContent.toString() ).contains(
					String.format("Scanning for JPA repositories in packages: [%s]", defaultValue) );
		}//end try
	}//end registerBeanDefinitions_whenValueIsDefault()
	
	@Test
	void registerBeanDefinitions_withValue() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("value", TEST_PACKAGE);
		
		when( metadata.getAnnotationAttributes( any() ) ).thenReturn( attributes );
		
		try( MockedConstruction<Reflections> mockedConstructor = Mockito.mockConstruction(Reflections.class, (mock, context) -> {
			Set<Class<?>> repositories = Set.of(FakeRepository.class);
			when( mock.getSubTypesOf( any()) ).thenReturn( repositories );
			when(mock.getConfiguration()).thenReturn( new ConfigurationBuilder() );
		})) {
			config.registerBeanDefinitions( metadata, registry );
			
			verify( registry, times(1) ).registerBeanDefinition( any(), any() );
		}//end try
	}//end registerBeanDefinitions_withValue()
	
	@Test
	void registerBeanDefinitions_withPackages() {
		Map<String, Object> attributes = new HashMap<>();
		String[] packages = {TEST_PACKAGE, "com.other.package"};
        attributes.put("basePackages", packages);
		
		when( metadata.getAnnotationAttributes( any() ) ).thenReturn( attributes );
		
		try( MockedConstruction<Reflections> mockedConstructor = Mockito.mockConstruction(Reflections.class, (mock, context) -> {
			Set<Class<?>> repositories = Set.of(FakeRepository.class);
			when( mock.getSubTypesOf( any()) ).thenReturn( repositories );
			when(mock.getConfiguration()).thenReturn( new ConfigurationBuilder() );
		})) {
			config.registerBeanDefinitions( metadata, registry );
			
			Stream.of(packages).forEach( pkg -> {
				assertThat( outContent.toString() ).contains(
						String.format("JPA repository interfaces in package %s", pkg) );
			});
		}//end try
	}//end registerBeanDefinitions_withPackages()
	
	@Test
	void registerBeanDefinitions_withClasses() {
		Map<String, Object> attributes = new HashMap<>();
		Class[] classes = {AutoMockedJpaRepositoriesConfigurationTest.class, AutoMockedJpaRepositoriesConfigurationTest.FakeRepository.class};
		attributes.put("basePackageClasses", classes);
		
		when( metadata.getAnnotationAttributes( any() ) ).thenReturn( attributes );
		
		try( MockedConstruction<Reflections> mockedConstructor = Mockito.mockConstruction(Reflections.class, (mock, context) -> {
			Set<Class<?>> repositories = Set.of(FakeRepository.class);
			when( mock.getSubTypesOf( any()) ).thenReturn( repositories );
			when(mock.getConfiguration()).thenReturn( new ConfigurationBuilder() );
		})) {
			config.registerBeanDefinitions( metadata, registry );
			
			Stream.of(classes).forEach( clazz -> {
				assertThat( outContent.toString() ).contains(
						String.format("JPA repository interfaces in package %s", clazz.getPackage().getName()) );
			});
		}//end try
	}//end registerBeanDefinitions_withClasses()

}
