import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DistanceVector implements Serializable {

    public HashMap<String, HopMetricSubnetTuple> routingTable = new HashMap<>();

    /**
     *
     * @param nodeNum : the rover number. This is required, just to set up the internal network.
     * @param subnet : subnet of the ip address
     */
    public DistanceVector(int nodeNum,String subnet){
        routingTable.put("10.10." + nodeNum + ".0",new HopMetricSubnetTuple("10.10." + nodeNum + ".0",1,subnet)); // this network can directly be reached as it is an internal network inside the rover
    }

    @Override
    public String toString() {
        String returningString = "";
        for (Map.Entry<String, HopMetricSubnetTuple> entry:this.routingTable.entrySet()){
            returningString += entry.getKey()+"/"+entry.getValue().subnet+"        "+entry.getValue().nextHop + "        "+entry.getValue().metric + "\n";
        }
        return returningString;
    }
}
