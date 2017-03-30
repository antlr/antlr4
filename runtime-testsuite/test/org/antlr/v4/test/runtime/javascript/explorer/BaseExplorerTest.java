/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.javascript.explorer;

import org.antlr.v4.test.runtime.javascript.browser.BaseBrowserTest;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.ie.InternetExplorerDriver;

public class BaseExplorerTest extends BaseBrowserTest {

	@Before
	public void initWebDriver() {
		System.setProperty("webdriver.ie.driver", "C:\\Program Files (x86)\\Selenium\\IEDriverServer.exe");
		driver = new InternetExplorerDriver();
	}

	@After
	public void closeWebDriver() {
		if(driver!=null) {
	      driver.quit();
	    }
	}

}
