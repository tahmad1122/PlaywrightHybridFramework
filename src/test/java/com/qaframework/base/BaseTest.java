package com.qaframework.base;

import java.util.ArrayList;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.qaframework.config.ConfigManger;
import com.qaframework.listeners.ExtentTestListener;
import com.qaframework.reports.ExtentManager;

@Listeners(ExtentTestListener.class)
public class BaseTest {

	protected Playwright playwright;
	protected Browser browser;
	protected BrowserContext context;
	protected Page page;

	public static ExtentReports extent;
	public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	@BeforeSuite(alwaysRun = true)
	public void setupReport() {
		if (extent == null) {
			ExtentManager extentClass = new ExtentManager();
			extentClass.startReport();
			extent = extentClass.extentReports;
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		playwright = Playwright.create();
		ArrayList<String> arguments = new ArrayList<String>();
		arguments.add("--start-maximized");
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(arguments));
		context = browser.newContext(new NewContextOptions().setViewportSize(null));
		page = context.newPage();
		page.navigate(ConfigManger.getProperty("baseUrl"));
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
	    if (context != null) context.close();
	    if (browser != null) browser.close();
	    if (playwright != null) playwright.close();
	}

	public Page getPage() {
		return page;
	}

	@AfterSuite(alwaysRun = true)
	public void flushReport() {
		if (extent != null) {
			extent.flush();
		}
	}
}