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

import java.util.concurrent.Semaphore;
/**
 * The JSONStreamProtector class is used to make any read or write object calls to a JSONOutputStream or a JSONInputStream 
 * thread safe.  If you are working in a multi-threaded environment you must use this class to ensure that your 
 * reads and writes to the underlying resources are not interleaved.  If you are working in a single threaded 
 * environment you can ignore this class.
 * <br/>
 * <br/>
 * To make your subsequent read or write object calls safe, instantiate a JSONStreamProtector and pass the 
 * JSONStreams to be protected to the <b><i>protectJSONStream</i></b> method.  
 * <br/>
 * <br/>
 * You can use one protector for multiple write streams, multiple read streams, read/write pairs that belong to 
 * the same underlying resource( a file, socket, etc.) or any other combination that is appropriate for your application.
 * 
 * @author Lee S. Barney
 *
 */
public class JSONStreamProtector{
	private Semaphore semaphore = new Semaphore(1, true);
	/**
	 * This method protects JSONStreams from having concurrent write or read errors 
	 * due to multiple threads doing concurrent access.
	 * @param aJsonStream - The JSONOutputStream or JSONInputStream to be protected
	 */
	public void protectJSONStream(JSONStream aJsonStream){
		aJsonStream.setProtector(this);
	}
	protected void claim() throws InterruptedException{
		semaphore.acquire(1);
	}
	protected void free(){
		semaphore.release(1);
	}
}