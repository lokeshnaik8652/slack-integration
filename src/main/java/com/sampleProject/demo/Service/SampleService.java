package com.sampleProject.demo.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sampleProject.demo.Controller.SlackResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class SampleService {


    
    @Value("${slack.token}")
    private String slackToken;
    
    @Value("${slack.channel}")
    private String channel;

    public String sendMessage(String text) {

        String url = "https://slack.com/api/chat.postMessage";

        
        
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String messageqq = String.format(
                "*🚀 New Lead Created*\n👤 *Name:* %s\n📧 *Email:* %s\n📞 *Phone:* %s",
                "Lokesh", "test@gmail.com", "9381470252"
        );

        Map<String, Object> body = new HashMap<>();
        body.put("channel", channel);
        body.put("text", messageqq);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<SlackResponse> response =
                restTemplate.postForEntity(url, request, SlackResponse.class);

        return response.getBody().message.text;
    }
    
    public String sendColoredMessage(String text, String type) {

        String url = "https://slack.com/api/chat.postMessage";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(slackToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String color = "good"; // default

        if ("error".equalsIgnoreCase(type)) {
            color = "danger";
        } else if ("warning".equalsIgnoreCase(type)) {
            color = "warning";
        }

        Map<String, Object> attachment = new HashMap<>();
        attachment.put("color", color);
        attachment.put("text", text);

        Map<String, Object> body = new HashMap<>();
        body.put("channel", channel);
        body.put("attachments", new Object[]{attachment});

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }
}