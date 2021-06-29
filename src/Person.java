/**
 * Superclass to be inherited by the bank customer class later.
 */

/**
 * @author carlc This class represents a Person object or instance.
 */
public class Person 
{
	// fields
	private String name;

	// methods
	public Person() 
	{
		this.name = " ";
	}

	public Person(String name) 
	{
		this.name = name;
	}
	
	/**
	 * @param name - the persons name to set
	 */
	public void setName(String name) 
	{
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}



}
