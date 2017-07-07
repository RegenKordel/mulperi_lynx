package com.mulperi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mulperi.models.database.StoredRequirement;


public interface StoredRequirementRepository extends JpaRepository<StoredRequirement, Long> { 
	
	//Query creation: http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
	
	StoredRequirement findByModelNameAndRequirementId(String modelName, String requirementId);
}
