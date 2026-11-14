package com.qaframework.pages;

import org.slf4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qaframework.utills.LoggerUtil;

public class BookingDetailsPage {
	private static final Logger log = LoggerUtil.getLogger(EventDetailsPage.class);

	Page page;
	Locator eventTitle;
	Locator cancelBuuton;
	Locator cancelPopUp;
	Locator cancelMessage;
	
	public BookingDetailsPage(Page page) {
		this.page=page;
		eventTitle=page.locator("//h1[@class='text-2xl font-bold text-gray-900']");
		cancelBuuton=page.getByText("Cancel Booking");
		cancelPopUp=page.getByTestId("confirm-dialog-yes");
		cancelMessage=page.getByText("Booking cancelled successfully");
	}
	
	public String getValue(String label) {
		Locator value = page.getByText(label, new Page.GetByTextOptions().setExact(true))
				.locator("xpath=following-sibling::*[1]"); // DOM ke hisaab se adjust karo
				String innerText = value.innerText();
		log.info("Inner Text: "+innerText);
		return innerText;
	}
	
	public void clickCancelBooking() {
		cancelBuuton.click();
	}
	
	public void clickCancelPopUp() {
		cancelPopUp.click();
	}
	
	public String getCancelMessage() {
		cancelMessage.waitFor();
		String text = cancelMessage.innerText().trim();
		log.info("Cancel message: {}", text);
		return text;
	}

}
