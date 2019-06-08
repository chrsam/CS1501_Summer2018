/* CS 1501
   Primitive chat client. 
   This client connects to a server so that messages can be typed and forwarded
   to all other clients.  Try it out in conjunction with ImprovedChatServer.java.
   You will need to modify / update this program to incorporate the secure elements
   as specified in the Assignment sheet.  Note that the PORT used below is not the
   one required in the assignment -- for your SecureChatClient be sure to 
   change the port that so that it matches the port specified for the secure
   server.

   Modifications done by Christian Mananghaya 
   CS 1501: Algorithm Implementation
   Project 4
   cam314@pitt.edu 
*/
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.BigInteger; 
import java.sql.Timestamp;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    // BufferedReader myReader;
    // PrintWriter myWriter;
    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String userName, serverName;
    Socket connection;
    
    BigInteger E;   // server's public key 
    BigInteger N;   // server's public mod value 
    ObjectOutputStream writer;
    ObjectInputStream reader; 
    SymCipher cipher; 
    String cipherType;  
    private byte[] encryptionName; 
    BigInteger encryptionKey;  
    BigInteger key; 

    public SecureChatClient ()
    {
        try {
        
        // "Handshaking" begins 
        userName = JOptionPane.showInputDialog(this, "Enter your user name: "); // stores user name input 
        serverName = JOptionPane.showInputDialog(this, "Enter the server name: "); // stores server name input 
        InetAddress addr =
                InetAddress.getByName(serverName);
        connection = new Socket(addr, PORT);   // Connect to server with new Socket

        writer = new ObjectOutputStream(connection.getOutputStream());  // creates an ObjectOutputStream on the socket (for writing)
        writer.flush(); // prevents deadlock

        reader = new ObjectInputStream(connection.getInputStream()); // creates an ObjectInputStream on the socket 

        E = (BigInteger) reader.readObject();   // receives server's public key 
        N = (BigInteger) reader.readObject();   // receives server's public mod value 
        System.out.println("Key E: " + E + "\nKey N: " + N);

        cipherType = (String) reader.readObject();  // receives server's preferred symmetric cipher as a String 
        System.out.println("Encryption type: " + cipherType);

        // based on the value of the cipher preference, it creates either a Substitute or an Add128 object, 
        // storing the resulting object in a SymCipher variable 
        if(cipherType.equalsIgnoreCase("sub")) {
            cipher = new Substitute(); 
        } else if(cipherType.equalsIgnoreCase("add")) {
            cipher = new Add128(); 
        }

        // gets the key from its cipher object using getKey(), and then converts the result into a BigInteger object 
        key = new BigInteger(1, cipher.getKey()); 
        System.out.println("Symmetric Key: " + key);
        encryptionKey = key.modPow(E, N); // RSA-encrypts the BigInteger version of the key using E and N 

        writer.writeObject(encryptionKey); // sends resulting BigInteger to server 
        writer.flush(); // flush to avoid deadlocking 

        encryptionName = cipher.encode(userName); // encrypts username

        writer.writeObject(encryptionName); // send encrypted username to server 
        writer.flush(); // flush to avoid deadlocking 

        // "Handshaking" complete 
        this.setTitle(userName);      // Set title to identify chatter

        Box b = Box.createHorizontalBox();  // Set up graphical environment for user 
        outputArea = new JTextArea(8, 30);  
        outputArea.setEditable(false);
        outputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        b.add(new JScrollPane(outputArea));

        outputArea.append("Welcome to the Chat Group, " + userName + "\n");

        inputField = new JTextField("");  // This is where user will type input
        inputField.addActionListener(this);

        prompt = new JLabel("Type your messages below:");
        Container c = getContentPane();

        c.add(b, BorderLayout.NORTH);
        c.add(prompt, BorderLayout.CENTER);
        c.add(inputField, BorderLayout.SOUTH);

        Thread outputThread = new Thread(this);  // Thread is to receive strings
        outputThread.start();                    // from Server

		addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        try {
                            writer.writeObject(cipher.encode("CLIENT CLOSING"));
                            writer.flush(); 
                        } catch(IOException io) {
                            System.out.println("Problem closing client!");
                        }
                        System.exit(0);
                    }
                }
            
        );

        setSize(500, 200);
        setVisible(true);

        } catch (Exception e) {
            System.out.println("Problem starting client!");
        }
    }

    public void run() {
        while (true) {
             try {
                byte[] cryptoMsg = (byte[]) reader.readObject();
                String currentMsg = cipher.decode(cryptoMsg); 
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                outputArea.append(" " + currentMsg + " " + "(" + timestamp + ")" + "\n");
                
                byte[] bytes = currentMsg.getBytes(); 
                System.out.println("Byte array received: " + Arrays.toString(cryptoMsg)); 
                System.out.println("Byte array decrypted: " + Arrays.toString(bytes));
                System.out.println("Corresponding string: " + currentMsg);
             }
             catch (Exception e)
             {
                System.out.println(e +  ", closing client!");
                break;
             }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e)
    {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");

        try {
            currMsg = userName + ":" + currMsg; // build string w/ userName for chat window output 
            byte[] byteMsg = cipher.encode(currMsg); // encode new message and send SymCipher obj for encryption
            writer.writeObject(byteMsg); // send encrypted message to server 
            writer.flush(); // flush to avoid deadlock 

            byte[] bytes = currMsg.getBytes(); 
            System.out.println("Original String Message: " + currMsg);
            System.out.println("Array of bytes: " + Arrays.toString(bytes)); 
            System.out.println("Encrypted array of bytes: " + Arrays.toString(byteMsg)); 
        } catch (IOException io ) {
            System.err.println("Error: sending message to server failed! ");
        }
    }                                             

    public static void main(String [] args)
    {
         SecureChatClient JR = new SecureChatClient();
         JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}