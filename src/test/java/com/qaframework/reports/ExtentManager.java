package com.qaframework.reports;



import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Builds the ExtentReports object and its HTML (Spark) reporter.
 * This class only configures the report - it does NOT create test
 * entries or log anything. That's the listener's job.
 */
public class ExtentManager {

	public ExtentReports extentReports;
	public ExtentSparkReporter sparkReporter;

	public void startReport() {
		// Fixed filename (not timestamped) - overwritten fresh every run,
		// so you always look at one up-to-date report instead of a pile
		// of old ones.
		String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";

		// Make sure the folder exists - ExtentSparkReporter will throw
		// an IOException on a fresh checkout if it doesn't.
		File reportDir = new File(reportPath).getParentFile();
		if (!reportDir.exists()) {
			reportDir.mkdirs();
		}

		extentReports = new ExtentReports();
		sparkReporter = new ExtentSparkReporter(reportPath);
		extentReports.attachReporter(sparkReporter);

		extentReports.setSystemInfo("Framework", "Playwright Java Automation Framework");
		extentReports.setSystemInfo("Environment", "QA");
		extentReports.setSystemInfo("Execution Type", "Automated Regression Suite");
		extentReports.setSystemInfo("OS", System.getProperty("os.name"));
		extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));

		sparkReporter.config().setDocumentTitle("Automation Execution Report");
		sparkReporter.config().setReportName("Playwright Java Automation Report");
//		sparkReporter.config().setTheme(Theme.DARK);
		sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
		sparkReporter.config().setOfflineMode(true); // embeds CSS/JS - fully portable HTML file
	}
}