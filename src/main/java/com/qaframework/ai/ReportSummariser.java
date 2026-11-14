package com.qaframework.ai;


import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Set;

import org.json.JSONObject;
import org.slf4j.Logger;

import com.qaframework.utills.LoggerUtil;

/**
 * Generates the AI executive summary for a whole test run and writes it
 * to test-output/ai-reports/ai-summary.json. Called once from
 * ExtentTestListener.onFinish().
 */
public final class ReportSummariser {

	private static final Logger log = LoggerUtil.getLogger(ReportSummariser.class);

	private ReportSummariser() {
	}

	/**
	 * @param total   total tests run
	 * @param passed  passed count
	 * @param failed  failed count
	 * @param skipped skipped count
	 * @param failedTestNames names of failed tests (for context in the prompt)
	 * @return the AI-generated summary text (also written to disk as JSON)
	 */
	public static String generate(int total, int passed, int failed, int skipped, Set<String> failedTestNames) {
		String failedNamesJoined = failedTestNames == null ? "" : String.join(", ", failedTestNames);

		String userPrompt = PromptBuilder.summaryPrompt(total, passed, failed, skipped, failedNamesJoined);
		String summaryText = GroqClient.chat(PromptBuilder.SUMMARY_SYSTEM_PROMPT, userPrompt);

		writeToDisk(total, passed, failed, skipped, failedNamesJoined, summaryText);
		return summaryText;
	}

	private static void writeToDisk(int total, int passed, int failed, int skipped,
			String failedNamesJoined, String summaryText) {
		try {
			String dirPath = System.getProperty("user.dir") + "/test-output/ai-reports";
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			JSONObject json = new JSONObject();
			json.put("generatedAt", LocalDateTime.now().toString());
			json.put("total", total);
			json.put("passed", passed);
			json.put("failed", failed);
			json.put("skipped", skipped);
			json.put("failedTests", failedNamesJoined);
			json.put("aiSummary", summaryText);

			try (FileWriter writer = new FileWriter(dirPath + "/ai-summary.json")) {
				writer.write(json.toString(2));
			}
		} catch (IOException e) {
			// Writing the JSON file is a nice-to-have, not critical -
			// never fail the suite because a report file couldn't be saved.
			log.warn("Could not write ai-summary.json: {}", e.getMessage());
		}
	}
}

