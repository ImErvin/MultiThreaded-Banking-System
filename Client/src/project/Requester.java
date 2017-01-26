package project;

//Imports
import java.io.*;
import java.net.*;
import java.util.Scanner;

//Class Requester provides socket information and client thread.

public class Requester {
	Socket requestSocket; // Allows dataflow
	ObjectOutputStream out; // An output stream to send
	ObjectInputStream in; // An input stream to receive
	String message = ""; // A blank message string
	String ipaddress; // IP Address String
	Scanner stdin; // Scanner named "stdin"

	// Default Constructor
	Requester() {
	}

	// Thread run method
	void run() {
		stdin = new Scanner(System.in); // Create a new instance of Scanner that
										// allows console writing
		int control = 0; // A control variable for While loops
		boolean passedLogin = true; // A boolean control to ensure the user has
									// logged in

		try {
			// 1. creating a socket to connect to the server
			// While loop will keep running to you successfully connect to the
			// server.
			do {
				try {
					System.out.println("Please Enter your IP Address");
					ipaddress = stdin.next();
					// Connects to socket at ip address... on port 2004.
					requestSocket = new Socket(ipaddress, 2005);
					control = 1;
				} catch (Exception e) {
					System.out.println("Wrong IP address, Try Again!");
				}
			} while (control == 0);
			System.out.println("Connected to " + ipaddress + " in port 2004");
			// 2. get Input and Output streams
			// Create new instances of input and output streams using the socket
			// to bind the client and server
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			System.out.println("\n+Welcome to Banking System -1.0+");
			System.out.println("|  Developed by Ervin Mamutov  |");
			System.out.println("|   Third Year Software Dev.   |");
			System.out.println("+-----------G.M.I.T------------+\n");
			// 3: Communicating with the server
			// Main do while loop that will keep menus alive until user prompts
			// to exit.
			do {
				// Receive the menu from the server as a message and prompt the
				// user for an option.
				// This option will then be sent the the server.
				try {
					message = (String) in.readObject();
					System.out.println(message);
					System.out.print("\t\t       Option: ");
					message = stdin.next();
					sendMessage(message);
					System.out.println();
				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				}

				// Will check what the server replied in accordance to the users
				// option.
				try {
					message = (String) in.readObject();
					System.out.println(message);
				} catch (ClassNotFoundException classNot) {
					System.err.println("data received in unknown format");
				}

				// Now uses the server message to check what option the user is
				// trying to access.
				if (message.equals("Register")) {
					// Flush the scanner
					stdin.nextLine();
					// Register will loop 5 times to prompt information for each
					// customer detail.
					for (int i = 0; i < 5; i++) {
						try {
							// The do while loop will keep running till the user
							// has successfully registered.
							do {
								// Will read in a message such as "Enter name"
								// and allow the user to enter
								// a "name" and send it to the server to be
								// stored.
								message = (String) in.readObject();
								System.out.println(message);
								message = stdin.nextLine();
								sendMessage(message);
								message = (String) in.readObject();
								// If the username already exists, the user will
								// be told.
								if (message.equals("error")) {
									System.out.println("Username already exists. Try Again.");
								}
							} while (!message.equals("success"));
						} catch (ClassNotFoundException classNot) {
							System.err.println("data received in unknown format");
						}
					}
				} else if (message.equals("Login")) {
					try {
						// The login will prompt the user for a username first.
						// The user has 4 attempts to enter a valid username,
						// added this feature to exit an infinite loop.
						// **The error handling here is quite poor and will mess
						// up the Menu**
						do {
							// Will provide a server message "Enter username"
							// and allow the user to enter their username
							// and send up the details to check if it's a valid
							// username in the hashmap.
							message = (String) in.readObject();
							System.out.println(message);
							message = stdin.next();
							sendMessage(message);
							message = (String) in.readObject();
							System.out.println(message);
							// Exit the loop if incorrect details are entered
							// too many times.
							if (message.equals("Failed too many times, try again.")) {
								passedLogin = false;
								break;
							}
						} while (!message.equals("Granted"));

						// The password will only show once the username is
						// successfully entered
						// The password works the same as the username, error
						// handling here is poor too.
						if (passedLogin == true) {
							do {
								message = (String) in.readObject();
								System.out.println(message);
								message = stdin.next();
								sendMessage(message);
								message = (String) in.readObject();
								System.out.println(message);
								if (message.equals("Failed too many times, try again.")) {
									passedLogin = false;
									break;
								}
							} while (!message.equals("Granted"));
						}
					} catch (ClassNotFoundException classNot) {
						System.err.println("data received in unknown format");
					}
				}
				// Error handling for incorrect username/password
				// Will now receive a message from the server to check if the
				// user has been authenticated.
				if (passedLogin == true) {
					try {
						message = (String) in.readObject();
					} catch (ClassNotFoundException classNot) {
						System.err.println("data received in unknown format");
					}
				}
				if (message.equals("Authenticated")) {
					try {
						// Do while will run infinitely till prompted to close.
						do {
							// Prints the menu from the server and follows
							// similar menu procedure like above
							message = (String) in.readObject();
							System.out.println(message);
							// Checks if the user is closing connection, use
							// this so "Option" doesn't get printed at the end
							if (message.equals("Bye")) {
								message = "Exit";
							} else {
								System.out.print("\t\t       Option: ");
							}
							message = stdin.next();
							sendMessage(message);
							// Server sends back a condition for the if/else
							// statements
							message = (String) in.readObject();
							if (message.equals("ChangeDetails")) {
								// Prints the menu to change details
								message = (String) in.readObject();
								System.out.println(message);
								System.out.print("\t\t       Option3: ");
								message = stdin.next();
								// If they select 0, brings them back to the
								// login menu
								if (message.equals("0")) {
									sendMessage(message);
									message = (String) in.readObject();
									System.out.println(message);
								} else {
									// Simple back and forth server and client
									// messages to change the details
									sendMessage(message);
									message = (String) in.readObject();
									System.out.println(message);
									stdin.nextLine();
									message = stdin.nextLine();
									sendMessage(message);
									message = (String) in.readObject();
									System.out.println(message);
								}
							} else if (message.equals("MakeLodgement")) {
								// Allows the user to add to the balance, simple
								// back and forth
								// server/client communication that allows the
								// user to enter how much
								// they want to deposit.
								message = (String) in.readObject();
								System.out.println(message);
								message = stdin.next();
								sendMessage(message);
								message = (String) in.readObject();
								System.out.println(message);
							} else if (message.equals("MakeWithdrawal")) {
								// Allows the user to remove from the balance,
								// simple back and forth
								// server/client communication that allows the
								// user to enter how much
								// they want to withdraw.
								message = (String) in.readObject();
								System.out.println(message);
								message = stdin.next();
								sendMessage(message);
								message = (String) in.readObject();
								System.out.println(message);
							} else if (message.equals("Log")) {
								// Simply requests strings from the server and
								// prints them to the screen.
								message = (String) in.readObject();
								System.out.println(message);
								message = (String) in.readObject();
								System.out.println(message);
							}

						} while (!message.equals("Exit"));
					} catch (ClassNotFoundException classNot) {
						System.err.println("data received in unknown format");
					}
				}

			} while (!message.equals("Exit"));
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// 4: Closing connection
			try {
				// Closes the streams and socket and exits the console.
				in.close();
				out.close();
				requestSocket.close();
				System.exit(0);
				;
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	// Method to send an object using the outputstream
	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Requester client = new Requester(); // New instance of requester named
											// client.
		client.run(); // calls the run method to start the thread.
	}
}