import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

/**
 *  * @author carlc

 *   This bank class allows for a GUI app to be run in which it simulates a fictional bank desktop app.
 *   Initially, the app allows a user to create a basic bank account with their name, initial depositing amount, 
 *   and bank account type.
 *   The user hits the submit button and the app takes the user to
 *   their account summary which has two buttons for withdrawing and depositing money.
 *   In addition, the scene displays customer name, balance, the date in which the account was made, and account type.
 *   When depositing, a new scene is shown that allows user to deposit any valid money amount and submit it with a button.  There is also another button
 *   to go back to the main scene.
 *   When withdrawing, a new scene is shown that allows user to withdraw a valid money amount and submit it with a button. Withdrawing scene also includes 
 *   a button to go back to the main scene.
 *   */

public class THBank extends Application 
{
	private final int AC_LABELS_SIZE = 4; // size of the accountCreationLabelCollection array
	private final int MLC_LABELS_SIZE = 5; // size of the myLabelCollection array
	private final int AC_TXTFIELDS_SIZE = 3; // size of the accountCreationTextFields array
	private Font myFont; // font to be used throughout the app
	private TextField[] accountCreationTextFields; // array to hold the text fields that will receive input in the  account creation scene
	private Label[] accountCreationLabelCollection; // array to hold the labels in the account creation scene.
	private Label[] myLabelCollection; // array to hold main account summary scene labels.
	private Label depositLabel; // label that displays deposit text.
	private Label withdrawLabel; // label that displays withdraw text.
	private Button button1; // button to be used as depositing or go back
	private Button button2; // button to be used as withdrawing or submit
	private TextField balanceAmountTextField; // text field that will take user input for money balance
	private GridPane layout; // gridpane layout to hold controls
	private Scene AccountCreationScene; // account creation scene (comes before main scene)
	private Scene AccountSummaryScene; // Main Account Summary Scene 
	private boolean clicked; // boolean flag used to indicate if button 1 was clicked or not
	private boolean clicked2 = true; // boolean flag used to indicate if button 2 was clicked or not
	private boolean withdrawModeActivated = false; // boolean flag var to indicate if the user is in the withdraw screen (necessary for updating the balance correctly, ie : not increasing the balance but reducing it instead)
	private boolean depositModeActivated = false; // boolean flag var to indicate if the user is in the deposit screen (necessary for updating the balance correctly, ie : not decreasing the balance but increasing it instead)
	private BankCustomer carl; // bankcustomer var that will store customer information (name, balance, account type) and send it out to be displayed in the summary scene
	
