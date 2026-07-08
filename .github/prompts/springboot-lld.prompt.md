Role: You are a Principal Software Architect and Lead Systems Engineer. Your job is to translate high-level constraints and business goals into a comprehensive, developer-ready Low-Level Design (LLD) document.

Context Inputs:
- High-Level Requirements / PRD: [Insert Requirements, e.g., features, non-functional targets, user scale]
- High-Level Design (HLD): [Insert HLD, e.g., microservices, databases, system components, or attach document]

Core Task: Draft an exhaustive, structured Low-Level Design document based on the provided HLD and requirements. Store the doc in /docs folder. Follow industry best practices (such as the [Azure Architecture Guide](https://medium.com/azure-hub/how-azure-architect-prepare-hld-lld-documentation-part-of-solution-cc29b13ce850) and [Low-Level Design Best Practices](https://www.linkedin.com/pulse/low-level-design-document-structure-comprehensive-godhandaraman-kzvnc)) to ensure the document is immediately usable for developers to implement the system.

Please structure the LLD document to include the following sections:

1. **Document Scope & Objectives**
   - Brief summary of the module/system being designed.
   - Reference links to the original HLD and PRD.

2. **Functional & Non-Functional Requirements Mapping**
   - Traceable table mapping requirements to low-level features.
   - NFRs broken down into exact constraints (e.g., latency, throughput thresholds, security/encryption).

3. **Component & Class Design (Object-Oriented)**
   - Detailed component breakdown for each service mentioned in the HLD.
   - Core classes, interfaces, and methods definitions.
   - Recommended Design Patterns (e.g., Strategy, Factory, Singleton) with justifications.

4. **Data Structures & Algorithms**
   - Detailed database schema (tables, primary/foreign keys, indexes).
   - Caching strategies and data structures to be used (e.g., Redis caching for session tokens).
   - Internal logic and algorithms required to process business rules.

5. **API / Interface Specifications**
   - Detailed API contracts (endpoint paths, HTTP methods).
   - Exact request/response JSON payloads.
   - Authentication, rate-limiting, and headers for each endpoint.

6. **Workflow & State Management**
   - State transition flows and sequence diagrams (represented via Mermaid.js or text-based flowcharts).
   - Thread synchronization and concurrency details.

7. **Error Handling & Edge Cases**
   - Exhaustive list of edge cases (boundary conditions, invalid inputs, network timeouts).
   - Fallback mechanisms and exact error code definitions.

8. **Unit & Integration Testing Plan**
   - Scenarios that must be covered in unit tests.
   - Testing tools and mock dependencies.

9. **Security Design**
   - Authentication and authorization mechanisms (e.g., JWT token structure, OAuth2 flows, roles/permissions matrix).
   - Data encryption at rest and in transit (TLS versions, cipher suites, key management).
   - Input validation and sanitization rules per endpoint.
   - OWASP Top 10 mitigations applicable to this system.

10. **Logging, Monitoring & Observability**
    - Log levels per component and log format (structured JSON preferred).
    - Metrics to be emitted (e.g., request count, error rate, latency percentiles).
    - Distributed tracing setup (e.g., Spring Sleuth + Zipkin correlation IDs).
    - Alerting thresholds and on-call escalation triggers.

11. **Configuration & Environment Management**
    - Externalized configuration properties per environment (dev/staging/prod).
    - Secrets management strategy (e.g., Vault, Kubernetes Secrets, environment variables).
    - Feature flags and their default states.

Please adopt a highly technical, precise tone. Ensure there are no ambiguous statements; clearly specify the "how" for each requirement.

---

## LLD Completeness Checklist

Use this checklist to verify the generated LLD document is developer-ready before implementation begins.

### 1. Document Scope & Objectives
- [ ] Module/system summary is concise and unambiguous
- [ ] Links to HLD and PRD are present and correct
- [ ] Document version, author, and last-updated date are included

### 2. Functional & Non-Functional Requirements Mapping
- [ ] Every functional requirement from the PRD has a corresponding LLD entry
- [ ] NFRs include numeric thresholds (latency ≤ Xms, throughput ≥ Y TPS, uptime SLA %)
- [ ] Security and compliance NFRs are explicitly listed
- [ ] Traceability matrix is complete (Req ID → Component → Test Case)

### 3. Component & Class Design
- [ ] All services from the HLD have a detailed component breakdown
- [ ] Core classes, interfaces, and method signatures are defined
- [ ] Design patterns are named with justifications (not just "we use Factory")
- [ ] Inter-component dependencies and communication contracts are specified

### 4. Data Structures & Algorithms
- [ ] All database tables are defined with column types, nullability, and defaults
- [ ] Primary keys, foreign keys, and unique constraints are specified
- [ ] Indexes are listed with rationale (query patterns they serve)
- [ ] Caching strategy includes TTL, eviction policy, and invalidation triggers
- [ ] Complex business logic algorithms are described step-by-step

### 5. API / Interface Specifications
- [ ] Every endpoint has path, HTTP method, request/response schema, and status codes
- [ ] Authentication header requirements are specified per endpoint
- [ ] Rate-limiting rules (requests/sec, burst limits) are defined
- [ ] Pagination, filtering, and sorting contract is documented
- [ ] Versioning strategy is applied consistently across all endpoints

### 6. Workflow & State Management
- [ ] All state machines have exhaustive state/transition tables
- [ ] Sequence diagrams cover the happy path and at least two failure paths
- [ ] Concurrency controls (locks, queues, idempotency keys) are specified
- [ ] Async workflows have timeout and retry policies defined

### 7. Error Handling & Edge Cases
- [ ] All error codes are enumerated with HTTP status, error code, and message template
- [ ] Boundary conditions (empty inputs, max values, concurrent writes) are addressed
- [ ] Fallback and circuit-breaker behavior is described
- [ ] Partial failure scenarios (e.g., DB write succeeds but cache update fails) are handled

### 8. Unit & Integration Testing Plan
- [ ] Unit test scenarios cover all business logic branches
- [ ] Integration test scenarios cover all external system interactions
- [ ] Test tools and frameworks are specified (JUnit 5, Mockito, Testcontainers, etc.)
- [ ] Mock/stub strategies for external dependencies are defined
- [ ] Code coverage targets are stated

### 9. Security Design
- [ ] JWT/OAuth2 token structure and validation rules are fully specified
- [ ] Role/permission matrix covers all API endpoints
- [ ] Encryption at rest and in transit is configured (TLS version, algorithms)
- [ ] Input validation rules are defined for all user-supplied fields
- [ ] OWASP Top 10 threats are addressed (SQL injection, XSS, CSRF, etc.)

### 10. Logging, Monitoring & Observability
- [ ] Log format (JSON fields, correlation ID) is standardized
- [ ] Log levels per component are defined
- [ ] Key business and system metrics are listed with collection method
- [ ] Distributed tracing is configured with trace/span propagation
- [ ] Alerting thresholds and PagerDuty/Slack routing are documented

### 11. Configuration & Environment Management
- [ ] All configurable properties are listed with type, default, and valid range
- [ ] Secrets are never hardcoded; vault/secret store references are provided
- [ ] Environment-specific overrides (dev/staging/prod) are documented
- [ ] Feature flags and their rollout conditions are defined

### Final Gate
- [ ] All sections are free of placeholder text (e.g., `[TBD]`, `[Insert here]`)
- [ ] Document has been peer-reviewed by at least one senior engineer
- [ ] All Mermaid diagrams render correctly
- [ ] LLD has been signed off before sprint/implementation kickoff

Generator directive:
- When producing the final LLD, write the full document to a new file in this repository under the `/docs` folder (choose a descriptive filename, e.g., `LLD-<module>.md`). Do NOT print the document contents in the chat console; instead reply only with the created file path and a one-line confirmation that the file was written.
