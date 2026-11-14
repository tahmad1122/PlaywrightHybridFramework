package com.qaframework.ai;

/**
 * Builds the prompts sent to Groq. Kept separate from GroqClient /
 * FailureAnalyser / ReportSummariser so prompt wording can be tuned in
 * one place without touching any calling logic.
 */
final class PromptBuilder {

	private PromptBuilder() {
	}

	static final String FAILURE_SYSTEM_PROMPT =
			"You are a senior test automation engineer reviewing a failed Playwright/TestNG test. " +
			"Given the test name and error/stack trace, respond with a SHORT root-cause hypothesis " +
			"(1-2 sentences) followed by one concrete suggestion to fix or investigate it. " +
			"Do not repeat the full stack trace back. Keep the whole answer under 80 words.";

	static String failureAnalysisPrompt(String testName, String errorMessage) {
		return "Test name: " + testName + "\n" +
				"Error/stack trace (may be truncated): " + errorMessage;
	}

	static final String SUMMARY_SYSTEM_PROMPT =
			"You are a senior QA lead writing a short executive summary of an automated test run for " +
			"a non-technical stakeholder. Respond ONLY with a compact summary in this exact structure, " +
			"no extra preamble:\n" +
			"Overall Status: <PASSED/FAILED/UNSTABLE>\n" +
			"Risk Level: <Low/Medium/High>\n" +
			"Summary: <2-3 sentences>\n" +
			"Key Findings:\n- <point>\n- <point>\n" +
			"Recommendations:\n- <point>";

	static String summaryPrompt(int total, int passed, int failed, int skipped, String failedTestNames) {
		return String.format(
				"Test run results:\nTotal: %d\nPassed: %d\nFailed: %d\nSkipped: %d\nFailed tests: %s",
				total, passed, failed, skipped,
				(failedTestNames == null || failedTestNames.isBlank()) ? "none" : failedTestNames);
	}
}

