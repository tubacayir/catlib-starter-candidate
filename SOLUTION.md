### 1.	What did the existing code do? Describe it briefly as if explaining to a teammate.
This application was a basic REST API Spring Boot Project. The project was sending a GET request 
to CATAAS API with /api/cat/{tag} endpoint. Tag was coming from request as a path variable.
It was using the tag variable to get response from the CATAAS API. After the project received the
response as JSON from the external API, it was reading the response body and extracting
the cat image id. Finally, a response created with the original tag and generated cat image url.

### 2.  What did you find wrong with it, and what did you do about it?.
The original code was working, but I have noticed a few point that could be fixed and improved.
There were several issues about managing exceptions handling. For example when I sent a request with a tag that was not valid (e.g tagssss, two 2),
the project was showing URL without tag.
The controller declared throws Exception, making the error handling too generic. This is hard to recognize what kind of
error could make and how they should be returned to the client.To improve error handling, I added a GlobalExceptionHandler.
Instead of returning Spring Boot's default Whitelabel HTML error page, the application now returns consistent JSON error responses.
Second point that I noticed that the CatService had too many responsibilities which are building the URL, sending the HTTP request, 
validating the response, parsing the JSON and creating the response object in a single method. 
I decided to split this method into smaller methods for giving single responsibility and readability code.
Additionally, I also replaced string concatenation with UriComponentsBuilder when creating URLs.
This makes the code safer because topics or tags containing spaces or special characters are automatically encoded correctly.
Moreover, I moved the HttpClient creation into a Spring configuration class and injected it where needed instead of instantiating it inside each service. 
This follows Spring's dependency injection approach and improves testability.

### 3.  Walk us through the decisions you made that weren't specified — structure, error handling, naming, anything you had to figure out yourself.

The task did not specify a project structure, so I chose to keep the responsibilities separated. 
The controllers are responsible only for handling HTTP requests and responses, while the logical workflow is coordinated by `TopicContentService`. 
This service manages the calls to the CATAAS API, the Open Library API, and the local storage service. 
I believe this makes the code easier to understand and test. For the external integrations, I created separate services
for CATAAS and Open Library so that each service is responsible for only one API. `HttpClientService` class is used to manage HTTP communication with the API services.
For local storage, I chose JSON files because the task mentioned that JSON was the preferred format. Each topic is stored in its own file under a local `storage` directory. 
This keeps the implementation simple and makes the stored data easy to inspect. For the Open Library integration, I decided to use the Search API with the general `q` parameter. 
Since the input is a free-form topic rather than a title, ISBN, or work ID, I considered this endpoint to be the most suitable choice. 
Because I have checked other endpoints of API, I recognized that the others was not checking each field of the book object. 
I also decided to use Java records for the DTOs that are only used to transfer data. Since these objects are immutable and contain no logic, records make the models easier to read.
To increase confidence in the implementation, I added unit tests for the managing logic in `TopicContentService`, covering both successful and failure scenarios. 
I also limited book numbers with 5 because to prevent huge number of books.
Finally, I followed a simple Git workflow while developing the solution. Firstly, I created a git repository and push the code to there in master branch.
I created a separate feature branch from master branch for each task and merged the changes using pull requests. These pull request can be found in the Github.

### 4. Which parts did you use AI tools for? What did you prompt, what did it give you, and did you change anything?

I used AI mainly as a design discussion and review tool rather than a code generator. 
For example, I discussed different approaches for implementing the local storage layer, including whether to use JSON files or a database. 
Since the assignment preferred local JSON storage, I chose JSON files and kept the database as a production improvement rather than introducing unnecessary complexity.
Since I recognized the problem with tag field validation, and exception handling was managing in Controller class, I asked with the following prompt:
How can I manage exceptions in a Spring Boot Rest API project with a controller endpoint that takes path variable as input.
AI suggested introducing a centralized exception handler with custom exceptions. And I also asked the best approach to do that and wanted some examples.
This led me to use `GlobalExceptionHandler`.
Moreover, I asked to AI about string concatenation in the first version of project like that:
Prompt: Should I build URLs using string concatenation or UriComponentsBuilder? For example, I have a URI definition like that: String imageUrl = CATAAS_BASE_URL + "/cat/" + root.path("id").asText();
AI recommended UriComponentsBuilder because it safely handles URL encoding and special characters.
Another area where I used AI was choosing the most appropriate Open Library endpoint. I compared different API options before deciding to use the Search API with the `q` parameter because 
the application accepts a free-form topic rather than a specific book identifier.
In most cases, I used AI to explore alternatives and understand the benefits. I reviewed the suggestions, selected the approaches that best fit the assignment, and modified the implementation where necessary.

### 5. What did the AI get wrong or miss, if anything?

The AI suggestions were generally useful as a starting point, but they still required review and refinement. 
For example, AI initially suggested adding validation annotations such as `@NotBlank` to the path variable. 
After discussing the request flow, I decided not to use this approach because requests without a path variable never reach the controller, making that validation unnecessary.
AI also suggested a few different approaches for local storage, including using a database. 
After reviewing the assignment requirements, I decided that storing the data as JSON files was a better fit because the task explicitly preferred a local JSON-based solution.
Finally, I reviewed all architectural suggestions instead of adopting them directly. In several cases, 
I simplified or refactored the proposed solutions to better match the scope of the assignment and keep the implementation clean and maintainable.

### 6. The storage is local for now. How would you approach making it production-ready?

For a production-ready version, I would replace the local file-based storage with a real database, such as PostgreSQL. 
I would store topics, cat image URLs, book metadata, and timestamps in structured tables instead of JSON files.
I would also define indexes for commonly queried fields such as topic and created date, and make sure the storage operations are transactional.
I would also research for the database migration tools to manage database operations.

### 7. What's the weakest part of your solution? What would you do differently with more time?

The weakest part of the solution is the local file-based storage. It works for the scope of this assignment and keeps the data easy to inspect, 
but it is not suitable for a real production environment. It does not handle concurrency, multiple application instances, indexing, or advanced querying very well.

With more time, I would replace it with a database-backed storage layer.
I would also improve the external API integration by adding timeouts, and more detailed logging.
I would add a logger system to the project to follow errors and warnings in different environments.

Another area I would improve is test coverage. I added unit tests for the orchestration logic, 
but with more time I would add integration tests for the storage layer.







