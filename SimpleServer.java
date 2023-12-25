import java.net.*;
import java.util.Random;
import java.io.*;
import java.util.*;
public class SimpleServer {
	
	public static void main(String[] args) throws IOException {

		// Hard code in port number if necessary:
		args = new String[] { "30121" };
		
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);

		try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				Socket clientSocket1 = serverSocket.accept();
				PrintWriter responseWriter1 = new PrintWriter(clientSocket1.getOutputStream(), true);
				BufferedReader requestReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
				) {
			String usersRequest;
			Random rand = new Random();
			String[] packets = new String[0];
			ArrayList<String> droppedPackages = new ArrayList<>();
			while ((usersRequest = requestReader1.readLine()) != null) {
			if(usersRequest.charAt(0) == '~') 
			{
					usersRequest = usersRequest.substring(1);
					if(usersRequest.length() > 0)
					{
						packets = usersRequest.split(" ");
						System.out.println("\"" + usersRequest + "\" received");
						HashSet<Integer> s = new HashSet<>();
						for(int i=0;i<packets.length;i++)
						{
							int index = randomIndexPicker(s, packets.length); 
							String response = "hrhgig#$@#" + index + "vjdfjfiejro--$$@" + packets[index].length() + "xbah*&bdj~" + packets[index];
							double random = rand.nextDouble();
							if(random > .2)
							{
								responseWriter1.println(response);
								System.out.println("sending" + response);
							}
						}
						responseWriter1.println("end" + packets.length);//Signals that all packages have been send + gives the amount of packages that should have been sent, so client knows if there are missing
					
					}else
					{
						responseWriter1.println("end0");
					}
				} else if(usersRequest.length() >= 7)//Specifically input that's requesting lost packages from the server - not new input. Signified by beginning with "y~%%#"
				{
					if(compareSubstring(usersRequest, "y~%%#")) 
					{
						System.out.println("\"" + usersRequest + "\" received");
						droppedPackages = new ArrayList<>();
						int count = 0;
						char c;
						int a;
						for(int i=5;i<usersRequest.length()-1;i+=2)
						{
							StringBuilder str = new StringBuilder();
							while(Character.isDigit(usersRequest.charAt(i)))
							{
								str.append(usersRequest.charAt(i));
								i++;
							}
							i--;
							a = Integer.parseInt(str.toString());
							String response = "hrhgig#$@#" + a + "vjdfjfiejro--$$@" + packets[a].length() + "xbah*&bdj~" + packets[a];
							droppedPackages.add(response);
						}
						HashSet<Integer> usedIndex = new HashSet<>();
						for(int i=0;i<droppedPackages.size();i++)
						{
							int index = randomIndexPicker(usedIndex, droppedPackages.size());
							double random = rand.nextDouble();
							if(random > .2)
							{
								responseWriter1.println(droppedPackages.get(index));
							}
						}
						responseWriter1.println("end" + packets.length);//Signals that all packages have been send + gives the amount of packages that should have been sent, so client knows if there are missing
					
					}
				}
				
			}
		} catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}

	}
	//This is just a random number picker to pick the next packet to send. It's enough code that I'd prefer to make the program modular
		public static int randomIndexPicker(HashSet<Integer> s, int max)
		{
			Random rand = new Random();
			int num = rand.nextInt(max);
			while(s.contains(num))
			{
				num = rand.nextInt(max);
			}
			s.add(num);
			return num;
		}
	//This compares a substring based on start and end characters to see what kind of user input it is
		public static boolean compareSubstring(String s, String wantedString)
		{
			int end = wantedString.length()-1;
			StringBuilder str = new StringBuilder();
			for(int a=0; a<= end;a++)
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
