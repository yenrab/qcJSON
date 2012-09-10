package org.quickconnectfamily.json.tests;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//import javax.swing.JButton;

import org.junit.Test;
import org.quickconnectfamily.json.JSONException;
import org.quickconnectfamily.json.JSONUtilities;

public class JSONUtilitiesTest {

	/*
	 * Since the stringify method is a facade for the underlying JSONOutputStream writeObject method it only 
	 * needs to be tested to verify the facade is working correctly.
	 */
	@Test
	public void testStringifySerializable() {
		/*
		 * Testing a valid 'happy path' scenario
		 */
		TestObject anObject = new TestObject("Hello there.", 7, new Date(1067899));
		try {
			String jsonString = JSONUtilities.stringify(anObject);
			assertEquals("{\"theString\":\"Hello there.\",\"theInt\":\"7\",\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\"}", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing a null parameter
		 */
		try {
			String jsonString = JSONUtilities.stringify((Serializable)null);
			assertNull(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing an array of Objects.  awt.Containters should be ignored.
		 */
		/*
		Object[] anObjectArray = {new Integer(4), "Hello", new JButton()};
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray);
			assertEquals("[\"4\",\"Hello\"]", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		*/
	}
	
	@Test
	public void testStringifyCollections(){
		ArrayList test = new ArrayList();
		test.add(9L);
		test.add(8L);
		test.add(7L);
		try {
			String testString = JSONUtilities.stringify(test);
			assertEquals("[\"9\",\"8\",\"7\"]",testString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}

	@Test
	public void testStringifySerializableEncoding() {
		/*
		 * Testing a valid 'happy path' scenario
		 */
		TestObject anObject = new TestObject("Hello there.", 7, new Date(1067899));
		try {
			String jsonString = JSONUtilities.stringify(anObject, JSONUtilities.encoding.UNICODE);
			assertEquals("{\"theString\":\"Hello there.\",\"theInt\":\"7\",\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\"}", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		try {
			String jsonString = JSONUtilities.stringify(anObject, JSONUtilities.encoding.UTF8);
			assertEquals("{\"theString\":\"Hello there.\",\"theInt\":\"7\",\"theDate\":\"Wed Dec 31 17:17:47 MST 1969\"}", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing a null parameter
		 */
		try {
			String jsonString = JSONUtilities.stringify((Serializable)null, JSONUtilities.encoding.UNICODE);
			assertNull(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}

		/*
		 * Testing a null encoding or other invalid encoding
		 */
		try {
			String jsonString = JSONUtilities.stringify(anObject, (JSONUtilities.encoding)null);
			fail("should have thrown exception");
		} catch (JSONException e) {}
		
		/*
		 * Testing an array of Objects.  awt.Containters should be ignored.
		 */
/*		
		Object[] anObjectArray = {new Integer(4), "Hello", new JButton()};
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UNICODE);
			assertEquals("[\"4\",\"Hello\"]", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UTF8);
			assertEquals("[\"4\",\"Hello\"]", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}
*/
		char[] someChars = {'a','b','c'};
		Object[] anObjectArray = {new Integer(4), "Hello", someChars};
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UNICODE);
			System.out.println(jsonString);
			assertEquals("[\"4\",\"Hello\"]", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		try {
			String jsonString = JSONUtilities.stringify(anObjectArray, JSONUtilities.encoding.UTF8);
			assertEquals("[\"4\",\"Hello\"]", jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
	}
	@Test
	public void testParseString() {
		try{
			//test the happy path with a well formed JSON string.
			HashMap testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}");
			String aNumber = (String)testMap.get("aNumber");
			assertNotNull(aNumber);
			assertEquals(aNumber, "16.5");
			
			String aString = (String)testMap.get("stringOne");
			assertNotNull(aString);
			assertEquals("Some sort of string", aString);
			
			aString = (String)testMap.get("20");
			assertNotNull(aString);
			assertEquals("some other stuff", aString);
			
			HashMap anObjectRepresentation = (HashMap)testMap.get("aTester");
				//check the attributes of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("stringAtt");
				assertNotNull(aString);
				assertEquals("hello", aString);
				
				aNumber = (String)anObjectRepresentation.get("doubleAtt");
				assertNotNull(aNumber);
				assertEquals("-4.5", aNumber);
				
				aNumber = (String)anObjectRepresentation.get("doubleObjAtt");
				assertNotNull(aNumber);
				assertEquals("1000.567789", aNumber);
				
				ArrayList aList = (ArrayList)anObjectRepresentation.get("listAtt");
				assertNotNull(aList);
				assertEquals(2, aList.size());
				//check the values of the items in the list
					aNumber = (String)aList.get(0);
					assertNotNull(aNumber);
					assertEquals("7", aNumber);
					
					aString = (String)aList.get(1);
					assertNotNull(aString);
					assertEquals("hello there from list", aString);
				
				//Check the last attribute of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("parentString");
				assertNotNull(aString);
				assertEquals("In The Parent", aString);
		}
		catch(Exception e){
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing passing a null string parameter.
		 */
		try {
			Object nullObject = JSONUtilities.parse(null);
			assertNull(nullObject);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing malformed JSON object.
		 */
		try {
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}");
			fail("Should have thrown exception");
		} catch (JSONException e) {}
		
		/*
		 * Testing malformed JSON array.
		 */
		try {
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]");
			fail("Should have thrown exception");
		} catch (JSONException e) {}
		
		
	}

	@Test
	public void testParseStringEncoding() {
		try{
			//test the happy path with a well formed JSON string.
			/*
			 * UNICODE
			 */
			HashMap testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}",JSONUtilities.encoding.UNICODE);
			String aNumber = (String)testMap.get("aNumber");
			assertNotNull(aNumber);
			assertEquals(aNumber, "16.5");
			
			String aString = (String)testMap.get("stringOne");
			assertNotNull(aString);
			assertEquals("Some sort of string", aString);
			
			aString = (String)testMap.get("20");
			assertNotNull(aString);
			assertEquals("some other stuff", aString);
			
			HashMap anObjectRepresentation = (HashMap)testMap.get("aTester");
				//check the attributes of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("stringAtt");
				assertNotNull(aString);
				assertEquals("hello", aString);
				
				aNumber = (String)anObjectRepresentation.get("doubleAtt");
				assertNotNull(aNumber);
				assertEquals("-4.5", aNumber);
				
				aNumber = (String)anObjectRepresentation.get("doubleObjAtt");
				assertNotNull(aNumber);
				assertEquals("1000.567789", aNumber);
				
				ArrayList aList = (ArrayList)anObjectRepresentation.get("listAtt");
				assertNotNull(aList);
				assertEquals(2, aList.size());
				//check the values of the items in the list
					aNumber = (String)aList.get(0);
					assertNotNull(aNumber);
					assertEquals("7", aNumber);
					
					aString = (String)aList.get(1);
					assertNotNull(aString);
					assertEquals("hello there from list", aString);
				
				//Check the last attribute of the object embedded as an attribute of the outer object
				aString = (String)anObjectRepresentation.get("parentString");
				assertNotNull(aString);
				assertEquals("In The Parent", aString);
				
				/*
				 * UTF-8
				 */
				//test the happy path with a well formed JSON string.
				testMap = (HashMap)JSONUtilities.parse("{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}",JSONUtilities.encoding.UTF8);
				aNumber = (String)testMap.get("aNumber");
				assertNotNull(aNumber);
				assertEquals(aNumber, "16.5");
				
				aString = (String)testMap.get("stringOne");
				assertNotNull(aString);
				assertEquals("Some sort of string", aString);
				
				aString = (String)testMap.get("20");
				assertNotNull(aString);
				assertEquals("some other stuff", aString);
				
				anObjectRepresentation = (HashMap)testMap.get("aTester");
					//check the attributes of the object embedded as an attribute of the outer object
					aString = (String)anObjectRepresentation.get("stringAtt");
					assertNotNull(aString);
					assertEquals("hello", aString);
					
					aNumber = (String)anObjectRepresentation.get("doubleAtt");
					assertNotNull(aNumber);
					assertEquals("-4.5", aNumber);
					
					aNumber = (String)anObjectRepresentation.get("doubleObjAtt");
					assertNotNull(aNumber);
					assertEquals("1000.567789", aNumber);
					
					aList = (ArrayList)anObjectRepresentation.get("listAtt");
					assertNotNull(aList);
					assertEquals(2, aList.size());
					//check the values of the items in the list
						aNumber = (String)aList.get(0);
						assertNotNull(aNumber);
						assertEquals("7", aNumber);
						
						aString = (String)aList.get(1);
						assertNotNull(aString);
						assertEquals("hello there from list", aString);
					
					//Check the last attribute of the object embedded as an attribute of the outer object
					aString = (String)anObjectRepresentation.get("parentString");
					assertNotNull(aString);
					assertEquals("In The Parent", aString);
		}
		catch(Exception e){
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing passing a null string parameter.
		 */
		try {

			/*
			 * UNICODE
			 */
			Object nullObject = JSONUtilities.parse(null, JSONUtilities.encoding.UNICODE);
			assertNull(nullObject);

			/*
			 * UTF-8
			 */

			nullObject = JSONUtilities.parse(null, JSONUtilities.encoding.UTF8);
			assertNull(nullObject);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		/*
		 * Testing malformed JSON object.
		 */
		try {
			/*
			 * UNICODE
			 */
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}", JSONUtilities.encoding.UNICODE);
			fail("Should have thrown exception");
		} catch (JSONException e) {}
		try{
			/*
			 * UTF-8
			 */
			Object someObject = JSONUtilities.parse("{\"key\": \"anotherKey\":\"a value\"}", JSONUtilities.encoding.UTF8);
			fail("Should have thrown exception");
		} catch (JSONException e) {}
		
		/*
		 * Testing malformed JSON array.
		 */
		try {
			/*
			 * UNICODE
			 */
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]", JSONUtilities.encoding.UNICODE);
			fail("Should have thrown exception");
		} catch (JSONException e) {}
		try{
			/*
			 * UTF8
			 */
			Object someObject = JSONUtilities.parse("[90, some stuff, \"other stuff\"]", JSONUtilities.encoding.UTF8);
			fail("Should have thrown exception");
		}
		catch (JSONException e){}
	}

}
