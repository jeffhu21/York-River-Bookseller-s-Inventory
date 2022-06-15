/*
EECS3421B
Project 4
Name: Jeffrey Hu
ID: 215677537
EECS Account: jeffhu17
*/

import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class YRB 
{
	private Connection conDB;
	private String url;
	private String str;
	private char choice;
	private Integer custID;
	private String custName;
	private String custCity;
	private Integer catNo;
	private String bookCat;
	private String bookTitle;
	private Integer bookNo;
	private Integer numOfBooks;
	
	Scanner input=new Scanner(System.in);
	
	public YRB(String[] args)
	{
		try
		{
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		}catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
		url="jdbc:db2:c3421a";
		
		try
		{
			conDB = DriverManager.getConnection(url);
		}
		catch(SQLException e) 
		{
            System.out.print("\nSQL: database connection error.\n");
            System.out.println(e.toString());
            System.exit(0);
        }    
		
		try
		{
			conDB.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			System.out.println("\nFailed trying to turn autocommit off.\n");
			e.printStackTrace();
			System.exit(0);
		}
				
		run();
		

		try
		{
			conDB.commit();
		}
		catch(SQLException e)
		{
			System.out.println("\nFailed trying to commit.\n");
			e.printStackTrace();
			System.exit(0);
		}
		
		try
		{
			conDB.close();
		}
		catch(SQLException e)
		{
			System.out.println("\nFailed trying to close the connection.\n");
			e.printStackTrace();
			System.exit(0);
		}
		
	}

        /*Display the choice.*/
	public boolean parseAnswer(char c)
	{
		if(c == 'Y' || c == 'y')
		{
			return true;
		}

		if(c == 'N' || c == 'n')
		{
			return false;
		}

		if(c != 'Y' || c != 'y' || c!= 'N' || c!= 'n')
		{
			throw new IllegalArgumentException("You didn't enter the choice!");
		}


		return false;
	}
	
	 /*Run the program.*/
	public void run()
	{
		str = "*************";
		System.out.println(str+"YRB Online Bookstore"+str+"\n");
		System.out.println("Please enter the customer number:\n");
		System.out.print("Customer ID: ##");
		custID = input.nextInt();
		input.nextLine();
		while(!findCustomer(custID))
		{
			System.out.println();
			System.out.println("Error: The customer number " + custID + " is not found.\n"+"Please enter the customer number:\n");
			System.out.print("Customer ID: ##");
			custID = input.nextInt();
		}
		System.out.print("Would you like to update the customer information? (Y/N)");
		choice = input.next().charAt(0);
		input.nextLine();
		
		if(parseAnswer(choice))
		{
			System.out.print("Customer Name: ");
			custName = input.nextLine();
			System.out.println();
			System.out.print("Customer City: ");
			custCity = input.nextLine();
			System.out.println();
			updateCustomer(custID,custName,custCity);

		}



		displayCat();	
			
		
	}

	 /*Search the customer.*/
	public boolean findCustomer(int i) 
	{
		String queryText = "";
        PreparedStatement querySt = null;
        ResultSet answers = null;
        boolean inDB = false;
        
        queryText = "SELECT cid,name,city       "+ "FROM yrb_customer "+ "WHERE cid = " + i;
       

        try 
        {
            querySt = conDB.prepareStatement(queryText);
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#1 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers = querySt.executeQuery();
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#1 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            if (answers.next()) 
            {
                inDB = true;
		System.out.println("cid = " + answers.getString("cid") + "\t name =  " + answers.getString("name") + "\t city = " + answers.getString("city"));
            } 
            else 
            {
                inDB = false;
            }
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#1 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#1 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#1 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        return inDB;
	}
	/*Update the Customer.*/
	public void updateCustomer(int cid,String name,String city)
	{
		try
		{
			Statement querySt = conDB.createStatement();

			String queryText = "UPDATE yrb_customer " + "SET name = '" + name + "', city = '" + city + "' WHERE cid = " + cid;

			querySt.executeUpdate(queryText);

			System.out.println("cid = " + cid + "\t name =  " + name + "\t city = " + city);
				

		}
		catch(Exception e)
		{
	            System.out.println("Failed");
	            System.out.println(e.toString());
        	    System.exit(0);

		}	
	}
	/*Check the amount of the book category.*/	
	private int countCategory()
	{
	       	 String queryText = "";	 
       		 PreparedStatement querySt = null;
       		 ResultSet answers = null;
       		 int size = 0;
		 
		 queryText = "SELECT COUNT(cat) AS count "+ "FROM yrb_category";
		
       		 try 
       		 	{
       			 	querySt = conDB.prepareStatement(queryText);
       		 	} 
	       	 catch(SQLException e) 
	       	 {
	           	 System.out.println("SQL#3 failed in prepare");
	           	 System.out.println(e.toString());
	          	 System.exit(0);
	     	 }
       
	        try 
	        {
		    answers = querySt.executeQuery();
	        } 
	        catch(SQLException e) 
	        {
	            System.out.println("SQL#3 failed in execute");
	            System.out.println(e.toString());
	            System.exit(0);
	        }	
        
	        try 
	        {
	            if (answers.next()) 
	            {
			size = answers.getInt("count");
	            } 
	        } 
	        catch(SQLException e) 
	        {
	            System.out.println("SQL#3 failed in cursor.");
	            System.out.println(e.toString());
	            System.exit(0);
	        }

		        
        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#3 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#3 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
		return size;	
	}
	/*Check the amount of book title.*/
         private int countTitle(String title,String category)
	{
	        String queryText = "";	 
       		 PreparedStatement querySt = null;
       		 ResultSet answers = null;
       		 int size = 0;
		 
		 queryText = "SELECT COUNT(title) AS count "+ "FROM yrb_book "+ "WHERE title = '" + title + "' and cat = '" + category + "'";			
       		 try 
       		 	{
       			 	querySt = conDB.prepareStatement(queryText);
       		 	} 
	       	 catch(SQLException e) 
	       	 {
	           	 System.out.println("SQL#3 failed in prepare");
	           	 System.out.println(e.toString());
	          	 System.exit(0);
	     	 }
       
	        try 
	        {
		    answers = querySt.executeQuery();
	        } 
	        catch(SQLException e) 
	        {
	            System.out.println("SQL#3 failed in execute");
	            System.out.println(e.toString());
	            System.exit(0);
	        }	
        
	        try 
	        {
	            if (answers.next()) 
	            {
			size = answers.getInt("count");
	            } 
	        } 
	        catch(SQLException e) 
	        {
	            System.out.println("SQL#3 failed in cursor.");
	            System.out.println(e.toString());
	            System.exit(0);
	        }
		        
        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#3 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#3 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
		return size;	
	}
	/*Display the book category.*/
	private void displayCat()
	{
		System.out.println();
		System.out.println(str+"Book Categories"+str+"\n");

		 String queryText = "";	 
       		 PreparedStatement querySt = null;
       		 ResultSet answers = null;
		 int index = -1;

		String[] categoryArray = new String[countCategory()];

		queryText = "SELECT cat       "+ "FROM yrb_category "; 

		 try 
       		 {
			 querySt = conDB.prepareStatement(queryText);
       		 } 
	       	 catch(SQLException e) 
	       	 {
	           	 System.out.println("SQL#4 failed in prepare");
	           	 System.out.println(e.toString());
	          	 System.exit(0);
	     	 }
       
	        try 
	        {
		    answers = querySt.executeQuery();
	        } 
	        catch(SQLException e) 
	        {
	            System.out.println("SQL#4 failed in execute");
	            System.out.println(e.toString());
	            System.exit(0);
	        }

		 try 
	       	 {
			
     		    while (answers.next()) 
	            {
			index++;
			categoryArray[index] = answers.getString("cat");
			System.out.println((index+1) + ". " + categoryArray[index]);
	            }

 
	       	 } 
	
	       	 catch(SQLException e) 
	       	 {
	            System.out.println("SQL#4 failed in cursor.");
	            System.out.println(e.toString());
	            System.exit(0);
	       	 }

		        
       		 try 
       		 {
           		 answers.close();
       		 } 
       		 catch(SQLException e) 
        	{
        	         System.out.print("SQL#4 failed closing cursor.\n");
            		 System.out.println(e.toString());
           		 System.exit(0);
       	        }
        
        	try 
        	{
            		querySt.close();
        	} 
        	catch(SQLException e) 
       		 {
            		System.out.print("SQL#4 failed closing the handle.\n");
            		System.out.println(e.toString());
            		System.exit(0);
       		 }

		chooseBook(categoryArray);
		
		

	}
	/*Pick the book.*/
	private void chooseBook(String[] categoryArray)
	{

		System.out.println();
		exit();
		System.out.print("Choose a category: ");
		catNo = input.nextInt();
		input.nextLine();
		System.out.println();
		bookCat = categoryArray[catNo-1];
		System.out.println("Category " + bookCat + " is selected.");
		System.out.println();	
		System.out.print("Title: ");
		bookTitle = input.nextLine();


		while(!findBook(bookTitle,bookCat))
		{
			System.out.println();
			System.out.println("Error: " + bookTitle + " in " + bookCat + " is not found.\n");

			System.out.print("Choose a category: ");
			catNo = input.nextInt();
			input.nextLine();
			System.out.println();
			bookCat = categoryArray[catNo-1];
			System.out.println("Category " + bookCat + " is selected.");
			System.out.println();
		
			System.out.print("Title: ");
			bookTitle = input.nextLine();

		}

		int size = countTitle(bookTitle,bookCat);

		String[] bookArray = new String[size];

		bookArray = displayBook(bookTitle,bookCat,size);
	}
	/*Search the book.*/
	private boolean findBook(String title,String category)
	{
		 String queryText = "";
   		 PreparedStatement querySt = null;
   		 ResultSet answers = null;
   		 boolean inDB = false;

   		 
   		queryText = "SELECT title,year,language,cat,weight "+ "FROM yrb_book "+ "WHERE title = '" + title + "' and cat = '" + category + "'";

	try 
        {
            querySt = conDB.prepareStatement(queryText);
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#5 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers = querySt.executeQuery();
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#5 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }


	 try 
        {
	   
	    	
            if (answers.next()) 
            {
                inDB = true;

            }
	    else
	    { 
                inDB = false;
		return inDB;
	    }
	   
	    	    
	}

        catch(SQLException e) 
        {
            System.out.println("SQL#5 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#5 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#5 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

		return inDB;

	}

	/*Display the book.*/
	private String[] displayBook(String title,String category,int size)
	{
		 String queryText = "";
   		 PreparedStatement querySt = null;
   		 ResultSet answers = null;
		 ResultSet a = null;
   		 boolean inDB = false;
                 int index = -1;

		String [] bookArray = new String[size];
		int [] yearArray = new int[size];
   		 
   		queryText = "SELECT title,year,language,cat,weight "+ "FROM yrb_book "+ "WHERE title = '" + title + "' and cat = '" + category + "'";	
       		
        try 
        {
            querySt = conDB.prepareStatement(queryText);
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#6 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers = querySt.executeQuery();
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#6 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
	   
             while(answers.next())
	    {
	    	System.out.println(answers.getRow()+ ". "+"title = " + answers.getString("title") + "\t year =  " + answers.getString("year") 
		+ "\t language = " + answers.getString("language")+ "\t cat = " + answers.getString("cat")
		+ "\t weight = " + answers.getString("weight"));
		index++;
		bookArray[index] = answers.getString("title");
		yearArray[index] = answers.getInt("year");
	    }
	    
	  	System.out.println();
		exit();
		System.out.print("Select a book to purchase: ");
		bookNo = input.nextInt();

		float price = displayMinPrice(bookArray[bookNo-1]);
		int qty = enterQuantity(price);
		
		purchaseBook(bookArray[bookNo-1],yearArray[bookNo-1],qty);
				

	    
	}

        catch(SQLException e) 
        {
            System.out.println("SQL#6 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }
        


        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#6 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#6 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
     
   		 return bookArray;
	}
	/*Display the minimum price of the book.*/
	private float displayMinPrice(String title)
	{
		String queryText = "";
        	PreparedStatement querySt = null;
        	ResultSet answers = null;
		float minimumPrice = 0;
        
       	         queryText = "SELECT o.title,min(o.price) AS minPrice       "+ "FROM yrb_offer o, yrb_member m "+"WHERE o.club = m.club AND o.title =  '" + title  + "' GROUP BY o.title";


		 try 
        	 {
            		querySt = conDB.prepareStatement(queryText);
        	 } 
        	 catch(SQLException e) 
       		 {
            		System.out.println("SQL#7 failed in prepare");
           		 System.out.println(e.toString());
           		 System.exit(0);
        	}
        
        try 
        {
            answers = querySt.executeQuery();
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#7 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }

		 try 
	        {
	            if (answers.next()) 
	            {
			minimumPrice = answers.getInt("minPrice");
	            } 
		    else
		    {
		    	System.out.println("Error: No book in the asking category.");
		    }
	        } 
	        catch(SQLException e) 
	        {
	            System.out.println("SQL#7 failed in cursor.");
	            System.out.println(e.toString());
	            System.exit(0);
	        }
		        
        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#7 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#7 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }


		return minimumPrice;
	}
	
	/*Enter the quantity of books to purchase.*/
	public int enterQuantity(float price)
	{
		exit();
		System.out.print("Please enter the quantity of books to buy:");
		numOfBooks = input.nextInt();
		input.nextLine();
		
		System.out.println("The total price is: " + price*numOfBooks);

		return numOfBooks;
	}
	/*Purchase the book.*/
	public void purchaseBook(String title,int year,int qnty)
	{
    	
	System.out.print("Would you like to purchase the book/books? (Y/N)");
	choice = input.next().charAt(0);
	input.nextLine();
		
	if(parseAnswer(choice))
	{
		insertPurchase(title,year,qnty);
		

		System.out.println("Thank you for your purchase.");
	}


		exit();

		displayCat();

		
	}
	/*Search the club.*/
	public String findClub(int cid) 
	{
		String queryText = "";
        PreparedStatement querySt = null;
        ResultSet answers = null;
        
        queryText = "SELECT club       "+ "FROM yrb_member "+ "WHERE cid = " + cid;
       

        try 
        {
            querySt = conDB.prepareStatement(queryText);
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#8 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers = querySt.executeQuery();
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#8 failed in execute");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            if (answers.next()) 
            {
		return answers.getString("club");
	    } 
            else 
            {
		return "";
	    }
        } 
        catch(SQLException e) 
        {
            System.out.println("SQL#8 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            answers.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#8 failed closing cursor.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        try 
        {
            querySt.close();
        } 
        catch(SQLException e) 
        {
            System.out.print("SQL#8 failed closing the handle.\n");
            System.out.println(e.toString());
            System.exit(0);
        }

		return "";
        
	}
	/*Purchase the book.*/
	public void insertPurchase(String title,int year,int qnty)
	{
		try
		{
			Statement querySt = conDB.createStatement();

			String club = findClub(custID);
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
			Date date = new Date();

			String queryText = "INSERT INTO yrb_purchase values("+ custID + ", '" + club + "', '"+ title + "', " + year + ", '" + formatter.format(date)+"', " + qnty + ")";
			

			querySt.executeUpdate(queryText);

				

		}
		catch(Exception e)
		{
	            System.out.println("Failed");
	            System.out.println(e.toString());
        	    System.exit(0);

		}	
	}

	/*Exit the program.*/
	public void exit()
	{
		System.out.print("Would you like to continue? (Y/N)");
		choice = input.next().charAt(0);
		input.nextLine();

		if(!parseAnswer(choice))
		{
			System.out.println("Good bye!");
			System.exit(0);
		}
	
	}

	
	public static void main(String[] args) 
	{
		YRB bookstore = new YRB(args);
	}
	
	
}
