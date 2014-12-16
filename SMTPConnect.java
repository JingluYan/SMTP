import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

/**
 * Filename:  SMTPConnect.java
 * Open an SMTP connection to a mailserver and send one mail.
 * input: envelope
 * output: command 
 *         exceptions
 * @author: Jinglu Yan
 * @ID: 200839014
 * @time: 20/10/2011
 */
public class SMTPConnect {
    /* The socket to the server */
    private Socket connection;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnect object. Create the socket and the 
    associated streams. Initialize SMTP connection. */
    public SMTPConnect(Envelope envelope) throws IOException {
        connection = new Socket(envelope.DestHost,SMTP_PORT);

        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream()); 

        /* Read a line from server and check that the reply code is 220.
        If not, throw an IOException. */
        String response = fromServer.readLine();
        int code = parseReply(response);
        if (code!=220) {
            throw new IOException("Failed Connection");
        }

        /* SMTP handshake. We need the name of the local machine.
        Send the appropriate SMTP handshake command. */
        String localhost = envelope.DestHost;
        String sayhello = "HELO "+localhost+CRLF;
        sendCommand( sayhello, 250 );
        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
    correct order. No checking for errors, just throw them to the
    caller. */
    public void send(Envelope envelope) throws IOException {
        /* Send all the necessary commands to send a message. Call
        sendCommand() to do the dirty work. Do _not_ catch the
        exception thrown from sendCommand(). */
        String mailFrom = "MAIL FROM: " + envelope.Sender + CRLF;
        sendCommand(mailFrom, 250);
        
        String[] recipients;//string array to store multiple recipients
        if((envelope.Recipient).contains("; ")){
        	recipients = (envelope.Recipient).split("; ");
        	for(int i = 0; i<recipients.length;i++){
        		sendCommand("RCPT TO: "+recipients[i]+ CRLF, 250);//send message to recipients
        	}
        }else{
        String rcpTo = "RCPT TO: "+envelope.Recipient+ CRLF;//if there is only one recipient, send message to him
        sendCommand(rcpTo, 250); 
        }
        
        if(!envelope.Cc.equals("")){//if Cc field is not empty
        String[] Ccs;//store multiple Cc
        if((envelope.Cc).contains("; ")){
        	Ccs = (envelope.Cc).split("; ");
        	for(int i = 0; i<Ccs.length;i++){
        		sendCommand("RCPT TO: "+Ccs[i]+ CRLF, 250);//send message to Ccs
        	}
        }else{
        String ccTo = "RCPT TO: "+envelope.Cc+ CRLF;//send message to Cc
        sendCommand(ccTo, 250); 
        }
        }
        
        String dataLine = "DATA" + CRLF;
        sendCommand(dataLine, 354);//send data command
        String messageLine = "Subject: "+ envelope.Message.Headers+ CRLF + envelope.Message.Body+CRLF +"." + CRLF;
        sendCommand(messageLine, 250);//send sunject command

    }

    /* Close the connection. First, terminate on SMTP level, then
    close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT"+CRLF, 221);//send QUIT command
            connection.close();// close the connection;
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
    what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        /* Write command to server and read reply from server. */
        toServer.writeBytes(command);//send to server
        String reply = fromServer.readLine();//receive the message from server
        int code = parseReply(reply);//response message's code

        /* Check that the server's reply code is the same as the parameter
        rc. If not, throw an IOException. */
        if(code!=rc)
            throw new IOException(reply);
    }

    /* Parse the reply line from the server. Returns the reply code. */
    private int parseReply(String reply) {
        StringTokenizer st = new StringTokenizer(reply, " ");
        String response = st.nextToken();//find the code
        int code = Integer.parseInt(response);//inverse the string type to integer type
        return code;
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if(isConnected) {
            close();
        }
        super.finalize();
    }
}
