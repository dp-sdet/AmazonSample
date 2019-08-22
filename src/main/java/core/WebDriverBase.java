package core;

/**
 * Class containing re-usuable selenium methods 
 * Most of the methods have been given self-explanatory names for better understanding.
 */

import exception.CustomException;
import objectlocator.ObjectLocators;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

public class WebDriverBase {
	protected WebDriver driver;
	private static final int TIMEOUT_IN_SECONDS = 60;
	private static final int POLL_INTERVAL = 500;
	protected static final Logger log = Logger.getLogger(WebDriverBase.class);

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public <U> U wait(ExpectedCondition<U> condition) {
		FluentWait<WebDriver> wait = (new FluentWait(this.driver)).ignoring(RuntimeException.class)
				.withTimeout(60L, TimeUnit.SECONDS).pollingEvery(500L, TimeUnit.MILLISECONDS);
		try {
			return (U) wait.until(condition);
		} catch (TimeoutException err) {

			String errMessage = "Bot encountered a timeout while waiting for a condition,  "
					+ err.getLocalizedMessage();
			throw new CustomException(errMessage);
		}
	}

	public <U> U wait(ExpectedCondition<U> condition, int timeoutInSeconds) {
		FluentWait<WebDriver> wait = (new FluentWait(this.driver)).ignoring(RuntimeException.class)
				.withTimeout(timeoutInSeconds, TimeUnit.SECONDS).pollingEvery(500L, TimeUnit.MILLISECONDS);
		try {
			return (U) wait.until(condition);
		} catch (TimeoutException err) {

			String errMessage = "Bot encountered a timeout while waiting for a condition,  "
					+ err.getLocalizedMessage();
			throw new CustomException(errMessage);
		}
	}

