package com.rishi.github_access_report.service;


import com.rishi.github_access_report.client.GitHubClient;
import com.rishi.github_access_report.dto.GitHubMemberResponse;
import com.rishi.github_access_report.dto.GitHubRepoResponse;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {

    private final GitHubClient gitHubClient;

    public ReportService(GitHubClient gitHubClient){
        this.gitHubClient = gitHubClient;
    }

    public List<UserRepoMapping> generateReport(String org) {

            List<GitHubRepoResponse> repos = gitHubClient.fetchRepositories(org, 1);


            List<CompletableFuture<List<UserRepoMapping>>> futures = repos.stream()
                    .map(repo -> CompletableFuture.supplyAsync(() -> {
                        List<GitHubMemberResponse> collaborators = gitHubClient.fetchCollaborators(repo.fullName(), 1);

                        return collaborators.stream()
                                .map(c -> new UserRepoMapping(c.login(), c.avatarUrl(), repo.name(), determineRole(c.permissions())))
                                .toList();
                    }))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .toList();
    }

    private String determineRole(java.util.Map<String, Boolean> permissions) {
        if(Boolean.TRUE.equals(permissions.get("admin"))) return "ADMIN";
        if (Boolean.TRUE.equals(permissions.get("push"))) return "WRITE";
        return "READ";
    }

    public record UserRepoMapping(String username, String avatarUrl, String RepoName, String role){}


}
