package org.quickconnectfamily.json.tests;

public class Assert {

	public static void Assert(boolean isTrue) throws Exception{
		if(!isTrue){
			throw new Exception("Assertion Failure");
		}
	}

}
