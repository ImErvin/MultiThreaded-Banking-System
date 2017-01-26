package project;

import java.util.LinkedList;

public class Account {
	// Variables
	private String name;
	private String address;
	private String bankAcNo;
	private String username;
	private String password;
	private double balance = 0;
	private LinkedList<String> transactions = new LinkedList<String>();

	// Constructors
	public Account() {

	}

	public Account(String name, String address, String bankAcNo, String username, String password) {
		super();
		this.name = name;
		this.address = address;
		this.bankAcNo = bankAcNo;
		this.username = username;
		this.password = password;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBankAcNo() {
		return bankAcNo;
	}

	public void setBankAcNo(String bankAcNo) {
		this.bankAcNo = bankAcNo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public LinkedList<String> getTransactions() {
		return transactions;
	}

	public void setTransactions(LinkedList<String> transactions) {
		this.transactions = transactions;
	}

	// Delegated methods for the LinkedList to allow other classes to use these
	// methods to manipulate the list.
	public boolean add(String e) {
		return transactions.add(e);
	}

	public String get(int index) {
		return transactions.get(index);
	}

	public String transactionsToString() {
		return transactions.toString();
	}

	public int size() {
		return transactions.size();
	}

}
