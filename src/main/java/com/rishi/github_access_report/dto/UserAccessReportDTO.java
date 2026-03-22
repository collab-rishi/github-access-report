package com.rishi.github_access_report.dto;

import java.util.List;

public record UserAccessReportDTO(
        String username,
        String avatarUrl,
        List<RepositoryAccessDetails> repositories
) {

    public record RepositoryAccessDetails(
            String repositoryName,
            String role
    ){}
}
