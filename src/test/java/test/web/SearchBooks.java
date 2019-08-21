package test.web;

import org.testng.annotations.Test;

import pagefactory.PageFactory;

import constant.Constant;
import web.pageObjects.HomePage;


public class SearchBooks extends Constant {
	@Test(priority = 1)
	public void CaseStudy() throws InterruptedException {
		loadurl();
		HomePage homePage = PageFactory.instantiatePage(driver, HomePage.class);
		homePage.loadPage(URL);
		homePage.selectCategoryDropDownPresent();
		homePage.enterSearchBoxPresent();
		homePage.clickSearchBtn();
		homePage.firstResultData();
	}

}
