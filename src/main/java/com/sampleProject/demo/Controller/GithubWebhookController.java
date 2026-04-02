package com.sampleProject.demo.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
public class GithubWebhookController {

    @PostMapping("/webhook")
    public String handleGithubEvent(@RequestBody Map<String, Object> payload) {
        return "OK";
    }
}