package com.persons.finder.presentation

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant
import javax.validation.ConstraintViolationException

/**
 * Global exception handler for consistent error responses
 * Follows RFC 7807 Problem Details standard
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        logger.warn("Resource not found: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = 404,
                error = "Not Found",
                message = ex.message ?: "Resource not found",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("Bad request: ${ex.message}")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = 400,
                error = "Bad Request",
                message = ex.message ?: "Invalid input",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate { 
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        logger.warn("Validation failed: $errors")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(
                status = 400,
                error = "Validation Failed",
                message = "Input validation failed",
                errors = errors,
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.constraintViolations.associate {
            it.propertyPath.toString() to (it.message ?: "Invalid value")
        }
        logger.warn("Constraint violation: $errors")
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(
                status = 400,
                error = "Validation Failed",
                message = "Parameter validation failed",
                errors = errors,
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val message = "Invalid value for parameter '${ex.name}': expected ${ex.requiredType?.simpleName}"
        logger.warn(message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(
                status = 400,
                error = "Bad Request",
                message = message,
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericError(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = 500,
                error = "Internal Server Error",
                message = "An unexpected error occurred. Please try again later.",
                timestamp = Instant.now()
            ))
    }
}

/**
 * Standard error response format
 */
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant
)

/**
 * Validation error response with field-specific errors
 */
data class ValidationErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val errors: Map<String, String>,  // field -> error message
    val timestamp: Instant
)
