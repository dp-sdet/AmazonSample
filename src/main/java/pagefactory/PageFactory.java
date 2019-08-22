package pagefactory;

/**
 * Class to handle driver methods
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.openqa.selenium.WebDriver;

public class PageFactory {
	public static <T> T instantiatePage(WebDriver driver, Class<T> pageClassToProxy) {
		try {
			Method m = null;
			m = pageClassToProxy.getMethod("setDriver", new Class[] { WebDriver.class });

			T classInstance = (T) pageClassToProxy.newInstance();
			Object[] parameters = { driver };
			m.invoke(classInstance, parameters);
			return classInstance;
		} catch (NoSuchMethodException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("Page Class doesnot inherit WebDriverBase", e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
