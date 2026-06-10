# AGENTS.md

## Core Principle

This repository is backend-first.

The primary objective is to demonstrate strong backend engineering skills.

Frontend exists primarily for project demonstration and portfolio presentation.

When trade-offs arise, prioritize:

1. Backend architecture
2. Database design
3. Testing
4. Performance
5. CI/CD
6. Cloud deployment

over frontend sophistication.

---

## Project Success Criteria

The final project should demonstrate:

- Spring Boot expertise
- PostgreSQL expertise
- API design
- Testing
- Docker
- CI/CD
- Cloud deployment
- Redis caching
- Event-Driven Architecture using Kafka
- GenAI integration
- Agentic workflows

The project should be deployable, testable, and production-oriented.

Every major feature should contribute to at least one of these goals.

## Current Development Phase

Current focus:

Phase 1 - Core Backend

Immediate priorities:

1. Complete analytics APIs
2. Add testing
3. Add Docker
4. Add Swagger/OpenAPI
5. Add global exception handling

Do not begin Kafka, Redis, GenAI, Agentic AI, or advanced frontend work unless explicitly requested.

Current Status:

- Cricsheet ingestion pipeline implemented
- Match, Player, Team, MatchPlayer, Delivery entities implemented
- PostgreSQL analytics database populated
- Player Search API implemented
- Player Profile API implemented
- Top Batters Analytics implemented

The agent should extend existing functionality rather than recreate it.

---

# Project Overview

Project Name: Cricket Intelligence Platform

A backend-first, production-oriented cricket analytics platform built to demonstrate modern software engineering practices and backend development skills.

The project should prioritize:

* Engineering quality
* Backend excellence
* Production readiness
* Resume value
* Learning value

over feature quantity.

---

# Target Architecture Roadmap

The project should evolve incrementally through the following phases.

## Phase 1 - Core Backend

Focus:

* Spring Boot
* PostgreSQL
* REST APIs
* Layered Architecture
* Validation
* Error Handling
* Logging

Deliverables:

* Player Search
* Player Profile
* Batting Analytics
* Bowling Analytics
* Player Comparison
* Leaderboards

---

## Phase 2 - Frontend Showcase

Focus:

* React
* Vite
* TypeScript
* Tailwind CSS

Purpose:

Frontend exists for project demonstration and portfolio presentation.

Frontend is NOT the primary learning objective.

Deliverables:

* Dashboard
* Player Search
* Player Profile
* Player Comparison
* Leaderboards
* AI Insights Page

---

## Phase 3 - Engineering Excellence

Focus:

* Unit Testing
* Integration Testing
* Docker
* API Documentation
* Code Quality

Deliverables:

* JUnit 
* README.md
* Mockito
* Spring Boot Test
* Testcontainers
* Swagger/OpenAPI
* Dockerfile
* Docker Compose

---

## Phase 4 - Production Readiness

Focus:

* CI/CD
* Cloud Deployment
* Performance Optimization
* Monitoring
* Redis Caching

Deliverables:

* GitHub Actions
* Cloud Run Deployment
* Query Optimization
* Redis Cache
* Health Checks

---

## Phase 5 - Distributed Systems

Focus:

* Event-Driven Architecture
* Kafka
* Asynchronous Processing

Deliverables:

* Event Publishing
* Event Consumption
* Retry Handling
* Event Documentation

Example Events:

* MatchImported
* PlayerStatsUpdated
* LeaderboardUpdated
* InsightGenerated

---

## Phase 6 - AI Layer

Focus:

* GenAI
* AI-powered Cricket Insights

Deliverables:

* AI Player Summary
* AI Match Summary
* AI Performance Analysis

Important:

AI should explain data.

AI should not calculate data.

Statistics must originate from business logic and PostgreSQL.

---

## Phase 7 - Agentic Layer

Focus:

* Tool Calling
* Multi-step Reasoning
* API-driven Agents

Deliverables:

* Cricket Analysis Agent
* Player Comparison Agent
* Match Intelligence Agent

Agents must use project APIs as tools.

Avoid fake agent workflows.

---

# Development Philosophy

Always prefer:

* Simplicity
* Maintainability
* Readability
* Incremental progress

over:

* Premature optimization
* Overengineering
* Unnecessary abstractions

Every feature should improve either:

* Learning value
* Engineering quality
* Resume value

If not, challenge its necessity before implementation.

---

## Documentation Rule

Whenever a significant feature is added:

- Update README.md
- Add setup instructions
- Add API examples
- Add deployment instructions if applicable
- Add testing instructions

Documentation should remain current with the codebase.

---

# Codex Operating Rules

## Planning First

Before implementing:

1. Analyze the requirement.
2. Create an implementation plan.
3. List impacted files.
4. Explain approach.
5. Then implement.

Never immediately modify code without understanding the task.

---

## Repository Scope

Do not scan the entire repository by default.

Inspect only files relevant to the requested task.

Request permission before performing repository-wide analysis or refactoring.

