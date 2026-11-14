package com.qaframework.ai;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.qaframework.config.ConfigManger;
import com.qaframework.utills.LoggerUtil;

/**
 * Low-level wrapper around Groq's chat completion API (OpenAI-compatible
 * schema: POST {model, messages[]} -> choices[0].message.content).
 *
 * This class only knows how to send a system+user prompt and get text
 * back. It does not know about tests, TestNG, or Extent - that logic
 * lives in FailureAnalyser / ReportSummariser, which are the only two
 * callers of this class. Keeping it this thin makes it trivial to swap
 * Groq for another provider (Claude, OpenAI, etc.) later without
 * touching the rest of the AI layer.
 */
public final class GroqClient {

	private static final Logger log = LoggerUtil.getLogger(GroqClient.class);

	// GROQ_API_KEY env var wins if set; otherwise falls back to
	// qa.properties (which ships blank on purpose - never commit a real key).
	private static final String API_KEY = firstNonBlank(
			System.getenv("GROQ_API_KEY"),
			ConfigManger.getPropertyOrDefault("groq.apiKey", ""));

	private static final String MODEL = ConfigManger.getPropertyOrDefault("groq.model", "llama-3.3-70b-versatile");
	private static final String BASE_URL = ConfigManger.getPropertyOrDefault("groq.baseUrl",
			"https://api.groq.com/openai/v1/chat/completions");
	private static final boolean ENABLED = Boolean.parseBoolean(ConfigManger.getPropertyOrDefault("groq.enabled", "true"))
			&& !API_KEY.isBlank();

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	private GroqClient() {
		// static utility - no instances
	}

	/** True if a Groq API key is configured. Callers should skip AI calls when false. */
	public static boolean isEnabled() {
		return ENABLED;
	}

	/**
	 * Sends a single-turn chat request to Groq and returns the model's
	 * reply text. Never throws - on any failure (missing key, network
	 * error, bad response, rate limit) it logs a warning and returns a
	 * short fallback string, so a flaky AI call can never fail a test run.
	 */
	public static String chat(String systemPrompt, String userPrompt) {
		if (!ENABLED) {
			log.warn("Groq AI layer skipped - no API key configured (set GROQ_API_KEY or groq.apiKey).");
			return "AI analysis skipped: no Groq API key configured.";
		}

		try {
			JSONArray messages = new JSONArray();
			messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
			messages.put(new JSONObject().put("role", "user").put("content", userPrompt));

			JSONObject requestBody = new JSONObject();
			requestBody.put("model", MODEL);
			requestBody.put("messages", messages);
			requestBody.put("temperature", 0.4);
			requestBody.put("max_tokens", 600);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.timeout(Duration.ofSeconds(30))
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + API_KEY)
					.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
					.build();

			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 429) {
				log.warn("Groq rate limit hit (HTTP 429). Skipping AI analysis for this call.");
				return "AI analysis skipped: Groq rate limit reached.";
			}

			if (response.statusCode() != 200) {
				log.warn("Groq API returned HTTP {} : {}", response.statusCode(), truncate(response.body(), 300));
				return "AI analysis unavailable (HTTP " + response.statusCode() + ").";
			}

			JSONObject responseJson = new JSONObject(response.body());
			return responseJson
					.getJSONArray("choices")
					.getJSONObject(0)
					.getJSONObject("message")
					.getString("content")
					.trim();

		} catch (IOException | InterruptedException e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			log.warn("Groq API call failed: {}", e.getMessage());
			return "AI analysis unavailable: " + e.getMessage();
		} catch (Exception e) {
			// Defensive catch-all - a malformed AI response must never
			// take down the test run or the reporting listener with it.
			log.warn("Unexpected error while calling Groq API: {}", e.getMessage());
			return "AI analysis unavailable due to an unexpected error.";
		}
	}

	private static String firstNonBlank(String a, String b) {
		return (a != null && !a.isBlank()) ? a : (b != null ? b : "");
	}

	private static String truncate(String text, int maxLen) {
		if (text == null) return "";
		return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
	}
}

