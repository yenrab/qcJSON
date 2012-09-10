package org.quickconnectfamily.json.tests;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class TestObject implements Serializable {
	@SuppressWarnings("unused")
	private String theString;
	@SuppressWarnings("unused")
	private int theInt;
	@SuppressWarnings("unused")
	private Date theDate;
	
	public TestObject(String aString, int anInt, Date aDate) {
		theString = aString;
		theInt = anInt;
		theDate = aDate;
	}

}
