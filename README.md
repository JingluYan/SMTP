SMTP
====
***********************************************************************

Programming: Mail Client with Browser Capabilities

NAME: Jinglu Yan
***********************************************************************

IMPLEMENTED EXTRA ONE

VERIFY SENDER ADDRESS

If user forget to input the From field, this program is able to find 
out the user name and the local host name and send mail successfully

In MailClient class, 
1. Check the From field has content
    if((fromField.getText()).equals(""))
  1.1 If there is content in From field, get the text be from name
      fromField.getText()
  1.2 If there is no content in From field, find out the user name 
      by System.getProperty("user.name"); and find out the local 
      host name by (InetAddress.getLocalHost()).getHostName();
2. Create mailMessage with parameters including from, to, subject, 
   message and Cc. 


TESTING

Only leave the From field empty and send mail to others with right 
E-mail address. 
The recipient can receive the mail and the mail address is like this: 
username@localhostname

=======================================================================

IMPLEMENTED EXTRA TWO

MULTIPLE RECIPIENTS

The original program can send mail to only one recipient, but now, 
this program is able to send multiple recipients which are divided by
 ";" in To field.


In SMTPConnect class,
Check to field has more than one recipient.
  1. If there are more than one recipient:
      1.1 Split the to field into string array by ";".
           String[] recipients = (envelope.Recipient).split("; ");
      1.2 For each recipient, send "RCPR TO" command.
           sendCommand("RCPT TO: "+recipients[i]+ CRLF, 250);
  2. If there is only one recipient:
      No split and send only one "RCPR TO" command.

In Message class,
Check to field has more than one recipient.
   1 If there are more than one recipient:
       1.1 Split the to field into string array by ";".
            String[] res = To.split("; ");
       1.2 check each recipient has only one "@" sign.
            two conditions:
              res[i].indexOf('@')< 1 || (res[i].length() - 
                                 res[i].indexOf('@')) <= 1
              res[i].indexOf('@')!= res[i].lastIndexOf('@')
   2 If there is only one recipient:
       check recipient has only one "@" sign.


TESTING

Input more than one recipients divided by ";", and all the recipients
can receive the mail. 

=======================================================================

IMPLEMENTED EXTRA THREE

USING Cc FIELD ACHIEVING MULTIPLE RECIPIENTS

This program is able to send mail with another field, but not To field. 
The interface shows new field and send mail to that address in Cc
field successfully. In addition, Cc field also allows to send mail to 
more than one Cc recipients.

In MailClient class,
1. Create Cc label and Cc field.
   private Label CcLabel = new Label("Cc: ");
   private TextField CcField = new TextField("", 40);
2. Create Cc panel 
   Panel CcPanel = new Panel(new BorderLayout());
3. Add Cc label and Cc field to right positions in Cc Panel
   CcPanel.add(CcLabel, BorderLayout.WEST);
   CcPanel.add(CcField, BorderLayout.CENTER);
4. Add Cc panel to fieldPanel panel
   fieldPanel.add(CcPanel);
5. When clear the content, clear Cc field at the same time.
   CcField.setText("");

In Message class,
1. Add Cc into Headers. 
   Headers +="Cc: " +cc + CRLF;
2. Create getCc() method, in order to get Cc content.
   return Cc;
3. Check to field has more than one recipient.(similar with To filed)
   3.1 If there are more than one recipient:
       3.11 Split the to field into string array by ";".
            String[] res = To.split("; ");
       3.12 check each recipient has only one "@" sign.
            two conditions:
              res[i].indexOf('@')< 1 || (res[i].length() - 
                                  res[i].indexOf('@')) <= 1
              res[i].indexOf('@')!= res[i].lastIndexOf('@')
   3.2 If there is only one recipient:
       check recipient has only one "@"sign.

In Envelope class,
Get Cc.


TESTING
Put no address, one address or addresses divided by ";" into Cc field.
If there is no address in, there is no error and no sending.
If there is one address in, that address recieves the mail.
If there are more than one addresses in, all addresses recieve the mail.

=======================================================================

IMPLEMENTED EXTRA FOUR

301 STATUS CODE

If the server replies with a 301 status code, split new location 
from header and jump to that location. In addition, the old 
address updates to new url. 

In HttpRequest class,
1. Check the status code, if status code is neither 200 nor 301, close 
   the connection and throw exception.
    if(status!=200&&status!=301)
2. Read the header lines. If there is a line started with "Location: ",
   store the new location into variable.
    if(data.startsWith("Location:")){ 
            	 tmp = data.split("//",2);
            	 String newLocation = tmp[1];
3. In order to get status and new location, there are two new methods 
   called getStatus() and getLocation(). These two methods just return 
   the excext values. 
   public int getStatus()
   public String getLocation()
            	
In MailClient class,
Run the object request firstly, then catch the status code. 
1. If the status code is 301:
   1.1 create new HttpRequest object with new 
       location with request.getLocation() method. 
       HttpRequest H = new HttpRequest(request.getLocation());
   1.2 Set the url field with new url. 
       urlField.setText(request.getLocation());
2. If the status code is not 301:
   Send http request. Returned String holds object.


TESTING
Type www.csc.liv.ac.uk/~gairing in url field. Then click get button.
The url field will display the new address: www.csc.liv.ac.uk/~gairing/
All content show in message text.
The first status code is 301 Moved Permanently, and then status code 
update to 200. Header, length of the item and status line are displayed. 


***********************************************************************