	public static void main(String[] args) 
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{		
		carl = new BankCustomer(); // object of the bankcustomer class will be initially empty
		primaryStage.setTitle("T&H Bank Customer App"); // set title of the GUI window
		myFont = new Font(30); // uses default font called System font with this font size 
		Image icon = new Image(getClass().getResourceAsStream("bankicon.png")); // load image to be used as the stage icon
		primaryStage.getIcons().add(icon); // set the icon image in the app window/stage
		
		// 1. Create controls (buttons, labels, text fields)
		accountCreationLabels(); // call the method to make the labels
		accountCreationTextFields(); // call the method to make the text fields
		Button goToMainScreen = new Button("Go to main screen"); // create button that lets user go to the main account screen
		goToMainScreen.setOnAction(e -> { // lambda expression - features e as parameter and a code block with an if statement
								if(validate()) // if no errors were found in validation
								{
									passDataToCustomerObj();  // call method to pass in text field data to bankcustomer object
									createMainAccountScene(primaryStage); // after button is pressed, create the main account scene (scene 2)
								}
		}); // goToMainScreen has event handling so when it is clicked, the new scene becomes main account scene (scene 2)
		
		// 2. Create layout and place controls in the layout
		GridPane loginLayout = new GridPane(); // Creates a GridPane layout obj 
		int row = 0;
		int column = 0;
		for (int x = 0; x < AC_LABELS_SIZE; x++, row++) 
		{
			loginLayout.add(accountCreationLabelCollection[x], column, row); // add each label to the layout
		}
		loginLayout.add(goToMainScreen, column, row); // add button to the layout
		
		int row2 = 1;
		int column2 = 1;
		for (int x = 0; x < AC_TXTFIELDS_SIZE; x++, row2++) 
		{			
			loginLayout.add(accountCreationTextFields[x], column2, row2); // add each text field to the layout

			switch(x) // check the value of x
			{
				case 0:
					accountCreationTextFields[x].setTranslateX(-220); //Sets the horizontal location of the specific text field with x subscript
					break;
				case 1:
					accountCreationTextFields[x].setTranslateX(-170); //Sets the horizontal location of the specific text field with x subscript
					break;
				case 2:
					accountCreationTextFields[x].setTranslateX(-30); //Sets the horizontal location of the specific text field with x subscript
					break;
				default:
					break;
			}
		}
		loginLayout.setPadding(new Insets(10, 10, 10, 10)); // add padding around the inside edge of pane
		loginLayout.setBackground(null); // set value of property background

		// 3. Create scene w/ layout objects
		AccountCreationScene = new Scene(loginLayout);

		// Set background color gradient of scene object
		AccountCreationScene.setFill(new LinearGradient(0, 0, 1, 0, true, // sizing - startX, startY, endX, endY are the direction of movement of the gradient
				CycleMethod.REPEAT, // cycling - Defines the cycle method that uses the terminal colors to fill the remaining area.
				new Stop(0, Color.GREEN), // colors - Stop class contains an offset and an color. Defines one element of the ramp of colors to use on a gradient.
				new Stop(0.5, Color.LIGHTGREEN), new Stop(0.9, Color.GREEN)));

		// 4. Set stage
		primaryStage.setScene(AccountCreationScene); // set the current scene
		primaryStage.show(); // make stage visible 
	}
	
	/*This method checks user inputs in the account creation scene for any invalid data that was entered and displays
	 * an alert if it detects invalid data to warn the user to fix their mistakes!*/
	public boolean validate() 
	{
		boolean errorsFound = false; // bool flag var
        StringBuilder errors = new StringBuilder(); // Java StringBuilder class is used to create mutable strings!
        
        // Check if mandatory fields are filled out
        if(accountCreationTextFields[0].getText().isEmpty() || accountCreationTextFields[1].getText().isEmpty() || accountCreationTextFields[2].getText().isEmpty())
        {
        	errors.append("Please fill out ALL of the fields!\n");
        }

        // Check if account type entered is from one of the correct options (checkings or savings)
        if(!(accountCreationTextFields[1].getText().equalsIgnoreCase("Checkings") || accountCreationTextFields[1].getText().equalsIgnoreCase("Savings")))
        {
            errors.append("Please enter a valid account type!\n");
        }
        
        // check if initial deposit amount is not blank
        if(!accountCreationTextFields[2].getText().isEmpty())
        {
        	try
        	{
    	        // check if initial amount is negative
    	        if(Double.parseDouble(accountCreationTextFields[2].getText()) < 0)
    	        {
    	        	errors.append("Please enter a valid initial deposit amount (no negative values allowed)!");
    	        }
    	        
        	}
        	catch (NumberFormatException e) 
        	{
        		// detected that invalid data was entered for the deposit amount so add an error message for that
	        	errors.append("Please enter numbers for your initial deposit amount (No letters allowed)!");

        	}
        }
        
        // check that the customer name is not made up of digits
        if(!accountCreationTextFields[0].getText().isEmpty())
        {
        	// matches method tells whether or not this string matches the given regular expression (uppercase,lowercase letters, spaces)
            if(!accountCreationTextFields[0].getText().matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$"))
            {
            	errors.append("Please enter letters only for your name!");
            }
        }

        // If any missing information is found, show the error messages and return false
        if (errors.length() > 0) 
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning"); // set all the text for the alert obj
            alert.setHeaderText("Invalid data entered for new account!");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return errorsFound;
        }

        // No errors were found so return true!
        return !errorsFound;
    }
	
