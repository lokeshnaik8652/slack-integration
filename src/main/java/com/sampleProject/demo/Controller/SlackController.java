package com.sampleProject.demo.Controller;


import org.springframework.web.bind.annotation.*;

import com.sampleProject.demo.Service.SampleService;

@RestController
@RequestMapping("/api/slack")
public class SlackController {

    private final SampleService slackService;

    public SlackController(SampleService slackService) {
        this.slackService = slackService;
    }

    // 🔹 API to send message
    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        return slackService.sendMessage(message);
    }
    
    @PostMapping("/send-color/{text}/{type}")
    public String sendColoredMessage(@PathVariable("text") String text,@PathVariable("type") String type) {
        return slackService.sendColoredMessage(text,type);
    }
}
