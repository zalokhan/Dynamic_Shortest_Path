import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import java.util.*;

public class distance_vector 
{
	public static Double infinity=99999.99;
	public static String path;
	public static String file;
	public static LinkedList <LinkedList<Double>> network = new LinkedList <LinkedList <Double>>();
	public static Double [][][] table;
	public static boolean neighbor [][];
	public static Double[] node1;
	public static Double[] node2;
	public static boolean[] visit;
	public static Double[][] nexthop;
	public static int totalnodes;
	public static int iterations;
	public static boolean flag;
	
	public static void decodefile () throws Exception
	{
		String inputfile = new String(readAllBytes(get(path)));
		String[] split = inputfile.split("\n");
		totalnodes = Integer.parseInt(split[0].trim());
		for (int i=1; i<split.length; i++)
		{
			String[] row = split[i].split(" ");
			LinkedList <Double> list1 = new LinkedList <Double>();
			list1.add(Double.parseDouble(row[0].trim()));
			list1.add(Double.parseDouble(row[1].trim()));
			list1.add(Double.parseDouble(row[2].trim()));
			network.add(list1);
			LinkedList <Double> list2 = new LinkedList <Double>();
			list2.add(Double.parseDouble(row[1].trim()));
			list2.add(Double.parseDouble(row[0].trim()));
			list2.add(Double.parseDouble(row[2].trim()));
			network.add(list2);
			
		}

		node1 = new Double[totalnodes];
		for(int j=0; j<node1.length; j++)
		{
			node1[j]=infinity;
		}
		node2 = new Double[totalnodes];
		for(int j=0; j<node2.length; j++)
		{
			node2[j]=infinity;
		}
		visit = new boolean[totalnodes];
		nexthop = new Double[totalnodes][totalnodes];
		table = new Double[totalnodes][totalnodes][totalnodes];
		neighbor = new boolean[totalnodes][totalnodes];
		for(int j=0; j<totalnodes; j++)
		{
			for (int i=0; i<totalnodes; i++)
			{
				neighbor[j][i]=false;
			}
		}
	}
	
	public static void displayarray (Double [] array, int node)
	{
		System.out.println("===================================");
		System.out.print("node\t");
		System.out.print("cost\t");
		System.out.println("next hop\t");
		System.out.println("-----------------------------------");
		for(int i = 0; i<array.length; i++)
		{
			System.out.print(i+1+"\t");
			System.out.print(array[i]+"\t");
			System.out.println(nexthop[node][i].intValue());
		}
		System.out.println("===================================");
		System.out.println();
	}
	
	public static void nexthop()
	{
		for(int i=0; i<totalnodes; i++)
		{
			for(int j=0; j<totalnodes; j++)
			{
				nexthop[i][j]++;
				
			}
		}
		for(int i=0; i<totalnodes;i++)
		{
			for(int j=0; j<totalnodes; j++)
			{
				double x=nexthop[i][j];
				while(nexthop[i][(int) x-1]!=(i+1))
				{
					x=nexthop[i][(int) (x-1)];
					if(nexthop[i][(int) (x-1)]==(x))
					{
						break;
					}
				}
				nexthop[i][j]=x;
			}
		}
	}
	
	public static void initialnode(int initial)
	{
		iterations ++;
		
		for(int j=0; j<totalnodes; j++)
		{
			boolean b = neighbor[initial][j];
			if(b==true)
			{
				table[j][initial]=table[initial][initial].clone();
			}
		}
		
		//flipping all flags
				for(int i=0; i<totalnodes; i++)
				{
					visit[i] = true;
				}
				
				//Updating tables
				for(int i=0; i<totalnodes; i++)
				{
					for(int j=0; j<totalnodes; j++)
					{
						for(int k=0; k<totalnodes; k++)
						{
							Double x = table[i][i][j];	//pre-stored value of x to z
							Double y = table[i][k][j];	//from y to z
							Double z = table[i][i][k];	//from x to y
							if(x>(y+z))
							{
								//change pre-stored value
								table[i][i][j] = y+z;
								//changing flag
								visit[i]=false;
							}
						}
					}
				}
				
				//Check if tables converged
				for(int i=0;i<visit.length; i++)
				{
					if(visit[i]==false)
					{
						return;
					}
				}
				
				
				
		
		//Exchanging tables
		for(int i = 0; i<totalnodes; i++)
		{
			if(i==(initial))
			{
				continue;
			}
				for(int j=0; j<totalnodes; j++)
				{
					boolean b = neighbor[i][j];
					if(b==true)
					{
						table[j][i]=table[i][i].clone();
					}
				}
		}
		
		
		//flipping all flags
		for(int i=0; i<totalnodes; i++)
		{
			visit[i] = true;
		}
		
		//Updating tables
		for(int i=0; i<totalnodes; i++)
		{
			for(int j=0; j<totalnodes; j++)
			{
				for(int k=0; k<totalnodes; k++)
				{
					Double x = table[i][i][j];	//pre-stored value of x to z
					Double y = table[i][k][j];	//from y to z
					Double z = table[i][i][k];	//from x to y
					if(x>(y+z))
					{
						//change pre-stored value
						table[i][i][j] = y+z;
						//changing flag
						visit[i]=false;
						if(neighbor[i][k]==true)
						{
							nexthop[i][j]=(double) (k);
						}
					}
				}
			}
		}
		
		//Check if tables converged
		for(int i=0;i<visit.length; i++)
		{
			if(visit[i]==false)
			{
				return;
			}
		}
	}
	
