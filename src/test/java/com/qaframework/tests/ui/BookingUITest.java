package com.qaframework.tests.ui;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.qaframework.base.BaseTest;
import com.qaframework.config.ConfigManger;
import com.qaframework.pages.BookingPage;
import com.qaframework.pages.EventDetailsPage;
import com.qaframework.pages.EventsPage;
import com.qaframework.pages.LoginPage;

public class BookingUITest extends BaseTest {
	

	@Test(groups = { "smoke", "regression" },
			description = "Log in, browse events, open one, and verify its details render")
	public void endToEndBookingFlow() throws Exception {
		test.get().assignAuthor("Ahmad");
		String userName = ConfigManger.getProperty("login.email");
		String password = ConfigManger.getProperty("login.password");

		test.get().info(MarkupHelper.createLabel("Logging in as: " + userName, ExtentColor.BLUE));

		LoginPage loginPage = new LoginPage(page);
		loginPage.login(userName, password);

		test.get().info("Browsing to the events list");
		EventsPage eventsPage = new EventsPage(page);
		eventsPage.clickEvent();
		eventsPage.getListOfEvents();

		test.get().info("Opening event details");
		EventDetailsPage eventDetailsPage = new EventDetailsPage(page);
		String eventTitle = eventDetailsPage.getEventTitle();
		test.get().info("<b>Event title: </b>" + eventTitle);
		
		Map<String, String> eventDetails = eventDetailsPage.getEventDetails();
		for (Map.Entry<String, String> entry : eventDetails.entrySet()) {
		    test.get().info("<b>"+entry.getKey() + "</b>: " + entry.getValue());
		}
		
		test.get().info("<b>Step 1</b>: Booking tickets for the event");
		String totalTicket=ConfigManger.getProperty("booking.tickets");
		int ticket=Integer.parseInt(totalTicket);
		eventDetailsPage.setNumberOfTickets(ticket);
		String ticketCount = eventDetailsPage.getTicketCount();
		test.get().info(MarkupHelper.createLabel("Number of tickets selected: " + ticketCount, ExtentColor.BLUE));
		
		test.get().info("<b>Step 2</b>: Entering attendee details");
		String name=ConfigManger.getProperty("attendee.name");
		eventDetailsPage.setFullName(name);
		String mail = ConfigManger.getProperty("attendee.mail");
		eventDetailsPage.setEmail(mail);
		String phone = ConfigManger.getProperty("attendee.phone");
		eventDetailsPage.setPhoneNumber(phone);
		test.get().info("Attendee details entered: Name="+name+", Email="+mail+", Phone="+phone);
		
		test.get().info("<b>Step 3</b>: Verifying price calculation");
		String subTotal = eventDetailsPage.getSubTotal();
		test.get().info(MarkupHelper.createLabel("Subtotal: " + subTotal, ExtentColor.ORANGE));
		
		String totalPrice = eventDetailsPage.getTotalPrice();
		test.get().info(MarkupHelper.createLabel("Total price: " + totalPrice, ExtentColor.ORANGE));

		test.get().info("<b>Step 4</b>: Confirming the booking");
		eventDetailsPage.clickConfirmBookingButton();
		test.get().pass("Booking confirmed successfully for " + ticketCount + " ticket(s)");
		
		test.get().info("<b>Step 5</b>: Verifying booking success message");
		String actualMessage = eventDetailsPage.bookingConfirmMessage();
		test.get().info("Confirmation message displayed: <b>" + actualMessage + "</b>");
		Assert.assertEquals(actualMessage, "Booking Confirmed! 🎉", "Confirmation message did not match expected text");

		test.get().info("<b>Step 6</b>: Fetching booking reference");
		String bookingReference = eventDetailsPage.getBookingRefrence();
		test.get().info("Booking Reference: <b>" + bookingReference + "</b>");

		test.get().info("<b>Step 7</b>: Navigating to 'My Bookings' page");
		eventDetailsPage.clickViewMyBookings();

		BookingPage bookingPage = new BookingPage(page);

		String headingText = bookingPage.getHeadingText();
		test.get().info("Booking heading: <b>" + headingText + "</b>");

		int totalBookings = bookingPage.getTotalBookings();
		test.get().info("Total bookings: <b>" + totalBookings + "</b>");

		test.get().info("<b>Step 8</b>: Clicking booking with reference: <b>" + bookingReference + "</b>");
		bookingPage.clickBookingByReference(bookingReference);
		test.get().info("Clicked booking with reference: <b>" + bookingReference + "</b>");
		

	}
}
