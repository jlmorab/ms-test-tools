package com.jlmorab.ms.annotation;


import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mockito.Mockito;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("rawtypes")
public class AutoMockedJpaRepositoriesConfiguration implements ImportBeanDefinitionRegistrar {
	
	@Override
	public void registerBeanDefinitions( AnnotationMetadata metadata, BeanDefinitionRegistry registry ) {
		Map<String, Object> attributes = metadata.getAnnotationAttributes( EnableAutoMockedJpaRepositories.class.getName() );
		
		if (attributes == null)
		    throw new IllegalStateException("@EnableAutoMockedJpaRepositories must be present and correctly configured.");
		
		Set<String> packagesToScan = new HashSet<>();
	    
	    String value = (String) attributes.get("value");
	    if( value != null && !value.isEmpty() ) {
	        packagesToScan.add( value );
	    }//end if
	    
	    String[] basePackages = (String[]) attributes.get("basePackages");
	    if( basePackages != null && basePackages.length > 0 ) {
	        packagesToScan.addAll( Arrays.asList(basePackages) );
	    }//end if
	    
	    Class<?>[] basePackageClasses = (Class<?>[]) attributes.get("basePackageClasses");
	    if( basePackageClasses != null && basePackageClasses.length > 0 ) {
	        for (Class<?> cls : basePackageClasses) {
	            packagesToScan.add( cls.getPackage().getName() );
	        }//end for
	    }//end if
	    
	    if (packagesToScan.isEmpty()) {
	    	try {
	    		Method valueMethod = EnableAutoMockedJpaRepositories.class.getMethod("value");
		    	String defaultValue = (String) valueMethod.getDefaultValue();
		        packagesToScan.add(defaultValue);
	    	} catch( Exception e ) {
	    		log.warn(e.getMessage(), e);
	    	}//end try
	    }//end if
	    log.info("Scanning for JPA repositories in packages: {}", packagesToScan);
	    
	    Set<Class<? extends JpaRepository>> allRepositoryInterfaces = new HashSet<>();
        
	    for( String packageToScan : packagesToScan ) {
            Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                    .forPackage(packageToScan)
                    .setScanners(Scanners.SubTypes.filterResultsBy(c -> true))
            );
            
            Set<Class<? extends JpaRepository>> repositoryInterfaces = reflections.getSubTypesOf(JpaRepository.class);
            
            allRepositoryInterfaces.addAll(repositoryInterfaces);
            log.info("Found {} JPA repository interfaces in package {}", repositoryInterfaces.size(), packageToScan);
        }//end for
        
        log.info("Total JPA repository interfaces found: {}", allRepositoryInterfaces.size());
        
        for( Class<?> repoClass : allRepositoryInterfaces ) {
            log.debug("Processing JPA repository: {}", repoClass.getName());
            
            GenericBeanDefinition mockDefinition = new GenericBeanDefinition();
            mockDefinition.setBeanClass(repoClass);
            mockDefinition.setInstanceSupplier(() -> Mockito.mock(repoClass));
            
            String beanName = Introspector.decapitalize(repoClass.getSimpleName());
            registry.registerBeanDefinition(beanName, mockDefinition);
            log.info("Registered mock for JPA repository: {}", beanName);
        }//end for
	}//end registerBeanDefinitions()
	
}
