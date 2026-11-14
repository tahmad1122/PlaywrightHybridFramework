package com.qaframework.pages;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.qaframework.utills.LoggerUtil;

public class EventDetailsPage {

	private static final Logger Log = LoggerUtil.getLogger(EventDetailsPage.class);

	 final Page page;
	private final Locator heading;
	private final Locator eventDetails;
	private final Locator numberOfTickets;
	private final Locator ticketCount;
	private final Locator fullName;
	private final Locator email;
	private final Locator phoneNumber;
	private final Locator subTotal;
	private final Locator totalPrice;
	private final Locator confirmBookingButton;
	private final Locator bookingConfirmMsg;
	private final Locator bookingRefrence;
	private final Locator viewBookings;


	public EventDetailsPage(Page page) {

		this.page = page;
		heading = page.locator("//h1[@class='text-2xl sm:text-3xl font-bold text-gray-900 mb-4']");
		eventDetails = page.locator("//p[@class='text-sm text-gray-800 font-medium mt-0.5']");
		numberOfTickets=page.getByText("+");
		ticketCount=page.locator("#ticket-count");
		fullName=page.getByLabel("Full Name*");
		email=page.getByLabel("Email*");
		phoneNumber=page.getByLabel("Phone Number*");
		subTotal=page.locator("//div[@class='flex justify-between text-gray-600']");
		totalPrice=page.locator("//span[@class='text-indigo-700']");
		confirmBookingButton=page.getByText("Confirm Booking");
		bookingConfirmMsg=page.getByText("Booking Confirmed! 🎉");
//		viewBookings=page.getByRole(AriaRole.TEXTBOX,new Page.GetByRoleOptions().setName("View My Bookings"));
		viewBookings=page.getByText(("View My Bookings"));
		bookingRefrence=page.locator("//span[@class='booking-ref font-mono font-bold text-indigo-600']");

	}

	public String getEventTitle() {

		String eventTitle = heading.innerText();
		Log.info("Event Title is: {}", eventTitle);
		return eventTitle;
	}

//	public void getEventDetails() {
//
//		int count = eventDetails.count();
//
//		Log.info("Total Event Details: {}", count);
//
//		String[] labels = { "day", "time", "venue", "city", "available", "price" };
//		for (int i = 0; i < count; i++) {
//			String detail = eventDetails.nth(i).innerText();
//			Log.info("Event Detail [{}]: {}: {}", i, labels[i], detail);
//		}
//	}
	public Map<String, String> getEventDetails() {

		int count = eventDetails.count();
		String[] labels = { "day", "time", "venue", "city", "available", "price" };

		Map<String, String> details = new LinkedHashMap<>();
		for (int i = 0; i < count; i++) {
			String detail = eventDetails.nth(i).innerText();
			details.put(labels[i], detail);
			Log.info("Event Detail: {}: {}", labels[i], detail);
		}
		return details;
	}
	
	public void setNumberOfTickets(int numberOfClicks) {
		Log.info("Clicking '+' {} time(s) to set ticket count", numberOfClicks);
		for (int i = 1; i <numberOfClicks; i++) {
			numberOfTickets.click();
		}
	}
	
	public String getTicketCount() {
		String innerText = ticketCount.innerText();
		Log.info("Ticket count is: {}", innerText);
		return innerText;
	}
	
	public void setFullName(String name) {
		Log.info("Entering full name: {}", name);
		fullName.fill(name);
	}
	
	public void setEmail(String mail) {
		Log.info("Entering email: {}", mail);
		email.fill(mail);
	}
	public void setPhoneNumber(String number) {
		Log.info("Entering phone number: {}", number);
		phoneNumber.fill(number);
	}

	public String getSubTotal() {
		String innerText = subTotal.innerText();
		Log.info("Subtotal is: {}", innerText);
		return innerText;
	}

	public String getTotalPrice() {
		String innerText = totalPrice.innerText();
		Log.info("Total price is: {}", innerText);
		return innerText;
	}

	public void clickConfirmBookingButton() {
		Log.info("Clicking Confirm Booking button");
		confirmBookingButton.click();
	}
	public String bookingConfirmMessage() {
		String innerText = bookingConfirmMsg.innerText();
		Log.info("Booking confirmation message is: {}", innerText);
		return innerText;
	}
	
	public String getBookingRefrence() {
		 String refText = bookingRefrence.innerText();
		 Log.info("Booking ref: {}", refText);
		 return refText;
	}
	
	public void clickViewMyBookings() {
		viewBookings.click();
	}
}
