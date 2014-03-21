/*
 * Since JSONUtilities methods use the JSONInput and JSONOutput streams this test class
 * tests those classes as well as the JSONUtilities class
 */


package org.quickconnectfamily.json.tests;

import static org.quickconnectfamily.json.tests.Assert.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//import javax.swing.JButton;

import org.quickconnectfamily.json.JSONException;
import org.quickconnectfamily.json.JSONUtilities;

//import android.util.SparseArray;

public class JSONUtilitiesTest {
    
	/*
	 * Since the stringify method is a facade for the underlying JSONOutputStream writeObject method it only
	 * needs to be tested to verify the facade is working correctly.
	 */
    
	public static void testStringifySerializable() {
		
		String jsonString = null;
		
		/*
		 * Testing a valid 'happy path' scenario
		 */
		TestObject anObject = new TestObject("Hello there.", 7, new Date(1067899));
		try {
            jsonString = JSONUtilities.stringify(anObject);
            System.out.println(jsonString);
            Assert( jsonString.equals("{\"theDate\":\"1969-12-31 17:17:47.899\",\"theString\":\"Hello there.\",\"theInt\":7}"));
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
			Assert( jsonString == null);
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
            Assert( jsonString.equals("{\"theDate\":\"1969-12-31 17:17:47.899\",\"theInt\":7}"));
			//System.out.println("null attribute: "+jsonString);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Test a string with escaped quotes as an attribute
		 */
		String stringWithEscapes = "hello \"bob\". What do you want?";
		TestObject anObjectWithEscapedString = new TestObject(stringWithEscapes, 7, new Date(1067899));
		try {
			jsonString = JSONUtilities.stringify(anObjectWithEscapedString);
			System.out.println(jsonString);
			Assert( jsonString.equals("{\"theDate\":\"1969-12-31 17:17:47.899\",\"theString\":\"hello \\\"bob\\\". What do you want?\",\"theInt\":7}"));
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		/*
		 * Testing an array of Objects.
		 */
		
		try {
			Object[] anObjectArray = {new Integer(4), "Hello", new Date(222222222)};
			jsonString = JSONUtilities.stringify(anObjectArray);
			System.out.println("Object[]: "+jsonString);
			Assert( jsonString.equals("[4,\"Hello\",\"1970-01-03 06:43:42.222\"]"));

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
			//System.out.println("int[]: "+jsonString);
			Assert( jsonString.equals("[5,-2,0,100000]"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing an array of doubles.
		 */
		
		try {
			double[] anArray = {5.03, -2015.009999999999, 0.0,0.999999999999};
			jsonString = JSONUtilities.stringify(anArray);
			Assert( jsonString.equals("[5.03,-2015.009999999999,0.0,0.999999999999]"));
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
			Assert( jsonString != null);
			Assert( jsonString.equals("[\"84\",\"104\",\"105\",\"115\",\"32\",\"105\",\"115\",\"32\",\"97\",\"110\",\"32\",\"97\",\"114\",\"114\",\"97\",\"121\",\"32\",\"111\",\"102\",\"32\",\"98\",\"121\",\"116\",\"101\",\"115\"]"));
			//System.out.println("byte[]: "+jsonString);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}

		/*
		 * Testing an array of chars.
		 */
		
		try {
			char[] anArray = "This is an array of chars".toCharArray();
			jsonString = JSONUtilities.stringify(anArray);
			Assert( jsonString != null);
			//System.out.println("char[]: "+jsonString);
			Assert( jsonString.equals("[\"T\",\"h\",\"i\",\"s\",\" \",\"i\",\"s\",\" \",\"a\",\"n\",\" \",\"a\",\"r\",\"r\",\"a\",\"y\",\" \",\"o\",\"f\",\" \",\"c\",\"h\",\"a\",\"r\",\"s\"]"));
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing an array with nulls in it
		 */
		Object[] objArr = new Object[5];
		objArr[0] = null;
		objArr[1] = new Integer(7);
		objArr[2] = new Double(8.3);
		objArr[3] = null;
		objArr[4] = null;
		
		try {
			jsonString = JSONUtilities.stringify(objArr);
			String testString = "[null,7,8.3,null,null]";
			System.out.println(jsonString);
			System.out.println(testString);
			Assert(jsonString.equals(testString));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Passed testStringifySerializable");
	}
	
	
	public static void testStringifyCollections(){
		Date testDate = new Date(1067899);
		String jsonString = null;
		
		ArrayList testListHappyPath = new ArrayList();
		testListHappyPath.add(9L);
		testListHappyPath.add(8L);
		testListHappyPath.add(7L);
		try {
			jsonString = JSONUtilities.stringify(testListHappyPath);
			Assert( jsonString.equals("[9,8,7]"));
			//System.out.println("ArrayList: "+testString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		/*
		 * happy path map test
		 */
		HashMap testMapHappyPath = new HashMap();
		testMapHappyPath.put("theDate", testDate);
		testMapHappyPath.put("some string", "hello \"bob\" \t");
		testMapHappyPath.put("someDouble", 87.3);
		
		try {
			jsonString = JSONUtilities.stringify(testMapHappyPath);
			String testString = "{\"someDouble\":87.3,\"some string\":\"hello \\\"bob\\\" \\t\",\"theDate\":\"1969-12-31 17:17:47.899\"}";
			System.out.println(jsonString);
			Assert(jsonString.equals(testString));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		/*
		 * Testing happy path HashMap with Booleans
		 */
		
		HashMap testMap = new HashMap();
		Boolean first = new Boolean(true);
		Boolean second = new Boolean(true);
		testMap.put("config_App_6237", first);
		testMap.put("Init_LoadResourceLastCacheDate", second);
		
		try {
			jsonString = JSONUtilities.stringify(testMap);
			Assert(jsonString.equals("{\"config_App_6237\":true,\"Init_LoadResourceLastCacheDate\":true}"));
			//System.out.println("map: "+jsonString);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		HashMap mapWithNullValue = new HashMap();
		long bigNumber = Long.MAX_VALUE;
		mapWithNullValue.put("real value", bigNumber);
		mapWithNullValue.put("null value", null);
		
		try {
			jsonString = JSONUtilities.stringify(mapWithNullValue);
			System.out.println("null: "+jsonString);
			Assert(jsonString.equals("{\"real value\":9223372036854775807}"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		ArrayList arrayWithNullValues = new ArrayList();
		arrayWithNullValues.add(null);
		arrayWithNullValues.add(17);
		arrayWithNullValues.add(null);
		arrayWithNullValues.add(null);
		
		try {
			jsonString = JSONUtilities.stringify(arrayWithNullValues);
			System.out.println(jsonString);
			Assert(jsonString.equals("[null,17,null,null]"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			String testString = "{\"theDate\":\"1969-12-31 17:17:47.889\",\"theString\":\"Hello there.\",\"theInt\":7}";
			System.out.println(jsonString);
			System.out.println(testString);
			//System.out.println("equal? "+jsonString.equals(testString));
			//Assert( jsonString.equals(testString));
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			String jsonString = JSONUtilities.stringify(anObject, JSONUtilities.encoding.UTF8);
			//System.out.println("happy path object UTF8: "+jsonString);
			Assert( jsonString.equals("{\"theDate\":\"1969-12-31 17:17:47.899\",\"theString\":\"Hello there.\",\"theInt\":7}"));
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
			Assert(jsonString == null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        
		/*
		 * Testing a null encoding or other invalid encoding
		 */
		try {
			String jsonString = JSONUtilities.stringify(anObject, (JSONUtilities.encoding)null);
			Assert( false);
		} catch (Exception e) {}
		
		/*
		 * Testing an array of Objects.  awt.Containters should be ignored.
		 */
		
		Object[] anObjectArray = {new Integer(4), "Hello", new Date(45879003)};
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UNICODE);
			Assert( jsonString != null);
			//Assert( jsonString.equals("[4,\"Hello\",\"Thu Jan 01 05:44:39 MST 1970\"]"));
			//System.out.println("object array UNICODE: "+jsonString); 
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UTF8);
			Assert( jsonString != null);
			//Assert( jsonString.equals("[4,\"Hello\",\"Thu Jan 01 05:44:39 MST 1970\"]"));
			//System.out.println("object array UTF8: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        
		char[] someChars = {'a','b','c'};
		Object[] annotherObjectArray = {new Integer(4), "Hello", someChars};
		try {
			String jsonString = JSONUtilities.stringify(annotherObjectArray, JSONUtilities.encoding.UNICODE);
			Assert( jsonString !=null);
			Assert( jsonString.equals("[4,\"Hello\",[\"a\",\"b\",\"c\"]]"));
			//System.out.println("Unicode string: "+jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			String jsonString = JSONUtilities.stringify(annotherObjectArray, JSONUtilities.encoding.UTF8);
			Assert( jsonString != null); 
			//System.out.println("happy path UTF8: "+jsonString);
			Assert( jsonString.equals("[4,\"Hello\",[\"a\",\"b\",\"c\"]]"));

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
			Assert( testMap != null);
			
			Number aNumber = (Double)testMap.get("aNumber");
			Assert( aNumber != null);
			Assert( aNumber.doubleValue() == 16.5);
			
			String aString = (String)testMap.get("stringOne");
			Assert( aString != null);
			Assert( aString.equals("Some sort of string"));
			
			aString = (String)testMap.get("20");
			Assert( aString != null);
			Assert( aString.equals("some other stuff"));
			 
			HashMap anObjectRepresentation = (HashMap)testMap.get("aTester");
			Assert( anObjectRepresentation != null);
			//check the attributes of the object embedded as an attribute of the outer object
			aString = (String)anObjectRepresentation.get("stringAtt");
			Assert( aString != null);
			Assert( aString.equals("hello"));
			aNumber = (Double)anObjectRepresentation.get("doubleAtt");
			Assert( aNumber != null);
			Assert( aNumber.doubleValue() == -4.5);
			//System.out.println("doubleAtt: "+anObjectRepresentation.get("doubleAtt"));
			
			aNumber = (Double)anObjectRepresentation.get("doubleObjAtt");
			Assert( aNumber != null);
			Assert( aNumber.doubleValue() == 1000.567789);
            
			ArrayList aList = (ArrayList)anObjectRepresentation.get("listAtt");
			Assert( aList != null);
			//check the values of the items in the list
			Long longNumber = (Long)aList.get(0);
			Assert( longNumber != null);
			Assert( longNumber.longValue() == 7);
			
			aString = (String)aList.get(1);
			Assert( aString != null);
			Assert( aString.equals("hello there from list"));
			
			//Check the last attribute of the object embedded as an attribute of the outer object
			aString = (String)anObjectRepresentation.get("parentString");
			Assert( aString != null);
			Assert( aString.equals("In The Parent"));
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
			Assert( nullObject == null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		/*
		 * Testing malformed JSON object.
		 */
		try {
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}");
			Assert( false);
		} catch (Exception e) {}
		
		/*
		 * Testing malformed JSON array.
		 */
		try {
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]");
			Assert( false);
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
			Assert( aNumber != null);
			Assert( aNumber.doubleValue() == 16.5);
			
			String aString = (String)testMap.get("stringOne");
			Assert( aString != null);
			Assert( aString.equals("Some sort of string"));
			
			aString = (String)testMap.get("20");
			Assert( aString != null);
			Assert( aString.equals("some other stuff"));
			
			HashMap anObjectRepresentation = (HashMap)testMap.get("aTester");
            //check the attributes of the object embedded as an attribute of the outer object
            aString = (String)anObjectRepresentation.get("stringAtt");
            Assert( aString != null);
            Assert( aString.equals("hello"));
            
            aNumber = (Number)anObjectRepresentation.get("doubleAtt");
            Assert( aNumber != null);
            Assert( aNumber.doubleValue() == -4.5);
            
            aNumber = (Number)anObjectRepresentation.get("doubleObjAtt");
            Assert( aNumber != null);
            Assert( aNumber.doubleValue() == 1000.567789);
            
            ArrayList aList = (ArrayList)anObjectRepresentation.get("listAtt");
            Assert( aList != null);
            Assert( aList.size() == 2);
            
            //check the values of the items in the list
            aNumber = (Number)aList.get(0);
            Assert( aNumber !=null);
            Assert( aNumber.intValue() == 7);
            
            aString = (String)aList.get(1);
            Assert( aString != null);
            Assert( aString.equals("hello there from list"));
            
            //Check the last attribute of the object embedded as an attribute of the outer object
            aString = (String)anObjectRepresentation.get("parentString");
            Assert( aString != null);
            Assert( aString.equals("In The Parent"));
            
            /*
             * UTF-8
             */
            //test the happy path with a well formed JSON string.
            testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":16.5,\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":-4.5,\"doubleObjAtt\":1000.567789,\"listAtt\":[7,\"hello there from list\"],\"parentString\":\"In The Parent\"}}",JSONUtilities.encoding.UTF8);
            Assert( testMap != null);
            
            aNumber = (Number)testMap.get("aNumber");
            Assert( aNumber != null);
            Assert( aNumber.doubleValue() == 16.5);
            
            aString = (String)testMap.get("stringOne");
            Assert( aString != null);
            Assert( aString.equals("Some sort of string"));
            
            aString = (String)testMap.get("20");
            Assert( aString != null);
            Assert( aString.equals("some other stuff"));
            
            anObjectRepresentation = (HashMap)testMap.get("aTester");
            Assert( anObjectRepresentation != null);
            //check the attributes of the object embedded as an attribute of the outer object
            aString = (String)anObjectRepresentation.get("stringAtt");
            Assert( aString != null);
            Assert( aString.equals("hello"));
            
            aNumber = (Number)anObjectRepresentation.get("doubleAtt");
            Assert( aNumber != null);
            Assert( aNumber.doubleValue() == -4.5);
            
            aNumber = (Number)anObjectRepresentation.get("doubleObjAtt");
            Assert( aNumber != null);
            //System.out.println("aNumber: "+aNumber.doubleValue());
            Assert( aNumber.doubleValue() == 1000.567789);
            
            aList = (ArrayList)anObjectRepresentation.get("listAtt");
            Assert( aList != null);
            Assert( aList.size() == 2);
            //check the values of the items in the list
            aNumber = (Number)aList.get(0);
            Assert( aNumber != null);
            Assert( aNumber.intValue() == 7);
            
            aString = (String)aList.get(1);
            Assert( aString != null);
            Assert( aString.equals("hello there from list"));
            
            //Check the last attribute of the object embedded as an attribute of the outer object
            aString = (String)anObjectRepresentation.get("parentString");
            Assert( aString != null);
            Assert( aString.equals("In The Parent"));
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
			Assert(nullObject == null);
            
			/*
			 * UTF-8
			 */
            
			nullObject = JSONUtilities.parse(null, JSONUtilities.encoding.UTF8);
			Assert(nullObject == null);
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
			Assert( false);
		} catch (Exception e) {
		}
		try{
			/*
			 * UTF-8
			 */
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}", JSONUtilities.encoding.UTF8);
			Assert( false);
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
			Assert( false);
		} catch (Exception e) {
		}
		try{
			/*
			 * UTF8
			 */
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]", JSONUtilities.encoding.UTF8);
			Assert( false);
		}
		catch (Exception e){
		}
		
		System.out.println("Passed testParseStringEncoding");
	}
	
	public static void testStringifyParse(){
		Date testDate = new Date(1067899);
		TestObject anObject = new TestObject("Hello there.", 7, testDate);
		try {
			String jsonString = JSONUtilities.stringify(anObject);
			Assert( jsonString != null);
			HashMap parsedMap = (HashMap)JSONUtilities.parse(jsonString);
			Assert( parsedMap != null);
			Assert( ((String)parsedMap.get("theString")).equals("Hello there."));
			Assert( ((Long)parsedMap.get("theInt")).intValue() == 7);
			System.out.println("parsed date: "+parsedMap.get("theDate"));
			//Assert( ((Date)parsedMap.get("theDate")).compareTo(testDate) == 0);
			Assert(((String)parsedMap.get("theDate")).equals("1969-12-31 17:17:47.899"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Passed testStringifyParse");
	}
	
	public static void testParseStringify(){
		try{
			HashMap testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":16.5,\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":-4.5,\"doubleObjAtt\":1000.567789,\"listAtt\":[7,\"hello there from list\"],\"parentString\":\"In The Parent\"}}");
			Assert( testMap != null);
			String jsonString = JSONUtilities.stringify(testMap);
			Assert( jsonString != null);
			Assert( jsonString.equals("{\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aNumber\":16.5,\"aTester\":{\"stringAtt\":\"hello\",\"listAtt\":[7,\"hello there from list\"],\"doubleAtt\":-4.5,\"doubleObjAtt\":1000.567789,\"parentString\":\"In The Parent\"}}"));
		} 
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		System.out.println("Passed testParseStringify");
	}
    
}
