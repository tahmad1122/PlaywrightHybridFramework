package com.qaframework.tests.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.qaframework.base.BaseTest;
import com.qaframework.config.ConfigManger;
import com.qaframework.listeners.ExtentTestListener;
import com.qaframework.pages.DashboardPage;
import com.qaframework.pages.LoginPage;
import com.qaframework.pages.Login_Page;

public class LoginUITest extends BaseTest {

	private LoginPage loginPage;
	String userName = ConfigManger.getProperty("login.email");
	String password = ConfigManger.getProperty("login.password");

//	@Test
//	public void loginTest() throws Exception {
//		
//		Login_Page loginPage=new Login_Page(page);
//		loginPage.setEmail(userName);
//		loginPage.setPassword(password);
//		loginPage.setLogin();
//		loginPage.invalidCredentials();
//		loginPage.verifyLogin();
//		page.waitForTimeout(2000);
//	}

	@Test(priority = 1, groups = { "smoke",
			"regression" }, description = "Valid credentials should redirect the user to the dashboard")
	public void validLoginRedirectsToDashboard() {
		test.get().assignAuthor("Ahmad");
		test.get().info(MarkupHelper.createLabel("Logging in with valid credentials: " + userName, ExtentColor.BLUE));

		loginPage = new LoginPage(page);
		DashboardPage dashboardPage = loginPage.login(userName, password);

		test.get().info("Verifying dashboard loaded");
		dashboardPage.verifyDashboardLoaded();
	}

	@Test(priority = 2, groups = {"smoke",
			"regression" }, description = "Invalid credentials should show an error message and not log the user in")
	public void invalidLoginShowsErrorMessage() {
		test.get().assignAuthor("Ahmad");
		String userName = ConfigManger.getProperty("login.invalid.email");
		String password = ConfigManger.getProperty("login.invalid.password");

		test.get().info(MarkupHelper.createLabel("Logging in with invalid credentials: " + userName, ExtentColor.RED));

		loginPage = new LoginPage(page);
		loginPage.login(userName, password);

		String actualError = loginPage.getInvalidLoginError();
		test.get().info("Error message displayed: " + actualError);
		Assert.assertEquals(actualError, "Invalid email or password", "Error message did not match expected text");
	}

}
