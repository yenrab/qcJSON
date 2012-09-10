package org.quickconnectfamily.json.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;

import org.junit.Test;
import org.quickconnectfamily.json.JSONException;
import org.quickconnectfamily.json.JSONOutputStream;

public class JSONOutputStreamTest {

	@SuppressWarnings("unused")
	@Test
	public void testWriteContructor(){
		/*
		 * Testing valid stream passed as parameter.
		 * Make sure that the internal writer object gets
		 * set.
		 */
		try{
			FileOutputStream aFileOutStream = new FileOutputStream("testFile");
			JSONOutputStream aJSONFileStream = new JSONOutputStream(aFileOutStream);
			Class<?> theJSONOutputStreamClass = aJSONFileStream.getClass();

		    Field theWriterField = theJSONOutputStreamClass.getDeclaredField("theWriter");
		    theWriterField.setAccessible(true);
		    assertNotNull(theWriterField.get(aJSONFileStream));
		}
		catch(Exception e){
			fail("Exception thrown: "+e);
		}
		
		try{
			JSONOutputStream aNullJSONStream = new JSONOutputStream(null);
			//fail since a null pointer exception should have been thrown
			fail("Should have thrown exception");
		}
		catch(NullPointerException e){/*do nothing since it should end up being caught here*/}
		
		
		
}
	@SuppressWarnings("unchecked")
	@Test
	public void testWriteObject() {
		
		
		
		JSONOutputStream testOut = setupTestFile();
		
		
		/*
		 * Happy path test
		 */
		byte blah = 3;
		@SuppressWarnings("rawtypes")
		HashMap aMap = new HashMap();
		aMap.put("stringOne", "Some sort of string");
		//aMap.put("aNumber", 16.5);
		aMap.put("aNumber",blah);
		aMap.put(20,"some other stuff" );
		aMap.put("aTester",new Tester());
		
		try {
			testOut.writeObject(aMap);
			closeTheWriterIn(testOut);
			File testFile = new File("test.json");
			assertTrue(testFile.exists());
			
		} catch (JSONException e) {
			e.printStackTrace();

			fail("Exception thrown");
		}
		String readResult = readTestFile();
		System.out.println(readResult);
		System.out.println("{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}");
		assertEquals(readResult, "{\"aNumber\":\"16.5\",\"stringOne\":\"Some sort of string\",\"20\":\"some other stuff\",\"aTester\":{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}}");
		
		
		/*
		 * Testing null
		 */
		testOut = setupTestFile();
		try {
			testOut.writeObject(null);
			closeTheWriterIn(testOut);
			readResult = readTestFile();
			assertNull(readResult);
				
		} 
		catch (JSONException e) {
			e.printStackTrace();
			fail("Threw exception");
		}

		/*
		 * Testing an array of Objects.  awt.Containters should be ignored.
		 */
		
		testOut = setupTestFile();
		
		Object[] anArray = {new Integer(4), "Hello", new JButton()};
		try {
			testOut.writeObject(anArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"4\",\"Hello\"]", readResult);
		

		testOut = setupTestFile();
		Tester aTesterObject = new Tester();
		Object[] anArrayOfObjects = new Object[5];
		Arrays.fill(anArrayOfObjects, aTesterObject);
		try {
			testOut.writeObject(anArrayOfObjects);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		assertEquals("[{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"},{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"},{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"},{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}{\"stringAtt\":\"hello\",\"doubleAtt\":\"-4.5\",\"doubleObjAtt\":\"1000.567789\",\"listAtt\":[\"7\",\"hello there from list\"],\"parentString\":\"In The Parent\"}]", readResult);
			

		
		/*
		 * Testing an array of ints
		 */
		
		testOut = setupTestFile();
		
		int[] anIntArray = {1,2,417,3,60, 50};
		try {
			testOut.writeObject(anIntArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"1\",\"2\",\"417\",\"3\",\"60\",\"50\"]", readResult);
		
		/*
		 * Testing an array of shorts
		 */
		testOut = setupTestFile();
		short[] aShortArray = {1,2,417,3,60, 50};
		try {
			testOut.writeObject(aShortArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"1\",\"2\",\"417\",\"3\",\"60\",\"50\"]", readResult);
		
		/*
		 * Testing an array of longs
		 */
		testOut = setupTestFile();
		long[] aLongArray = {1,2,417,3,60, 50};
		try {
			testOut.writeObject(aLongArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"1\",\"2\",\"417\",\"3\",\"60\",\"50\"]", readResult);
		/*
		 * Testing an array of doubles
		 */
		
		testOut = setupTestFile();
		double[] aDoubleArray = {1.1,3.14,2.124,3.0,.00078, 1.0/3.0};
		try {
			testOut.writeObject(aDoubleArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"1.1\",\"3.14\",\"2.124\",\"3.0\",\"7.8E-4\",\"0.3333333333333333\"]", readResult);
		
		/*
		 * Testing an array of floats
		 */

		testOut = setupTestFile();
		double[] aFloatArray = {1.1,3.14,2.124,3.0,.00078, 1.0/3};
		try {
			testOut.writeObject(aFloatArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"1.1\",\"3.14\",\"2.124\",\"3.0\",\"7.8E-4\",\"0.3333333333333333\"]", readResult);
		
		/*
		 * Testing an array of chars
		 */

		testOut = setupTestFile();
		char[] aCharArray = new String("Hello there!").toCharArray();
		try {
			testOut.writeObject(aCharArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"H\",\"e\",\"l\",\"l\",\"o\",\" \",\"t\",\"h\",\"e\",\"r\",\"e\",\"!\"]", readResult);
		
		/*
		 * Testing an array of bytes
		 */

		testOut = setupTestFile();
		byte[] aByteArray = {4,1,0,7,89,76};
		try {
			testOut.writeObject(aByteArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		
		assertEquals("[\"4\",\"1\",\"0\",\"7\",\"89\",\"76\"]", readResult);
		
		/*
		 * Testing array of null object values passed as the parameter
		 */
		testOut = setupTestFile();
		Object[] aObjectArray = new Object[5];
		try {
			testOut.writeObject(aObjectArray);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("exception thrown");
		}
		readResult = readTestFile();
		assertEquals("[]", readResult);
		
	}
	
	private void closeTheWriterIn(JSONOutputStream testOut) {
		try {
			Field writerField = testOut.getClass().getDeclaredField("theWriter");
			//make the private attribute public
			writerField.setAccessible(true);
			PrintWriter theWriter = (PrintWriter)writerField.get(testOut);
			theWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception");
		}
		
	}
	private String readTestFile() {
		String fileContents = null;
		try {
			BufferedReader testFileReader = new BufferedReader(new FileReader("test.json"));
			fileContents = testFileReader.readLine();
			testFileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("could not find test file");
		} catch (IOException e) {
			e.printStackTrace();
			fail("could not read from test file");
		}
		return fileContents;
	}
	private JSONOutputStream setupTestFile(){
		/*
		 * Cleanup the test file
		 */
		File testFile = new File("test.json");
		if(testFile.exists()){
			testFile.delete();
		}
		FileOutputStream testFileStream = null;
		try {
			testFileStream = new FileOutputStream(testFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("unable to create test.json file");
		}
		
		
		return new JSONOutputStream(testFileStream);
		
	}
	
	
	/*
	 * An inner class used for testing purposes.
	 */
	@SuppressWarnings("serial")
	private class Tester extends TesterParent implements Serializable{
		@SuppressWarnings("unused")
		private String stringAtt = "hello";
		@SuppressWarnings("unused")
		private double doubleAtt = -4.5;
		@SuppressWarnings("unused")
		private Double doubleObjAtt = 1000.5677890;
		@SuppressWarnings("rawtypes")
		private ArrayList listAtt = new ArrayList();
		
		@SuppressWarnings("unchecked")
		public Tester(){
			listAtt.add(7);
			listAtt.add("hello there from list");
		}
	}
	
	private class TesterParent{
		@SuppressWarnings("unused")
		private String parentString = "In The Parent";
	}

}