	// This method allows for bankcustomer object to collect all info from the text fields in the account creation screen.
	public void passDataToCustomerObj()
	{
		//Displaying current date and time in 12 hour format with AM/PM
		// DateFormat is abstract class for date/time formatting subclasses (like SimpleDateFormat which formats dates)
    	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); // make SimpleDateFormat obj using the given pattern (month,day,year)
    	String dateString = dateFormat.format(new Date()).toString(); // make Date obj and initialize it with the time of its creation to nearest millisecond. Then convert it to a string. 
		// pass the name, account type, balance, date created into the objects setters below
		carl.setName(accountCreationTextFields[0].getText());
		carl.setAccountType(accountCreationTextFields[1].getText());
		carl.setBalance(Double.parseDouble(accountCreationTextFields[2].getText())); // convert string to double before passing it to the customers balance field
		carl.setDateCreated(dateString);
	}
	
	/* This method creates the account main page which displays buttons for withdrawing and depositing and the
	 * users account info like their name, date of creation, account type, and current amount on the side  */
	public void createMainAccountScene(Stage primeStage)
	{
		// 1. Create controls (buttons, labels, text fields)
		makeLabels(carl);
		button1 = new Button("Deposit"); // create new button object with deposit text
		button1.setFont(new Font(25)); // set to desired font
		/*
		 * Credits to :
		 * https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#
		 * setTranslateX-double- This method Sets the value of the property translateX.
		 */
		button1.setOnAction(new ButtonClickHandler());// set an action for the button click event
		button2 = new Button("Withdraw"); // create new button object with withdraw text
		button2.setFont(new Font(25)); // set to desired font
		button2.setOnAction(new Button2ClickHandler()); // set an action for the button click event
		
		// 2. Create layout and place controls in the layout
		layout = new GridPane();
		layout.add(button1, 0, 1); // add deposit and withdraw buttons
		layout.add(button2, 0, 2);
		layout.add(myLabelCollection[0], 1, 0); // add the bank name at the top of the screen

		int i = 1; // counter var
		while (i < MLC_LABELS_SIZE) 
		{
			layout.add(myLabelCollection[i], 1, i); // add the customers name, account balance, date made,account type to layout
			i++;
		}

		layout.setVgap(10); // set vertical spacing gap between cells (think of spacing between rows)
		layout.setHgap(50); // set vertical spacing gap between cells (think of spacing between rows)
		// layout.setGridLinesVisible(true); // show grid lines if needed
		layout.setPadding(new Insets(10, 10, 10, 10)); // add padding around inside edge of pane
		layout.setBackground(null); // Sets the value of the property background.
		// It is possible for a layouts Background to be empty, where it has neither
		// fills nor images, and is semantically equivalent to null.

		// 3. Create scene w/ layout objects
		AccountSummaryScene = new Scene(layout);

		// Set background of scene object
		AccountSummaryScene.setFill(new LinearGradient(0, 0, 1, 0, true, // sizing - startX, startY, endX, endY are the direction of movement of the gradient
				CycleMethod.REPEAT, // cycling - Defines the cycle method that uses the terminal colors to fill the remaining area.
				new Stop(0, Color.GREEN), // colors - Stop class contains an offset and an color. Defines one element of the ramp of colors to use on a gradient.
				new Stop(0.5, Color.LIGHTGREEN), new Stop(0.9, Color.GREEN)));
		/*
		 * Credits to :
		 * https://edencoding.com/scene-background/#:~:text=The%20simplest%20way%20to%
		 * 20set%20the%20JavaFX%20Scene,background%2C%20which%20can%20accept%20multiple%
		 * 20images%20and%20fills. For different color looks, setFill allows me to
		 * create a linear gradient by passing a LinearGradient object to the setFill()
		 * method. I need to specify all positional, cycling and color stop arguments
		 */		
		primeStage.setScene(AccountSummaryScene);
	}
	
	/* This method creates an array of labels and loads them with prompt 
	 * information from an included file in the project folder. */
	public void accountCreationLabels() 
	{
		myFont = new Font(30); // use default font with this font size 
		accountCreationLabelCollection = new Label[AC_LABELS_SIZE]; // instantiate the array of labels

		try // run code in here which may or may not cause errors
		{
			File iFile = new File("accountCreationLabelTexts.txt"); // create file object with text file
			Scanner fileScan = new Scanner(iFile); // create scanner object to get all the text input from the text file
			int x = 0; // counter for the while loop
			while (fileScan.hasNext()) // keep going until reaching end of the file
			{
				String dataPrompt = fileScan.nextLine(); // store current line in the string
				accountCreationLabelCollection[x] = new Label(dataPrompt); // add label to the array
				accountCreationLabelCollection[x].setFont(myFont); // set the custom font
				x++;
			}
			fileScan.close(); // close the file after using it all
		} catch (FileNotFoundException e) // handle errors thrown in the try block
		{
			Alert alert = new Alert(Alert.AlertType.WARNING); // create alert related to file not found
            alert.setTitle("Warning"); // set all the text for the alert obj
            alert.setHeaderText("File not found!");
            alert.setContentText("Your file couldnt be found for the account creation scene labels!\nEnding Program Now!");
            alert.showAndWait(); // display alert
            System.exit(0); // end program
		}
	}

	/* This method creates an array of text fields for use in the
	 * account creation screen, the user will enter information into each text field
	 * like their name, their initial deposit, and account type.*/
	public void accountCreationTextFields() 
	{
		accountCreationTextFields = new TextField[AC_TXTFIELDS_SIZE]; // array to hold the text fields 
		try // run code in here which may or may not cause errors
		{
			File iFile = new File("accountCreationTextFieldPlaceHolders.txt"); // create file object with text file 
			Scanner fileScan = new Scanner(iFile); // create scanner object to get inputted text for text fields
			int x = 0; // counter for the while loop
			while (fileScan.hasNext()) // keep going until reaching end of the file
			{
				String placeHolderText = fileScan.nextLine(); // store each line in the string
				accountCreationTextFields[x] = new TextField(); // create new text field obj
				accountCreationTextFields[x].setPromptText(placeHolderText);
				accountCreationTextFields[x].setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);"); // controls prompt text color
				x++;
			}
			fileScan.close(); // close the file after using it all
		} catch (FileNotFoundException e) // handle errors thrown in the try block
		{
			Alert alert = new Alert(Alert.AlertType.WARNING); // create alert related to file not found
            alert.setTitle("Warning"); // set all the text for the alert obj
            alert.setHeaderText("File not found!");
            alert.setContentText("Your file couldnt be found for the account creation scene text fields!\nEnding Program Now!");
            alert.showAndWait(); // display alert
            System.exit(0); // end program
		}		
	}

	/* This method creates an array of labels for use in the main account summary scene. 
	 * It takes a bank customer as a parameter
	 * because the bank customer holds all the relevant data to go in the labels.*/
	public void makeLabels(BankCustomer customer) 
	{
		myFont = new Font(30); // use default font called System font with this font size 
		myLabelCollection = new Label[MLC_LABELS_SIZE]; // array to hold labels 
		myLabelCollection[0] = new Label("T&H Bank"); // Create new label objects (representing the bank name, customers balance, account type, etc.)
		myLabelCollection[0].setFont(new Font("Bell MT", 50)); // give the bank name a bigger font size than the other  labels 
		myLabelCollection[1] = new Label("Welcome, " + customer.getName() + "!");
		myLabelCollection[2] = new Label("Account Balance: $" + String.format("%.2f", customer.getBalance()));
		myLabelCollection[3] = new Label("Account creation date : " + customer.getDateCreated());
		myLabelCollection[4] = new Label("Account Type : " + customer.getAccountType());

		int i = 1; // counter for the loop below (starting at the 2nd element)
		do 
		{
			myLabelCollection[i].setFont(myFont); // set the font with existing font object
			i++; // increment counter
		} while (i < MLC_LABELS_SIZE);
	} // end of makeLabels method

	/* This method is called after the deposit or withdraw buttons have been pressed
	 * and the user wants to GO BACK to the original main screen that displays their
	 * customer information along with the bank name at the top.*/
	public void remakeMainScreenScene() 
	{
		// remove controls (labels,buttons,txt fields) from the old scene first
		layout.getChildren().remove(balanceAmountTextField); 
		layout.getChildren().remove(depositLabel); 
		layout.getChildren().remove(button1); 
		layout.getChildren().remove(button2);
		layout.getChildren().remove(withdrawLabel);
		button1.setText("Deposit"); // change the text of button 1
		button2.setText("Withdraw"); // change the text of button 2
		layout.add(button1, 0, 1); // add deposit and withdraw buttons to the left side
		layout.add(button2, 0, 2);
		layout.add(myLabelCollection[0], 1, 0); // add the bank name at the top of the screen
		int i = 1; // counter var
		while (i < MLC_LABELS_SIZE) 
		{
			layout.add(myLabelCollection[i], 1, i); // add customer info to the layout
			i++;
		}
	}

	/*This method changes up the scene so it looks like a deposit screen for the
	 * user to enter an amount to add to their balance. After they click submit,
	 * their balance will be updated and they have another button they can press to
	 * go back to the main screen.*/
	public void depositScene() 
	{
		for (int x = 0; x < MLC_LABELS_SIZE; x++)
			layout.getChildren().remove(myLabelCollection[x]); // remove labels from the screen to make way for a new scene
		layout.getChildren().remove(button1); // remove all the buttons from the screen to make way for a new scene
		layout.getChildren().remove(button2); 
		button1.setText("Go Back"); // change the text of the button
		button1.setTranslateX(0); // change x position of the button
		button2.setText("Submit"); // change the text of the button
		button2.setTranslateX(0); // change x position of the button
		button2.setOnAction(new Button2ClickHandler()); // set an action for the button click event
		balanceAmountTextField = new TextField(); // create text field object that will take user input for money balance
		balanceAmountTextField.setPromptText("0.00"); // Sets the promptText (text that appears in grey in the text field)
		balanceAmountTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);"); // controls prompt text color
		depositLabel = new Label("Enter amount to deposit :  $"); // label object that displays deposit message
		layout.add(depositLabel, 0, 0); // add the label to the layout grid
		layout.add(balanceAmountTextField, 1, 0); //add the text field to the layout grid
		layout.add(button1, 0, 1); // add the buttons to the layout grid
		layout.add(button2, 0, 2); 
		layout.setVgap(10); // set vertical spacing gap between cells ( think of spacing between rows)
		layout.setBackground(null); // Sets the value of the property background.
		// It is possible for a layouts Background to be empty, where it has neither
		// fills nor images, and is semantically equivalent to null.
		layout.setAlignment(Pos.BASELINE_CENTER); // set alignment of controls in the scene
	} // end of depositscene method

	/*This method changes up the scene so it looks like a withdraw screen for the
	 * user to enter an amount to take out of their balance. After they click
	 * submit, their balance will be updated. There is also another button they can
	 * press to go back to the main screen.*/
	public void withdrawScene() 
	{
		for (int x = 0; x < MLC_LABELS_SIZE; x++)
			layout.getChildren().remove(myLabelCollection[x]); // remove all the labels from the screen
		layout.getChildren().remove(button1); // remove all the buttons from the screen to make way for a new scene
		layout.getChildren().remove(button2); 
		button1.setText("Go Back"); // change the text of the button
		button1.setTranslateX(0); // change x position of the button
		button1.setOnAction(new ButtonClickHandler()); // set an action for the button click event
		button2.setText("Submit"); // change the text of the button
		button2.setTranslateX(0); // change x position of the button
		button2.setOnAction(new Button2ClickHandler()); // set an action for the button click event
		balanceAmountTextField = new TextField(); // create text field object that will take user input
		balanceAmountTextField.setPromptText("0.00"); // Sets the promptText (text that appears in grey in the text field)
		balanceAmountTextField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -45%);"); // controls prompt text color
		withdrawLabel = new Label("Enter amount to withdraw :  $"); // label object that displays a withdraw message
		layout.add(withdrawLabel, 0, 0); // add the label to the layout grid
		layout.add(button1, 0, 1); // add the buttons to the layout grid
		layout.add(button2, 0, 2); 
		layout.add(balanceAmountTextField, 1, 0); // add the text field to the layout grid
		layout.setVgap(10); // set vertical spacing gap between cells ( think of spacing between rows)
		layout.setBackground(null); // Sets the value of the property background.
		// It is possible for a layouts Background to be empty, where it has neither
		// fills nor images, and is semantically equivalent to null.
		layout.setAlignment(Pos.BASELINE_CENTER);
	}

	/* This class is associated with button1 as an eventhandler and checks to
	 * see if the user clicked on the deposit or the go back buttons then
	 * calls the appropriate methods to display the correct scenes and switches
	 * the boolean variables values so each scene is loaded in correctly.
	 */
	public class ButtonClickHandler implements EventHandler<ActionEvent> 
	{
		@Override
		public void handle(ActionEvent arg0) 
		{

			if (clicked == false) // user clicked on deposit button
			{
				depositScene(); // call the function to set up the new depositing scene
				depositModeActivated = true; // set this to true so users balance can be increased in the other buttons (submit) Button2ClickHandler handle method
				withdrawModeActivated = false; // set this to false so depositMode can work correctly
				clicked = true; //Set this to true because the current button was clicked (this also causes the current button to become the Go Back button)
				clicked2 = false; // Set this to false then the OTHER BUTTON (submit) will submit customer info 
			} 
			else // user clicked on go back button
			{
				remakeMainScreenScene(); // call the method to remake the original starting scene (since user clicked on go back button this time)
				clicked = false; //Set this to false because the current button was clicked (this also causes the current button to become the Deposit button)
				clicked2 = true; // Set this to false then the OTHER BUTTON (withdraw) will withdraw money
			} // end of else
		} // end of handle
	} // end of Buttonclickhandler class

	public class Button2ClickHandler implements EventHandler<ActionEvent> 
	{
		@Override
		public void handle(ActionEvent arg0) 
		{
			if (clicked2 == false) // user clicked on submit button
			{
				if (withdrawModeActivated) // withdraw mode is true so this withdraw button will decrease the balance of the user
				{
					if (!(balanceAmountTextField.getText().isEmpty())) // check if text field is currently empty
					{
						try
						{
							//Try block code will parse text field as double but if it fails then numberformatexception is thrown
							Double.parseDouble(balanceAmountTextField.getText());
							//check if the amount entered in the withdraw balance text field SURPASSES the users balance 
							if (Double.parseDouble(balanceAmountTextField.getText()) > carl.getBalance()) 
							{
								// Display an alert dialog if the users actual balance is too low for withdrawal to
								// work!
								Alert alert = new Alert(AlertType.ERROR,
										"You do not have sufficient funds! Your balance is $"
												+ String.format("%.2f", carl.getBalance()) + "!");
								alert.showAndWait();
							}
							else if(Double.parseDouble(balanceAmountTextField.getText()) < 0) // check if amount entered in the withdraw balance textfield is a negative amount
							{
								// Display an alert dialog if the amount in the withdraw balance text field is less than 0
								Alert alert = new Alert(AlertType.ERROR,"Negative amounts cannot be withdrawed! Positive amounts only!");
								alert.showAndWait();
							}
							else // amount entered into the text field is a valid amount so withdraw action will work!
							{
								carl.withdrawMoney(Double.parseDouble(balanceAmountTextField.getText())); // bankcustomer object withdraws the money entered into the text field
								myLabelCollection[2] = new Label("Account Balance: $" + String.format("%.2f", carl.getBalance())); // update account balance label with the updated balance
								myLabelCollection[2].setFont(myFont); // set the custom font for the label
							} // end of else
						}
						catch (NumberFormatException e) // handles numberformatexceptions thrown from the try block
						{
							// create an alert obj that will be displayed and let the user know about an error
							Alert myAlert = new Alert(AlertType.ERROR, "You must enter digits for an amount to be withdrawn, NOT LETTERS!");
							myAlert.showAndWait();
						}
					} // end of if (!(balanceAmountTextField.getText().isEmpty()))
					else // withdrawal amount text field is empty and user pressed submit button
					{
						// create an alert obj that will be displayed and let the user know about an error
						Alert myAlert = new Alert(AlertType.ERROR, "You must enter a valid withdrawal amount, no blanks allowed!");
						myAlert.showAndWait();
					}
				} // end of if (withdrawModeActivated)
				else // button must be in deposit mode
				{
					if (depositModeActivated) // deposit mode is true so make sure to increase the balance of the user (balance + deposited amount)
					{
						if (!balanceAmountTextField.getText().isEmpty()) // check if the text field isnt empty
						{
							try
							{
								//Try block code will parse text field as double but if it fails then numberformatexception is thrown
								Double.parseDouble(balanceAmountTextField.getText());
								if (Double.parseDouble(balanceAmountTextField.getText()) < 0) // check if the amount entered in the deposit balance text field is a negative value
								{
									// Display an alert dialog if the users deposit amount is a negative number
									Alert alert = new Alert(AlertType.ERROR,
											"Negative amounts cannot be deposited! Positive amounts only!");
									alert.showAndWait();
								}
								else // amount is a positive value or 0
								{
									carl.depositMoney(Double.parseDouble(balanceAmountTextField.getText())); // // bankcustomer object deposits the money entered into the text field
									myLabelCollection[2] = new Label("Account Balance: $" + String.format("%.2f", carl.getBalance())); // update account balance label with the updated balance
									myLabelCollection[2].setFont(myFont); // set the custom font for the label
								}
							}
							catch (NumberFormatException e) // handles numberformatexceptions thrown from the try block
							{
								// create an alert obj that will be displayed and let the user know about an error
								Alert myAlert = new Alert(AlertType.ERROR, "You must enter digits for an amount to be deposited, NOT LETTERS!");
								myAlert.showAndWait();
							}
						} // end of	if(!balanceAmountTextField.getText().isEmpty())
						else // deposit amount text field is empty and user pressed submit button
						{
							// create an alert obj that will be displayed and let the user know about an error
							Alert myAlert = new Alert(AlertType.ERROR, "You must enter a valid deposit amount, no blanks allowed!");
							myAlert.showAndWait();
						}
					} // end of if (depositModeActivated)
				} // end of else
			} // end of if (clicked2 == false)
			else // user clicked on the withdraw button (the button said withdraw, not submit)
			{
				withdrawModeActivated = true; // set this to true so users balance can be decreased in this buttons handle method
				depositModeActivated = false; // set this to false so withdrawMode can work correctly
				withdrawScene(); // create the withdraw scene
				clicked2 = false; // Set this to false to get the submit button to work
				clicked = true; // Set this to true to get the go back button to work
			}
		}// end of handle method  
	} // end of button2clickhandler
} // end of test bank GUI 
