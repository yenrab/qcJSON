package org.quickconnectfamily.json.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.quickconnectfamily.json.JSONException;
import org.quickconnectfamily.json.JSONInputStream;

public class JSONInputStreamTest {

	@Test
	public void testReadConstructor(){

		String aJSONString = "{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}\n";

		byte[] byteArray = aJSONString.getBytes(); 
		ByteArrayInputStream theByteStream = new ByteArrayInputStream(byteArray);
		JSONInputStream aJSONInputStream = new JSONInputStream(theByteStream);
		Class<?> theJSONInputStreamClass = aJSONInputStream.getClass();

	    try {
			Field theParserField = theJSONInputStreamClass.getDeclaredField("aParser");
			theParserField.setAccessible(true);
			assertNotNull(theParserField.get(aJSONInputStream));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
	}
	/*
	@Test
	public void testReadObject() {
		String aJSONString = "{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}";

		byte[] byteArray = aJSONString.getBytes(); 
		ByteArrayInputStream theByteStream = new ByteArrayInputStream(byteArray);
		JSONInputStream aJSONStream = new JSONInputStream(theByteStream);
		try {
			HashMap aMap = (HashMap)aJSONStream.readObject();
			assertEquals("16.5", (String)aMap.get("aNumber"));
			assertEquals("Some sort of string", (String)aMap.get("stringOne"));
			assertEquals("some other stuff", (String)aMap.get("20"));
			Object aPotentialMap = aMap.get("aTester");
			assertEquals(HashMap.class, aPotentialMap.getClass());
			HashMap aTesterMap = (HashMap)aPotentialMap;
			//check the values of the tester objects' attributes
				assertEquals("hello", (String)aTesterMap.get("stringAtt"));
				Object aPotentialArrayList = aTesterMap.get("listAtt");
				assertEquals(ArrayList.class, aPotentialArrayList.getClass());
				ArrayList anAttributeThatIsAnArrayList = (ArrayList)aPotentialArrayList;
				//check the values in the array attribute of the tester object
					assertEquals(2, anAttributeThatIsAnArrayList.size());
					assertEquals("7", (String)anAttributeThatIsAnArrayList.get(0));
					assertEquals("hello there from list", (String)anAttributeThatIsAnArrayList.get(1));
				assertEquals("-4.5", (String)aTesterMap.get("doubleAtt"));
				assertEquals("1000.567789", (String)aTesterMap.get("doubleObjAtt"));
				assertEquals("In The Parent", (String)aTesterMap.get("parentString"));
			
		} catch (JSONException e) {
			e.printStackTrace();
			fail("should not have thrown exception");
		}
		
	}
	
*/
	@Test
	public void testMultipleObjectRead() {
		File multiObjectFile = new File("multi_objects.json");
		if(multiObjectFile.exists()){
			multiObjectFile.delete();
		}
		try {
			PrintWriter fileWriter = new PrintWriter(new FileOutputStream(multiObjectFile));
			fileWriter.println("{\"name\":\"Bob\",\"age\":27}[7,16,0 -4]");
			fileWriter.flush();

			JSONInputStream jsonIn = new JSONInputStream(new FileInputStream(multiObjectFile));

			/*
			 * Read the first object from the file
			 */
			Object aPossibleMap = jsonIn.readObject();
			assertEquals(HashMap.class, aPossibleMap.getClass());
			HashMap aMap = (HashMap)aPossibleMap;
			assertEquals("Bob", aMap.get("name"));
			assertEquals((long)27, aMap.get("age"));
			
			/*
			 * Test if a second entry be read correctly?
			 */
			Object aPossibleArrayList = jsonIn.readObject();
			assertEquals(ArrayList.class, aPossibleArrayList.getClass());
			ArrayList anArrayList = (ArrayList)aPossibleArrayList;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not have thrown exception");
		}
		
		
		
	}

}
