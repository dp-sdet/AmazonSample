package core;

import com.paulhammant.ngwebdriver.NgWebDriver;
import exception.CustomException;
import objectlocator.ObjectLocators;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;





public class WebDriverBase
{
  protected WebDriver driver;
  private static final int TIMEOUT_IN_SECONDS = 60;
  private static final int POLL_INTERVAL = 500;
  protected static final Logger log = Logger.getLogger(WebDriverBase.class);

  
  public void setDriver(WebDriver driver) { this.driver = driver; }
  
  public <U> U wait(ExpectedCondition<U> condition) {
    FluentWait<WebDriver> wait = (new FluentWait(this.driver)).ignoring(RuntimeException.class).withTimeout(60L, TimeUnit.SECONDS).pollingEvery(500L, TimeUnit.MILLISECONDS);
    try {
      return (U)wait.until(condition);
    } catch (TimeoutException err) {
      
      String errMessage = "Bot encountered a timeout while waiting for a condition,  " + err.getLocalizedMessage();
      throw new CustomException(errMessage);
    } 
  }


  public <U> U wait(ExpectedCondition<U> condition, int timeoutInSeconds) {
    FluentWait<WebDriver> wait = (new FluentWait(this.driver)).ignoring(RuntimeException.class).withTimeout(timeoutInSeconds, TimeUnit.SECONDS).pollingEvery(500L, TimeUnit.MILLISECONDS);
    try {
      return (U)wait.until(condition);
    } catch (TimeoutException err) {
      
      String errMessage = "Bot encountered a timeout while waiting for a condition,  " + err.getLocalizedMessage();
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


  protected void pageLoadTime(int loadTimeInSec) { this.driver.manage().timeouts().pageLoadTimeout(loadTimeInSec, TimeUnit.SECONDS); }


  public void refresh() { this.driver.navigate().refresh(); }



  
  protected void clickOnElement(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    WebElement element = this.driver.findElement(locator);
    if (element.isDisplayed()) {
      
      element.click();
      log.info("Clicked on element: " + locator);
    } 
  }






  
  protected String getWindowHandle() { return this.driver.getWindowHandle(); }












  
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
      throw new RuntimeException("Could not locate any element described by the locator " + elementLocator
          .toString() + " with text " + elementText);
    }
  }

  
  protected void clickOnElementAtPosition(String elementLocator, int x, int y) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    WebElement element = this.driver.findElement(locator);
    Actions action = new Actions(this.driver);
    action.moveToElement(element, x, y).click().build().perform();
  }
  
  protected WebElement findElementWithText(String elementLocator, String elementText) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    List<WebElement> elementList = this.driver.findElements(locator);
    int index = 0;
    for (WebElement element : elementList) {
      if (element.getText().trim().equalsIgnoreCase(elementText) && element.isDisplayed()) {
        index++;
        return element;
      } 
    } 
    if (index == elementList.size()) {
      throw new RuntimeException("Could not locate any element described by the locator " + elementLocator
          .toString() + " with text " + elementText);
    }
    return null;
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
    WebElement element = (WebElement)wait(Until.elementToBeClickable(elementLocator));
    sleep(1);
    element.sendKeys(new CharSequence[] { text });
    sleep(1);
    log.info("Entered " + text + " into the " + elementLocator + " text field");
  }







  
  protected void type(String elementLocator, String text, Keys keys) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    log.info("Entering " + text + " into the " + locator + " text field and pressing " + keys);
    WebElement element = this.driver.findElement(locator);
    element.sendKeys(new CharSequence[] { text });
    element.sendKeys(new CharSequence[] { keys });
  }






  
  protected void goToUrl(String url) {
    log.info("Loading the URL:" + url);
    this.driver.get(url);
  }








  
  protected String getCssAttribute(String elementLocator, String attribute) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    log.info("Getting CSS value of " + attribute + " from the locator " + locator);
    return this.driver.findElement(locator).getCssValue(attribute);
  }








  
  protected String getAttribute(String elementLocator, String attribute) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    String value = this.driver.findElement(locator).getAttribute(attribute);
    log.info("Read " + attribute + " attribute value " + value + ", from the locator " + locator);
    return value;
  }








  
  protected void submit(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    log.info("Submitting the Form");
    this.driver.findElement(locator).submit();
  }







  
  protected int getNumberOfOpenWindows() { return this.driver.getWindowHandles().size(); }






  
  protected void navigateBack() {
    this.driver.navigate().back();
    log.info("Navigating to the previous page");
  }





  
  protected void navigateForward() {
    this.driver.navigate().forward();
    log.info("Navigating to the next page");
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








  
  protected void dragAndDrop(String fromElementLocator, String toElementLocator) {
    By fromLocator = ObjectLocators.getBySelector(fromElementLocator);
    By toLocator = ObjectLocators.getBySelector(toElementLocator);
    Actions action = new Actions(this.driver);
    action.dragAndDrop(this.driver.findElement(fromLocator), this.driver.findElement(toLocator)).build()
      .perform();
  }





  
  protected void dragAndDropByPixel(String source, int xOffset, int yOffset) {
    By locator = ObjectLocators.getBySelector(source);
    WebElement element = this.driver.findElement(locator);
    (new Actions(this.driver)).dragAndDropBy(element, xOffset, yOffset).build().perform();
  }




  
  protected void moveToElement(String sourceElement, int xoffset, int yoffset) {
    By locator = ObjectLocators.getBySelector(sourceElement);
    WebElement element = this.driver.findElement(locator);
    (new Actions(this.driver)).moveToElement(element, xoffset, yoffset).build().perform();
  }


  
  protected void scrollPageDown() {
    JavascriptExecutor jse = (JavascriptExecutor)this.driver;
    jse.executeScript("scrollBy(0, -2500)", new Object[0]);
    sleep(5);
  }





  
  protected void forceRefresh() {
    Actions action = new Actions(this.driver);
    log.info("Forcefully refreshing the page");
    action.keyDown(Keys.CONTROL).sendKeys(new CharSequence[] { Keys.F5 }).keyUp(Keys.CONTROL).perform();
  }






  
  protected void hoverOver(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    Actions action = new Actions(this.driver);
    log.info("Hovering over the mouse on the element " + locator);
    WebElement element = this.driver.findElement(locator);
    action.moveToElement(element).build().perform();
  }

  
  protected void focusElement(String elementLocator) {
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    By locator = ObjectLocators.getBySelector(elementLocator);
    WebElement element = this.driver.findElement(locator);
    jsExecutor.executeScript("document.getElementByXpath(" + element + ").focus();", new Object[0]);
  }








  
  protected void pressKey(String elementLocator, Keys key) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    this.driver.findElement(locator).sendKeys(new CharSequence[] { key });
  }









  
  protected Boolean isElementEnabled(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    return Boolean.valueOf(this.driver.findElement(locator).isEnabled());
  }









  
  protected Boolean isElementDisabled(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    return Boolean.valueOf(!this.driver.findElement(locator).isEnabled());
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






  
  protected void switchToDefaultContent() { this.driver.switchTo().defaultContent(); }






  
  protected void refreshPage() { this.driver.navigate().refresh(); }







  
  protected void doubleClick(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    Actions builder = new Actions(this.driver);
    WebElement element = this.driver.findElement(locator);
    Action hoverOverRegistrar = (Action)builder.doubleClick(element);
    hoverOverRegistrar.perform();
  }





  
  protected void clearCookies() { this.driver.manage().deleteAllCookies(); }









  
  protected WebElement getDisplayedElement(String elementLocator) {
    log.info("Finding the displayed Element for the locator provided--" + elementLocator);
    By locator = ObjectLocators.getBySelector(elementLocator);
    List<WebElement> elementList = findElements(locator);
    for (WebElement element : elementList) {
      if (element.isDisplayed())
        return element; 
    } 
    throw new CustomException("Element not found--" + elementLocator + " displayed");
  }






  
  protected String getCurrentUrl() { return this.driver.getCurrentUrl(); }








  
  protected int getListSize(String propKey) {
    By locator = ObjectLocators.getBySelector(propKey);
    return findElements(locator).size();
  }







  
  private List<WebElement> findElements(By by) throws CustomException {
    try {
      return this.driver.findElements(by);
    } catch (NoSuchElementException e) {
      String msg = "Element could not be located " + by.toString();
      log.info(msg);
      throw new CustomException(msg);
    } 
  }



  
  protected void closePopupWindow() {
    this.driver.close();
    Iterator iterator = this.driver.getWindowHandles().iterator(); if (iterator.hasNext()) { String name = (String)iterator.next();
      this.driver.switchTo().window(name);
      log.info("popup window closed : " + name); }
  
  }







  
  protected void closePopupWindow(String windowID) { this.driver.switchTo().window(windowID).close(); }






  
  protected void closepopUpAndSwitchtoParent(String windowID) {
    closePopupWindow(windowID);
    Iterator iterator = this.driver.getWindowHandles().iterator(); if (iterator.hasNext()) { String name = (String)iterator.next();
      this.driver.switchTo().window(name); }
  
  }








  
  protected boolean isElementPresent(String propKey) {
    By locator = ObjectLocators.getBySelector(propKey);
    log.debug("Checking the presence of the Element: " + propKey + " : " + propKey);
    return isElementPresent(locator);
  }
  
  protected boolean isElementPresentInElement(WebElement baseElement, String propKey) {
    By locator = ObjectLocators.getBySelector(propKey);
    log.debug("Checking the presence of the Element: " + propKey + " : " + propKey);
    return isElementPresent(baseElement, locator);
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
  
  private boolean isElementPresent(WebElement baseElement, By by) {
    try {
      WebElement element = baseElement.findElement(by);
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






  
  protected void javascriptDragAndDrop(String sourcePropKey, String destPropKey) {
    By sourceElementLocator = ObjectLocators.getBySelector(sourcePropKey);
    By destElementLocator = ObjectLocators.getBySelector(destPropKey);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    WebElement source = findElement(sourcePropKey);
    WebElement dest = findElement(destPropKey);
    log.info("Performs javascript drag and drop from " + sourceElementLocator + " to " + destElementLocator);
    
    String java_script = "var src=arguments[0],tgt=arguments[1];var dataTransfer={dropEffect:'',effectAllowed:'all',files:[],items:{},types:[],setData:function(format,data){this.items[format]=data;this.types.append(format);},getData:function(format){return this.items[format];},clearData:function(format){}};var emit=function(event,target){var evt=document.createEvent('Event');evt.initEvent(event,true,false);evt.dataTransfer=dataTransfer;target.dispatchEvent(evt);};emit('dragstart',src);emit('dragenter',tgt);emit('dragover',tgt);emit('drop',tgt);emit('dragend',src);";







    
    jsExecutor.executeScript(java_script, new Object[] { source, dest });
  }





  
  protected void javascriptDragAndDrop(WebElement source, WebElement dest) {
    log.info("Performs javascript drag and drop from " + source + " to " + dest);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    String java_script = "var src=arguments[0],tgt=arguments[1];var dataTransfer={dropEffect:'',effectAllowed:'all',files:[],items:{},types:[],setData:function(format,data){this.items[format]=data;this.types.append(format);},getData:function(format){return this.items[format];},clearData:function(format){}};var emit=function(event,target){var evt=document.createEvent('Event');evt.initEvent(event,true,false);evt.dataTransfer=dataTransfer;target.dispatchEvent(evt);};emit('dragstart',src);emit('dragenter',tgt);emit('dragover',tgt);emit('drop',tgt);emit('dragend',src);";







    
    jsExecutor.executeScript(java_script, new Object[] { source, dest });
  }






  
  protected void javascriptClick(String propKey) {
    By by = ObjectLocators.getBySelector(propKey);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    WebElement element = findElement(propKey);
    log.info("Clicking on the element " + by + " using JavaScript");
    jsExecutor.executeScript("arguments[0].click();", new Object[] { element });
  }





  
  protected void javascriptClick(WebElement element) {
    log.info("Clicking on the element " + element + " using JavaScript");
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    jsExecutor.executeScript("arguments[0].click();", new Object[] { element });
  }







  
  protected void switchToParentWindow(String parentWindowId) {
    String windowId = "";
    Set<String> set = this.driver.getWindowHandles();
    log.info("Number of windows opened: " + set.size());
    Iterator<String> iterator = set.iterator();
    while (iterator.hasNext()) {
      windowId = (String)iterator.next();
      if (windowId.equals(parentWindowId)) {
        log.info("Switching to the window: " + parentWindowId);
        this.driver.switchTo().window(parentWindowId);
      } 
      log.info("windowId" + windowId);
    } 
  }







  
  protected void selectMultipleListItems(String listBoxLocator, int[] indexes) {
    By locator = ObjectLocators.getBySelector(listBoxLocator);
    Actions action = new Actions(this.driver);
    WebElement listItem = this.driver.findElement(locator);
    List<WebElement> listOptions = listItem.findElements(By.tagName("option"));
    action.keyDown(Keys.CONTROL).perform();
    for (int i : indexes) {
      ((WebElement)listOptions.get(i)).click();
    }
    action.keyUp(Keys.CONTROL).perform();
  }







  
  protected void switchToPopUpWindow(String parentWindowId) {
    String windowId = "";
    Set<String> set = this.driver.getWindowHandles();
    log.info("Number of windows opened: " + set.size());
    Iterator<String> iterator = set.iterator();
    while (iterator.hasNext()) {
      windowId = (String)iterator.next();
      if (!windowId.equals(parentWindowId)) {
        log.info("Switching to the window: " + parentWindowId);
        this.driver.switchTo().window(windowId);
      } 
      log.info("Popup windowId" + windowId);
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





  
  protected String getAlertText() {
    log.info("Getting the Alert text");
    return this.driver.switchTo().alert().getText();
  }



  
  protected void acceptAlert() {
    log.info("Confirming the operation");
    this.driver.switchTo().alert().accept();
  }



  
  protected void dismissAlert() {
    log.info("Cancelling the operation");
    this.driver.switchTo().alert().dismiss();
  }







  
  protected String executeJavaScript(String script) {
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    return (String)jsExecutor.executeScript(script, new Object[0]);
  }







  
  protected Set<String> getWindowHandles() { return this.driver.getWindowHandles(); }






  
  protected String getWindowTitle() {
    log.info("Getting the title of the page");
    return this.driver.getTitle();
  }






  
  protected boolean switchToWindowWithElement(String elementLocator) {
    for (String window : this.driver.getWindowHandles()) {
      this.driver.switchTo().window(window);
      if (isElementPresent(elementLocator))
        return true; 
    } 
    return false;
  }






  
  protected boolean switchToWindowWithURLPart(String URLPart) {
    for (String window : this.driver.getWindowHandles()) {
      this.driver.switchTo().window(window);
      if (getCurrentUrl().contains(URLPart))
        return true; 
    } 
    return false;
  }






  
  protected List<String> getTextOfSimilarElements(String elementLocator) {
    List<WebElement> elementList = (List)wait(Until.elementsToBeDisplayed(elementLocator));
    List<String> elementTextList = new ArrayList<String>();
    
    log.debug("There are " + elementList.size() + " Similar elements(Element with locator " + elementLocator + " )");
    
    for (WebElement anElement : elementList) {
      elementTextList.add(anElement.getText());
    }
    
    return elementTextList;
  }

  
  protected List<WebElement> getSimilarElements(String elementLocator) {
    log.debug("Getting Similar elements with locator " + elementLocator + " )");
    return (List)wait(Until.elementsToBeDisplayed(elementLocator));
  }






  
  protected String getPageSource() { return getPageSource(); }









  
  protected void setCheckBox(String elementLocator, boolean check) {
    WebElement checkbox = (WebElement)wait(Until.elementToBeClickable(elementLocator));
    if (check) {
      if (!checkbox.isSelected()) {
        checkbox.click();
      }
    }
    else if (checkbox.isSelected()) {
      checkbox.click();
    } 
  }








  
  protected void clearTextBox(String elementLocator) { ((WebElement)wait(Until.elementToBeClickable(elementLocator))).clear(); }








  
  protected void chooseOptionUsingJavaScript(String propKey, boolean select) {
    By by = ObjectLocators.getBySelector(propKey);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    log.info("Choosing the option button " + by + " using Java Script");
    jsExecutor.executeScript("arguments[0].checked = arguments[1];", new Object[] { this.driver.findElement(by), 
          Boolean.valueOf(select) });
  }






  
  protected void clickOnElementUsingJavaScript(String propKey) {
    By by = ObjectLocators.getBySelector(propKey);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    log.info("Clicking on the element " + by + " using JavaScript");
    jsExecutor.executeScript("arguments[0].click();", new Object[] { this.driver.findElement(by) });
  }






  
  protected void typeUsingJavaScript(String propKey, String text) {
    By by = ObjectLocators.getBySelector(propKey);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    log.info("typing " + text + " on the element " + by + " using JavaScript");
    jsExecutor.executeScript("arguments[0].value=arguments[1];", new Object[] { this.driver.findElement(by), text });
  }






  
  protected void scrollToElementJavaScript(String propKey, boolean scrollTop) {
    By by = ObjectLocators.getBySelector(propKey);
    JavascriptExecutor jsExecutor = (JavascriptExecutor)this.driver;
    log.info("scrolling to the element " + by + " using JavaScript");
    jsExecutor.executeScript("arguments[0].scrollIntoView(arguments[1]);", new Object[] { this.driver.findElement(by), 
          Boolean.valueOf(scrollTop) });
  }
  
  protected void highlightElement(String propKey) {
    By by = ObjectLocators.getBySelector(propKey);
    for (int i = 0; i < 2; i++) {
      JavascriptExecutor js = (JavascriptExecutor)this.driver;
      js.executeScript("arguments[0].setAttribute('style', arguments[1]);", new Object[] { this.driver.findElement(by), "background-color: yellow;" });
      
      js.executeScript("arguments[0].setAttribute('style', arguments[1]);", new Object[] { this.driver.findElement(by), "" });
    } 
  }

  
  protected void highlightElement(WebElement element) {
    for (int i = 0; i < 2; i++) {
      JavascriptExecutor js = (JavascriptExecutor)this.driver;
      js.executeScript("arguments[0].setAttribute('style', arguments[1]);", new Object[] { element, "background-color: #a8d1ff;" });
      
      js.executeScript("arguments[0].setAttribute('style', arguments[1]);", new Object[] { element, "" });
    } 
  }



  
  protected String getBrowser() {
    Capabilities cap = ((RemoteWebDriver)this.driver).getCapabilities();
    return cap.getBrowserName().toLowerCase();
  }


  
  protected Long horizontalPageScrollXPosition(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    WebElement element = this.driver.findElement(locator);
    Point point = element.getLocation();
    Long xcord = Long.valueOf(point.getX());
    log.info("Element's Position from left side Is " + xcord + " pixels.");



    
    return xcord;
  }


  
  protected void iframeScroller(String elementLocator, String text) {
    log.info("Entering Text");
    WebElement element = this.driver.switchTo().activeElement();
    element.sendKeys(new CharSequence[] { text });
  }

  
  public Long getScollBarPositionInVerticalAxis() {
    JavascriptExecutor executor = (JavascriptExecutor)this.driver;
    return (Long)executor.executeScript("return window.scrollY;", new Object[0]);
  }

  
  public Long getScrollBarPositioninHorizontalAxis() {
    JavascriptExecutor executor = (JavascriptExecutor)this.driver;
    return (Long)executor.executeScript("return window.scrollX;", new Object[0]);
  }

  
  protected void scrollDown() {
    JavascriptExecutor javascript = (JavascriptExecutor)this.driver;
    javascript.executeScript("window.scrollTo(0, document.body.scrollHeight)", new Object[] { "" });
    sleep(4);
  }


  
  protected void scrollUp() {
    JavascriptExecutor javascript = (JavascriptExecutor)this.driver;
    javascript.executeScript("window.scrollTo(document.body.scrollHeight,0)", new Object[] { "" });
    sleep(4);
  }

  
  protected Long horizontalPageScrollYPosition(String elementLocator) {
    By locator = ObjectLocators.getBySelector(elementLocator);
    WebElement element = this.driver.findElement(locator);
    Point point = element.getLocation();
    Long ycord = Long.valueOf(point.getY());
    log.info("Element's Position from top side Is " + ycord + " pixels.");
    return ycord;
  }

  
  public boolean sortAsc(List<String> elementList) {
    boolean flag = true;
    elementList.removeAll(Arrays.asList(new String[] { "", null }));
    for (int i = 0; i < elementList.size() - 1; i++) {
      System.out.println((String)elementList.get(i));
      System.out.println((String)elementList.get(i + 1));
      int j = ((String)elementList.get(i)).compareToIgnoreCase((String)elementList.get(i + 1));
      System.out.println(j);
      if (((String)elementList.get(i)).compareToIgnoreCase((String)elementList.get(i + 1)) > 0) {
        flag = false;
        
        break;
      } 
    } 
    if (!flag) {
      log.info("The list is not sorted in Ascending order");
    }
    return flag;
  }

  
  public boolean sortDesc(List<String> elementList) {
    boolean flag = true;
    elementList.removeAll(Arrays.asList(new String[] { "", null }));
    for (int i = 0; i < elementList.size() - 1; i++) {
      System.out.println((String)elementList.get(i));
      System.out.println((String)elementList.get(i + 1));
      int j = ((String)elementList.get(i)).compareToIgnoreCase((String)elementList.get(i + 1));
      System.out.println(j);
      if (((String)elementList.get(i)).compareToIgnoreCase((String)elementList.get(i + 1)) < 0) {
        
        flag = false;
        
        break;
      } 
    } 
    if (!flag) {
      log.info("The list is not sorted in desc order");
    }
    return flag;
  }

  
  public boolean sortAscdate(List<String> elementList) {
    boolean flag = true;
    elementList.removeAll(Arrays.asList(new String[] { "", null }));
    for (int i = 0; i < elementList.size() - 1; i++) {
      System.out.println((String)elementList.get(i));
      System.out.println((String)elementList.get(i + 1));

      
      Date date = new Date((String)elementList.get(i));
      
      Date date1 = new Date((String)elementList.get(i + 1));
      int j = date.compareTo(date1);
      System.out.println(j);
      if (date.compareTo(date1) > 0) {
        flag = false;
        
        break;
      } 
    } 
    if (!flag) {
      log.info("The list is not sorted in Ascending order");
    }
    return flag;
  }

  
  public boolean sortDescdate(List<String> elementList) {
    boolean flag = true;
    elementList.removeAll(Arrays.asList(new String[] { "", null }));
    for (int i = 0; i < elementList.size() - 1; i++) {
      System.out.println((String)elementList.get(i));
      System.out.println((String)elementList.get(i + 1));

      
      Date date = new Date((String)elementList.get(i));
      
      Date date1 = new Date((String)elementList.get(i + 1));
      int j = date.compareTo(date1);
      System.out.println(j);
      if (date.compareTo(date1) < 0) {
        flag = false;
        
        break;
      } 
    } 
    if (!flag) {
      log.info("The list is not sorted in Descending order");
    }
    return flag;
  }


  
  protected void selectCurrentDateFromPicker(String propKey) {
    By by = ObjectLocators.getBySelector(propKey);
    DateFormat dateFormat2 = new SimpleDateFormat("dd");
    Date date2 = new Date();
    
    String today1 = dateFormat2.format(date2);
    String today = null;
    if (today1.startsWith("0")) {
      today = today1.substring(1);
    } else {
      today = today1.substring(0);
    } 
    System.out.println(today);
    WebElement dateWidget = this.driver.findElement(by);
    List<WebElement> columns = dateWidget.findElements(By.tagName("td"));
    for (WebElement cell : columns) {
      if (cell.getText().equals(today)) {
        cell.findElement(By.linkText(today)).click();
        break;
      } 
    } 
  }

  
  protected int countXpath(String elementLocator) { return this.driver.findElements(ObjectLocators.getBySelector(elementLocator)).size(); }



  
  protected void uploadFile(String elementLocator, String absoulutePath) { this.driver.findElement(ObjectLocators.getBySelector(elementLocator)).sendKeys(new CharSequence[] { absoulutePath }); }



  
  protected void waitForAngular() {
    log.info("Waiting For Angular Calls to finish");
    (new NgWebDriver((JavascriptExecutor)this.driver)).waitForAngularRequestsToFinish();
  }







  
  public boolean waitForPageToLoad() {
    WebDriverWait wait = new WebDriverWait(this.driver, 60L);

    
    ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>()
      {
        public Boolean apply(WebDriver driver) {
          try {
            WebDriverBase.log.info("Page is still loading");
            return Boolean.valueOf((((Long)((JavascriptExecutor)driver).executeScript("return jQuery.active", new Object[0])).longValue() == 0L));
          } catch (Exception e) {
            
            return Boolean.valueOf(true);
          } 
        }
      };

    
    ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>()
      {
        public Boolean apply(WebDriver driver) {
          WebDriverBase.log.info("Page is still loading");
          return Boolean.valueOf(((JavascriptExecutor)driver).executeScript("return document.readyState", new Object[0]).toString()
              .equals("complete"));
        }
      };
    return (((Boolean)wait.until(jQueryLoad)).booleanValue() && ((Boolean)wait.until(jsLoad)).booleanValue());
  }
}
