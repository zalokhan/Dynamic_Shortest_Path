import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.*;

public class peer_dv implements Runnable
{
	public static Double infinity = 99999.99;
	public static String hostip;
	public static int serverport = 7735;
	public static int totalnodes;
	public static String path;
	public static Double [][] table;
	public static String [] neighbor;	//Neighbor's ip address
	public static boolean visit;
	public static String [] ip;
	public static String [] adjip;
	public static Double [] adjtable;
	public static String neighborip;
	public static String nexthop[];
	
	public static DatagramSocket serversocket;
	
	public static void displayarray (Double [] array)
	{
		for(int i = 0; i<array.length; i++)
		{
			System.out.println(array[i]);
		}
		System.out.println();
	}
	public static void displayarray (boolean [] array)
	{
		for(int i = 0; i<array.length; i++)
		{
			System.out.println(array[i]);
		}
		System.out.println();
	}
	public static void displayarray (String [] array)
	{
		for(int i = 0; i<array.length; i++)
		{
			System.out.println(array[i]);
		}
		System.out.println();
	}
	public static void displayarray (byte [] array)
	{
		for(int i = 0; i<array.length; i++)
		{
			System.out.println(array[i]);
		}
		System.out.println();
	}
	public static void displaytable ()
	{
		System.out.println("==========TABLE============");
		for(int i = 0; i<totalnodes; i++)
		{
			System.out.println(ip[i]);
			for(int j=0; j<totalnodes; j++)
			{
				System.out.println(table[i][j]);
			}
			System.out.println("---------------------------");
		}
		System.out.println("============================");
	}
	
	public static void decodefile () throws Exception
	{
		String inputfile = new String(readAllBytes(get(path)));
		String[] split = inputfile.split("\n");
		totalnodes = Integer.parseInt(split[0].trim());
		ip = new String[totalnodes];
		neighbor = new String[split.length-1];
		nexthop = new String[totalnodes];
		
		String[] address =InetAddress.getLocalHost().toString().split("/");
		hostip = address[1];
		visit = false;
		
		table = new Double[totalnodes][totalnodes];
		
		//initializing table
		for(int i=0; i<totalnodes; i++)
		{
			ip[i]="0";
			for(int j=0; j<totalnodes; j++)
			{
				table[i][j]=infinity;
			}
		}

		ip[0]=hostip;
		for(int i=0; i<totalnodes; i++)
		{
			nexthop[i]=hostip;
		}
		
		for (int i=1; i<split.length; i++)
		{
			String[] row = split[i].split(";");
			ip[i] = row[0];
			table[0][i]=Double.parseDouble(row[1]);
			neighbor[i-1] = row[0];
			nexthop[i]=row[0];
		}

		table[0][0] = 0.0;
	}
	
	public static void nexthop()
	{
			for(int j=1; j<totalnodes; j++)
			{
				String x=nexthop[j];
				String z=nexthop[j];
				int pointer = 999;
				
				while(!(z.equals(hostip)))
				{
					for(int i=1; i<totalnodes; i++)
					{
						if(ip[i].equals(x))
						{
							pointer =i;
							break;
						}
					}
					x=z;
					z=nexthop[pointer];
					if(z.equals(x))
					{
						break;
					}
				}
				nexthop[j]=x;
			}
	}
	
