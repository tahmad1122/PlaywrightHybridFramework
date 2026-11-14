package com.qaframework.listeners;

import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import com.qaframework.ai.FailureAnalyser;
import com.qaframework.ai.ReportSummariser;
import com.qaframework.base.BaseTest;

/**
 * Bridges TestNG's test lifecycle to ExtentReports.
 *
 * Registered on BaseTest via @Listeners, so every test class that extends
 * BaseTest gets ExtentReports logging automatically - no per-test-class
 * wiring, and no risk of a subclass silently skipping reporting by
 * overriding setUp() without calling super().
 */
public class ExtentTestListener implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String description = result.getMethod().getDescription();

		ExtentTest extentTest = BaseTest.extent.createTest(testName, description);

		// Category badges (e.g. Smoke, Regression) - pulled from
		// @Test(groups = {...}) on the test method.
		for (String group : result.getMethod().getGroups()) {
			extentTest.assignCategory(group);
		}

		// Author badge is NOT set here - each test method calls
		// test.get().assignAuthor("...") directly, so it's visible right
		// where the test is written instead of hidden behind reflection.

		// Device/browser badge - pass -Dbrowser=chromium|firefox|webkit.
		extentTest.assignDevice(System.getProperty("browser", "chromium").toUpperCase());

		BaseTest.test.set(extentTest);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		BaseTest.test.get().log(Status.PASS, "Test passed");
		BaseTest.test.remove();
	}

	@Override
	public void onTestFailure(ITestResult result) {
		ExtentTest extentTest = BaseTest.test.get();
		extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable());
		attachScreenshot(result, extentTest);

		// AI root-cause analysis - runs synchronously so it lands in the
		// report right below the failure. GroqClient never throws, so this
		// can't break the test run even if the AI call fails or times out.
		String testName = result.getMethod().getMethodName();
		String errorText = result.getThrowable() == null ? "Unknown error" : result.getThrowable().toString();
		String aiAnalysis = FailureAnalyser.analyse(testName, errorText);
		extentTest.log(Status.INFO, "<b>🤖 AI Root Cause Analysis:</b><br>" + aiAnalysis.replace("\n", "<br>"));

		BaseTest.test.remove();
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		BaseTest.test.get().log(Status.SKIP, "Test skipped: " + result.getThrowable());
		BaseTest.test.remove();
	}

	@Override
	public void onFinish(ITestContext context) {
		// flush() happens in BaseTest's @AfterSuite instead, so the report
		// is written once per suite even if onFinish fires per <test> block.

		int passed = context.getPassedTests().size();
		int failed = context.getFailedTests().size();
		int skipped = context.getSkippedTests().size();
		int total = passed + failed + skipped;

		Set<String> failedTestNames = context.getFailedTests().getAllResults().stream()
				.map(r -> r.getMethod().getMethodName())
				.collect(Collectors.toSet());

		String aiSummary = ReportSummariser.generate(total, passed, failed, skipped, failedTestNames);

		// Own top-level entry in the report (not attached to any single
		// test) so it reads as a suite-level executive summary.
		BaseTest.extent.createTest("🤖 AI Executive Summary")
				.log(Status.INFO, aiSummary.replace("\n", "<br>"));
	}


	/**
	 * Grabs a screenshot from the live Playwright page (via BaseTest) and
	 * embeds it directly in the HTML report as base64.
	 */
	private void attachScreenshot(ITestResult result, ExtentTest extentTest) {
		Object instance = result.getInstance();
		if (!(instance instanceof BaseTest)) {
			return;
		}

		Page page = ((BaseTest) instance).getPage();
		if (page == null) {
			return;
		}

		try {
			byte[] screenshotBytes = page.screenshot();
			String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
			extentTest.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
		} catch (Exception e) {
			extentTest.log(Status.WARNING, "Could not capture screenshot: " + e.getMessage());
		}
	}
}
