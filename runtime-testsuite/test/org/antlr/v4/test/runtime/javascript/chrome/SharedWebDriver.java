package org.antlr.v4.test.runtime.javascript.chrome;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.assertTrue;

public class SharedWebDriver {

	static WebDriver driver;
	static Timer timer;

	public static WebDriver init() {
		if(driver==null) {
			String path = SharedWebDriver.class.getPackage().getName().replace(".", "/") + "/chromedriver.bin";
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			File file = new File(url.toExternalForm().substring(5)); // skip 'file:'
			assertTrue(file.exists());
			System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
			driver = new ChromeDriver();
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