	public static void update()
	{
		int position=infinity.intValue();
		
		//Determine position
		for(int i=0; i<ip.length; i++)
		{
			String s = ip[i];
			if(s.equals(neighborip))
			{
				position = i;
				break;
			}
		}
		
		
		//Check for new ip
		loop:
		for(int i=0; i<adjip.length; i++)
		{
			String s1 = adjip[i];
			String s2;
			for(int j=0; j<ip.length; j++)
			{
				s2= ip[j];
				if(s1.equals(s2))
				{
					continue loop;
				}
			}
			//adding new ip
			for(int j=0;j<ip.length;j++)
			{
				String s3 = ip[j];
				if(s3.equals("0"))
				{	
					ip[j]=s1;					
				}
			}				
		}
		
		//adding entry 
		for(int i=0; i<adjip.length; i++)
		{
			String s1 = adjip[i];
			String s2;
			for(int j=0; j<ip.length; j++)
			{
				s2 = ip[j];
				if(s1.equals(s2))
				{					
					table[position][j] = adjtable[i];
				}
			}			
		}
		
		//MAIN UPDATING
		for (int i=0; i<totalnodes; i++)
		{
			for (int j=0; j<totalnodes; j++)
			{
				Double x = table[0][i];	//pre-stored value of x to z
				Double y = table[j][i];	//from y to z
				Double z = table[0][j];	//from x to y
				if(x>(y+z))
				{
					//change pre-stored value
					table[0][i] = y+z;
					//changing flag
					visit=false;
					nexthop[i]=ip[j];
				}
			}
		}	
	}
	
	public void run()
	{
		try
		{
			while(true)
			{

				adjtable = new Double[totalnodes];
				adjip = new String[totalnodes];
				//Clearing all adjacent tables
				for(int i=0; i<adjtable.length; i++)
				{
					adjtable[i]=infinity;
				}
				for(int i=0; i<adjip.length; i++)
				{
					adjip[i]="none";
				}
				
				//Receiving tables
				byte buffer[]=new byte[50000];
				DatagramPacket clientsocket=new DatagramPacket(buffer,buffer.length);
				serversocket.setSoTimeout(30000);
				serversocket.receive(clientsocket);
				
				String data=new String(buffer);
				data = data.trim();
				neighborip=(clientsocket.getAddress()).toString();
				neighborip =neighborip.substring(1, neighborip.length());
				 
				//Decoding tables
				String [] split = data.split("\n");
				String [] row1 = (split[0].trim()).split(";");	//ip tables
				String [] row2 = (split[1].trim()).split(";");	//weight tables
				
				for(int i=0; i<row1.length; i++)
				{
					String s = row1[i];
					adjip[i]=s;
					Double d = Double.parseDouble(row2[i]);
					adjtable[i]=d;
				}
				update();
			}
		}
		catch(Exception e)
		{
			nexthop();
			System.out.println("The tables have converged");
			System.out.println("\n");
			System.out.println("===========================================");
			System.out.print("node\t");
			System.out.print("cost\t");
			System.out.println("next hop");
			System.out.println("-------------------------------------------");
			for(int i=0; i<totalnodes; i++)
			{
				System.out.print(ip[i]+"\t");
				System.out.print(table[0][i]+"\t");
				System.out.println(nexthop[i]);
			}
			System.out.println("===========================================");
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
		serversocket = new DatagramSocket(serverport);
		while(true)
		{
			System.out.println("Enter network file path...");
			path = br.readLine();
			boolean b = new File(path).exists();
			if(b==true)
			{
				break;
			}
			else
			{
				System.out.println("Invalid directory name...\nPlease try again !");
			}
		}
		
		decodefile();
		
		peer_dv p = new peer_dv();
		Thread t = new Thread (p);
		t.start();
				
		Thread.sleep(10000);
		
		while (true)
		{
			if(visit == false)
			{
				//FORMATION OF DATA
				String data = ip[0]+";";
				for(int i=1; i< totalnodes; i++)
				{
					data = data+ip[i];
					data = data+";";
				}
				data = data.substring(0, data.length()-1);
				data = data+"\n";
				
				for(int i=0; i< totalnodes; i++)
				{
					data = data+table[0][i];
					data = data+";";
				}
				data = data.substring(0, data.length()-1);
				
				byte[] buffer = new byte[1024];
				buffer=data.getBytes();
				
				for(int i=0; i<neighbor.length; i++)
				{
					DatagramPacket udpsend=new DatagramPacket(buffer,buffer.length,InetAddress.getByName(neighbor[i]),7735);
					serversocket.send(udpsend);
				}
				visit=true;
			}
		}
	}
}