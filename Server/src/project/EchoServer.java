package project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class EchoServer {
	public static void main(String[] args) throws Exception {
		// 10 sockets on port 2004
		ServerSocket m_ServerSocket = new ServerSocket(2005, 10);
		// Create a hashmap with a key/value pair (username, account)
		Map<String, Account> database = new HashMap<String, Account>();
		int id = 0;
		while (true) {
			Socket clientSocket = m_ServerSocket.accept(); // Accepts
															// connections on
															// the socket
			ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++, database); // Multithreaded
																									// client
			cliThread.start(); // Calls the run method
		}
	}
}

class ClientServiceThread extends Thread {
	Socket clientSocket; // Allows dataflow
	String message; // Message string
	int clientID = -1; // Client ID
	boolean running = true; // Control variable
	ObjectOutputStream out; // Outputstream to send
	ObjectInputStream in; // Inputstream to receive
	Map<String, Account> database; // Hashmap to store all the accounts
	Account a = new Account(); // New instance of account

	// Constructor
	ClientServiceThread(Socket s, int i, Map<String, Account> database) {
		clientSocket = s;
		clientID = i;
		this.database = database;
	}

	// Send message method
	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// Thread run method
	public void run() {
		// Variables
		String username = "";
		String password = "";
		int control = 0;
		int loginAttemptCnt = 0;
		boolean authenticated = false;
		double deposit = 0;
		double withdrawal = 0;
		String usernameCheck = "";
		String log = "";
		Date date = new Date();
		String lastTransactions = "";
		double limit = -1000;

		System.out.println(
				"Accepted Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		try {
			// connect streams to socket
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Accepted Client : ID - " + clientID + " : Address - "
					+ clientSocket.getInetAddress().getHostName());

			do {
				// Send out the menu to the client and prompt them for an
				// option.
				sendMessage(firstMenu());
				try {
					message = (String) in.readObject();
					if (message.equals("1")) {
						// If they choose to register, store their messages into
						// the account object "a"
						// Sends out a control message for if/else statements on
						// the client side.
						sendMessage("Register");
						sendMessage("Enter Name: ");
						a.setName((String) in.readObject());
						sendMessage("success");
						System.out.println("name: " + a.getName());
						sendMessage("Enter Address: ");
						a.setAddress((String) in.readObject());
						sendMessage("success");
						System.out.println("Address: " + a.getAddress());
						sendMessage("Enter Bank Account Number: ");
						a.setBankAcNo((String) in.readObject());
						System.out.println("BAN: " + a.getBankAcNo());
						sendMessage("success");
						// Check if the username already exists as it needs to
						// be unique for key/value pairs on the hashmap
						// Will keep running till they choose a unique username
						do {
							sendMessage("Enter Username: ");
							usernameCheck = (String) in.readObject();
							if (database.containsKey(usernameCheck) == true) {
								sendMessage("error");
							} else {
								a.setUsername(usernameCheck);
								System.out.println("username: " + a.getUsername());
								sendMessage("success");
							}
						} while (database.containsKey(usernameCheck) == true);
						sendMessage("Enter Password: ");
						a.setPassword((String) in.readObject());
						System.out.println("password: " + a.getPassword());
						sendMessage("success");

						// Store the account into the HashMap
						database.put(a.getUsername(), a);
						System.out.println("User Registered.");
						sendMessage("Registered");
					} else if (message.equals("2")) {
						// Sends out a control message for if/else statements on
						// the client side
						sendMessage("Login");
						do {
							// Prompts the user for their username
							sendMessage("Enter Username: ");
							username = (String) in.readObject();
							// Will check if they exist or not
							// **Error handling for "failed too many times" is
							// poor and messes up the inputstreams on the client
							// side
							// and reads the messages in the wrong parts of the
							// client code, apart from that the rest works**
							if (database.containsKey(username) == true) {
								sendMessage("Granted");
								control = 1;
							} else {
								if (loginAttemptCnt > 3) {
									sendMessage("Failed too many times, try again.");
									control = 2;
									loginAttemptCnt = 0;
								} else {
									sendMessage("Incorrect Try-Again.");
									loginAttemptCnt++;
								}
							}
						} while (control == 0);

						// Error handling
						if (control == 2) {

						} else {
							do {
								// Once the username was successfully entered,
								// now I use a similar method for taking in the
								// password
								// and compare it to the password stored in the
								// hashmap
								sendMessage("Enter Password: ");
								password = (String) in.readObject();
								if (database.get(username).getPassword().equals(password) == true) {
									sendMessage("Granted");
									sendMessage("Authenticated");
									authenticated = true;
									control = 0;
								} else {
									if (loginAttemptCnt > 3) {
										sendMessage("Failed too many times, try again.");
										loginAttemptCnt = 0;
										break;
									} else {
										sendMessage("Incorrect Try-Again.");
										loginAttemptCnt++;
									}
								}
							} while (control == 1);
						}
						// Once they have successfully logged in, they may view
						// the logged in menu
						if (authenticated == true) {
							do {
								// Display the menu and and recieve the users
								// option
								sendMessage(loggedInMenu(database.get(username).getBalance()));
								message = (String) in.readObject();
								if (message.equals("1")) {
									// Display the change details menu
									sendMessage("ChangeDetails");
									sendMessage(changeDetailsMenu());
									message = (String) in.readObject();
									// Receive the users option and change
									// detail accordingly using key (username
									// entered in the login).
									if (message.equals("1")) {
										sendMessage("Enter New Name:");
										message = (String) in.readObject();
										database.get(username).setName(message);
										sendMessage("Name Changed");
									} else if (message.equals("2")) {
										sendMessage("Enter New Address:");
										message = (String) in.readObject();
										database.get(username).setAddress(message);
										sendMessage("Address Changed");
									} else if (message.equals("3")) {
										sendMessage("Enter New Password:");
										message = (String) in.readObject();
										database.get(username).setPassword(message);
										sendMessage("Password Changed");
									} else if (message.equals("0")) {
										sendMessage("Back");
										message = "";
									}
								} else if (message.equals("2")) {
									// Allows the user to make a lodgement
									sendMessage("MakeLodgement");
									sendMessage("Enter Amount: ");
									message = (String) in.readObject();
									// Convert the String into a double, no
									// error handling done here.
									deposit = Double.parseDouble(message);
									// Create a log from the previous
									// transactions, simply a string with
									// variable details.
									log = database.get(username).getUsername() + "||Deposit of " + message + " at "
											+ date.toString();
									// Add the log to the linked list on the
									// account.
									database.get(username).add(log);
									// set the balance variable in the account
									// to deposit + current balance
									database.get(username).setBalance(database.get(username).getBalance() + deposit);
									sendMessage("Lodgement Successful");
								} else if (message.equals("3")) {
									// Works the same as the deposit but removes
									// from the balance with some error handling
									sendMessage("MakeWithdrawal");
									sendMessage("Enter Amount: ");
									message = (String) in.readObject();
									withdrawal = Double.parseDouble(message);
									log = database.get(username).getUsername() + "||Withdrawal of " + message + " at "
											+ date.toString();
									// Will not let the user take out more money
									// than their balance and limit of -1000
									if (database.get(username).getBalance() - withdrawal < limit) {
										sendMessage("Insufficient funds.");
									} else {
										// Same as above, will add the log to
										// the users linked list.
										database.get(username).add(log);
										//Set their new balance after withdraw
										database.get(username)
												.setBalance(database.get(username).getBalance() - withdrawal);
										sendMessage("Withdrawal Successful");
									}
								} else if (message.equals("4")) {
									// This option will allow the user to see
									// their 10 lastest transactions.
									sendMessage("Log");
									sendMessage("Previous Logs");
									// If there are less than 10 transactions
									if (database.get(username).size() < 10) {
										
										if(database.get(username).size() == 0) {
											// Error handling to let the user know
											// there are no transactions to show.
											lastTransactions = "No Transactions Available.";
											sendMessage(lastTransactions);
											lastTransactions = "";
										}else{
											// Start the loop at the last node - 1,
											// and decrement while building a string
											// at every iteration
											for (int i = database.get(username).size() - 1; i >= 0; i--) {
												lastTransactions = lastTransactions
														+ database.get(username).get(i).toString() + "\n";
											}
											// Send the built string and reset it.
											sendMessage(lastTransactions);
											lastTransactions = "";
										}
									} else if (database.get(username).size() >= 10) {
										// If there are more than 10
										// transactions, it will start at the
										// last node - 1 and iterate over the
										// last 10 nodes and build a string
										// containing each transaction.
										for (int i = database.get(username).size()
												- 1; i >= (database.get(username).size() - 10); i--) {
											lastTransactions = lastTransactions + database.get(username).get(i) + "\n";
										}
										// Send the string up and reset.
										sendMessage(lastTransactions);
										lastTransactions = "";
									}
								} else if (message.equals("0")) {

								} else {
									sendMessage("Wrong Input");
								}
							} while (!message.equals("0"));
						}
					} else if (message.equals("0")) {
						message = "0";
					} else {
						sendMessage("Wrong Input! try-again.");
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data received in unknown format");
				}
			} while (!message.equals("0"));
			sendMessage("Bye");
			System.out.println(
					// Closes the connection
					"Ending Client : ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 3 menus, one contains the users balance.
	public String firstMenu() {
		String menu = "--------------Menu--------------\n" + " [1] Register\n" + " [2] Login\n" + " [0] Exit\n"
				+ "--------------------------------";

		return menu;
	}

	public String loggedInMenu(double balance) {
		String menu = "--------------Menu--------------\n" + "         Balance: " + balance
				+ "\n [1] Change Customer Details\n" + " [2] Make a Lodgement\n" + " [3] Make a Withdrawal\n"
				+ " [4] View last ten Transactions\n" + " [0] Exit\n" + "--------------------------------";

		return menu;
	}

	public String changeDetailsMenu() {
		String menu = "--------------Menu--------------\n" + " [1] Change Name\n" + " [2] Change Address\n"
				+ " [3] Change Password\n" + " [0] Back\n" + "--------------------------------";

		return menu;
	}
}
