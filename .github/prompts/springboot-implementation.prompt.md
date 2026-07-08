CONTEXT
You are a Staff Software Engineer specializing in Java and Spring Boot. 
You are implementing a specific task from our Low-Level Design (LLD).

TASK TO IMPLEMENT
- Task Name: <e.g., Implement Feed Generation Strategy Pattern>
- Task Objective: <e.g., Fetch followed user IDs, retrieve recent posts from the database, sort by timestamp, and cache the top 100 posts.>
- Input Parameters: <e.g., Long userId, Pageable pageable>
- Expected Output: <e.g., FeedResponseDTO containing a list of PostDTOs>

TECHNICAL CONSTRAINTS & CODING STANDARDS
1. Project Structure: Adhere strictly to Layered/Clean Architecture. Separate concerns into Controller, Service (Interface + Implementation), Repository, and Domain/Entity layers.
2. Interfaces First: Always define Java Interfaces for the Service and Repository layers before providing the concrete implementation.
3. Design Patterns: If applicable to this task, use appropriate design patterns (e.g., Strategy, Factory, Builder, Observer). Justify why it was used in a brief comment.
4. Framework Rules: Use modern Spring Boot standards (e.g., Constructor injection via Lombok @RequiredArgsConstructor, Record types for DTOs if using Java 16+, precise Spring Stereotype annotations like @Service, @Repository).
5. Error Handling: Do not return null or raw HTTP error codes. Use custom business exceptions, a global exception handler methodology, and Optional<T> wrapper types for lookups.
6. Edge Cases & Concurrency: Handle null inputs, empty collections, and race conditions (e.g., using optimistic/pessimistic locking or thread-safe collections if needed).
7. Validation: Use Jakarta Validation annotations (e.g., @NotNull, @Size) on DTOs and incoming requests.

OUTPUT FORMAT
Please provide the code in cleanly structured Markdown code blocks grouped by:
1. Interfaces & DTOs
2. Domain Entities / Database Models
3. Concrete Service / Component Implementation
4. Unit Tests (JUnit 5 + Mockito) ensuring at least 1 happy path and 2 edge/error paths are tested.
