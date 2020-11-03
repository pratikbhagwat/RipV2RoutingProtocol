Name: Pratik Bhagwat
RIT ID: PB1606

note: each rover will take 1 argument as rover number and it will have exactly one internal network inside it of ip address 10.10.<rovernumber>.0/24.
Running instructions.

1. Build the docker using.  command: `docker build -t javaapptest .`
2. Create the node network. command: `docker network create --subnet=172.18.0.0/16 nodenet `
3. Run the node-1 by specifying the ip address from the network created. command: `docker run -it -p 8080:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.21 javaapptest 1 `
4 similarly you can run multiple nodes with multiple ip address and different ports eg : `docker run -it -p 8081:8080 --cap-add=NET_ADMIN --net nodenet --ip 172.18.0.22 javaapptest 2 `


5. to block a connection between nodes
`curl "http://localhost:8080/?block=172.18.0.22" ` this will block the connection from node 1 to node 2.
similarly `curl "http://localhost:8081/?block=172.18.0.21" ` this will block the connection from node 2 to node 1.

6. Unblocking the node
`curl "http://localhost:8080/?unblock=172.18.0.22" ` this will unblock the connection from node 1 to node 2


you can play around by creating new nodes and blocking and unblocking the connections to see how the RIPv2 works.



