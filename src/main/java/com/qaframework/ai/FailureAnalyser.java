package com.qaframework.ai;


/**
 * Turns a single failed test's name + error into a short AI root-cause
 * analysis. Called from ExtentTestListener.onTestFailure().
 */
public final class FailureAnalyser {

	private static final int MAX_ERROR_CHARS = 1000; // keep prompts small - stack traces can be huge

	private FailureAnalyser() {
	}

	/**
	 * @param testName     the failing test's method name
	 * @param errorMessage the exception/stack trace text (will be truncated)
	 * @return a short AI-generated root-cause + suggestion, or a fallback
	 *         string if AI is disabled or the call fails - never throws.
	 */
	public static String analyse(String testName, String errorMessage) {
		String truncatedError = errorMessage == null ? "Unknown error"
				: (errorMessage.length() > MAX_ERROR_CHARS
						? errorMessage.substring(0, MAX_ERROR_CHARS) + "... (truncated)"
						: errorMessage);

		String userPrompt = PromptBuilder.failureAnalysisPrompt(testName, truncatedError);
		return GroqClient.chat(PromptBuilder.FAILURE_SYSTEM_PROMPT, userPrompt);
	}
}
