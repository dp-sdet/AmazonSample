package core;
/**
 * Class for providing wait methods so that it can be reused across the pageObjects/ program logic for TC
 * Method names are self explanatory
 */

import objectlocator.ObjectLocators;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Until {
	static Logger log = Logger.getLogger(Until.class);

	public static ExpectedCondition<List<WebElement>> elementsToBeDisplayed(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the elements specified by " + locator + " are displayed");
		return ExpectedConditions.visibilityOfAllElementsLocatedBy(locator);
	}

	public static ExpectedCondition<WebElement> elementToBeDisplayed(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the element " + locator + " is displayed");
		return ExpectedConditions.visibilityOfElementLocated(locator);
	}

	public static ExpectedCondition<List<WebElement>> elementsToBePresent(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the elements specified by " + locator + " are present");
		return ExpectedConditions.presenceOfAllElementsLocatedBy(locator);
	}

	public static ExpectedCondition<WebElement> elementToBePresent(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the elements specified by " + locator + " is present");
		return ExpectedConditions.presenceOfElementLocated(locator);
	}

	public static ExpectedCondition<WebElement> elementToBeClickable(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the element " + locator + " becomes clickable");

		return ExpectedConditions.elementToBeClickable(locator);
	}

	public static ExpectedCondition<Boolean> elementToBeInvisible(String elementLocator) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the element " + locator + " becomes invisible");
		return ExpectedConditions.invisibilityOfElementLocated(locator);
	}

	public static ExpectedCondition<Boolean> newWindowOpens(final int numberOfCurrentWindowsOpened) {
		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return Boolean.valueOf((d.getWindowHandles().size() >= numberOfCurrentWindowsOpened));
			}
		};
	}

	public static ExpectedCondition<Boolean> cssPropertyChangesTo(String elementLocator, final String attribute,
			final String expectedProperty) {
		final By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the element's " + locator + " css property " + attribute + " is changed to "
				+ expectedProperty);

		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				try {
					WebElement ele = d.findElement(locator);
					if (ele != null && ele.isDisplayed()) {
						System.out.println(ele.getCssValue(attribute));
						return Boolean.valueOf(ele.getCssValue(attribute).trim().contains(expectedProperty));
					}
					return Boolean.valueOf(false);
				} catch (StaleElementReferenceException err) {
					return Boolean.valueOf(false);
				}
			}

			public String toString() {
				return "Until the css attribute" + expectedProperty + " of element " + locator.toString()
						+ "changes to " + expectedProperty;
			}
		};
	}

	public static ExpectedCondition<Boolean> textToBePresentInElement(String elementLocator, String expectedText) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the text " + expectedText + " is displayed on the element" + locator);

		return ExpectedConditions.textToBePresentInElementLocated(locator, expectedText);
	}

	public static ExpectedCondition<Boolean> textToBePresentInElementValue(String elementLocator, String expectedText) {
		By locator = ObjectLocators.getBySelector(elementLocator);
		log.info("Waiting until the text " + expectedText + " is displayed on the element" + locator);

		return ExpectedConditions.textToBePresentInElementLocated(locator, expectedText);
	}

	public static ExpectedCondition<Boolean> urlContainsParam(final String expectedParam) {
		log.info("Waiting until the URL contains the string: " + expectedParam);
		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return Boolean.valueOf(d.getCurrentUrl().contains(expectedParam));
			}

			public String toString() {
				return "Until the URL Contains the param : " + expectedParam;
			}
		};
	}

	public static ExpectedCondition<Boolean> titleContains(String expectedPageTitle) {
		return ExpectedConditions.titleContains(expectedPageTitle);
	}

	public static ExpectedCondition<Boolean> titleIs(String expectedPageTitle) {
		return ExpectedConditions.titleIs(expectedPageTitle);
	}
}
