/*
 * Since JSONUtilities methods use the JSONInput and JSONOutput streams this test class
 * tests those classes as well as the JSONUtilities class
 */


package org.quickconnectfamily.json.tests;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//import javax.swing.JButton;

import org.quickconnectfamily.json.JSONUtilities;

public class JSONUtilitiesTest {

	/*
	 * Since the stringify method is a facade for the underlying JSONOutputStream writeObject method it only 
	 * needs to be tested to verify the facade is working correctly.
	 */

	public static void testStringifySerializable() {
		
		/*
		 * Testing a valid HashMap with Booleans
		 */
		
		HashMap testMap = new HashMap();
		Boolean first = new Boolean(true);
		Boolean second = new Boolean(true);
		testMap.put("config_App_6237", first);
		testMap.put("Init_LoadResourceLastCacheDate", second);
		String jsonString = null;
		try {
			jsonString = JSONUtilities.stringify(testMap);
			assert jsonString.equals("{\"config_App_6237\"=true, \"Init_LoadResourceLastCacheDate\"=true}");
			//System.out.println("map: "+jsonString);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		/*
		 * Testing a valid 'happy path' scenario
		 */
		TestObject anObject = new TestObject("Hello there.", 7, new Date(1067899));
		try {
			 jsonString = JSONUtilities.stringify(anObject);
			 assert jsonString.equals("{\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\",\"theString\":\"Hello there.\",\"theInt\":7}");
			//System.out.println("happy path object: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing a null parameter
		 */
		try {
			jsonString = JSONUtilities.stringify((Serializable)null);
			assert jsonString == null;
			//System.out.println("null serializable: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		/*
		 * Testing a null attribute
		 */
		TestObject anObjectWithNull = new TestObject(null, 7, new Date(1067899));
		try {
			 jsonString = JSONUtilities.stringify(anObjectWithNull);
			 assert jsonString.equals("{\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\",\"theInt\":7}");
			//System.out.println("null attribute: "+jsonString);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing an array of Objects. 
		 */
		
		try {
			Object[] anObjectArray = {new Integer(4), "Hello", new Date()};
			jsonString = JSONUtilities.stringify(anObjectArray);
			assert jsonString.equals("[4,\"Hello\",\"Fri Sep 14 10:55:25 MDT 2012\"]");
			//System.out.println("Object[]: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing an array of ints.  
		 */
		
		try {
			int[] anArray = {5, -2, 0,100000};
			jsonString = JSONUtilities.stringify(anArray);
			assert jsonString.equals("[5, -2, 0,100000]");
			//System.out.println("int[]: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing an array of ints.  
		 */
		
		try {
			double[] anArray = {5.03, -2015.009999999999, 0.0,0.999999999999};
			jsonString = JSONUtilities.stringify(anArray);
			assert jsonString.equals("[5.03,-2015.009999999999,0.0,0.999999999999]");
			//System.out.println("double[]: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		/*
		 * Testing an array of bytes.  
		 */
		
		try {
			byte[] anArray = "This is an array of bytes".getBytes();
			jsonString = JSONUtilities.stringify(anArray);
			assert jsonString != null;
			assert jsonString.equals("[\"84\",\"104\",\"105\",\"115\",\"32\",\"105\",\"115\",\"32\",\"97\",\"110\",\"32\",\"97\",\"114\",\"114\",\"97\",\"121\",\"32\",\"111\",\"102\",\"32\",\"98\",\"121\",\"116\",\"101\",\"115\"]");
			//System.out.println("byte[]: "+jsonString);
		} 	
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Passed testStringifySerializable");
	}
	
	
	public static void testStringifyCollections(){
		ArrayList test = new ArrayList();
		test.add(9L);
		test.add(8L);
		test.add(7L);
		try {
			String testString = JSONUtilities.stringify(test);
			assert testString.equals("[9,8,7]");
			//System.out.println("ArrayList: "+testString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Passed testStringifyCollections");
	}

	
	public static void testStringifySerializableEncoding() {
		/*
		 * Testing a valid 'happy path' scenario
		 */
		TestObject anObject = new TestObject("Hello there.", 7, new Date(1067899));
		try {
			String jsonString = JSONUtilities.stringify(anObject, JSONUtilities.encoding.UNICODE);
			assert jsonString.equals("{\"theString\":\"Hello there.\",\"theInt\":\"7\",\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\"}");
			//System.out.println("happy path object unicode: "+jsonString);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			String jsonString = JSONUtilities.stringify(anObject, JSONUtilities.encoding.UTF8);
			assert jsonString.equals("{\"theString\":\"Hello there.\",\"theInt\":\"7\",\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\"}");
			//System.out.println("happy path object UTF8: "+jsonString);
		} 
		catch (Exception e) {
			e.printStackTrace();
			//fail("Should not have thrown exception");
		}
		
		/*
		 * Testing a null parameter
		 */
		try {
			String jsonString = JSONUtilities.stringify((Serializable)null, JSONUtilities.encoding.UNICODE);
			//assertNull(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			//fail("Should not have thrown exception");
		}

		/*
		 * Testing a null encoding or other invalid encoding
		 */
		try {
			String jsonString = JSONUtilities.stringify(anObject, (JSONUtilities.encoding)null);
			assert false;
		} catch (Exception e) {}
		
		/*
		 * Testing an array of Objects.  awt.Containters should be ignored.
		 */
		
		Object[] anObjectArray = {new Integer(4), "Hello", new Date(45879003)};
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UNICODE);
			assert jsonString != null;
			assert jsonString.equals("[4,\"Hello\",\"Thu Jan 01 05:44:39 MST 1970\"]");
			//System.out.println("object array UNICODE: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UTF8);
			assert jsonString != null;
			assert jsonString.equals("[4,\"Hello\",\"Thu Jan 01 05:44:39 MST 1970\"]");
			//System.out.println("object array UTF8: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		char[] someChars = {'a','b','c'};
		Object[] annotherObjectArray = {new Integer(4), "Hello", someChars};
		try {
			String jsonString = JSONUtilities.stringify(annotherObjectArray, JSONUtilities.encoding.UNICODE);
			assert jsonString !=null;
			assert jsonString.equals("[4,\"Hello\",[\"a\",\"b\",\"c\"]]");
			//System.out.println("Unicode string: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UTF8);
			assert jsonString != null;
			assert jsonString.equals("[\"4\",\"Hello\"]");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Passed testStringifySerializableEncoding");
	}
	
	public static void testParseString() {
		try{
			//test the happy path with a well formed JSON string.
			HashMap testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":16.5,\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":-4.5,\"doubleObjAtt\":1000.567789,\"listAtt\":[7,\"hello there from list\"],\"parentString\":\"In The Parent\"}}");
			assert testMap != null;
			
			Number aNumber = (Double)testMap.get("aNumber");
			assert aNumber != null;
			assert aNumber.doubleValue() == 16.5;
			
			String aString = (String)testMap.get("stringOne");
			assert aString != null;
			assert aString.equals("Some sort of string");
			
			aString = (String)testMap.get("20");
			assert aString != null;
			assert aString.equals("some other stuff");
			
			HashMap anObjectRepresentation = (HashMap)testMap.get("aTester");
			//check the attributes of the object embedded as an attribute of the outer object
			aString = (String)anObjectRepresentation.get("stringAtt");
			assert aString != null;
			assert aString.equals("hello");
			aNumber = (Double)anObjectRepresentation.get("doubleAtt");
			assert aNumber != null;
			assert aNumber.doubleValue() == -4.5;
			//System.out.println("doubleAtt: "+anObjectRepresentation.get("doubleAtt"));
			
			aNumber = (Double)anObjectRepresentation.get("doubleObjAtt");
			assert aNumber != null;
			assert aNumber.doubleValue() == 1000.567789;
				
			ArrayList aList = (ArrayList)anObjectRepresentation.get("listAtt");
			assert aList != null;
			//check the values of the items in the list
			Long longNumber = (Long)aList.get(0);
			assert longNumber != null;
			assert longNumber.longValue() == 7;
			
			aString = (String)aList.get(1);
			assert aString != null;
			assert aString.equals("hello there from list");
			
			//Check the last attribute of the object embedded as an attribute of the outer object
			aString = (String)anObjectRepresentation.get("parentString");
			assert aString != null;
			assert aString.equals("In The Parent");
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing passing a null string parameter.
		 */
		try {
			Object nullObject = JSONUtilities.parse(null);
			assert nullObject == null;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing malformed JSON object.
		 */
		try {
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}");
			assert false;
		} catch (Exception e) {}
		
		/*
		 * Testing malformed JSON array.
		 */
		try {
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]");
			assert false;
		} catch (Exception e) {}
		System.out.println("Passed testParseString");
		
	}

	
	public static void testParseStringEncoding() {
		try{
			//test the happy path with a well formed JSON string.
			/*
			 * UNICODE
			 */
			HashMap testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":16.5,\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":-4.5,\"doubleObjAtt\":1000.567789,\"listAtt\":[7,\"hello there from list\"],\"parentString\":\"In The Parent\"}}",JSONUtilities.encoding.UNICODE);
			Number aNumber = (Number)testMap.get("aNumber");
			assert aNumber != null;
			assert aNumber.doubleValue() == 16.5;
			
			String aString = (String)testMap.get("stringOne");
			assert aString != null;
			assert aString.equals("Some sort of string");
			
			aString = (String)testMap.get("20");
			assert aString != null;
			assert aString.equals("some other stuff");
			
			HashMap anObjectRepresentation = (HashMap)testMap.get("aTester");
				//check the attributes of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("stringAtt");
				assert aString != null;
				assert aString.equals("hello");
				
				aNumber = (Number)anObjectRepresentation.get("doubleAtt");
				assert aNumber != null;
				assert aNumber.doubleValue() == -4.5;
				
				aNumber = (Number)anObjectRepresentation.get("doubleObjAtt");
				assert aNumber != null;
				assert aNumber.doubleValue() == 1000.567789;
				
				ArrayList aList = (ArrayList)anObjectRepresentation.get("listAtt");
				assert aList != null;
				assert aList.size() == 2;

				//check the values of the items in the list
					aNumber = (Number)aList.get(0);
					assert aNumber !=null;
					assert aNumber.intValue() == 7;
					
					aString = (String)aList.get(1);
					assert aString != null;
					assert aString.equals("hello there from list");
				
				//Check the last attribute of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("parentString");
				assert aString != null;
				assert aString.equals("In The Paraent");
				
				/*
				 * UTF-8
				 */
				//test the happy path with a well formed JSON string.
				testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":16.5,\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":-4.5,\"doubleObjAtt\":1000.567789,\"listAtt\":[7,\"hello there from list\"],\"parentString\":\"In The Parent\"}}",JSONUtilities.encoding.UTF8);
				assert testMap != null;
				
				aNumber = (Number)testMap.get("aNumber");
				assert aNumber != null;
				assert aNumber.doubleValue() == 16.5;
				
				aString = (String)testMap.get("stringOne");
				assert aString != null;
				assert aString.equals("Some sort of string");
				
				aString = (String)testMap.get("20");
				assert aString != null;
				assert aString.equals("some other stuff");
				
				anObjectRepresentation = (HashMap)testMap.get("aTester");
				assert anObjectRepresentation != null;
				//check the attributes of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("stringAtt");
				assert aString != null;
				assert aString.equals("hello");
				
				aNumber = (Number)anObjectRepresentation.get("doubleAtt");
				assert aNumber != null;
				assert aNumber.doubleValue() == -4.5;
				
				aNumber = (Number)anObjectRepresentation.get("doubleObjAtt");
				assert aNumber != null;
				assert aNumber.doubleValue() == 1000.567798;
				
				aList = (ArrayList)anObjectRepresentation.get("listAtt");
				assert aList != null;
				assert aList.size() == 2;
				//check the values of the items in the list
				aNumber = (Number)aList.get(0);
				assert aNumber != null;
				assert aNumber.intValue() == 7;
				
				aString = (String)aList.get(1);
				assert aString != null;
				assert aString.equals("hello there from list");
					
				//Check the last attribute of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("parentString");
				assert aString != null;
				assert aString.equals("In The Parent");
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing passing a null string parameter.
		 */
		try {

			/*
			 * UNICODE
			 */
			Object nullObject = JSONUtilities.parse(null, JSONUtilities.encoding.UNICODE);
			//assertNull(nullObject);

			/*
			 * UTF-8
			 */

			nullObject = JSONUtilities.parse(null, JSONUtilities.encoding.UTF8);
			//assertNull(nullObject);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing malformed JSON object.
		 */
		try {
			/*
			 * UNICODE
			 */
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}", JSONUtilities.encoding.UNICODE);
			assert false;
		} catch (Exception e) {
		}
		try{
			/*
			 * UTF-8
			 */
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}", JSONUtilities.encoding.UTF8);
			assert false;
		} 
		catch (Exception e) {
		}
		
		/*
		 * Testing malformed JSON array.
		 */
		try {
			/*
			 * UNICODE
			 */
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]", JSONUtilities.encoding.UNICODE);
			assert false;
		} catch (Exception e) {
		}
		try{
			/*
			 * UTF8
			 */
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]", JSONUtilities.encoding.UTF8);
			assert false;
		}
		catch (Exception e){
		}
		
		System.out.println("Passed testParseStringEncoding");
	}

}
