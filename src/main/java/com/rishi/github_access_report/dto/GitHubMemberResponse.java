package com.rishi.github_access_report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record GitHubMemberResponse(
        String login,
        @JsonProperty("avatar_url") String avatarUrl,
        Map<String, Boolean> permissions
) {
}
