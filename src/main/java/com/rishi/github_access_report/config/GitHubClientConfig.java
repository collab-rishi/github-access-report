package com.rishi.github_access_report.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GitHubClientConfig {

    @Bean
    public RestClient githubRestClient(GitHubProperties props) {
        return RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("Authorization", "Bearer" + props.getToken())
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }


}
