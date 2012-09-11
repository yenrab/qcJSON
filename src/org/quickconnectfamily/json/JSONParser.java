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
 
  @author FangYidong<fangyidong@yahoo.com.cn>. Greatly modified by Lee S. Barney
 */
package org.quickconnectfamily.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



/**
 * Parser for JSON text. Please note that JSONParser used independently is NOT thread-safe.  Use the JSONInputStream with the JSONStreamProtector and is is thread safe.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>. Greatly modified by Lee S. Barney
 */
public class JSONParser {
        public static final int S_INIT=0;
        public static final int S_IN_FINISHED_VALUE=1;//string,number,boolean,null,object,array
        public static final int S_IN_OBJECT=2;
        public static final int S_IN_ARRAY=3;
        public static final int S_PASSED_PAIR_KEY=4;
        public static final int S_IN_PAIR_VALUE=5;
        public static final int S_END=6;
        public static final int S_IN_ERROR=-1;
        
        public static final int FIRST_JSON_CHAR_TYPE_UNSET=-515;
        
        private Yylex lexer = new Yylex((Reader)null);
        private Yytoken token = null;
        private int status = S_INIT;
        
        private int firstCharType = FIRST_JSON_CHAR_TYPE_UNSET;//an invalid type for checking

		private int numUnmatchedCharTypeCount = 0;
		private Reader in;
       

		@SuppressWarnings("rawtypes")
		private int peekStatus(LinkedList statusStack){
                if(statusStack.size()==0)
                        return -1;
                Integer status=(Integer)statusStack.getFirst();
                return status.intValue();
        }
		
		public JSONParser(Reader in){
			this.in = in;
			reset(this.in);
		}
        
    /**
     *  Reset the parser to the initial state without resetting the underlying reader.
     *
     */
    public void setFirstCharType(int firstCharType) {
		this.firstCharType = firstCharType;
	}
    
    public void closeReader() throws IOException{
    	in.close();
    }
    
    public void setNumUnmatchedCharTypeCount(int numUnmatchedCharTypeCount) {
		this.numUnmatchedCharTypeCount = numUnmatchedCharTypeCount;
	}
    
    public void reset(){
    	firstCharType = FIRST_JSON_CHAR_TYPE_UNSET;
    	numUnmatchedCharTypeCount = 0;
        token = null;
        status = S_INIT;
    }
    
    /**
     * Reset the parser to the initial state with a new character reader.
     * 
     * @param in - The new character reader.
     * @throws IOException
     * @throws ParseException
     */
        public void reset(Reader in){
        		firstCharType = FIRST_JSON_CHAR_TYPE_UNSET;
        		numUnmatchedCharTypeCount = 0;
                lexer.yyreset(in);
                reset();
        }
        
        /**
         * @return The position of the beginning of the current token.
         */
        public int getPosition(){
                return lexer.getPosition();
        }
        
        public Object parse() throws IOException, ParseException{
                return parse((ContainerFactory)null);
        }
        
