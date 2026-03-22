# GitHub Access Report Service

A high-performance Spring Boot service designed to aggregate user access permissions across all repositories within a GitHub Organization.

## 🚀 Features
- **Parallel Processing:** Uses `CompletableFuture` to fetch collaborator data for 100+ repositories in parallel, meeting high-scale requirements.
- **Data Aggregation:** Puts "User" at the center, mapping every repository they can access along with their highest permission level.
- **Secure Auth:** Configured to use GitHub Personal Access Tokens (PAT).
- **Clean Architecture:** Separation of concerns between Clients, Services, and Controllers.

## 1. How to Run the Project

### Prerequisites

- Java  17
- Maven
- A GitHub Personal Access Token (PAT)

### Steps
1. Clone the repository:

   ```
   git clone <your-repo-url>
   cd github-access-report
   ```
   
2. Configure Environment Variables:
   Create a ```.env``` file in the root directory (see the Authentication section below).

3. Build and Run:
     ```
    mvn clean install
    mvn spring-boot:run
   ```


## 2. How Authentication is Configured

The application authenticates with the GitHub REST API using a Personal Access Token (PAT).

### Setup

1. Create a .env file in the root folder.
<br><br>
2. Add your token to the file:
    ```
   GITHUB_TOKEN=your_secret_token_here
   ```
   <br><br>
3. **Technical Note**: The application uses spring-boot-configuration-processor to map this variable into a GitHubProperties bean, which is then injected into a RestClient bean that automatically attaches the Authorization: Bearer <token> header to every outgoing request.





## 3. API Endpoint

The service exposes a single REST endpoint to generate the aggregated report.

Endpoint: GET /api/v1/reports/{orgName}

Example Request:
```
http://localhost:8080/api/v1/reports/google
```
Example Response:
```
[
 {
    "username": "octocat",
    "avatarUrl": "https://...",
    "repositories": [
    { "repositoryName": "hello-world", "role": "ADMIN" },
    { "repositoryName": "spoon-knife", "role": "READ" }
  ]
 }
]
```


## 4. Assumptions and Design Decisions
### Design Decisions

- **Parallel Processing**: To support the scale of 100+ repositories and 1000+ users, I implemented asynchronous fetching using CompletableFuture. Instead of sequential "N+1" API calls, the service fetches collaborator data for all repositories in parallel, significantly reducing latency.
  <br><br>
- **In-Memory Aggregation**: I chose a stateless, in-memory approach using Java Streams (groupingBy) to pivot data from a Repository-centric view to a User-centric view. This simplifies deployment and maximizes speed for real-time reporting.
  <br><br>
- **Modern Stack**: Used the new Spring RestClient (introduced in Spring Boot 3.2) and Java Records for immutable, clean data transfer objects (DTOs).

### Assumptions
- **Permissions**: It is assumed the provided GitHub Token has the necessary repo or admin:org scopes to view collaborator lists. If the token lacks permissions for a specific repo, that repo is gracefully skipped via error handling.
<br><br>
- **Rate Limits**: For the purpose of this assignment, it is assumed the organization size fits within the standard GitHub API rate limits (5,000 requests/hour for PATs).
  <br><br>
- **Pagination**: The service is configured to fetch the first 100 repositories and collaborators per repository (the maximum allowed by GitHub per single page) to optimize performance.