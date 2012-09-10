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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
/**
 * The JSONInputStream class is used when you want to read JSON from any type of InputStream such
 * as a FileInputStream or a SocketInputStream.  If you want to convert JSON string to the 
 * appropriate Object and Array representations use the JSONUtilities.parse methods instead.
 * <br/>
 * <br/>
 * When your JSON is being parsed any JSON objects that it finds will be converted to HashMaps and
 *  any arrays found will be converted to ArrayLists.  All keys and values in the HashMaps generated
 *   are Strings and all values in the ArrayLists generated are Strings.
 *  <br/>
 *  <ul>
 *  <li><b>Example 1 JSON:</b> ["1", "hello", {"name":"fred","age":"23"}]</li>
 *  <li><b>Example 1 Result:</b>  An ArrayList with three values: a String "1", a String "hello", and a HashMap 
 *  as the third value in the ArrayList.  This HashMap has two key/value pairs: "name"/"fred" and "age"/"23".</li>
 *  </ul>
 * <br/>
 * <ul>
 * <li><b>Example 2 JSON:</b> {"state":"Idaho", "city":"Rexburg", "people":["bob","sue"]}</li>
 * <li><b>Example 2 Result:</b> A HashMap with three key/value pairs: "state"/"Idaho", "city"/"Rexburg", and
 *  "people"/ArrayList.  The ArrayList that is the value for the "people" key has two String values 
 *  "bob" and "sue".</li>
 *  </ul>
 *  <br/>
 *  <br/>
 *  While these examples only show two levels of depth your JSON string can go to any depth level.  You
 *  can have any combination of arrays, objects, sub-arrays, and sub-objects.
 *  
 * 
 * @author Lee S. Barney
 *
 */
public class JSONInputStream extends JSONStream{
	
	JSONParser aParser;

	/**
	 * 
	 * @param theByteStream - the stream from which the JSON is to be read.
	 */
	public JSONInputStream(InputStream theByteStream) {
		if(theByteStream == null){
			throw new NullPointerException();
		}
		InputStreamReader inReader = new InputStreamReader(theByteStream);
		aParser = new JSONParser(inReader);
	}


	/**
	 * Reads a HashMap or ArrayList from the underlying stream
	 * @return an Object of type HashMap if the JSON being read is an object or of type
	 * ArrayList if the JSON being read is an array.
	 * @throws JSONException
	 */
	public Object readObject() throws JSONException{
		if(theProtector != null){
			try {
				theProtector.claim();
			} catch (InterruptedException e) {
				throw new JSONException("Calling Thread interupted");
			}
		}
		try {
			Object parsedObject = aParser.parse();
			if(theProtector != null){
				theProtector.free();
			}
			return parsedObject;
		} catch (IOException e) {
			throw new JSONException("unable to read JSON");
		} catch (ParseException e) {
			throw new JSONException("Invalid JSON String");
		}
	}
	/**
	 * Closes the stream used.
	 * @throws IOException
	 */
	public void close() throws IOException{
		aParser.closeReader();
	}
}
