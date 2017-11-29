package ca.ece.ubc.cpen221.mp5;

import java.io.*;
import java.net.Socket;

/**
 * YelpDBClient is a client that sends requests to the YelpDBServer and
 * interprets its replies. A new YelpDBClient is "open" until the close() method
 * is called, at which point it is "closed" and may not be used further.
 */

public class YelpDBClient {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private BufferedReader stdin;
	// Rep invariant: socket, in, out != null

	/**
	 * Make a FibonacciClient and connect it to a server running on hostname at the
	 * specified port.
	 * 
	 * @throws IOException
	 *             if can't connect
	 */
	public YelpDBClient(String hostname, int port) throws IOException {
		socket = new Socket(hostname, port);
		stdin = new BufferedReader(new InputStreamReader(System.in));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	/**
	 * Send a request to the server. Requires this is "open".
	 * 
	 * @param x
	 *            to find Fibonacci(x)
	 * @throws IOException
	 *             if network or server failure
	 */
	public void sendRequest(String args) throws IOException {
			out.print(args + "\n");
			out.flush();
	}

	/**
	 * Get a reply from the next request that was submitted. Requires this is
	 * "open".
	 * 
	 * @return the requested Fibonacci number
	 * @throws IOException
	 *             if network or server failure
	 */
	public String getReply() throws IOException {
		String reply = in.readLine();
		if (reply == null) {
			throw new IOException("connection terminated unexpectedly");
		}
		try {
			return new String(reply);
		} catch (NullPointerException e) {
			throw new IOException("null reply: " + reply);
		}

	}

	/**
	 * Closes the client's connection to the server. This client is now "closed".
	 * Requires this is "open".
	 * 
	 * @throws IOException
	 *             if close fails
	 */
	public void close() throws IOException {
		in.close();
		out.close();
		socket.close();
	}

	/**
	 * Use a FibonacciServer to find the first N Fibonacci numbers.
	 */
	public static void main(String[] args) {
		try {
			String fromClient = "";
			YelpDBClient client;
			if (args.length == 0){client = new YelpDBClient("localhost",4949);}
			else{client = new YelpDBClient("localhost", Integer.parseInt(args[0]));}
			do {
				System.out.println("Enter command: ");
				fromClient = client.stdin.readLine();
				client.sendRequest(fromClient);

				String y = client.getReply();
				System.out.println("Reply: " + y);	
			} while (!fromClient.equals("end"));
			
			client.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
