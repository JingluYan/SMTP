import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Filename:  HttpRequest.java
 * Class for downloading one object from a http server.
 * input: url 
 * output: host name
 *         path name
 *         request message
 *         statu code 
 *         body length 
 *         header 
 *         location
 *         content length 
 *         Exception     
 * @author: Jinglu Yan
 * @ID: 200839014
 * @time: 20/10/2011
 *
 */
public class HttpRequest {

    private String host;
    private String path;
    private String requestMessage;
    private int status;		// status code
    private String newLocation; 

    private static final int HTTP_PORT = 80;
    private static final String CRLF = "\r\n";
    private static final int BUF_SIZE = 8192; 
    private static final int MAX_OBJECT_SIZE = 102400;

    /* Create an HttpRequest object. */
    public HttpRequest(String url) {

        /* Split "URL" into "host name" and "path name", and
         * set host and path class variables. 
         * if URL is only a host, use "/" as path 
         */		

    	if(url.contains("/")){//if url contains "/" will divide the url into two parts. 
            String[] divide = url.split("/",2);
            host = divide[0];//the first part is host name
            path = "/" + divide[1];//the second part is path name
    	}else{//otherwise host is the whole url and the path is only "/"
    		host = url;
    		path = "/";
    	}

        /* Construct requestMessage, add a header line so that
         * server closes connection after one response. */		

        requestMessage = "GET "+path+" HTTP/1.1"+CRLF+"Host: "+host+CRLF+"User-agent: Mozilla/4.0"+CRLF+"Connection: close"+CRLF+"Accep-language: en"+CRLF+CRLF;

        return;
    }	

    /* Send Http request, parse response and return requested object 
     * as String (if no error), 
     * otherwise return meaningfull error message. 
     * Don't catch IOExceptions. Just throw them to MailClient*/		
    public String sendRequest() throws IOException {

        /* buffer to read object in 8kB chunks */
        char[] buf = new char[BUF_SIZE];

        /* Maximum size of object is 100kB, which should be enough for most objects. 
         * Change constant if you need more. */		
        char[] body = new char[MAX_OBJECT_SIZE];

        String statusLine="";	// status line
        String headers="";	// headers
        int bodyLength=-1;	// lenghth of body

        String[] tmp;

        /* The socket to the server */
        Socket connection;

        /* Streams for reading and writing the socket */
        BufferedReader fromServer;
        DataOutputStream toServer;

        System.out.println("Connecting server: " + host+CRLF);

        /* Connect to http server on port 80.
         * Assign input and output streams to connection. */		
        connection = new Socket(host,HTTP_PORT);//new socked objection with host name and port number
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));//message from server
        toServer = new DataOutputStream(connection.getOutputStream());//message to server

        System.out.println("Send request:\n" + requestMessage);

        /* Send requestMessage to http server */
        toServer.writeBytes(requestMessage);

        /* Read the status line from response message */
        statusLine= fromServer.readLine();
        System.out.println("Status Line:\n"+statusLine+CRLF);

        /* Extract status code from status line and assign it to
         * "status" variable". */
        tmp = statusLine.split(" ");
        status = Integer.parseInt(tmp[1]);

        /* Check if status code is correct, i.e. response hold 
         * the requested object. If status code is not 200 or 301,
         * close connection and return an error message. */	
        
        if(status!=200&&status!=301){
            connection.close();
            throw new IOException();
        }

        /* Read header lines from response message. Assign String 
         * of headers to "headers" variable. 
         * Recall that an empty line indicates end of headers.
         * Extract length  from "Content-Length:" (or "Content-length:") 
         * header line, if present, and assign to "bodyLength" variable. 
         */

        String data = " ";
        while(!data.equals("")){
            data = fromServer.readLine();
            headers = headers+data+CRLF;
            if(data.startsWith("Content-Length:")){
                tmp = data.split(" ",2);
                bodyLength = Integer.parseInt(tmp[1]);
            }
            if(data.startsWith("Location:")){//if the response message is 301, there will be a line start with "Location". 
            	 tmp = data.split("//",2);
            	 newLocation = tmp[1];//get the new location address
            	 
            }
        }
        // requires about 10 lines of code
        System.out.println("Headers:\n"+headers+CRLF);
        System.out.println("The length of the item is: " + bodyLength);

        /* No Content-Length header */		
        boolean noLength=false;
        if (bodyLength==-1) {
            noLength=true;
        }

        /* If object is larger than MAX_OBJECT_SIZE, close the connection and 
         * return meaningful message. */
        if (bodyLength>MAX_OBJECT_SIZE) {
            connection.close();
            return("The max object size is: "+bodyLength+". Therefore, the object is not complete.");
        }

        /* Read the body in chunks of BUF_SIZE using buf[] and copy the chunk
         * into body[]. Stop when either we have
         * read Content-Length bytes or when the connection is
         * closed (when there is no Connection-Length in the response). 
         * Use one of the read() methods of BufferedReader here, NOT readLine().
         * Also make sure not to read more than MAX_OBJECT_SIZE characters.
         */				
        int bytesRead = 0;
                                                               // Requires 10-20 lines of code
        while(bytesRead<bodyLength&&bytesRead<MAX_OBJECT_SIZE){//make sure that the message is not more than body langhth and the maximum size
           for(int i = 0; i<BUF_SIZE; i++){    //read the message not more than buffer size
               if(bytesRead<bodyLength){ 	      
        	     buf[i] = (char)(fromServer.read());//store the message into buf array
               }else break;//if read more than the buffer size, break this loop
                }
           for(int j=0; j<BUF_SIZE; j++){//read the message not more than buffer size
        	   if(bytesRead<bodyLength){
        		   bytesRead++;//increace the read bytes
                   body[bytesRead-1] = buf[j];//copy the message to body array
        	   }else break;//if read more than body length, then break this loop
             
             if(bytesRead<1)//make sure that there are connection length in the response
                break;
           }
        }

        /* At this points body[] should hold to body of the downloaded object and 
         * bytesRead should hold the number of bytes read from the BufferedReader
         */

        /* Close connection and return object as String. */
        System.out.println("Done reading file. Closing connection.");
        connection.close();
        return(new String(body, 0, bytesRead));
    }
    
    public int getStatus(){
    	return status;
    }
    
    public String getLocation(){
    	return newLocation;
    }
}

