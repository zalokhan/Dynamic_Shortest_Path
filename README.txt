Instructions for compiling and running the three programs. (Windows Command Prompt)

Install jdk 1.7.0. 
Enter new system variable path with the path of the bin folder of "jdk 1.7.0".

LINK STATE

1. Complile the file link_state.java using the command
	javac link_state.java

2. Now you have to run the program as well as enter the required data as follows
	java   link_state  FileName   SourceNode   DestinationNode

NOTE : The FileName should include the entire path of the file too. Make sure that there are no spaces in the file path.

3. The output will be displayed with the routing tables for both source and destination nodes which include the next hop node and least cost to destination.

DISTANCE VECTOR

1. Complile the file distance_vector.java using the command
	javac distance_vector.java

2. Now you have to run the program as well as enter the required data as follows
	java   distance_vector  FileName   Initial Node SourceNode   DestinationNode

NOTE : The FileName should include the entire path of the file too. Make sure that there are no spaces in the file path.

3. The output will be displayed with the routing tables for both source and destination nodes which include the next hop node and least cost to destination, as well as the number of iterations required for the tables to converge.

P2P Distance vector

1. First note down IP addresses of all four nodes in the network.

2. Create a configuration file at each node which includes IP addresses of only its neighbouring nodes and the cost to those nodes. The format of the file should be as quoted below:
    "  
       4 (Number of nodes)
       IP address;Cost
       152.46.17.96;1.0
       152.7.99.89;6.0
    "

3. Complile the file distance_vector.java using the command
	javac peer_dv.java

4. Now you have to run the program as well as enter the required data as follows
	java   peer_dv

5. Now it will promt you to enter the source path of configuration file.

6. After a certain time, the tables will converge and the output will be displayed.