        /**
         * Parse JSON text into java object from the input source.
         *      
         * @param in
     * @param containerFactory - Use this factory to create your own JSON object and JSON array containers.
         * @return Instance of the following:
         *  org.json.simple.JSONObject,
         *      org.json.simple.JSONArray,
         *      java.lang.String,
         *      java.lang.Number,
         *      java.lang.Boolean,
         *      null
         * 
         * @throws IOException
         * @throws ParseException
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Object parse(ContainerFactory containerFactory) throws IOException, ParseException{
                LinkedList statusStack = new LinkedList();
                LinkedList valueStack = new LinkedList();
                
                try{
                        do{
                        		if(status!=S_IN_FINISHED_VALUE){
                        			nextToken();
                        		}
                                switch(status){
                                case S_INIT:
                                        switch(token.type){
                                        case Yytoken.TYPE_VALUE:
                                                status=S_IN_FINISHED_VALUE;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(token.value);
                                                break;
                                        case Yytoken.TYPE_LEFT_BRACE:
	                                        	if(firstCharType == FIRST_JSON_CHAR_TYPE_UNSET){
	                                        		firstCharType = Yytoken.TYPE_LEFT_BRACE;
	                                        	}
	                                        	if(firstCharType == Yytoken.TYPE_LEFT_BRACE){
	                                        		numUnmatchedCharTypeCount++;
	                                        	}
                                                status=S_IN_OBJECT;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(createObjectContainer(containerFactory));
                                                break;
                                        case Yytoken.TYPE_LEFT_SQUARE:
	                                        	if(firstCharType == FIRST_JSON_CHAR_TYPE_UNSET){
	                                        		firstCharType = Yytoken.TYPE_LEFT_SQUARE;
	                                        	}
	                                        	if(firstCharType == Yytoken.TYPE_LEFT_SQUARE){
	                                        		numUnmatchedCharTypeCount++;
	                                        	}
                                                status=S_IN_ARRAY;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(createArrayContainer(containerFactory));
                                                break;
                                        default:
                                                status=S_IN_ERROR;
                                        }//inner switch
                                        break;
                                        
                                case S_IN_FINISHED_VALUE:
                                        if(token.type==Yytoken.TYPE_EOF || numUnmatchedCharTypeCount == 0){
	                                        	firstCharType = FIRST_JSON_CHAR_TYPE_UNSET;  
	                                        	status = S_INIT;
	                                        	return valueStack.removeFirst();
                                        }
                                        else
                                                throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                                        
                                case S_IN_OBJECT:
                                        switch(token.type){
                                        case Yytoken.TYPE_COMMA:
                                                break;
                                        case Yytoken.TYPE_VALUE:
                                                if(token.value instanceof String){
                                                        String key=(String)token.value;
                                                        valueStack.addFirst(key);
                                                        status=S_PASSED_PAIR_KEY;
                                                        statusStack.addFirst(new Integer(status));
                                                }
                                                else{
                                                        status=S_IN_ERROR;
                                                }
                                                break;
                                        case Yytoken.TYPE_RIGHT_BRACE:

                                        	if(firstCharType == Yytoken.TYPE_LEFT_BRACE){
                                        		numUnmatchedCharTypeCount--;
                                        	}
                                                if(valueStack.size()>1){
                                                        statusStack.removeFirst();
                                                        valueStack.removeFirst();
                                                        status=peekStatus(statusStack);
                                                }
                                                else{
                                                        status=S_IN_FINISHED_VALUE;
                                                }
                                                break;
                                        default:
                                                status=S_IN_ERROR;
                                                break;
                                        }//inner switch
                                        break;
                                        
                                case S_PASSED_PAIR_KEY:
                                        switch(token.type){
                                        case Yytoken.TYPE_COLON:
                                                break;
                                        case Yytoken.TYPE_VALUE:
                                                statusStack.removeFirst();
                                                String key=(String)valueStack.removeFirst();
                                                Map parent=(Map)valueStack.getFirst();
                                                parent.put(key,token.value);
                                                status=peekStatus(statusStack);
                                                break;
                                        case Yytoken.TYPE_LEFT_SQUARE:

	                                        	if(firstCharType == Yytoken.TYPE_LEFT_SQUARE){
	                                        		numUnmatchedCharTypeCount++;
	                                        	}
                                                statusStack.removeFirst();
                                                key=(String)valueStack.removeFirst();
                                                parent=(Map)valueStack.getFirst();
                                                List newArray=createArrayContainer(containerFactory);
                                                parent.put(key,newArray);
                                                status=S_IN_ARRAY;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(newArray);
                                                break;
                                        case Yytoken.TYPE_LEFT_BRACE:

	                                        	if(firstCharType == Yytoken.TYPE_LEFT_BRACE){
	                                        		numUnmatchedCharTypeCount++;
	                                        	}
                                                statusStack.removeFirst();
                                                key=(String)valueStack.removeFirst();
                                                parent=(Map)valueStack.getFirst();
                                                Map newObject=createObjectContainer(containerFactory);
                                                parent.put(key,newObject);
                                                status=S_IN_OBJECT;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(newObject);
                                                break;
                                        default:
                                                status=S_IN_ERROR;
                                        }
                                        break;
                                        
                                case S_IN_ARRAY:
                                        switch(token.type){
                                        case Yytoken.TYPE_COMMA:
                                                break;
                                        case Yytoken.TYPE_VALUE:
                                                List val=(List)valueStack.getFirst();
                                                val.add(token.value);
                                                break;
                                        case Yytoken.TYPE_RIGHT_SQUARE:
	                                        	if(firstCharType == Yytoken.TYPE_LEFT_SQUARE){
	                                        		numUnmatchedCharTypeCount--;
	                                        	}
                                                if(valueStack.size()>1){
                                                        statusStack.removeFirst();
                                                        valueStack.removeFirst();
                                                        status=peekStatus(statusStack);
                                                }
                                                else{
                                                        status=S_IN_FINISHED_VALUE;
                                                }
                                                break;
                                        case Yytoken.TYPE_LEFT_BRACE:

	                                        	if(firstCharType == Yytoken.TYPE_LEFT_BRACE){
	                                        		numUnmatchedCharTypeCount++;
	                                        	}
                                                val=(List)valueStack.getFirst();
                                                Map newObject=createObjectContainer(containerFactory);
                                                val.add(newObject);
                                                status=S_IN_OBJECT;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(newObject);
                                                break;
                                        case Yytoken.TYPE_LEFT_SQUARE:

	                                        	if(firstCharType == Yytoken.TYPE_LEFT_SQUARE){
	                                        		numUnmatchedCharTypeCount++;
	                                        	}
                                                val=(List)valueStack.getFirst();
                                                List newArray=createArrayContainer(containerFactory);
                                                val.add(newArray);
                                                status=S_IN_ARRAY;
                                                statusStack.addFirst(new Integer(status));
                                                valueStack.addFirst(newArray);
                                                break;
                                        default:
                                                status=S_IN_ERROR;
                                        }//inner switch
                                        break;
                                case S_IN_ERROR:
                                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                                }//switch
                                if(status==S_IN_ERROR){
                                        throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
                                }
                        }while(token.type!=Yytoken.TYPE_EOF);
                }
                catch(IOException ie){
                        throw ie;
                }
                
                throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
        }
        
        private void nextToken() throws ParseException, IOException{
                token = lexer.yylex();
                if(token == null)
                        token = new Yytoken(Yytoken.TYPE_EOF, null);
        }
        
        @SuppressWarnings("rawtypes")
		private Map createObjectContainer(ContainerFactory containerFactory){
                if(containerFactory == null)
                        return new HashMap();
                Map m = containerFactory.createObjectContainer();
                
                if(m == null)
                        return new HashMap();
                return m;
        }
        
        @SuppressWarnings("rawtypes")
		private List createArrayContainer(ContainerFactory containerFactory){
                if(containerFactory == null)
                        return new ArrayList();
                List l = containerFactory.creatArrayContainer();
                
                if(l == null)
                        return new ArrayList();
                return l;
        }
}
