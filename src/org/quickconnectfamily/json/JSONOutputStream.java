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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
/**
 * The JSONOutputStream class is used when you want to send an object as a JSON string to any type of OutputStream such
 * as a FileOutputStream or a SocketOutputStream.  If you want to generate a JSON string from an object
 *  directly use the JSONUtilities.stringify methods instead.
 *  <br/>
 *  <br/>
 *  Attributes inherited from parent classes are also included in the JSON string.
 * <br/>
 * <br/>
 * <ul>
 *  <li><b>Example 1 Object:</b>  An ArrayList with three values: a String "1", a String "hello", and a HashMap 
 *  as the third value in the ArrayList.  This HashMap has two key/value pairs: "name"/"fred" and "age"/"23".</li>
 *  
 *  <li><b>Example 1 JSON result:</b> ["1", "hello", {"name":"fred","age":"23"}]</li>
 *  </ul>
 * <br/>
 * <ul>
 * <li><b>Example 2 Object:</b> A HashMap with three key/value pairs: "state"/"Idaho", "city"/"Rexburg", and
 *  "people"/ArrayList.  The ArrayList that is the value for the "people" key has two String values 
 *  "bob" and "sue".</li>
 *  
 * <li><b>Example 2 JSON result:</b> {"state":"Idaho", "city":"Rexburg", "people":["bob","sue"]}</li>
 * </ul>
 *  <br/>
 *  <br/>
 *  While these examples only show two levels of depth your Objects can go to any attribute depth level.  You
 *  can have any combination of Java objects as attributes.
 *  <br/>
 *  <br/>
 *  <h2>Restrictions</h2>
 *  The Java object from which the JSON string is being generated can not be a raw Object.  I can be anything that inherits from Object.
 *  The Java object from which the JSON string is being generated can not inherit from java.awt.container.
 *  
 * 
 * @author Lee S. Barney
 *
 */
public class JSONOutputStream extends JSONStream{
	private PrintWriter theWriter;
	private int levelCountLimit;
	//this is a hack work around since Android is returning true when a string is sent the isInstance("java.awt.Container") method call.
	boolean isAndroid = false;
	
	/**
	 * 
	 * @param aStream - the stream to which the JSON is to be written
	 */
	public JSONOutputStream(OutputStream aStream){
		if(aStream == null){
			throw new NullPointerException();
		}
		theWriter = new PrintWriter(aStream);
		levelCountLimit = 30;
		//hack work around.  See message regarding isAndroid above.
		try{
			Object aContainer = Class.forName("java.awt.Container");
			if(aContainer == null){
				isAndroid = true;
			}
		}
		catch(Exception e){
			isAndroid = true;
		}
	}
	
	/**
	 * Writes a Serializable Object to the underlying stream as a JSON string
	 * @param aSerializableObject - any Serializable object other than a raw Java Object and anything that inherits from java.awt.container
	 * @throws JSONException
	 */
	
	public void writeObject(Serializable aSerializableObject) throws JSONException{
		if(theProtector != null){
			try {
				theProtector.claim();
			} catch (InterruptedException e) {
				throw new JSONException("Calling Thread interupted");
			}
		}
		writeObject(aSerializableObject, 0);
		if(theProtector != null){
			theProtector.free();
		}
	}
	@SuppressWarnings("rawtypes")

