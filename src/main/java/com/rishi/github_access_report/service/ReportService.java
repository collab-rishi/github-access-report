package com.rishi.github_access_report.service;


import com.rishi.github_access_report.client.GitHubClient;
import com.rishi.github_access_report.dto.GitHubMemberResponse;
import com.rishi.github_access_report.dto.GitHubRepoResponse;
import com.rishi.github_access_report.dto.UserAccessReportDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
                                .map(c -> new UserRepoMapping(
                                        c.login(),
                                        c.avatarUrl(),
                                        repo.name(),
                                        determineRole(c.permissions())))
                                .toList();
                    }))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .toList();
    }

    public List<UserAccessReportDTO> getAggregatedReport(String org) {

        List<UserRepoMapping> flatMappings = generateReport(org);

        return flatMappings.stream()
                .collect(Collectors.groupingBy(
                       UserRepoMapping::username,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            String avatarUrl = list.get(0).avatarUrl();
                            List<UserAccessReportDTO.RepositoryAccessDetails> repos = list.stream()
                                    .map(m -> new UserAccessReportDTO.RepositoryAccessDetails(m.RepoName(), m.role()))
                                    .toList();
                            return new UserAccessReportDTO(list.get(0).username(), avatarUrl, repos);
                        })
                ))
                .values()
                .stream()
                .toList();
    }


    private String determineRole(java.util.Map<String, Boolean> permissions) {
        if(Boolean.TRUE.equals(permissions.get("admin"))) return "ADMIN";
        if (Boolean.TRUE.equals(permissions.get("push"))) return "WRITE";
        return "READ";
    }

    public record UserRepoMapping(String username, String avatarUrl, String RepoName, String role){}


}
