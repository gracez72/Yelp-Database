package ca.ece.ubc.cpen221.mp5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * DBServer is a server that handles request from a client using the YelpDB. It
 * accepts requests of the form: Request ::= Number "\n" Number ::= [0-9]+ and
 * for each request, returns a reply of the form: Reply ::= (Number | "err")
 * "\n" where a Number is the request Fibonacci number, or "err" is used to
 * indicate a misformatted request. DBServer can handle multiple concurrent
 * clients.
 * 
 * FORMAT FROM: FIBONACCISERVER EXAMPLE
 */
public class YelpDBServer {
	/** Default port number where the server listens for connections. */

	private ServerSocket serverSocket;

	// Rep invariant: serverSocket != null

	/**
	 * Make a DBServer that listens for connections on port.
	 * 
	 * @param port
	 *            port number, requires 0 <= port <= 65535
	 */
	public YelpDBServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.err.println("Started server on port " + port);
	}

	/**
	 * Run the server, listening for connections and handling them.
	 * 
	 * @throws IOException
	 *             if the main server socket is broken
	 */
	public void serve() throws IOException {
		while (true) {
			// block until a client connects
			final Socket socket = serverSocket.accept();
			// create a new thread to handle that client
			Thread handler = new Thread(new Runnable() {
				public void run() {
					try {
						try {
							handle(socket);
						} finally {
							socket.close();
						}
					} catch (IOException ioe) {
						// this exception wouldn't terminate serve(),
						// since we're now on a different thread, but
						// we still need to handle it
						ioe.printStackTrace();
					}
				}
			});
			// start the thread
			handler.start();
		}
	}

	/**
	 * Handle one client connection. Returns when client disconnects.
	 * 
	 * @param socket
	 *            socket where client is connected
	 * @throws IOException
	 *             if connection encounters an error
	 */
	private void handle(Socket socket) throws IOException {
		System.err.println("client connected");

		// get the socket's input stream, and wrap converters around it
		// that convert it from a byte stream to a character stream,
		// and that buffer it so that we can read a line at a time
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// similarly, wrap character=>bytestream converter around the
		// socket output stream, and wrap a PrintWriter around that so
		// that we have more convenient ways to write Java primitive
		// types to it.
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

		
		try {

			YelpDB<Restaurant> db = new YelpDB<Restaurant>(
					"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/restaurants.json?token=Ad5rmn2sjveuyRPCkEY2WXTzAtGjTlLYks5aJhUMwA%3D%3D",
					"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/users.json?token=Ad5rmtqKeZTfXUn11R35DZcTczpgqLc4ks5aIm_WwA%3D%3D",
					"https://raw.githubusercontent.com/CPEN-221/f17-mp51-gracez72_andradazoltan/master/data/reviews.json?token=Ad5rmsox3KwRPuEEBRwJq6p-rHsUg5mmks5aIm_DwA%3D%3D");
			
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				System.err.println("request: " + line.toString());
				try {
					String reply = "ERR: ILLEGAL_REQUEST";
					String[] split = line.split("\\s+");

					if (split.length > 1) {
						StringBuilder builder = new StringBuilder();
						for (int i = 1; i < split.length; i++) {
							builder.append(split[i] + " ");
						}
						split[1] = builder.toString().trim();
						if (split[0].equals("GETRESTAURANT"))
							reply = db.getRestaurant(split[1]);
						else if (split[0].equals("ADDUSER"))
							reply = db.addUser(split[1]);
						else if (split[0].equals("ADDRESTAURANT"))
							reply = db.addRestaurant(split[1]);
						else if (split[0].equals("ADDREVIEW"))
							reply = db.addReview(split[1]);
					} else if (split[0].equals("end"))
						reply = "Closing client...";
					out.println(reply);
					System.err.println("reply: " + reply);

				} catch (NullPointerException e) {
					// complain about ill-formatted request
					System.err.println("reply: err");
					out.print("err\n");
				}
				// important! our PrintWriter is auto-flushing, but if it were
				// not:
				// out.flush();
			}
		} finally {
			out.close();
			in.close();
		}
	}

	/**
	 * Start a DBServer running on the default port.
	 */
	public static void main(String[] args) {
		try {
			YelpDBServer server = new YelpDBServer(Integer.parseInt(args[0]));
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}