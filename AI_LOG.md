# AI Usage Log

This document Details key interactions with AI tools during development.
Claude Sonnet 4.5 was used through the GitHub Copilot integration with IntelliJ

---

## 1. AI LLM Integration

**What I asked:**
- I asked the AI to generate template code for connecting to Gemini API.
- I asked the AI to ensure that the prompt was sanitised.
- Upon 

**AI's contribution:**
- Generated a service for generating bios using a gemini-2.0-flash key.
- Added logic for sanitising the prompt and preventing any hacking that could interfere.

**What I changed/refined:**
- Touched up the implementation to better meet my needs as a service in the general project (adding parameters).
- Improved the default messages in case of API issues (access denied issues, etc).
- Throughly tested the outcome to ensure that no further issues were made, and altered the prompt to increase the quality of responses.

**Outcome:**
- The AI implementation saved alot of time in getting something working; however due to limitations with API keys Gemini was no longer an option and needed to pivot to another model.
- The AI was able to pivot the code to using Ollama competently without breaking the code.

---

## 2. PostgreSQL Spatial Query Optimization

**What I asked:**
- I asked the AI to help calculate the distance between different users, and whether it was within a certain range.
- I asked the AI to debug issues with the Spatial query, and assist in evaluating database solutions.


**AI's contribution:**
- The AI suggested a calculation query using the Haversine formula.
- Upon issues with performance in the initial H2 integration, the AI gave a suggestion to pivot to PostgreSQL.

**What I changed/refined:**
- Validated query was working using H2 and DBeaver tools.
- Added additional parameters to the query for removing the search id from the query.
- Validated the setup the PostgreSQL.

**Outcome:**
- Performance was greatly improved upon migrating to the PostgreSQL, with good advice from the AI.
- It was confirmed that the distance calculation was also correct, through custom queries using DBeaver.

---

## 3. Test Coverage Files

**What I asked:**
- I asked the AI to analyse the code base and generate test files.
- I asked the AI to generate a script for creating 1M entries, for performance testing.

**AI's contribution:**
- The AI generated simple scripts (seed_database.sh, test_api.sh), which could be used to test functionality.
- The AI also generated tests for me for each service (BioGeneratorService & PersonsService) as well as the Controller.


**What I changed/refined:**
- Reviewed the tests, and made changes that were appropriate for the datasets and anything the AI missed.
- Refined any issues when the tests threw errors, either due to errors with how the tests were run, or with the code itself.

**Outcome:**
- This was my first time doing testing with a backend API, so the generated test files were very helpful.
- The AI still had a few build and logic errors, but after some small tweaks, the tests were better suited for my needs.
- Saved a lot of time in learning and generating files, in what is normally a tedious task.

---

## Overall Reflection

**AI helped me:**
- The AI helped with a lot of menial tasks, which freed me up to work on and analyse the architecture of the project.
- Tasks which required experience which I did not necessarily have (AI LLM integration, Geo-Spacial calculations, Backend Testing, etc) were able to be handled more efficiently by the AI, and I could review and understand what was implemented a bit better as a result.
- While not covered in the above points, the AI tool also helped in finding and resolving build errors, which also freed up a lot of time for me.

**AI struggled with:**
- The AI struggled with 3rd party APIs, especially in terms of the initial Gemini implementation.
- While the code seemed to work, it was unable to identify the cause for why I did not get any initial responses from Gemini API, and actually created a new issue when it used up the keys by testing, leading it to assume that was the cause for the problem.

**What I learned:**
- One key takeaway was that AI can be very helpful in evaluating code and pointing out measures for improvement.
- That said, AI must be used wisely, as with third-party libraries and outside APIs, an AI may fail to find and resolve issues, while a more local solution suits AI code assistance better, at least in identifying issues. 
