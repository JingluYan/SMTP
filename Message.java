import java.util.*;
import java.text.*;

/* $Id: Message.java,v 1.5 1999/07/22 12:10:57 kangasha Exp $ */

/**
 * Filename:  Message.java
 * Mail message.
 * input: from
 *        to 
 *        subject
 *        text
 *        Cc
 * output: header
 *         body
 *         exceptions
 * @author: Jinglu Yan
 * @ID: 200839014
 * @time: 20/10/2011
 */
public class Message {
    /* The headers and the body of the message. */
    public String Headers;
    public String Body;

    /* Sender, recipient and Cc. With these, we don't need to extract them
    from the headers. */
    private String From;
    private String To;
    private String Cc;

    /* To make it look nicer */
    private static final String CRLF = "\r\n";

    /* Create the message object by inserting the required headers from
    RFC 822 (From, To, Date). */
    public Message(String from, String to, String subject, String text, String cc) {
        /* Remove whitespace */
        From = from.trim();
        To = to.trim();
        Cc = cc.trim();
        Headers = "From: " + From + CRLF;
        Headers += "To: " + To + CRLF;
        Headers +="Cc: " +cc + CRLF;//add Cc into headers 
        Headers += "Subject: " + subject.trim() + CRLF;

        /* A close approximation of the required format. Unfortunately
        only GMT. */
        SimpleDateFormat format = 
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        Headers += "Date: " + dateString + CRLF;
        Body = text;
    }

    /* Three functions to access the sender, recipient and Cc. */
    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }
    
    public String getCc() {//function to access Cc
    	return Cc;
    }

    /* Check whether the message is valid. In other words, check that
    both sender and recipient contain only one @-sign. */
    public boolean isValid() {
        int fromat = From.indexOf('@');
        
        if(fromat < 1 || (From.length() - fromat) <= 1) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(fromat != From.lastIndexOf('@')) {
            System.out.println("Sender address is invalid");
            return false;
        }
        
        /*
         * these if else sentence is used to find out the multiple recptions
         */
        if(To.contains(";")){//if there is ";" in to field 
        	String[] res = To.split("; ");//divide to field into res array 
        	for(int i = 0; i<res.length; i++){//check each recipient has only one "@"
        		int toat = res[i].indexOf('@');
        		if(toat < 1 || (res[i].length() - toat) <= 1) {
                    System.out.println("Recipient address is invalid");
                    return false;
                }
                if(toat != res[i].lastIndexOf('@')) {
                    System.out.println("Recipient address is invalid");
                    return false;
                }
        	}
        }
        else{
        int toat = To.indexOf('@');
        if(toat < 1 || (To.length() - toat) <= 1) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        if(toat != To.lastIndexOf('@')) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        }
        
        if(!Cc.equals("")){//allow Cc field empty
        	if(Cc.contains(";")){//if there is ";" in to field 
            	String[] res = Cc.split("; ");//divide Cc field into res array 
            	for(int i = 0; i<res.length; i++){//check each Cc has only one "@"
            		int Ccat = res[i].indexOf('@');
            		if(Ccat < 1 || (res[i].length() - Ccat) <= 1) {
                        System.out.println("Cc address is invalid");
                        return false;
                    }
                    if(Ccat != res[i].lastIndexOf('@')) {
                        System.out.println("Cc address is invalid");
                        return false;
                    }
            	}
            }
            else{
        	int Ccat = Cc.indexOf('@');//check Cc has only one "@"
            if(Ccat < 1 || (Cc.length() - Ccat) <= 1) {
               System.out.println("Multiple Recipient address is invalid");
               return false;
            }
            if(Ccat != Cc.lastIndexOf('@')) {
               System.out.println("Multiple Recipient address is invalid");
               return false;
            }
            }
        
        }
        return true;
    }

    /* For printing the message. */
    public String toString() {
        String res;

        res = Headers + CRLF;
        res += Body;
        return res;
    }
}

