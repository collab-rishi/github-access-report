package com.rishi.github_access_report.client;


import com.rishi.github_access_report.dto.GitHubMemberResponse;
import com.rishi.github_access_report.dto.GitHubRepoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<GitHubRepoResponse> fetchRepositories(String org, int page) {
        String uri = UriComponentsBuilder.fromPath(("/orgs/{org}/repos"))
                .queryParam("page", page)
                .queryParam("per_page", 100)
                .buildAndExpand(org)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }


    public List<GitHubMemberResponse> fetchCollaborators(String fullName, int page) {
        String uri = UriComponentsBuilder.fromPath(("/repos/{owner}/{repo}/collaborators"))
                .queryParam("page", page)
                .queryParam("per_page", 100)
                .buildAndExpand(fullName)
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}


