# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
Yes, I would refactor it. The codebase mixes several persistence styles, which makes it harder to follow and maintain. 
I prefer a consistent, predictable approach like a clean hexagonal pattern where gateways define contracts, repositories implement them, and use cases handle business logic.
I wouldn’t rewrite everything at once, but I would gradually align the code toward one clear structure to reduce complexity for future development.

2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
Both approaches work, but I do have a preference. OpenAPI‑first gives you a clear, shared contract and keeps documentation, validation, and implementation aligned.
It is especially helpful when multiple teams or external systems depend on the API. The downside is that generated code can feel restrictive and sometimes heavier than necessary.
Hand‑written endpoints are more flexible and faster to iterate on, especially early in a project, but they rely heavily on discipline to keep documentation and behavior in sync.
If this were my codebase, I would choose OpenAPI‑first for any public or cross‑team API, and keep code‑first for internal endpoints where speed and simplicity matter more than formal contracts.

3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
I would prioritize tests that protect the core business rules rather than trying to cover everything. The warehouse logic has the highest complexity, so I would focus first 
on unit tests for capacity rules, stock validation, and replacement constraints. 
Next, I would add a few integration tests around persistence and transaction boundaries, especially for the Store flow where the legacy sync must happen after commit. 
Finally, I would include a small set of API-level tests for the most important endpoints.
To keep coverage effective over time, I would enforce a minimum threshold in CI and add tests whenever new rules or bug fixes are introduced.