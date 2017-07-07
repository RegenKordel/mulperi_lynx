package com.mulperi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mulperi.models.kumbang.ParsedModel;


public interface ParsedModelRepository extends JpaRepository<ParsedModel, Long> { 
	
	//Query creation: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	
	//StoredRequirement findByModelNameAndRequirementId(String modelName, String requirementId);
}
