package com.qaframework.pages;

import org.slf4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qaframework.utills.LoggerUtil;

public class BookingPage {

	private static final Logger log = LoggerUtil.getLogger(EventDetailsPage.class);

	Page page;
	Locator heading;
	Locator bookingcard;
	
	
	
	public BookingPage(Page page) {
		this.page=page;
		heading=page.locator("//h1[@class='text-3xl font-bold text-gray-900']");
		bookingcard=page.getByTestId("booking-card");
	}
	
	public String getHeadingText() {
		String text = heading.innerText();
		log.info("Heading text: {}", text);
		return text;
	}
	
	public int getTotalBookings() {
	    bookingcard.first().waitFor();
	    int count = bookingcard.count();
	    log.info("Total bookings available: {}", count);
	    return count;
	}

	public void clickBookingByReference(String ref) {

	    int totalBookings = getTotalBookings();

	    for (int i = 0; i < totalBookings; i++) {

	        Locator card = bookingcard.nth(i);
	        String bookingText = card.innerText().trim();

	        if (bookingText.contains(ref)) {
	            Locator viewDetailsBtn = card.getByText("View Details");  // scoped to THIS card only
	            viewDetailsBtn.waitFor();
	            viewDetailsBtn.click();
	            log.info("Booking found. Clicked 'View Details' for ref: {}", ref);
	            return;
	        }
	    }

	    throw new RuntimeException("Booking not found with ref: " + ref);
	}
	
	
}
