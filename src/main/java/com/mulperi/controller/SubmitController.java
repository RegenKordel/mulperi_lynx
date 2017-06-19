package com.mulperi.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.ParsedModel;
import com.mulperi.models.submit.Relationship;
import com.mulperi.models.submit.Requirement;
import com.mulperi.services.FormatTransformerService;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("submit")
public class SubmitController {

	private FormatTransformerService transform = new FormatTransformerService();
	
	@RequestMapping(value = "/simple", method = RequestMethod.POST)
    public Requirement simpleIn(@RequestBody Requirement req) {
		ParsedModel pm = transform.SimpleToKumbang(req);
		
        return req;
    }
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
    public Requirement test() { //@RequestParam String content
		Requirement koe = new Requirement();
		koe.setId("Koe");
		Relationship yksi = new Relationship();
		yksi.setId("koe");
		yksi.setType("isa");
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.add(yksi);
		koe.setRelationships(relationships);
        return koe;
    }

}