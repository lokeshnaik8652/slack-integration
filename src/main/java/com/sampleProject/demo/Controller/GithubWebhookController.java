package com.sampleProject.demo.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public String handleGithubEvent(@RequestBody Map<String, Object> payload) {

        // 👤 user
        Map<String, Object> sender = (Map<String, Object>) payload.get("sender");
        String user = sender != null ? sender.get("login").toString() : "Unknown";

        // 📦 repo
        Map<String, Object> repo = (Map<String, Object>) payload.get("repository");
        String repoName = repo != null ? repo.get("name").toString() : "Unknown";

        // 📝 commit message
        String commitMsg = "No commit";
        if (payload.containsKey("commits")) {
            var commits = (java.util.List<Map<String, Object>>) payload.get("commits");
            if (!commits.isEmpty()) {
                commitMsg = commits.get(0).get("message").toString();
            }
        }

        // 🔥 Slack Message
        String message = String.format(
                "*🚀 New GitHub Push*\n👤 *User:* %s\n📦 *Repo:* %s\n📝 *Commit:* %s",
                user, repoName, commitMsg
        );

        slackService.sendColoredMessage(message, "success");

        return "OK";
    }
}