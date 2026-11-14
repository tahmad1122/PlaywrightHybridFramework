package com.qaframework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class DashboardPage {
	
	private final Page page;
	private final Locator adminText;

    public DashboardPage(Page page) {
    	 this.page = page;
         adminText = page.getByText("Admin");
    }
    
    public void verifyDashboardLoaded() {
        PlaywrightAssertions.assertThat(adminText).isVisible();
    }
    
//    public LoginPage logout() {
//        openProfileMenu();
//        logoutButton.click();
//        LoggerUtil.logStep(log, "Logged out. Current URL: " + page.url());
//        return new LoginPage(page);
//    }


}
