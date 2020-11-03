import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class UdpMulticastClient implements Runnable {

    public String selfIPAddress;
    public int port = 63001; // port to listen on
    public String broadcastAddress; // multicast address to listen on
    public DistanceVector distanceVectorToBeUpdated;
    public HashMap<String,Long> routeAndTimerMap = new HashMap<>();

    /**
     *
     * @param thePort : port address for multicast sender
     * @param broadcastIp : multicast ip
     * @param selfIPAddress : internal ipv4 address
     * @param distanceVector : distance vector to be broadcasted
     */
    public UdpMulticastClient(int thePort, String broadcastIp,DistanceVector distanceVector,String selfIPAddress )
    {
        port = thePort;
        broadcastAddress = broadcastIp;
        this.distanceVectorToBeUpdated = distanceVector;
        this.selfIPAddress = selfIPAddress;
    }



    /**
     * listens to the ipaddress and reports when a message arrived
     * @throws IOException
     */
    public void receiveUDPMessage() throws
            IOException {
        byte[] buffer=new byte[1024];

        // create and initialize the socket
        MulticastSocket socket=new MulticastSocket(port);
        InetAddress group=InetAddress.getByName(broadcastAddress);
        socket.joinGroup(group);


        while(true){
            try {
                DatagramPacket packet=new DatagramPacket(buffer,buffer.length);

                // blocking call.... waits for next packet
                socket.receive(packet);

                if (HandleReceivedPacket.doTheJob(packet,this.distanceVectorToBeUpdated,this.routeAndTimerMap)){// handling the recieved packet. and if the internal routing table changes
                    printTheRoutingTable(distanceVectorToBeUpdated);
                }

                if (checkLinkAliveStatusAndUpdateTheRoutingTable()){
                    printTheRoutingTable(distanceVectorToBeUpdated);
                }// check whether all the links are alive and update the routing table. if there are any changes in routing table then print the routing table.

            }catch(IOException | ClassNotFoundException ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     *
     * @param distanceVectorToBeUpdated : the rovers distance vector object
     */
    private void printTheRoutingTable(DistanceVector distanceVectorToBeUpdated) {
        System.out.println("Address----------------"+"Next Hop--------" + "Cost");
        System.out.println("====================================================");
        System.out.println(distanceVectorToBeUpdated);
        System.out.println();
    }

    /**
     * checking the alive status of all the neighboring links and updating the cost of the link to 16 if the rover does not respond before 10 seconds.
     * @return : whether the internal routing table was updated or not
     */
    private boolean checkLinkAliveStatusAndUpdateTheRoutingTable() {
        boolean tableChanged = false;
        ArrayList<String> keysToBeRemovedFromrouteAndTimerMap = new ArrayList<>();
        for (Map.Entry<String,Long> entry:this.routeAndTimerMap.entrySet()){
            if (System.currentTimeMillis() - entry.getValue()>10000){// 10 seconds check

                for (Map.Entry<String, HopMetricSubnetTuple> routingTableEntry : this.distanceVectorToBeUpdated.routingTable.entrySet()){
                    if (routingTableEntry.getValue().nextHop.equals( entry.getKey() )){
                        if (routingTableEntry.getValue().metric != 16){
                            routingTableEntry.getValue().metric = 16;
                            tableChanged = true;
                        }
                    }
                }
                keysToBeRemovedFromrouteAndTimerMap.add(entry.getKey());
            }
        }
        for (String keyToBeRemoved : keysToBeRemovedFromrouteAndTimerMap){
            this.routeAndTimerMap.remove(keyToBeRemoved);
        }
        return tableChanged;
    }

    // the thread runnable.  just starts listening.
    @Override
    public void run(){
        try {
            receiveUDPMessage();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}