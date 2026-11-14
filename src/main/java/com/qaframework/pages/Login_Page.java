package com.qaframework.pages;

import org.slf4j.Logger;
import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.qaframework.utills.LoggerUtil;

public class Login_Page {

    private static final Logger log =  LoggerUtil.getLogger(LoginPage.class);

	private Page page;

	public Login_Page(Page page) {
		this.page = page;
	}

	// Locators
//	Locator email=page.getByLabel("Email");
//	Locator pass=page.locator("#password");
//	Locator submit=page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

//	Locator email=page.getByPlaceholder("E-Mail Address");
//	Locator pass=page.getByPlaceholder("Password");
//	Locator submit=page.locator("input[type='submit']");

	// Action method
	public void setEmail(String username) {
		page.getByLabel("Email").fill(username);
	}

	public void setPassword(String password) {
		page.locator("#password").fill(password);
	}

	public void setLogin() {
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
		System.out.println("After click URL: " + page.url());
		log.info("Clicked login button. Current URL: " + page.url());


	}
	
	public void invalidCredentials() {
		Locator email = page.locator("#email");
		String userValue = email.inputValue();
		String passwordValue = page.locator("#password").inputValue();

		Locator byText = page.getByText("Invalid email or password");
		String innerText = byText.innerText();
		System.out.println(innerText + ", UserName: " + userValue + ", Password: " + passwordValue);
		
	}

	public void verifyLogin() {
		Locator locator = page.getByText("Admin");
		locator.waitFor();
		System.out.println("Visible: " + locator.isVisible());
		Assert.assertTrue(locator.isVisible(), "Admin text is not displayed");
	}

}

// Action methods
//    public void setEmail(String username) {
//        page.getByPlaceholder("E-Mail Address").fill(username);
//    }
//
//    public void setPassword(String password) {
//        page.getByPlaceholder("Password").fill(password);
//    }
//
//    public void setLogin() {
//        page.locator("input[type='submit']").click();
//    }
//	
//}
