/**
 * Filename:  MailClient.java
 * This class is used to create interface with layout and add the action listeners. This class is used to create objects to connect other classes
 * input: from field
 *        to field
 *        Cc field
 *        url
 *        local mail server
 *        suject 
 *        data of mail 
 * output: exceptions
 *         button functions
 *         new location
 *         status code
 * 
 * @author: Jinglu Yan
 * @ID: 200839014
 * @time: 20/10/2011
 */
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

public class MailClient extends Frame {
    /* The stuff for the GUI. */
    private Button btSend = new Button("Send");
    private Button btClear = new Button("Clear");
    private Button btQuit = new Button("Quit");
    private Label serverLabel = new Label("Local mailserver:");
    private TextField serverField = new TextField("mail.csc.liv.ac.uk", 40);
    private Label fromLabel = new Label("From:");
    private TextField fromField = new TextField("", 40);
    private Label toLabel = new Label("To:"); 
    private TextField toField = new TextField("", 40);//create Cc field
    private Label CcLabel = new Label("Cc: ");//create Cc label
    private TextField CcField = new TextField("", 40);
    private Label subjectLabel = new Label("Subject:");
    private TextField subjectField = new TextField("", 40);
    private Label messageLabel = new Label("Message:");
    private TextArea messageText = new TextArea(30, 80);
    private Label urlLabel = new Label("HTTP://");
    private TextField urlField = new TextField("www.csc.liv.ac.uk/~gairing/test.txt", 40);
    private Button btGet = new Button("Get");

    /**
     * Create a new MailClient window with fields for entering all
     * the relevant information (From, To, Subject, and message).
     */
    public MailClient() {
        super("Java Mailclient");

        /* Create panels for holding the fields. To make it look nice,
        create an extra panel for holding all the child panels. */
        Panel serverPanel = new Panel(new BorderLayout());
        Panel fromPanel = new Panel(new BorderLayout());
        Panel toPanel = new Panel(new BorderLayout());
        Panel CcPanel = new Panel(new BorderLayout());//create Cc panel to contain Cc
        Panel subjectPanel = new Panel(new BorderLayout());
        Panel messagePanel = new Panel(new BorderLayout());
        serverPanel.add(serverLabel, BorderLayout.WEST);
        serverPanel.add(serverField, BorderLayout.CENTER);
        fromPanel.add(fromLabel, BorderLayout.WEST);
        fromPanel.add(fromField, BorderLayout.CENTER);
        toPanel.add(toLabel, BorderLayout.WEST);
        toPanel.add(toField, BorderLayout.CENTER);
        CcPanel.add(CcLabel, BorderLayout.WEST);//add Cc label to west position
        CcPanel.add(CcField, BorderLayout.CENTER);//add Cc field to center position
        subjectPanel.add(subjectLabel, BorderLayout.WEST);
        subjectPanel.add(subjectField, BorderLayout.CENTER);
        messagePanel.add(messageLabel, BorderLayout.NORTH);	
        messagePanel.add(messageText, BorderLayout.CENTER);
        Panel fieldPanel = new Panel(new GridLayout(0, 1));
        fieldPanel.add(serverPanel);
        fieldPanel.add(fromPanel);
        fieldPanel.add(toPanel);
        fieldPanel.add(CcPanel);//add Cc panel to field panel
        fieldPanel.add(subjectPanel);
        

        /* Create a panel for the URL field and add listener to the GET 
        button. */
        Panel urlPanel = new Panel(new BorderLayout());
        urlPanel.add(urlLabel, BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);
        urlPanel.add(btGet, BorderLayout.EAST);
        fieldPanel.add(urlPanel);
        btGet.addActionListener(new GetListener());

        /* Create a panel for the buttons and add listeners to the
        buttons. */
        Panel buttonPanel = new Panel(new GridLayout(1, 0));
        btSend.addActionListener(new SendListener());
        btClear.addActionListener(new ClearListener());
        btQuit.addActionListener(new QuitListener());
        buttonPanel.add(btSend);
        buttonPanel.add(btClear);
        buttonPanel.add(btQuit);

        /* Add, pack, and show. */
        add(fieldPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        show();
    }

    static public void main(String argv[]) {
        new MailClient();
    }

    /* Handler for the Send-button. */
    class SendListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.out.println("Sending mail");

            /* Check that we have the local mailserver */
            if ((serverField.getText()).equals("")) {
                System.out.println("Need name of local mailserver!");
                return;
            }

            /* Check that we have the sender and recipient. */
            
            if((toField.getText()).equals("")) {
                System.out.println("Need recipient!");
                return;
            }

            /* Create the message, and check that if there is from text */
            Message mailMessage; 
            InetAddress localHost;
            if((fromField.getText()).equals("")) {//if from field is empty
            	String userName = System.getProperty("user.name");//gey user name from system
            	try{
            	localHost = InetAddress.getLocalHost();//get local host 
            	}catch(UnknownHostException e){
            		return;
            	}
            	mailMessage = new Message(userName+"@"+localHost.getHostName(), //create from field by system with username and host name
                        toField.getText(), 
                        subjectField.getText(), 
                        messageText.getText(),CcField.getText());
                }
            else{
            mailMessage = new Message(fromField.getText(), 
                    toField.getText(), 
                    subjectField.getText(), 
                    messageText.getText(),CcField.getText());

            }
            /* Check that the message is valid, i.e., sender and
            recipient addresses look ok. */
            if(!mailMessage.isValid()) {
                return;
            }
            /* Create the envelope, open the connection and try to send
            the message. */
            Envelope envelope;
            try {
                envelope = new Envelope(mailMessage, 
                    serverField.getText());
            } catch (UnknownHostException e) {
                /* If there is an error, do not go further */
                return;
            }
            try {
                SMTPConnect connection = new SMTPConnect(envelope);
                connection.send(envelope);
                connection.close();
            } catch (IOException error) {
                System.out.println("Sending failed: " + error);
                return;
            }
            System.out.println("Mail sent succesfully!");
        }
    }

    /* Get URL if specified. */
    class GetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            String receivedText;

            /* Check if URL field is empty. */
            if((urlField.getText()).equals("")) {
                System.out.println("Need URL!");
                return;
            }			
            /* Pass string from URL field to HTTPGet (trimmed);
            returned string is either requested object 
            or some error message. */
            
            HttpRequest request = new HttpRequest(urlField.getText().trim());

            // Send http request. Returned String holds object 
            try {
            receivedText=request.sendRequest();
            }
            catch (IOException error) {
            messageText.setText("Downloading File failed.\r\nIOException: " + error);
            return;
            }
            
            /* Pass the new location address from request message; 
             * returned string is either requested object 
             * or some error message. */
            
            if(request.getStatus()==301){
            	HttpRequest H = new HttpRequest(request.getLocation());
            	urlField.setText(request.getLocation());
            	 // Send http H. Returned String holds object 
            	try {
                    receivedText=H.sendRequest();
                    }
                    catch (IOException error) {
                    messageText.setText("Downloading File failed.\r\nIOException: " + error);
                    return;
                    }
            }
            
            // Change message text 
            messageText.setText(receivedText);
            
            
        }
    }

    /* Clear the fields on the GUI. */
    class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("Clearing fields");
            fromField.setText("");
            toField.setText("");
            CcField.setText("");//clear Cc field
            subjectField.setText("");
            messageText.setText("");
        }
    }

    /* Quit. */
    class QuitListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
}

