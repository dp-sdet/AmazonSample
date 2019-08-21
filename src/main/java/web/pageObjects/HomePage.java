package web.pageObjects;

import objectlocator.WebObjectLocators;

import static org.testng.Assert.assertEquals;

import core.Until;

import core.WebDriverBase;
import dataReader.TestData;

public class HomePage extends WebDriverBase {
	
	private static String categorydropDown = WebObjectLocators.getLocator("categorydropDown");
	private static String searchbox = WebObjectLocators.getLocator("searchbox");
	private static String searchBtn = WebObjectLocators.getLocator("searchBtn");
	private static String bookTitle = WebObjectLocators.getLocator("bookTitle");
	private static String authorName = WebObjectLocators.getLocator("authorName");
	private static String bookType = WebObjectLocators.getLocator("bookType");
	private static String price = WebObjectLocators.getLocator("price");
	private static String description = WebObjectLocators.getLocator("description");
	
	private static String SearchBoxText = TestData.getData("SearchBoxText");
	
	public void loadPage(String url) {
		goToUrl(url);
		assertEquals(getWindowTitle(), "Amazon.com: Online Shopping for Electronics, Apparel, Computers, Books, DVDs & more");
		log.info("URL gets loaded successfully");

	}
	public void selectCategoryDropDownPresent() {
		isElementPresent(categorydropDown);
		log.info("Category dropdown is present");
		selectValueFromDropDown(categorydropDown, "Books");
		log.info("Books is selected");
	}
	
	public void enterSearchBoxPresent() {
		isElementPresent(searchbox);
		log.info("searchbox is present");
		type(searchbox, SearchBoxText);
		log.info("Entered the text "+SearchBoxText+" in the search area");
	}
	
	public void clickSearchBtn() {
		clickOnElement(searchBtn);
		log.info("Search button is pressed");
	}
	
	public void firstResultData() {
		
		System.out.println("Title --->" + getText(bookTitle));
		System.out.println("Author --->" + getText(authorName));
		System.out.println("BookType --->" + getText(bookType));
		System.out.println("Price --->" + getText(price));
		System.out.println("Description --->" + getText(description));
		
		
	}
}
