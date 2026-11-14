package com.qaframework.pages;

import org.slf4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.qaframework.utills.LoggerUtil;

public class LoginPage {

    private static final Logger log = LoggerUtil.getLogger(LoginPage.class);

	private final Page page;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator submitButton;
    private final Locator errorMessage;
    
    public LoginPage(Page page) {
        this.page = page;
        emailInput = page.getByLabel("Email");
        passwordInput = page.locator("#password");
        submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));
        errorMessage = page.getByText("Invalid email or password");
    }
    
    public void setEmail(String username) {
    	emailInput.fill(username);
    	 log.info("Entered email: " + username);
	}
    
    public void setPassword(String password) {
    	passwordInput.fill(password);
    	 log.info( "Entered password");
	}
    
    public void clickLogin() {
    	submitButton.click();
        log.info("Clicked login. Current URL: {}", page.url());
//        LoggerUtil.logStep(log, "Clicked login button. Current URL: " + page.url());
    }
    
    public DashboardPage login(String username, String password) {
        setEmail(username);
        setPassword(password);
        clickLogin();
        return new DashboardPage(page);
    }
    
    public String getInvalidLoginError() {
        errorMessage.waitFor();
        String text = errorMessage.innerText();
        log.info("Invalid login error displayed: {}", text);
        return text;
    }
}
