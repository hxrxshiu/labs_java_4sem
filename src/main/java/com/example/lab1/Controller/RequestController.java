package com.example.lab1.Controller;

import com.example.lab1.Service.RequestCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private final RequestCounter requestCounter;

    @Autowired
    public RequestController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping("/counter")
    public String getRequestCount() {
        return "Total requests: " + requestCounter.getCount();
    }
}

