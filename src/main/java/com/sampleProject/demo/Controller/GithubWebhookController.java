package com.sampleProject.demo.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sampleProject.demo.Service.SampleService;

@RestController
@RequestMapping("/github")
public class GithubWebhookController {

    private final SampleService slackService;

    public GithubWebhookController(SampleService slackService) {
        this.slackService = slackService;
    }

    @PostMapping("/webhook")
    public String handleGithubEvent(
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody Map<String, Object> payload) {

        switch (eventType) {

            case "push":
                return handlePush(payload);

            case "pull":
                return handlePR(payload);

            default:
                return "Event ignored: " + eventType;
        }
    }

    private String handlePush(Map<String, Object> payload) {

        String user = ((Map<String, Object>) payload.get("sender")).get("login").toString();
        String repo = ((Map<String, Object>) payload.get("repository")).get("name").toString();

        var commits = (java.util.List<Map<String, Object>>) payload.get("commits");
        String commitMsg = commits.isEmpty() ? "No commit" : commits.get(0).get("message").toString();

        String message = String.format(
                "*🚀 New Push*\n👤 *User:* %s\n📦 *Repo:* %s\n📝 *Commit:* %s",
                user, repo, commitMsg
        );

        slackService.sendColoredMessage(message, "success");

        return "Push handled";
    }

    private String handlePR(Map<String, Object> payload) {

        Map<String, Object> pr = (Map<String, Object>) payload.get("pull");

        String title = pr.get("title").toString();
        String action = payload.get("action").toString();
        String user = ((Map<String, Object>) payload.get("sender")).get("login").toString();

        String message = String.format(
                "*🔀 Pull Request %s*\n👤 *User:* %s\n📌 *Title:* %s",
                action.toUpperCase(), user, title
        );

        String type = action.equals("opened") ? "success" : "warning";

        slackService.sendColoredMessage(message, type);

        return "PR handled";
    }
}