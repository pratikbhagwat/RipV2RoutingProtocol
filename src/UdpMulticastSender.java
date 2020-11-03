import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;




public class UdpMulticastSender implements Runnable  {

    public int port = 63001; // port to send on
    public String broadcastAddress; // multicast address to send on
    public String selfIPAddress ; // the arbitrary node number of this executable
    public DistanceVector distanceVectorToBeSent;



    /**
     *
     * @param thePort : port address for multicast sender
     * @param broadcastIp : multicast ip
     * @param selfIPAddress : internal ipv4 address
     * @param distanceVector : distance vector to be broadcasted
     */
    public UdpMulticastSender(int thePort, String broadcastIp, String selfIPAddress,DistanceVector distanceVector)
    {
        port = thePort;
        broadcastAddress = broadcastIp;
        this.selfIPAddress = selfIPAddress;
        this.distanceVectorToBeSent = distanceVector;

    }

    /**
     * This method will send the internal distance vector to all its connected nodes.
     * @throws IOException
     */
    public void sendUdpMessage() throws IOException {

        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(broadcastAddress);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(distanceVectorToBeSent);
        objectOutputStream.flush();


        byte[] msg = byteArrayOutputStream.toByteArray();
        DatagramPacket packet = new DatagramPacket(msg, msg.length, group, port);

        socket.send(packet);
        socket.close();
    }


    @Override
    public void run(){
        while (true)
        {
            try {
                sendUdpMessage();
                Thread.sleep(5000);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}