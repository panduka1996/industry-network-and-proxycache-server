/**
 * ProxyCache.java - Simple caching proxy
 *
 * $Id: ProxyCache.java,v 1.3 2004/02/16 15:22:00 kangasha Exp $
 *
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class ProxyCache {
    /** Port for the proxy */
    private static int port;
    /** Socket for client connections */
    private static ServerSocket socket;
    /** Create the ProxyCache object and the socket */
    private static Map<String, String> cache = new Hashtable<String, String>();

   
	
	

    public  static void caching(HttpRequest req, HttpResponse res) throws IOException{
    	File cachedSites;
    	DataOutputStream output;


    	cachedSites = new File("cachedSites.txt");
    	FileOutputStream fileOutputStream = new FileOutputStream(cachedSites);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            
			objectOutputStream.writeObject(cache);
			objectOutputStream.close();
			fileOutputStream.close();
    	cache.put(req.URI, cachedSites.getAbsolutePath());
    	System.out.println("Caching from: "+req.URI+" for "+cachedSites.getAbsolutePath());

    }


  	public  static byte[] uncaching(String name) throws IOException{
  		File file1cached;
  		FileInputStream fileInput;
  		String hashfile;
  		byte[] bytescached;

  		if((hashfile = cache.get(name))!=null){
  			file1cached = new File(hashfile);
  			fileInput = new FileInputStream(file1cached);
  			bytescached = new byte[(int)file1cached.length()];
  			fileInput.read(bytescached);
  			System.out.println("Caching: Hit on "+name+" returning cache to user");
  			return bytescached;
  		}
  		else {
  			System.out.println("Caching: No hit on "+name);
  			return bytescached = new byte[0];
			
		
	
			
  		}

  	}


  	public static void init(int p) {
	port = p;
		try {
	    	socket = new ServerSocket(port);
		} catch (IOException e) {
	    		System.out.println("Error creating socket: " + e);
	    		System.exit(-1);
			}
  	}



    /** Read command line arguments and start proxy */
    public static void main(String args[]) {
	int myPort = 0;
	
    
	try {
	    myPort = Integer.parseInt(args[0]);
	} catch (ArrayIndexOutOfBoundsException e) {
	    System.out.println("Need port number as argument");
	    System.exit(-1);
	} catch (NumberFormatException e) {
	    System.out.println("Please give port number as integer.");
	    System.exit(-1);
	}
	
	init(myPort);

	/** Main loop. Listen for incoming connections and spawn a new
	 * thread for handling them */
	Socket client = null;
	
	while (true) {
	    try {
		client = socket.accept(); /* Accepts new customers */
		(new Thread(new Threads(client))).start(); /* Create threads for each new client */
	    } catch (IOException e) {
		System.out.println("Error reading request from client: " + e);
		/* Definitely cannot continue processing this request,
		 * so skip to next iteration of while loop. */
		continue;
	    }
	}

    }
}