	public void sleep(int timeOutInSeconds) {
		try {
			Thread.sleep((timeOutInSeconds * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void pageLoadTime(int loadTimeInSec) {
		this.driver.manage().timeouts().pageLoadTimeout(loadTimeInSec, TimeUnit.SECONDS);
	}

	public void refresh() {
		this.driver.navigate().refresh();
	}

	protected void clickOnElement(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		WebElement element = this.driver.findElement(locator);
		if (element.isDisplayed()) {

			element.click();
			log.info("Clicked on element: " + locator);
		}
	}

		protected void clickOnElementWithText(String elementLocator, String elementText) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		List<WebElement> elementList = this.driver.findElements(locator);
		int index = 0;
		for (WebElement element : elementList) {
			if (element.getText().trim().equalsIgnoreCase(elementText) && element.isDisplayed()) {
				element.click();
				index++;
				break;
			}
		}
		if (index == elementList.size()) {
			throw new RuntimeException("Could not locate any element described by the locator "
					+ elementLocator.toString() + " with text " + elementText);
		}
	}

	protected void clearText(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		WebElement element = this.driver.findElement(locator);
		if (element.isDisplayed()) {

			element.clear();
			log.info("Element cleared: " + locator);
		}
	}

	protected void type(String elementLocator, String text) {
		log.info("Entering Text");
		WebElement element = (WebElement) wait(Until.elementToBeClickable(elementLocator));
		sleep(1);
		element.sendKeys(new CharSequence[] { text });
		sleep(1);
		log.info("Entered " + text + " into the " + elementLocator + " text field");
	}

	protected void goToUrl(String url) {
		log.info("Loading the URL:" + url);
		this.driver.get(url);
	}

	protected void selectValueFromDropDown(String elementLocator, String visibleText) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Selecting " + visibleText + " from the DropDown");
		WebElement dropDownElement = this.driver.findElement(locator);
		Select dropDownSelect = new Select(dropDownElement);
		dropDownSelect.selectByVisibleText(visibleText);
	}

	protected String getFirstValueFromDropDown(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		WebElement dropDownElement = this.driver.findElement(locator);
		Select dropDownSelect = new Select(dropDownElement);
		return dropDownSelect.getFirstSelectedOption().getText();
	}

	protected void selectValueFromDropDown(String elementLocator, int index) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Selecting " + index + " from the DropDown");
		WebElement dropDownElement = this.driver.findElement(locator);
		Select dropDownSelect = new Select(dropDownElement);
		dropDownSelect.selectByIndex(index);
	}

	protected void hoverOver(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		Actions action = new Actions(this.driver);
		log.info("Hovering over the mouse on the element " + locator);
		WebElement element = this.driver.findElement(locator);
		action.moveToElement(element).build().perform();
	}

	protected void switchToWindowByTitle(String titleOfNewWindow) {
		Set<String> windowHandles = this.driver.getWindowHandles();
		for (String windowHandle : windowHandles) {
			this.driver.switchTo().window(windowHandle);
			if (this.driver.getTitle().contains(titleOfNewWindow)) {
				break;
			}
		}
	}

	protected void clearCookies() {
		this.driver.manage().deleteAllCookies();
	}

	protected String getCurrentUrl() {
		return this.driver.getCurrentUrl();
	}

	protected void closePopupWindow(String windowID) {
		this.driver.switchTo().window(windowID).close();
	}

	protected boolean isElementPresent(String propKey) {
		By locator = ObjectLocators.getBySelector(propKey);
		log.debug("Checking the presence of the Element: " + propKey + " : " + propKey);
		return isElementPresent(locator);
	}

	protected WebElement findElement(String propKey) {
		By locator = ObjectLocators.getBySelector(propKey);
		return this.driver.findElement(locator);
	}

	protected boolean isElementVisible(String propKey) {
		By locator = ObjectLocators.getBySelector(propKey);
		log.debug("Checking the presence of the Visble: " + propKey + " : " + propKey);
		return isElementVisible(locator);
	}

	protected boolean isElementChecked(String propKey) {
		By locator = ObjectLocators.getBySelector(propKey);
		log.debug("Checking the presence of the Visble: " + propKey + " : " + propKey);
		return isElementChecked(locator);
	}

	private boolean isElementPresent(By by) {
		try {
			WebElement element = this.driver.findElement(by);
			if (element != null) {
				log.debug("Element is present: " + by.toString());
				return true;
			}
			log.warn("Element is NOT present: " + by.toString());
			return false;
		} catch (NoSuchElementException e) {
			return false;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return false;
		}
	}

	private boolean isElementChecked(By by) {
		try {
			WebElement element = this.driver.findElement(by);
			if (element.isSelected()) {
				log.debug("Element is checked: " + by.toString());
				return true;
			}
			log.warn("Element is NOT checked: " + by.toString());
			return false;
		} catch (NoSuchElementException e) {
			return false;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return false;
		}
	}

	private boolean isElementVisible(By by) {
		try {
			WebElement element = this.driver.findElement(by);
			if (element.isDisplayed()) {
				log.debug("Element is present: " + by.toString());
				return true;
			}
			log.warn("Element is NOT present: " + by.toString());
			return false;
		} catch (NoSuchElementException e) {
			return false;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return false;
		}
	}

	protected void switchToFrame(int frameId) {
		log.info("Switching to the frame: " + frameId);
		this.driver.switchTo().frame(frameId);
	}

	protected void switchToFrame(String frameName) {
		log.info("Switching to the frame: " + frameName);
		By locator = null;
		try {
			locator = ObjectLocators.getBySelector(frameName);
		} catch (CustomException e) {
			locator = null;
		}
		if (locator == null) {
			wait(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
		} else {
			wait(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
		}
	}

	protected String getText(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Getting the text of the element: " + locator);
		return this.driver.findElement(locator).getText();
	}

	protected String getTextWithInElement(WebElement baseElement, String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Getting the text of the element: " + locator);
		return baseElement.findElement(locator).getText();
	}

	protected String getWindowTitle() {
		log.info("Getting the title of the page");
		return this.driver.getTitle();
	}

	protected void clearTextBox(String elementLocator) {
		((WebElement) wait(Until.elementToBeClickable(elementLocator))).clear();
	}

	protected int countXpath(String elementLocator) {
		return this.driver.findElements(ObjectLocators.getBySelector(elementLocator)).size();
	}

}