Avoid repository-wide refactors.

Avoid touching unrelated code.

---

## Change Management

After every implementation provide:

* Files changed
* What changed
* Why it changed
* How to test

---

## Refactoring Rules

Do NOT:

* Rename packages
* Rename classes
* Rename APIs
* Change API contracts
* Move files
* Change database schema

unless explicitly requested.

Prefer small, focused changes.

---

# Backend Guidelines

Preferred architecture:

Controller
→ Service
→ Repository
→ Database

Rules:

* Controllers remain thin.
* Business logic belongs in services.
* Repositories handle persistence only.
* Use DTOs when beneficial.
* Follow REST conventions.
* Use proper HTTP status codes.
* Use centralized exception handling.

---

# API Design Guidelines

Prefer:

- Consistent response structures
- Pagination for large datasets
- Versionable APIs
- DTO-based responses

Avoid:

- Exposing JPA entities directly
- Inconsistent response formats
- Breaking existing contracts


---

# Database Guidelines

Database: PostgreSQL

Requirements:

* Proper schema design
* Appropriate indexing
* Query optimization
* Pagination support
* Avoid N+1 query issues

Before introducing schema changes:

Explain:

* Purpose
* Relationships
* Index strategy

---

# Frontend Guidelines

Frontend exists for demonstration purposes.

Backend remains the primary focus.

Preferred stack:

* React
* Vite
* TypeScript
* Tailwind CSS

Avoid:

* Redux
* Complex state management
* Excessive animations
* Unnecessary frontend abstractions

Frontend should consume real backend APIs.

Suggested pages:

* Dashboard
* Search Players
* Player Profile
* Compare Players
* Leaderboards
* AI Insights

---

# Testing Requirements

Testing is mandatory.

Preferred stack:

* JUnit 5
* Mockito
* Spring Boot Test
* Testcontainers

For every significant feature:

* Add tests when practical.
* Verify existing tests still pass.
* Provide testing instructions.

Work is not complete without testing guidance.

---

# Docker Requirements

Application must remain containerized.

Requirements:

* Dockerfile
* Docker Compose

Preferred startup:

docker compose up

must remain functional.

---

# CI/CD Requirements

Preferred platform:

GitHub Actions

Pipeline should include:

1. Build
2. Test
3. Package
4. Docker Build
5. Deploy (when applicable)

Keep CI/CD simple and maintainable.

---

# Cloud Deployment Requirements

Preferred order:

1. GCP Cloud Run
2. AWS equivalent

Requirements:

* Low cost
* Easy deployment
* Well documented

Deployment steps should always be documented.

---

## Architecture Documentation

Maintain documentation as the project evolves.

For major architectural decisions:

- Explain the problem
- Explain the chosen solution
- Explain alternatives considered
- Explain tradeoffs

Store decisions in docs/adr.

Preferred structure:

```text
docs/
├── architecture.md
├── deployment.md
└── adr/
```
---

# Redis Caching Guidelines

Introduce caching only when justified.

Good candidates:

* Leaderboards
* Aggregated statistics
* Frequently requested analytics

Before adding cache:

Explain:

* Why caching is needed
* Cache key strategy
* Invalidation strategy

---

# Event-Driven Architecture Guidelines

Preferred technology:

Kafka

Kafka should only be introduced for meaningful asynchronous workflows.

Good use cases:

* Match Imported
* Stats Recalculation
* Leaderboard Updates
* Insight Generation

For every event document:

* Event Name
* Producer
* Consumer
* Payload
* Failure Handling

Avoid Kafka for trivial operations.

---

# GenAI Guidelines

GenAI responsibilities:

* Summaries
* Explanations
* Insights
* Natural language analysis

GenAI must not:

* Calculate statistics
* Replace business logic
* Replace database queries

Data must originate from backend services.
Prefer Retrieval-Augmented Generation (RAG) using project data over hallucinated responses.

---

# Agentic AI Guidelines

Agents should:

* Use backend APIs as tools
* Perform multi-step workflows
* Retrieve real project data

Preferred flow:

User Query
→ Agent
→ Tool Calls
→ Data Retrieval
→ Response Generation

Avoid simulated agents.

Use real APIs.

---

# Token Optimization Rules

To reduce token usage:

* Inspect only necessary files.
* Avoid repository-wide scans.
* Avoid rewriting entire files when small changes are sufficient.
* Reuse existing implementations.
* Prefer incremental improvements.

---

# Resume Optimization Priority

Prioritize work in this order:

1. Spring Boot
2. PostgreSQL
3. Testing
4. Docker
5. CI/CD
6. Cloud Deployment
7. Redis
8. Kafka
9. GenAI
10. Agentic AI

Do not skip foundational layers for advanced technologies.

---

# Definition of Done

A task is complete only when:

- Code compiles
- Tests pass
- APIs work
- Documentation updated
- Docker setup works
- No unrelated code modified
- Changes summarized
- Testing instructions provided

Never trade maintainability for speed.
