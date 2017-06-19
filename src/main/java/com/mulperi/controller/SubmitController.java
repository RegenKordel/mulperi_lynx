package com.mulperi.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class SubmitController {

	@RequestMapping(value = "/submitModel", method = RequestMethod.POST)
    @ResponseBody
    public String home() {
		
        return "TBD";
    }

}