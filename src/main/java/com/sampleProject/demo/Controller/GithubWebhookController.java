package com.sampleProject.demo.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sampleProject.demo.Entity.Author;
import com.sampleProject.demo.Entity.CommitEntity;
import com.sampleProject.demo.Repo.AuthorRepository;
import com.sampleProject.demo.Repo.CommitRepository;
import com.sampleProject.demo.Service.SampleService;

@RestController
@RequestMapping("/github")
public class GithubWebhookController {

    private final SampleService slackService;
    private final AuthorRepository authorRepo;
    private final CommitRepository commitRepo;

    public GithubWebhookController(AuthorRepository authorRepo, CommitRepository commitRepo,
            SampleService slackService) {
    	   this.authorRepo = authorRepo;
           this.commitRepo = commitRepo;
           this.slackService = slackService;
    }
    
    

    @PostMapping("/webhook")
    public String handleGithubEvent(
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody Map<String, Object> payload) {

        switch (eventType) {

            case "push":
                return handlePush(payload);

            case "pull_request":
                return handlePR(payload);

            default:
                return "Event ignored: " + eventType;
        }
    }

    private String handlePush(Map<String, Object> payload) {

        // 👤 Extract author (GitHub user)
        String username = ((Map<String, Object>) payload.get("sender"))
                .get("login").toString();

        // 📦 Repo name
        String repo = ((Map<String, Object>) payload.get("repository"))
                .get("name").toString();

        // 📦 Commit list
        var commits = (java.util.List<Map<String, Object>>) payload.get("commits");

        // 🔍 Find or create author
        Author author = authorRepo.findByUsername(username)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setUsername(username);
                    return authorRepo.save(a);
                });

        StringBuilder commitMessages = new StringBuilder();

        // 🔥 LOOP ALL COMMITS
        for (Map<String, Object> commitData : commits) {

            // 📝 message
            String message = commitData.get("message").toString();

            // 🆔 commit id (SHA)
            String commitId = commitData.get("id").toString();

            // 👤 commit author (inside commit object)
            Map<String, Object> commitAuthor =
                    (Map<String, Object>) commitData.get("author");

            String commitAuthorName = commitAuthor.get("name").toString();

            // 💾 Save into DB
            CommitEntity commit = new CommitEntity();
            commit.setMessage(message);
            commit.setCommitId(commitId);
            commit.setAuthor(author);

            commitRepo.save(commit);

            // 📤 Build Slack message
            commitMessages.append("• ")
                    .append(message)
                    .append(" (").append(commitAuthorName).append(")\n");
        }

        
        // 🔥 Final Slack Message
        String slackMsg = String.format(
                "*🚀 New Push*\n👤 *User:* %s\n📦 *Repo:* %s\n📝 *Commits:*\n%s",
                username, repo, commitMessages.toString()
        );

        slackService.sendColoredMessage(slackMsg, "success");

        return "Push handled";
    }

    private String handlePR(Map<String, Object> payload) {

        Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");

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