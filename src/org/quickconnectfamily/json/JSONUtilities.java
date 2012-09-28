/*
 Copyright (c) 2008, 2009 Lee Barney
 Permission is hereby granted, free of charge, to any person obtaining a 
 copy of this software and associated documentation files (the "Software"), 
 to deal in the Software without restriction, including without limitation the 
 rights to use, copy, modify, merge, publish, distribute, sublicense, 
 and/or sell copies of the Software, and to permit persons to whom the Software 
 is furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be 
 included in all copies or substantial portions of the Software.

 
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 
 
 */
package org.quickconnectfamily.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
/**
 * This class contains several utility methods for generating and parsing JSON strings.  
 * Care has been taken to make these match the JavaScript JSON API as much as possible.
 *   Since these methods are static you may use them without worry from within any thread 
 *   or in multiple threads.
 * @author Lee S. Barney
 *
 */
public class JSONUtilities {

	public static int version = 1;
	public static int subversion = 5;
	public static boolean isBeta = true;
	/**
	 * Encodings available for parsed and generated JSON Strings
	 * @author Lee S. Barney
	 *
	 */
	public enum encoding {
		UNICODE,
		UTF8
	}
	
	
	public static String getVersion(){
		return "V "+version+"."+subversion+" "+ (JSONUtilities.isBeta ? "beta": "release");
	}
	/**
	 * Converts a Serializable object into a JSON formatted string
	 * @param aSerializableObject - the object to be JSONed.  This can be any Serializable Object except 
	 * a raw Object or anything that inherits from java.awt.container.
	 * @return a JSON formatted String or if null is passed in null is returned.
	 * @throws JSONException
	 */
	public static String stringify(Serializable aSerializableObject) throws JSONException{
		
		if(aSerializableObject == null){
			return null;
		}
		ByteArrayOutputStream theByteStream = new ByteArrayOutputStream();
		JSONOutputStream theStream = new JSONOutputStream(theByteStream);
		theStream.writeObject(aSerializableObject);
		return new String(theByteStream.toByteArray());
	}
	
	/**
	 * Converts a Serializable object into a JSON formatted string using the specified Encoding
	 * @param aSerializableObject - the object to be JSONed.  This can be any Serializable Object except 
	 * a raw Object or anything that inherits from java.awt.container.
	 * @param theEncoding - the encoding to use for the desired string.  It must be one 
	 * of the encodings declared in JSONUtilities.encoding.
	 * @return aJSON formatted String using the desired encoding
	 * @throws JSONException
	 */
	public static String stringify(Serializable aSerializableObject, JSONUtilities.encoding theEncoding) throws JSONException{
		if(aSerializableObject == null){
			return null;
		}
		if(theEncoding != encoding.UNICODE && theEncoding != encoding.UTF8){
			throw new JSONException("Unsupported encoding: "+theEncoding);
		}
		ByteArrayOutputStream theByteStream = new ByteArrayOutputStream();
		JSONOutputStream theStream = new JSONOutputStream(theByteStream);
		theStream.writeObject(aSerializableObject);
		try {
			return theByteStream.toString(theEncoding == encoding.UNICODE ? "ISO-8859-1" : "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new JSONException("Unsupported encoding: "+theEncoding);
		}
	}
	

	/**
	 * Parses a string using the default platform encoding.  Either a HashMap or ArrayList is generated.
	 * @param aJSONString - the string to be parsed.  It is assumed that aJSONString uses the
	 *  default encoding for the platform.
	 * @return either a HashMap or an ArrayList depending on the contents of the parameter string
	 * @throws JSONException
	 */
	public static Object parse(String aJSONString) throws JSONException{
		if(aJSONString == null){
			return null;
		}
		byte[] byteArray = aJSONString.getBytes();
		ByteArrayInputStream theByteStream = new ByteArrayInputStream(byteArray);
		JSONInputStream theStream = new JSONInputStream(theByteStream);
		return theStream.readObject();
	}
	

	/**
	 * Parses a string using the defined encoding.  Either a HashMap or ArrayList is generated.
	 * @param aJSONString - the string to be parsed.  For normal behavior it must be in the encoding
	 *  declared as the theEncoding parameter.
	 * @param theEncoding - the encoding of the String passed in as aJSONString.  It must be one 
	 * of the encodings declared in JSONUtilities.encoding.
	 * @return either a HashMap or an ArrayList depending on the contents of the parameter string
	 * @throws JSONException
	 */
	public static Object parse(String aJSONString, JSONUtilities.encoding theEncoding) throws JSONException{
		if(aJSONString == null){
			return null;
		}
		if(theEncoding != encoding.UNICODE && theEncoding != encoding.UTF8){
			throw new JSONException("Unsupported encoding: "+theEncoding);
		}
		
		byte[] byteArray;
		try {
			byteArray = aJSONString.getBytes(theEncoding == encoding.UNICODE ? "ISO-8859-1" : "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			throw new JSONException("Unsupported encoding: "+theEncoding);
		}
		ByteArrayInputStream theByteStream = new ByteArrayInputStream(byteArray);
		JSONInputStream theStream = new JSONInputStream(theByteStream);
		return theStream.readObject();
	}
}
