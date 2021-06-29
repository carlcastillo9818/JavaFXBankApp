/**
 * Subclass called BankCustomer that will hold fields and methods related to the bank customer
 * and inherits from the Person superclass
 */

/**
 * @author carlc This class represents a Bank Customer object or instance.
 */
public class BankCustomer extends Person
{
	private double balance; // user account balance
	private String dateCreated; // date when the account was made
	private String accountType; // student checkings, savings, etc.

	/**
	 * @param balance  - the balance to set
	 * @param dateCreated - the date the account was created to set
	 * @param accountType - the account type to set
	 */
	public BankCustomer(String name, double balance, String dateCreated, String accountType) 
	{
		super(name); // call parent constructor and pass in name arg
		this.balance = balance; // set the rest of the fields in this class
		this.dateCreated = dateCreated;
		this.accountType = accountType;
	}

	/**
	 * no arg constructor
	 */
	public BankCustomer() 
	{
		super(); // call superclass constructor
		this.balance = 0;
		this.dateCreated = " ";
		this.accountType = " ";
	}

	/*
	 * This method allows user to withdraw money from their bank accounts balance
	 * and updates the balance.
	 * 
	 * @param amountToWithdraw - a double value (the money) being withdrawn from the
	 * users balance.
	 * 
	 */
	public void withdrawMoney(double amountToWithdraw) 
	{
		this.balance -= amountToWithdraw;
	}

	/*
	 * This method allows user to deposit money to their bank account balance
	 * and updates the balance.
	 * 
	 * @param amountToDeposit - a double value (the money) that is being deposited
	 * to the balance
	 */
	public void depositMoney(double amountToDeposit) 
	{
		this.balance += amountToDeposit; // add the amount arg to the balance
	}


	/**
	 * @param balance - the balance to set
	 */
	public void setBalance(double balance) 
	{
		this.balance = balance;
	}

	/**
	 * @param dateCreated - the date the account was created to set
	 */
	public void setDateCreated(String dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	/**
	 * @param accountType - the account type to set
	 */
	public void setAccountType(String accountType) 
	{
		this.accountType = accountType;
	}
	
	/**
	 * @return the date the account was created
	 */
	public String getDateCreated() 
	{
		return dateCreated;
	}

	/**
	 * @return the account balance of the account
	 */
	public double getBalance() 
	{
		return balance;
	}

	/**
	 * @return the bank account type
	 */
	public String getAccountType() 
	{
		return accountType;
	}
}
