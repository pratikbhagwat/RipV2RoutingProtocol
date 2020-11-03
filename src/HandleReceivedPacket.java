import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class HandleReceivedPacket {
    /**
     *  This method handles the recieved packet.
     * @param packet : recieved datagram packet
     * @param distanceVectorToBeUpdated : the distance vector which is to be updated
     * @param routeAndTimerMap: the ip and timer map. this map stores when the link was last active
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static boolean doTheJob(DatagramPacket packet, DistanceVector distanceVectorToBeUpdated, HashMap<String,Long> routeAndTimerMap ) throws IOException, ClassNotFoundException {
        boolean tableChanged = false;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( packet.getData() );
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        DistanceVector recievedDistanceVector = (DistanceVector)objectInputStream.readObject();
        if (packet.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())){// if we recieve our own packet.
            return false;
        }

        for (Map.Entry<String,HopMetricSubnetTuple> entry: distanceVectorToBeUpdated.routingTable.entrySet()){
            if (entry.getValue().nextHop.equals(packet.getAddress().getHostAddress())){
                if (recievedDistanceVector.routingTable.get(entry.getKey()).metric == 16){
                    if (entry.getValue().metric != recievedDistanceVector.routingTable.get(entry.getKey()).metric){
                        entry.getValue().metric = recievedDistanceVector.routingTable.get(entry.getKey()).metric;
                        tableChanged = true;
                    }
                }else {
                    if (entry.getValue().metric != recievedDistanceVector.routingTable.get(entry.getKey()).metric+1){
                        entry.getValue().metric = recievedDistanceVector.routingTable.get(entry.getKey()).metric+1;
                        tableChanged = true;
                    }
                }
            }
        }
        for (Map.Entry<String, HopMetricSubnetTuple> entry:recievedDistanceVector.routingTable.entrySet()){
            if (entry.getValue().nextHop.equals(InetAddress.getLocalHost().getHostAddress())){// split horizon (dealing with count to infinity problem)
                continue;
            }
            if (!distanceVectorToBeUpdated.routingTable.containsKey(entry.getKey())){
                distanceVectorToBeUpdated.routingTable.put(entry.getKey(),new HopMetricSubnetTuple(packet.getAddress().getHostAddress(),1+entry.getValue().metric,entry.getValue().subnet));
                tableChanged = true;
            }else {
                if (entry.getValue().metric + 1 < distanceVectorToBeUpdated.routingTable.get(entry.getKey()).metric){
                    distanceVectorToBeUpdated.routingTable.get(entry.getKey()).metric = entry.getValue().metric + 1;
                    distanceVectorToBeUpdated.routingTable.get(entry.getKey()).nextHop = packet.getAddress().getHostAddress();
                    tableChanged = true;
                }
            }
        }
        routeAndTimerMap.put(packet.getAddress().getHostAddress(),System.currentTimeMillis());// updating all the links
        return tableChanged;
    }
}
