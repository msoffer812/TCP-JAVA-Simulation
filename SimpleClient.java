import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.*;

public class SimpleClient {
    public static void main(String[] args) throws IOException {
        
		// Hardcode in IP and Port here if required
    	args = new String[] {"127.0.0.1", "30121"};
    	
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter requestWriter = // stream to write text requests to server
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader responseReader= // stream to read text response from server
                new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())); 
            BufferedReader stdIn = // standard input stream to get user's requests
                new BufferedReader(
                    new InputStreamReader(System.in))
        ) {
            String userInput;
			String serverResponse;
			//This loop will continue as long as the user keeps putting in input
            while ((userInput = stdIn.readLine()) != null) {
            	requestWriter.println("~" + userInput); // send request to server
            	boolean cont, finished=false;
            	TreeMap<Integer, String> p = new TreeMap<>();//This will intake all the packets and their indexes, in the correct order.
            	//This will continue to loop until the size of the treeMap, which holds and orders the packages, is the size of the total expected packages. 
            	//Inside it, packages will be verified to make sure they aren't corrupted and are then added to the treeMap
            	while(!finished)
            	{
            		do
                	{
                		cont = true;
                		serverResponse = responseReader.readLine();//This will continuously read the packages from the server
                        System.out.println("SERVER RESPONDS: \"" + serverResponse + "\"");if(serverResponse.length()>= 4)//Minimal length for ending package
                		{
                        	if(compareSubstring(serverResponse, "end", 0))//This checks if it's the last package from the server. If so, the reading loop will stop
                        	{
                       			cont = false;
                       			if(serverResponse.charAt(3) == '0')//If received end packet saying there are no packages- this handles case scenerio that there's was an empty message sent
                       			{
                                	finished = true;
                       			}
                       		}else if(serverResponse.length()>= 38)//This is the absolution minimum length for a non-corrupted package, what with the strings added for integrity. If the package is less than this, there's no point in continuing
                       		{
                       			int z = 10;
                    			if(compareSubstring(serverResponse, "hrhgig#$@#", 0))
                    			{
                    				StringBuilder s = new StringBuilder();
                        			while(Character.isDigit(serverResponse.charAt(z)))
                        			{
                        				s.append(serverResponse.charAt(z));
                        				z++;
                        			}
                        			if(s.toString().length()>0)
                        			{

                            			int place = Integer.parseInt(s.toString());
                        				int x = 16 + z;
                            			if(x < serverResponse.length())//just keeping to check that no elements of the string were deleted
                            			{
                            				//this will find the length the string at the end is supposed to be. Again,just to confirm package made it through okay
                                			StringBuilder strl = new StringBuilder();
                            				while(Character.isDigit(serverResponse.charAt(x)))
                                			{
                                				strl.append(serverResponse.charAt(x));
                                				x++;
                                			}
                            				boolean lenMatches = false;
                            				if(strl.toString().length() > 0)
                            				{
                            					int strLen = Integer.parseInt(strl.toString());
                            					lenMatches = strLen == serverResponse.length() - (x+10); 
                            				}
                   
                            				
                            				//Finally, if all checks as to structure and composition check out, the index and string can be put into the treeMap
            	                			if(compareSubstring(serverResponse, "vjdfjfiejro--$$@", z) && compareSubstring(serverResponse, "xbah*&bdj~", x) && lenMatches)
            	                			{
            		                			p.put(place, serverResponse);
            	                			}
                            			}
                        			}
                    			}
                       		}
                		}
                	}while(cont);
            		if(serverResponse.equals("end0"))//if user accidentally sent empty message
            		{
            			System.out.println("Message had no content");
            		}else
            		{
            			//Below parses out the amount of packages that were supposed to be sent. The last package from the server contains that information after "end"
                		StringBuilder s = new StringBuilder();
            			int z = 3;
            			while(z<=serverResponse.length()-1)
            			{
            				s.append(serverResponse.charAt(z));
            				z++;
            			}
            			int fullSize = Integer.parseInt(s.toString());
            			//Finally, we check we have all our packages. If so, yay! The loop will end and we can go on to print out the string. 
            			//Otherwise, this contains the logic to figure out which indexes are missing and parses that information into a recognizable(to the server) string
                		if(p.size() < fullSize)
                		{
                			int count=0;
                			StringBuilder dropped = new StringBuilder("y~%%#"); 
                			for(Integer key:p.keySet())
                			{
                				while(count < key)
                				{
                					dropped.append(count);
                					dropped.append("-");
                					count++;
                				}
                				count++;
                			}
                			//In case there are packages missing from the end, we need to add those in too:
                			while(count <= fullSize-1)
                			{
                				dropped.append(count);
            					dropped.append("-");
            					count++;
                			}
                			requestWriter.println(dropped.toString());
                		}else
                		{
                			finished = true;
                		}
                	
                	
                		StringBuilder str = new StringBuilder();
                    	boolean write;
                    	for(Integer key: p.keySet())
                    	{
                    		int len = p.get(key).length();
                    		write = false;
                    		for(int a =0;a<len;a++)
                    		{
                    			if(write)
                    			{
                    				str.append(p.get(key).charAt(a));
                    			}
                    			if(p.get(key).charAt(a) == '~')
                    			{
                    				write = true;
                    			}
                    		}
                    		str.append(" ");
                    	}
                    	System.out.println("SERVER RESPONDS: \"" + str.toString() + "\"");
            		}
            	}
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
  //This compares a substring based on start and end characters to see what kind of input it is - is it the last packet? etc.
  		public static boolean compareSubstring(String s, String wantedString, int start)
  		{
  			int end = wantedString.length() + start -1;
  			StringBuilder str = new StringBuilder();
  			for(int a=start; a<= end;a++)
  			{
  				str.append(s.charAt(a));
  			}
  			if(wantedString.equals(str.toString()))
  			{
  				return true;
  			}
  			return false;
  		}
}
