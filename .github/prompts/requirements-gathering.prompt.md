You are an elite, systematic Product Manager and Business Analyst. I am currently in a high-stakes, 90-minute duration window focused on AI Collaboration and Problem Framing. 

The core problem statement/use case I am given to solve is: [INSERT ASSIGNED INTERVIEW PROBLEM]

Please act as my requirement-gathering co-pilot and generate a comprehensive, production-grade Product Requirements Document (PRD) in the docs folder. Focus strictly on defining what the system must achieve, its scope, and its functional/non-functional constraints. Do not write any code, HLD, or LLD architectures. Do not assume any new feautures or capabilities beyond what is explicitly stated in the problem statement. Also give an option text field to input the answer on his own.

Please structure the requirements using these exact sections:

1. PROBLEM FRAMING & SCOPE BOUNDARIES:
- Clarify the explicit core problem this feature solves.
- Define what is strictly "In-Scope" for a rapid 90-minute implementation cycle vs. what must be documented as "Out-of-Scope" for future iterations.
- Explicitly state 3 critical assumptions about the business logic, inputs, or end-user behavior.

2. TARGET PERSONAS & PRODUCT JOURNEYS:
- Define the primary user/actor persona interacting with this specific use case.
- Map the sequential user steps required to successfully achieve the desired value outcome.

3. MULTI-LAYERED REQUIREMENTS MATRIX:
Provide a detailed table with the columns: [ID, Requirement Type, Description, Business Priority (P0/P1/P2), and Business Impact].
Ensure you explicitly capture multiple types of requirements across ALL of these categories — do not skip any:

  a) Functional Requirements — mandatory user actions, system actions, and feature capabilities.

  b) Data & Content Requirements — what specific data types, states, or assets the system must accept, store, and manage.

  c) Non-Functional Requirements (NFRs) — this section is MANDATORY and must be thorough. Cover ALL of the following NFR sub-categories with concrete, measurable targets (no vague terms like "fast" or "reliable"):
     - Performance: API response time targets (p50, p95, p99 latency in ms), throughput (req/sec)
     - Scalability: expected concurrent users, data volume growth, horizontal vs vertical scale strategy
     - Availability & Reliability: uptime SLA (e.g. 99.9%), RTO (Recovery Time Objective), RPO (Recovery Point Objective)
     - Security: authentication mechanism, authorization model, data encryption at rest and in transit, PII handling
     - Observability: logging requirements, metrics to capture, alerting thresholds
     - Usability: accessibility standards (WCAG level), max acceptable page load time
     - Maintainability: deployment strategy (zero-downtime?), backward compatibility constraints

4. SYSTEM EDGE CASES & FAIL-STATES:
- Define how the product should behave when users provide invalid, missing, or malformed data.
- Detail the fallback behavior or user experience when external system dependencies fail or time out.
- Specify behavior under peak load (graceful degradation vs hard failure).

5. USER ACCEPTANCE CRITERIA (UAC):
- For the core P0 workflows, provide clear User Acceptance Criteria written in a non-technical, human-readable format so a QA or stakeholder can immediately validate that the requirement is fulfilled.
- Include at least one UAC that validates a non-functional requirement (e.g. response time, privacy, or availability).

---

At the very end of the PRD, append this exact PRD Quality Checklist and mark each item as checked or unchecked based on whether the generated document satisfies it:

## PRD Quality Checklist

### Scope & Framing
- [ ] Core problem is stated in one clear sentence
- [ ] In-Scope items are bounded to the 90-minute implementation window
- [ ] Out-of-Scope items are explicitly listed (not implied)
- [ ] Exactly 3 critical assumptions are stated

### Personas & Journeys
- [ ] At least one named persona with role and motivation defined
- [ ] Each persona has a sequential step-by-step user journey

### Requirements Matrix
- [ ] Functional requirements (user actions + system actions) captured
- [ ] Data & Content requirements captured (entities, states, constraints)
- [ ] NFR — Performance: p50, p95, p99 latency targets present with units (ms)
- [ ] NFR — Performance: throughput target present (req/sec)
- [ ] NFR — Scalability: concurrent user count and growth projection stated
- [ ] NFR — Availability: uptime SLA % stated; RTO and RPO stated with units
- [ ] NFR — Security: auth mechanism, authorization model, encryption at rest + in transit
- [ ] NFR — Privacy: compliance scope (GDPR/CCPA/N/A) explicitly stated
- [ ] NFR — Observability: specific metrics and alerting thresholds named
- [ ] NFR — Usability: page load time or interaction response time stated
- [ ] NFR — Maintainability: deployment strategy and backward compatibility stated
- [ ] All NFR targets are concrete numbers — no vague terms ("fast", "reliable", "scalable")
- [ ] All requirements have a Business Priority (P0/P1/P2) assigned

### Edge Cases & Fail-States
- [ ] Invalid/missing/malformed input behavior defined
- [ ] External dependency failure (timeout/down) behavior defined
- [ ] Peak load behavior defined (graceful degradation vs hard failure)

### User Acceptance Criteria
- [ ] Every P0 functional requirement has at least one UAC
- [ ] At least one UAC validates a non-functional requirement (latency, privacy, or availability)
- [ ] All UACs follow Given/When/Then or equivalent non-technical format

Note for generators:
- When producing the final PRD, write the full document to a new file in this repository under the `/docs` folder (choose a descriptive filename, e.g., `PRD-<feature>.md`). Do NOT print the document contents in the chat console; instead reply only with the created file path and a one-line confirmation that the file was written.

