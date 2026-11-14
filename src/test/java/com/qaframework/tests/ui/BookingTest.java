package com.qaframework.tests.ui;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.qaframework.base.BaseTest;
import com.qaframework.config.ConfigManger;
import com.qaframework.pages.BookingDetailsPage;
import com.qaframework.pages.BookingPage;
import com.qaframework.pages.EventDetailsPage;
import com.qaframework.pages.EventsPage;
import com.qaframework.pages.LoginPage;

public class BookingTest extends BaseTest {

	private static final String BOOKING_CONFIRMED_MSG = "Booking Confirmed! \uD83C\uDF89";
	private static final String CANCEL_MSG = "Booking cancelled successfully";

	private EventDetailsPage eventPage;

	@Test(groups = { "regression" }, description = "Book an event, verify booking details, cancel booking")
	public void shouldBookAndCancelEventSuccessfully() {
		test.get().assignAuthor("Ahmad");
		loginAndOpenEvent();
		String bookingReference = bookEvent();
		openBookingFromMyBookings(bookingReference);
		verifyBookingDetails(bookingReference);
	}

	public void loginAndOpenEvent() {
		String email = ConfigManger.getProperty("login.email");
		String password = ConfigManger.getProperty("login.password");

		test.get().info("<b>Step 1</b>: Logging in as: " + email);
		new LoginPage(page).login(email, password);

		test.get().info("<b>Step 2</b>: Opening an event");
		EventsPage eventsPage = new EventsPage(page);
		eventsPage.clickEvent();
		eventsPage.getListOfEvents();

		test.get().info("<b>Step 3</b>: Event details page ready");
		eventPage = new EventDetailsPage(page);
	}

	// helper method
	private String bookEvent() {
		int tickets = Integer.parseInt(ConfigManger.getProperty("booking.tickets"));

		test.get().info("<b>Step 4</b>: Selecting tickets: " + tickets);
		eventPage.setNumberOfTickets(tickets);
		Assert.assertEquals(eventPage.getTicketCount(), String.valueOf(tickets), "Ticket count mismatch");

		test.get().info("<b>Step 5</b>: Entering attendee details");
		eventPage.setFullName(ConfigManger.getProperty("attendee.name"));
		eventPage.setEmail(ConfigManger.getProperty("attendee.email"));
		eventPage.setPhoneNumber(ConfigManger.getProperty("attendee.phone"));

		test.get().info("<b>Step 6</b>: Confirming booking");
		eventPage.clickConfirmBookingButton();

		test.get().info("<b>Step 7</b>: Verifying booking success message");
		String actualMessage = eventPage.bookingConfirmMessage();
		test.get().info("Confirmation message displayed: <b>" + actualMessage + "</b>");
		Assert.assertEquals(actualMessage, BOOKING_CONFIRMED_MSG, "Confirmation message mismatch");

		test.get().info("<b>Step 8</b>: Fetching booking reference");
		String reference = eventPage.getBookingRefrence();
		test.get().info("Booking Reference: <b>" + reference + "</b>");
		return reference;
	}

	private void openBookingFromMyBookings(String bookingReference) {
		test.get().info("<b>Step 9</b>: Navigating to 'My Bookings' page");
		eventPage.clickViewMyBookings();

		BookingPage bookingPage = new BookingPage(page);

		test.get().info("<b>Step 10</b>: My Bookings page");
		test.get().info("Booking heading: <b>" + bookingPage.getHeadingText() + "</b>");
		int totalBookings = bookingPage.getTotalBookings();
		test.get().info("Total bookings: <b>" + totalBookings + "</b>");
		Assert.assertTrue(totalBookings >= 1, "No bookings found in My Bookings");

		test.get().info("<b>Step 11</b>: Opening booking with reference: <b>" + bookingReference + "</b>");
		bookingPage.clickBookingByReference(bookingReference);
	}

	private void verifyBookingDetails(String bookingReference) {
		BookingDetailsPage details = new BookingDetailsPage(page);

		test.get().info("<b>Step 12</b>: Booking Details page");

		test.get().info("<b>Event Details</b>");
		for (String label : new String[] { "Event", "Category", "Date", "Venue", "City" }) {
			test.get().info(label + ": <b>" + details.getValue(label) + "</b>");
		}

		test.get().info("<b>Customer Details</b>");
		String name = details.getValue("Name");
		String mail = details.getValue("Email");
		String phone = details.getValue("Phone");
		test.get().info("Name: <b>" + name + "</b>");
		test.get().info("Email: <b>" + mail + "</b>");
		test.get().info("Phone: <b>" + phone + "</b>");

		test.get().info("<b>Payment Summary</b>");
		String ticketsOnPage = details.getValue("Tickets");
		test.get().info("Tickets: <b>" + ticketsOnPage + "</b>");
		test.get().info("Price per ticket: <b>" + details.getValue("Price per ticket") + "</b>");
		test.get().info("Total Paid: <b>" + details.getValue("Total Paid") + "</b>");
		test.get().info("Refund: <b>" + details.getValue("Refund") + "</b>");

		test.get().info("<b>Booking Information</b>");
		test.get().info("Booked on: <b>" + details.getValue("Booked on") + "</b>");
		test.get().info("Booking ID: <b>" + details.getValue("Booking ID") + "</b>");

		Assert.assertEquals(name, ConfigManger.getProperty("attendee.name"), "Name mismatch");
		Assert.assertEquals(mail, ConfigManger.getProperty("attendee.email"), "Email mismatch");
		Assert.assertEquals(phone, ConfigManger.getProperty("attendee.phone"), "Phone mismatch");
		Assert.assertEquals(ticketsOnPage, ConfigManger.getProperty("booking.tickets"), "Tickets mismatch");

		test.get().info("<b>Step 13</b>: Clicking 'Cancel Booking'");
		details.clickCancelBooking();
		test.get().info("Clicked 'Cancel Booking' button");

		test.get().info("<b>Step 13.1</b>: Confirming cancellation in popup");
		details.clickCancelPopUp();
		test.get().info("Confirmed cancellation in popup for booking: <b>" + bookingReference + "</b>");

		test.get().info("<b>Step 14</b>: Verifying cancel message");
		String cancelMessage = details.getCancelMessage();
		test.get().info("Cancel message displayed: <b>" + cancelMessage + "</b>");
		Assert.assertEquals(cancelMessage, CANCEL_MSG, "Cancel message mismatch");

		test.get().pass("Booking <b>" + bookingReference + "</b> booked, verified and cancelled successfully");
	}
}