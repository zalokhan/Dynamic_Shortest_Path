import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.util.*;

public class link_state 
{
	public static Double infinity=99999.99;
	public static String path;
	public static String file;
	public static LinkedList <LinkedList<Double>> network = new LinkedList <LinkedList <Double>>();
	public static Double[] node1;
	public static Double[] nodesource;
	public static boolean[] visit;
	public static Double[] nexthop;
	public static int totalnodes;
	
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
		visit = new boolean[totalnodes];
		nexthop = new Double[totalnodes];
		nodesource = new Double[totalnodes];
	}
	
	public static void displayarray (Double [] array, int n1)
	{
		System.out.println("Shortest path from "+n1);
		System.out.println("===========================================");
		System.out.print("node\t");
		System.out.print("cost\t");
		System.out.println("nexthop");
		System.out.println("-------------------------------------------");
		for(int i = 0; i<array.length; i++)
		{
			System.out.print((i+1)+"\t");
			System.out.print(array[i]+"\t");
			System.out.println(nexthop[i].intValue());
		}
		System.out.println("===========================================");
	}
	
	public static void dijkstra (Double n1)
	{
		for(int j=0; j<node1.length; j++)
		{
			node1[j]=infinity;
		}
		visit = new boolean[totalnodes];
		nexthop = new Double[totalnodes];
		
		node1[(int) (n1-1)] = (double) 0; //Setting weight to itself to 0
		visit[(int) (n1-1)] = true;
		for(int j=0; j<nexthop.length; j++)
		{
			nexthop[j]=n1;
		}
		
		//initializing visiting array
		for(int i=0; i<visit.length; i++)
		{
			visit[i] = false;
		}
		
		//adjacent nodes for n1
		for(int i=0; i<network.size(); i++)
		{
			if(network.get(i).get(0).equals(n1))
			{
				int row = (network.get(i).get(1)).intValue();
				node1[row-1]=network.get(i).get(2);
			}
		}
		
		Double pointer ;
		
		for(int i=0; i<totalnodes-1; i++)
		{
			pointer = infinity;
			Double min = infinity;
			
			for(int j=0; j<node1.length; j++)
			{
				if(node1[j]<min)
				{
					if(visit[j]==false)
					{
						pointer = (double) j;
						min = node1[j];
					}
				}
			}
			
			visit[pointer.intValue()] = true;
			
			for(int j=0; j<network.size(); j++)
			{
				Double z = network.get(j).get(0);
				if(z.equals(pointer+1))
				{
					Double x = network.get(j).get(2);	//weight
					Double y = network.get(j).get(1);	//adjacent node
					
					x = x+node1[pointer.intValue()];
					
					if(x<node1[y.intValue()-1])
					{
						node1[y.intValue()-1] = x;
						nexthop[y.intValue()-1] = pointer+1;
					}
				}
			}
		}
	}
	
	public static void nexthop(int n)
	{
		
			for(int j=0; j<nexthop.length; j++)
			{
				double x=nexthop[j];
				while(nexthop[(int) x-1]!=n)
					{
						x=nexthop[(int) (x-1)];
					}
				nexthop[j]=x;
				
			}
			for(int i=0; i<nexthop.length; i++)
			{
				if(nexthop[i]==n)
				{
					nexthop[i]=(double) i+1;
				}
			}
	}
	
	
	public static void main(String[] args) throws Exception 
	{

		long st;
		long et;
		st = System.currentTimeMillis();
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
		
		String[] input = new String [3];
		input = args;
		path = input [0];
		int n1 = Integer.parseInt(input [1]);
		int n2 = Integer.parseInt(input [2]);
			
		decodefile();	
		
		System.out.println(totalnodes);
		dijkstra((double)n1);
		
		nexthop(n1);
		
		System.out.println("Least cost paths for source node "+n1);
		displayarray(node1,n1);
		
		System.out.println();
		
		dijkstra((double)n2);
		nexthop(n2);
		System.out.println("Least cost paths for destination node "+n2);
		displayarray(node1,n2);
		
		et = System.currentTimeMillis();
		double time1 = et-st;
		System.out.println();
		System.out.println("Least cost from "+n1+" to "+n2+" is "+node1[n1-1]);
		System.out.println("Running Time for the computation is = "+time1+" milliseconds");
	}
}