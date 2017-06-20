package com.mulperi.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.submit.Requirement;
import com.mulperi.services.FormatTransformerService;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("submit")
public class SubmitController {

	private FormatTransformerService transform = new FormatTransformerService();
	
	@RequestMapping(value = "/simple", method = RequestMethod.POST)
    public String simpleIn(@RequestBody List<Requirement> requirements) {
		
        return transform.SimpleToKumbang("TestModel", requirements);
    }

}