	public static void bellmanford(int initial)
	{
		
		//initializing the table
		for(int k=0; k<totalnodes; k++)
		{
			Double [][] t = new Double [totalnodes][totalnodes];
			for(int i=0; i<totalnodes; i++)
			{
				for(int j=0; j<totalnodes; j++)
				{
					t [i][j] = infinity;
				}
			}
			table[k] = t.clone();
		}
		for(int i=0;i<totalnodes;i++)
		{
			for(int j=0; j<network.size(); j++)
			{
				Double x = network.get(j).get(0);
				Double y = network.get(j).get(1);
				Double z = network.get(j).get(2);
				if(x==(i+1))
				{
					table[i][i][(int) (y-1)]=z;
					neighbor[i][(int) (y-1)]=true;
				}
			}
		}
		
		//initializing next hop
		for(int i=0; i<totalnodes; i++)
		{
			for (int j=0; j<totalnodes; j++)
			{
				if(neighbor[i][j]==true)
				{
					nexthop[i][j] = (double) j;
				}
				else
				{
					nexthop[i][j] = (double) i;
				}
			}
		}
		
		for(int i=0; i<totalnodes; i++)
		{
			table[i][i][i] = 0.0;
		}
		
		flag =false;
		initialnode(initial);
		loop:
		while (flag==false)
		{
			iterations ++;
			//Exchanging tables
			for(int i = 0; i<totalnodes; i++)
			{
				if(visit[i]==false)
				{
					for(int j=0; j<totalnodes; j++)
					{
						boolean b = neighbor[i][j];
						if(b==true)
						{
							table[j][i]=table[i][i].clone();
						}
					}
				}
			}
			
			
			//flipping all flags
			for(int i=0; i<totalnodes; i++)
			{
				visit[i] = true;
			}
			
			//Updating tables
			for(int i=0; i<totalnodes; i++)
			{
				for(int j=0; j<totalnodes; j++)
				{
					for(int k=0; k<totalnodes; k++)
					{
						Double x = table[i][i][j];	//pre-stored value of x to z
						Double y = table[i][k][j];	//from y to z
						Double z = table[i][i][k];	//from x to y
						if(x>(y+z))
						{
							//change pre-stored value
							table[i][i][j] = y+z;
							//changing flag
							visit[i]=false;
							nexthop[i][j]=(double) (k);
						}
					}
				}
			}
			
			//Check if tables converged
			for(int i=0;i<visit.length; i++)
			{
				if(visit[i]==false)
				{
					continue loop;
				}
			}
			flag=true;
		}
	}
	
	public static void main(String[] args) throws Exception 
	{
		iterations = 0;
		/*
		BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
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
		*/
		
		int initial = Integer.parseInt(args[0]);
		path = args[1];
		int n1 = Integer.parseInt(args[2]);
		int n2 = Integer.parseInt(args[3]);
			
		decodefile();			
		bellmanford(initial-1);
		
		nexthop();
		
		System.out.println("Displaying routing table for node : "+n1);
		displayarray(table[n1-1][n1-1],n1-1);
		System.out.println("Displaying routing table for node : "+n2);
		displayarray(table[n2-1][n2-1],n2-1);
		
		System.out.println("Least cost path from "+n1+" to "+n2+" has cost = "+table[n2-1][n2-1][n1-1]);
		System.out.println("Total number of iterations for tables to converge = "+iterations);
		
		
	}
}
//C:\Users\Zeeshan\Google Drive\Fall 2014\ECE 573\project 3\small-net.txt