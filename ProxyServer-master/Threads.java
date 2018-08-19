import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

public class Threads implements Runnable{
	
	private final Socket client;

    public Threads(Socket client) {
        this.client = client;
    }	


	public void run() {
	Socket server = null;
	HttpRequest request = null;
	HttpResponse response = null;

	/* Process request. If there are any exceptions, then simply
	 * return and end this request. This unfortunately means the
	 * client will hang for a while, until it timeouts. */

	/* Read request */
	try {
	    BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    request = new HttpRequest(fromClient);
	} catch (IOException e) {
	    System.out.println("Error reading request from client: " + e);
	    return;
	}
	/* Send request to server */
	try {
	    /* Open socket and write request to socket */
	    server = new Socket(request.getHost(), request.getPort()); /* Create socket */
	    DataOutputStream toServer = new DataOutputStream(server.getOutputStream()); /* Create outputstream for server on socket */
        toServer.writeBytes(request.toString()); /* Create outputstream for the serviceScribe request to the outputstreamidor on the socket */
	} catch (UnknownHostException e) {
	    System.out.println("Unknown host: " + request.getHost());
	    System.out.println(e);
	    return;
	} catch (IOException e) {
	    return;
	}
	/* Read response and forward it to client */
	try {
		byte[] cache = ProxyCache.uncaching(request.URI);
		if(cache.length==0) {
		    DataInputStream fromServer = new DataInputStream(server.getInputStream()); /* Create server inputstream */
		    response = new HttpResponse(fromServer); /* Create object with server response */
		    DataOutputStream toClient = new DataOutputStream(client.getOutputStream());

		    

		    toClient.writeBytes(response.toString()); /* Write headers */
		    toClient.write(response.body); /* Write body */
		    /* Write response to client. First headers, then body */

		    ProxyCache.caching(request, response); /* Save to cache */

		    client.close();
		    server.close();
		    /* Insert object into the cache */
		    /* Fill in (optional exercise only) */
		}
		else{
			DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
			toClient.write(cache);
			client.close();
			server.close();
		}

	} catch (IOException e) {
	    System.out.println("Error writing response to client: " + e);
	}
    }
}
