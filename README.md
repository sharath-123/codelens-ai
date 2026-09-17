 CodeLens AI 🔍



 The 24/7 Intelligent Code Reviewer



CodeLens AI is an AI-powered code review platform that combines deterministic static analysis, historical engineering rules, Gemini-powered reasoning, and persistent review history.



Instead of relying only on static analysis or only on an LLM, CodeLens combines both approaches to produce explainable, repeatable, and actionable code reviews.



\---



\## Demo Overview



CodeLens AI allows developers to:



\- Submit source code in multiple languages.

\- Detect security vulnerabilities, performance issues, maintainability problems, and architectural concerns.

\- Receive AI-generated review recommendations from Gemini.

\- Apply historical engineering best practices during review generation.

\- Track previous reviews through persistent Firestore storage.

\- View an overall code quality score.



\---



\## Features



\### AI-Powered Code Reviews



CodeLens uses \*\*Gemini via Vertex AI\*\* to generate structured code reviews that include:



\- Bugs

\- Security vulnerabilities

\- Performance issues

\- Maintainability concerns

\- Architecture observations

\- Recommended improvements



\### Deterministic Static Analysis



Before calling Gemini, the backend performs rule-based analysis.



Current checks include:



\- SQL Injection Risk

\- Database Calls Inside Loops

\- `System.out.println` detection

\- Single-character variable names



These findings become structured evidence for the AI review.



\### Historical Engineering Rules



A built-in historical rule engine enriches reviews with previously established engineering guidance.



Examples include:



\- Parameterized SQL queries

\- Avoid database calls inside loops

\- Use structured logging

\- Service-layer business logic

\- DTOs at API boundaries

\- Null handling

\- Exception handling

\- Descriptive variable names



Historical rules are stored in:



```text

backend/src/main/resources/historical-rules.csv

```



The backend automatically selects relevant rules based on detected findings and includes them in the Gemini prompt.



\### Quality Score



Each review receives a score from \*\*1–10\*\*.



The scoring engine starts at \*\*10\*\* and deducts points based on deterministic findings.



| Severity | Score Impact |

|----------|-------------:|

| Critical | -3.0 |

| High | -2.0 |

| Medium | -1.0 |

| Low | -0.25 |



The final score is rounded to one decimal place.



\### Persistent Review History



Every completed review is stored in \*\*Google Cloud Firestore\*\*.



Developers can:



\- View previous reviews

\- Reload earlier results

\- Compare quality scores

\- Preserve AI reviews across sessions



\---



\# Why CodeLens AI?



Traditional static analyzers are excellent at detecting known patterns.



LLMs provide useful explanations but may lack consistency and engineering context.



CodeLens combines both.



```text

Deterministic Analysis

&#x20;       +

Historical Engineering Rules

&#x20;       +

Gemini AI Reasoning

&#x20;       +

Persistent Review History

&#x20;       ↓

Comprehensive Code Review

```



This creates a review pipeline that is both explainable and repeatable.



\---



\# Architecture



<AsyncImage query="CodeLens AI architecture diagram React Spring Boot Cloud Run Firestore Gemini flowchart" aspectRatio="16:9" width="100%" maxHeight=420/>



\## System Architecture



```text

                   ┌──────────────────────┐

                   │   React + Vite UI    │

                   │     TypeScript       │

                   └──────────┬───────────┘
                              │ REST API
                              ▼
                   ┌──────────────────────┐
                   │ Spring Boot Backend  │

                   │     Cloud Run        │

                   └──────────┬───────────┘
                              │
                ┌─────────────┼─────────────┐

               │             │             │

                ▼             ▼             ▼

      ┌──────────────┐ ┌────────────┐ ┌──────────────┐

      │ Local Static │ │ Historical │ │    Gemini    │
       │   Analyzer   │ │ Rule Engine│ │  Vertex AI   │

       └──────┬───────┘ └─────┬──────┘ └──────┬───────┘

              │               │               │

              └───────────────┼───────────────┘

                              │

                              ▼

                   ┌──────────────────────┐

                   │ Google Firestore     │

                   │ Reviews \& Findings   │

                   └──────────────────────┘

\---



\# Review Flow



<AsyncImage query="developer code review workflow diagram code submitted analysis AI review persistence" aspectRatio="16:9" width="100%" maxHeight=420/>



```text

1\. Developer submits code

       ↓

2\. Spring Boot creates a review

       ↓

3\. Local static analyzer scans the code

       ↓

4\. Historical rules are matched

       ↓

5\. Source + historical guidance → Gemini

       ↓

6\. AI review is generated

       ↓

7\. Findings + score are saved

       ↓

8\. Firestore stores the review

       ↓

9\. Frontend displays the completed review

```



\---



\# Google Cloud Services Used



| Service | Purpose |

|---------|---------|

| Cloud Run | Hosts the Spring Boot backend |

| Vertex AI | Runs Gemini |

| Gemini 2.5 Flash | AI code review |

| Firestore | Persistent review history |

| Cloud Build | Builds containers automatically |

| Cloud Logging | Backend logs |



The backend is deployed to:



```text

