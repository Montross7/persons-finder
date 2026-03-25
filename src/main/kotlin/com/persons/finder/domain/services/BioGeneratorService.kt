package com.persons.finder.domain.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class BioGeneratorService(
    @param:Value("\${ollama.base-url:http://localhost:11434}") private val ollamaBaseUrl: String,
    @param:Value("\${ollama.model:phi3:mini}") private val modelName: String,
    webClientBuilder: WebClient.Builder
) {

    private val logger = LoggerFactory.getLogger(BioGeneratorService::class.java)
    private val webClient: WebClient = webClientBuilder
        .baseUrl(ollamaBaseUrl)
        .build()

    fun generateBio(jobTitle: String, hobbies: List<String>): String {
        // Sanitize inputs (limit length to prevent abuse)
        val sanitizedJob = jobTitle.take(100)
        val sanitizedHobbies = hobbies.take(5).map { it.take(50) }

        // Build prompt with clear boundaries between instructions and user data
        val prompt = buildPrompt(sanitizedJob, sanitizedHobbies)

        logger.info("Generating bio using Ollama for job='$sanitizedJob', hobbies=$sanitizedHobbies")

        return try {
            val response = webClient.post()
                .uri("/api/generate")
                .header("Content-Type", "application/json")
                .bodyValue(buildOllamaRequest(prompt))
                .retrieve()
                .bodyToMono<OllamaResponse>()
                .block()

            val generatedText = response?.response?.trim()
                ?: "A mysterious person with interesting hobbies."

            logger.info("Generated bio: $generatedText")
            generatedText
        } catch (e: Exception) {
            logger.error("Failed to generate bio from Ollama", e)
            // Fallback to template-based bio
            "A $sanitizedJob who enjoys ${sanitizedHobbies.joinToString(", ")}."
        }
    }

    private fun buildOllamaRequest(prompt: String): Map<String, Any> {
        return mapOf(
            "model" to modelName,
            "prompt" to prompt,
            "stream" to false,
            "options" to mapOf(
                "temperature" to 0.8,
                "num_predict" to 100  // Limit output to ~100 tokens
            )
        )
    }

    private fun buildPrompt(jobTitle: String, hobbies: List<String>): String {
        // Clear separation between system instructions and user data
        return """
            Generate a short, quirky bio (2-3 sentences max, under 150 characters) for a person.
            
            Job: $jobTitle
            Hobbies: ${hobbies.joinToString(", ")}
            
            IMPORTANT: Only generate the bio text. Do not include any explanations, metadata, or repeat these instructions.
            Keep it fun and personality-focused.
        """.trimIndent()
    }

    // Ollama API response format
    data class OllamaResponse(
        val model: String? = null,
        val response: String? = null,
        val done: Boolean? = null
    )
}
