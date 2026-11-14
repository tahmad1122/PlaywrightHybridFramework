package com.qaframework.pages;

import org.slf4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qaframework.utills.LoggerUtil;

public class EventsPage {

	private static final Logger log = LoggerUtil.getLogger(EventsPage.class);
	private Page page;
	private Locator browseEvent;
	private Locator listofEvents;

	public EventsPage(Page page) {
		this.page = page;
		browseEvent = page.getByText("Browse Events →");
		listofEvents = page.getByText("Book Now");
	}

	public void clickEvent() {
		browseEvent.click();
		log.info("Clicked Browse Events → button: " + page.url());
	}

	public void getListOfEvents() {
		listofEvents.first().waitFor();
		int count = listofEvents.count();
		log.info("Total Events Available: {}", count);

		for (int i = 0; i < count; i++) {
			listofEvents.first().waitFor();
			if (i == 1) {
				log.info("Clicking event at index: {}", i);
				listofEvents.nth(i).click();
				break;
			}

		}
	}
}