Region: us-central1

Platform: Google Cloud Run

```



\---



\# Historical Knowledge Integration



One of the core ideas behind CodeLens AI is that historical engineering knowledge should improve future reviews.



Example dataset:



```csv

id,type,description

R001,SECURITY,Use parameterized SQL queries instead of string concatenation.

R004,PERFORMANCE,Avoid database calls inside loops.

R006,MAINTAINABILITY,Use structured logging instead of System.out.println.

R008,ARCHITECTURE,Keep business logic inside service classes.

```



During a review:



```text

Finding

  ↓

Finding Type

  ↓

Historical Rule Matching

  ↓

Relevant Engineering Guidance

 ↓

Gemini Context

 ↓

Final AI Review

```



\---



\# Technology Stack



\## Frontend



\- React

\- TypeScript

\- Vite

\- Axios

\- React Markdown

\- CSS



\## Backend



\- Java

\- Spring Boot

\- Maven

\- Lombok



\## Google Cloud



\- Cloud Run

\- Firestore

\- Vertex AI

\- Gemini 2.5 Flash

\- Cloud Build



\---



\# Project Structure



```text

codelens-ai/

│

├── backend/

│   ├── src/

│   ├── pom.xml

│   ├── mvnw

│   └── ...

│

├── frontend/

│   ├── src/

│   ├── public/

│   ├── package.json

│   └── ...

│

├── README.md

└── .gitignore

```



\---



\# Running the Project



\## Backend



Navigate to the backend.



```bash

cd backend

```



Run locally.



```bash

./mvnw spring-boot:run

```



Deploy to Cloud Run.



```bash

gcloud run deploy codelens-backend \\

 --source . \\

 --region us-central1 \\

 --allow-unauthenticated

```



\---



\## Frontend



Navigate to the frontend.



```bash

cd frontend

```



Install dependencies.



```bash

npm install

```



Run locally.



```bash

npm run dev

```



The frontend runs on:



```text

http://localhost:5173

```



The frontend communicates with the deployed Cloud Run backend through the configured Vite proxy.



\---



\# API Endpoints



\## Create Review



```http

POST /api/v1/reviews

```



Example request:



```json

{

 "language": "Java",

 "sourceCode": "public void test() { }"

}

```



Example response:



```json

{

 "id": "review-id",

 "status": "COMPLETED",

 "qualityScore": 7.8

}

```



\---



\## Get Review



```http

GET /api/v1/reviews/{id}

```



Returns a previously completed review.



\---



\## Get Review History



```http

GET /api/v1/reviews

```



Returns previous reviews stored in Firestore.



\---



\## Get Findings



```http

GET /api/v1/reviews/{id}/findings

```



Returns deterministic findings associated with a review.



\---



\# Example Review



Input:



```java

public void getUser(String id) {



   String query =

       "SELECT \* FROM users WHERE id = '" + id + "'";



   System.out.println(query);

}

```



CodeLens detects:



\- SQL Injection

\- System.out.println usage



Gemini then expands the review with:



\- Security explanation

\- Maintainability recommendations

\- Architecture observations

\- Suggested fixes



\---



\# Security



The backend follows a cloud-native credential model.



Cloud Run uses its Google Cloud service identity to access Vertex AI and Firestore instead of embedding credentials.



No secrets or service-account JSON files should be committed.



> The current implementation uses a temporary `demo-user` identity. Production authentication is planned as the next enhancement.



\---



\# Future Improvements



Planned production enhancements include:



\- Google Identity Platform authentication

\- Per-user review history

\- GitHub Pull Request integration

\- Larger historical engineering datasets

\- More deterministic analysis rules

\- Review trend dashboard

\- Developer quality analytics

\- Organization-specific rule libraries

\- Async review processing with Pub/Sub



\---



\# What Makes CodeLens AI Different?



Instead of asking an LLM a single question:



> "Is this code good?"



CodeLens builds a complete review pipeline.



```text

Source Code

    ↓

Static Evidence

    ↓

Historical Engineering Knowledge

   ↓

Gemini Reasoning

    ↓

Quality Score

    ↓

Persistent Review History

```



This makes reviews more consistent, explainable, and useful for long-term developer improvement.



\---



\# Code Kitchen Submission



\*\*Track:\*\* The 24/7 Intelligent Code Reviewer



CodeLens AI demonstrates how deterministic software engineering practices and generative AI can work together to build a practical continuous code-review assistant.



The project showcases:



\- End-to-end cloud deployment

\- AI-assisted code review

\- Historical knowledge integration

\- Persistent review history

\- Modern full-stack architecture



\---



\# Author



\*\*Sharath Akkaldevi\*\*



Full Stack Developer



Built with ❤️ using Java, Spring Boot, React, TypeScript, Google Cloud, Vertex AI, Gemini, and Firestore.

