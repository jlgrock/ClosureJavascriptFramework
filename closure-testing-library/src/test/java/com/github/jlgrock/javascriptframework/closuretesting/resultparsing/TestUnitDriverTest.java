package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestUnitDriverTest {

	private BrowserVersion bv;

	@Before
	public void setUp() {
		bv = null;
	}

	@Test
	public void shouldNotReturnNull() {
		bv = TestUnitDriver.getBrowserVersionSafe(null);
		assertNotNull(bv);
	}

	@Test
	public void shouldResolveIEByDefault() {
		BrowserVersion expected = BrowserVersion.getDefault();
		// note that the default browser is IE8 ... interesting
		assertEquals(BrowserVersion.INTERNET_EXPLORER_8.getNickname(), expected.getNickname());

		bv = TestUnitDriver.getBrowserVersionSafe("OPERA");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("Chuck Norris");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe(null);
		assertEquals(expected.getNickname(), bv.getNickname());
	}

	@Test
	public void shouldResolveChrome() {
		BrowserVersion expected = BrowserVersion.CHROME;
		bv = TestUnitDriver.getBrowserVersionSafe("CHROME");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("chrome");
		assertEquals(expected.getNickname(), bv.getNickname());
	}

	@Test
	public void shouldResolveIE() {
		BrowserVersion expected = BrowserVersion.INTERNET_EXPLORER_10;
		bv = TestUnitDriver.getBrowserVersionSafe("INTERNET EXPLORER");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("IE");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("internet explorer");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("ie");
		assertEquals(expected.getNickname(), bv.getNickname());
	}

	@Test
	public void shouldResolveFF() {
		BrowserVersion expected = BrowserVersion.FIREFOX_17;
		bv = TestUnitDriver.getBrowserVersionSafe("FIREFOX");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("FF");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("firefox");
		assertEquals(expected.getNickname(), bv.getNickname());
		bv = TestUnitDriver.getBrowserVersionSafe("ff");
		assertEquals(expected.getNickname(), bv.getNickname());
	}
}
