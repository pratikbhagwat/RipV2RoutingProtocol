import java.io.Serializable;

public class HopMetricSubnetTuple implements Serializable {
    public String nextHop;
    public int metric;
    public String subnet;

    /**
     *
     * @param nextHop: the next hop ip address (router ip address)
     * @param metric : cost to reach the network
     * @param subnet : subnet of
     */
    public HopMetricSubnetTuple(String nextHop, int metric, String subnet){
        this.nextHop = nextHop;
        this.metric=metric;
        this.subnet = subnet;
    }

    @Override
    public String toString() {
        return "HopMetricSubnetPair{" +
                "nextHop='" + nextHop + '\'' +
                ", metric=" + metric +
                ", subnet='" + subnet + '\'' +
                '}';
    }
}
