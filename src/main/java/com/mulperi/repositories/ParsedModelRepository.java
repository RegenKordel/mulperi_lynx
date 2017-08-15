package com.mulperi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mulperi.models.kumbang.ParsedModel;


public interface ParsedModelRepository extends JpaRepository<ParsedModel, Long> { 
	
	//Query creation: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	//TODO: consider overwriting the old model in case a newer one is submitted (though IDs should be unique)
	ParsedModel findFirstByModelName(String modelName);
}
