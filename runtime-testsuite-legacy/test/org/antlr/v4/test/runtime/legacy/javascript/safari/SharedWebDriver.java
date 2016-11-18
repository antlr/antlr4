package org.antlr.v4.test.runtime.legacy.javascript.safari;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Timer;
import java.util.TimerTask;

public class SharedWebDriver {

	static WebDriver driver;
	static Timer timer;

	public static WebDriver init() {
		if(driver==null) {
			System.setProperty("webdriver.safari.noinstall", "true");
			driver = new SafariDriver();
		} else if(timer!=null) {
			timer.cancel();
			timer = null;
		}

		return driver;
	}

	public static void close() {
		if(driver!=null) {
			if(timer!=null) {
				timer.cancel();
				timer = null;
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override public void run() {
					driver.quit();
					driver = null;
				}
			}, 2000); // close with delay to allow next Test to start
		}
	}

}