	private void writeObject(Serializable aSerializableObject, int levelCount) throws JSONException{
		/*
		 * Android doesn't have awt.
		 */
		try{
			 if(aSerializableObject == null || aSerializableObject.getClass().equals(Object.class)){
				 return;
			 }

			 //The following line returns true when run in Android when it should not.  Don't use it.
			 //else if(|| aSerializableObject.getClass().isInstance("java.awt.Container")){
			 /*
			  * run up the inheritance tree and see if it is a container.
			  */
			 Class aClass = aSerializableObject.getClass();
			 if(!isAndroid){ 
				 while((aClass = aClass.getSuperclass())  != null && !aClass.getName().equals("java.awt.Container")){
					 //System.out.println("class Name: "+aClass.getName());
				 }
				 
				 if(aClass != null && aClass.getName().equals("java.awt.Container")){
					return;
				}
			 }
		}
		catch(Throwable t){
			//do the Android specific check
			 if(aSerializableObject == null || aSerializableObject.getClass().equals(Object.class)){
					return;
				}
		}
		levelCount++;
		if(levelCount > 30){
			throw new JSONException("Depth limit of "+levelCountLimit+" exceeded in object "+aSerializableObject+" of class "+aSerializableObject.getClass().getName()+".");
		}

		if(aSerializableObject instanceof Boolean){
			theWriter.write(((Boolean)aSerializableObject).toString());
		}

		else if(aSerializableObject instanceof Date){
			Timestamp aStamp = new Timestamp(((Date)aSerializableObject).getTime());
			theWriter.write("\""+aStamp.toString()+"\"");
		}
		else if(aSerializableObject instanceof Map){
			Map aMap = (Map)aSerializableObject;
			theWriter.write("{");
			Set keys = aMap.keySet();
			Iterator keyIt = keys.iterator();
			int count = 0;
			while(keyIt.hasNext()){
				Object key = keyIt.next();
				Object value = aMap.get(key);
				if(value == null){
					continue;
				}
				if(count != 0){
					theWriter.write(",");
				}
				if(!(value instanceof Serializable)){
					throw new JSONException("Unable to JSON non-serializable object ("+value+") of type "+value.getClass().toString()+".");
				}
				theWriter.write("\""+key.toString()+"\":");
				writeObject(((Serializable)value), levelCount);
				
				count++;
			}
			theWriter.write("}");
		}
		else if(aSerializableObject instanceof List){
			List aList = (List)aSerializableObject;
			Iterator keyIt = aList.iterator();
			theWriter.write("[");
			while(keyIt.hasNext()){
				Object value = keyIt.next();
				if(value == null){
					value = "null";
				}
				if(!(value instanceof Serializable)){
					throw new JSONException("Unable to JSON non-serializable object ("+value+") of type "+value.getClass().toString()+".");
				}
				writeObject(((Serializable)value), levelCount);
				if(keyIt.hasNext()){
					theWriter.write(",");
				}
			}

			theWriter.write("]");
		}
		else if(aSerializableObject instanceof String){
			
			String appendString = (String)aSerializableObject;
			if(!appendString.equals("null")){
				appendString = "\""+escapeStringForJSON( ((String)aSerializableObject) )+"\"";
			}
			theWriter.append(appendString);
		}
		else if(aSerializableObject instanceof Number){
			theWriter.append(aSerializableObject.toString());
			/*
			if(aSerializableObject instanceof Integer){
				theWriter.append(((Integer)aSerializableObject).toString());
			}
			else if(aSerializableObject instanceof Long){
				theWriter.append(((Long)aSerializableObject).toString());
			}
			else if(aSerializableObject instanceof Short){
				theWriter.append(((Integer)aSerializableObject).toString());
			}
			else if(aSerializableObject instanceof Double){
				theWriter.append(((Double)aSerializableObject).toString());
			}
			else if(aSerializableObject instanceof Float){
				theWriter.append(((Float)aSerializableObject).shortValue()+"\"");
			}
			else{
				theWriter.append("\""+aSerializableObject.toString()+"\"");
			}
			*/
		}
		//object arrays
		else if(aSerializableObject instanceof Object[]){
			theWriter.append('[');
			Object[] theArray = (Object[])aSerializableObject;
			for(int i = 0; i < theArray.length; i++){
				Object anObject = theArray[i];
				if(anObject == null){
					anObject = "null";
				}
				else if(!(anObject instanceof Serializable)){
					continue;
				}
				writeObject(((Serializable)anObject), levelCount);
				if(i < theArray.length - 1){
					theWriter.append(',');
				}
			}
			theWriter.append(']');
		}
		//primative arrays
		else if(aSerializableObject.getClass().isArray()){
			Class primitiveArrayClass = aSerializableObject.getClass();
			if(int[].class == primitiveArrayClass){
				theWriter.append('[');
				int[] theArray = (int[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					int aValue = theArray[i];
					theWriter.append(Integer.toString(aValue));
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}
			else if(short[].class == primitiveArrayClass){
				theWriter.append('[');
				short[] theArray = (short[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					short aValue = theArray[i];
					theWriter.append(Short.toString(aValue));
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}
			else if(long[].class == primitiveArrayClass){
				theWriter.append('[');
				long[] theArray = (long[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					long aValue = theArray[i];
					theWriter.append(Long.toString(aValue));
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}

			else if(double[].class == primitiveArrayClass){
				theWriter.append('[');
				double[] theArray = (double[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					double aValue = theArray[i];
					theWriter.append(Double.toString(aValue));
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}
			else if(float[].class == primitiveArrayClass){
				theWriter.append('[');
				float[] theArray = (float[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					float aValue = theArray[i];
					theWriter.append(Float.toString(aValue));
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}
			else if(char[].class == primitiveArrayClass){
				theWriter.append('[');
				char[] theArray = (char[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					char aValue = theArray[i];
					theWriter.append("\""+aValue+"\"");
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}
			else if(byte[].class == primitiveArrayClass){
				theWriter.append('[');
				byte[] theArray = (byte[])aSerializableObject;
				for(int i = 0; i < theArray.length; i++){
					byte aValue = theArray[i];
					theWriter.append("\""+aValue+"\"");
					if(i < theArray.length - 1){
						theWriter.append(',');
					}
				}
				theWriter.append(']');
			}
			
		}
		else/*is instance of java.lang.Object*/{
			theWriter.write('{');
			writeAllAttributesOf(aSerializableObject, aSerializableObject.getClass(), levelCount);
			theWriter.write('}');
		}
		theWriter.flush();
	}
	
	
	private void writeAllAttributesOf(Serializable aSerializableObject, Class<?> aClass, int levelCount) throws JSONException{
		//aClass may be the final child class or one of the parent classes
		Field[] theFields = aClass.getDeclaredFields();
		try{
			for(int i = 0; i < theFields.length; i++){
				Field aField = theFields[i];
				aField.setAccessible(true);
				
				String fieldName = aField.getName();
				Object value = aField.get(aSerializableObject);
				if(!(value instanceof Serializable)){
					continue;
				}
				if( i != 0){
					theWriter.write(",");
				}
				int modifiers = aField.getModifiers();
				//ignore final attributes, attributes that are null, and any outer class references of inner classes
				if(!Modifier.isFinal(modifiers) && value != null && !fieldName.equals("this$0")){
					
					theWriter.write("\""+fieldName+"\":");
					writeObject(((Serializable)value), levelCount);
				}
				
			}
		}
		catch(IllegalAccessException e){
			throw new JSONException("Unable to access one of the attributes of "+aSerializableObject);
		}
		//The Object class has no attributes.  Null has no attributes.
		if(aClass.getSuperclass() != null && aClass.getSuperclass() != Object.class ){
			//since all parent attributes come after the last child attribute add a comma
			theWriter.write(",");
			writeAllAttributesOf(aSerializableObject, aClass.getSuperclass(), levelCount);
		}
	}
	/**
	 * Closes the output stream and the underlying stream
	 */
	public void close() {
		theWriter.close();
	}
	
	private String escapeStringForJSON(String text) {
		text = text.replaceAll("(\\r\\n?|\\n)", "\\\\n")
			.replaceAll("([^\\\\]?)\\\"", "$1\\\\\"")
			.replaceAll("(\\/)", "\\\\/")
			.replaceAll("(\\f)", "\\\\f")
			.replaceAll("(\\t)", "\\\\t")
			.replaceAll("([^\\\\])\\\\([^\\\\ntfb\\/\\\"])", "$1\\\\\\\\$2");
		return text;
	}
}
