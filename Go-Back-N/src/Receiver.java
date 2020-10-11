import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;


public class Receiver {
	
	public static final double PROBABILITY = 0.1;

	public static void main(String[] args) throws Exception {
		
		DatagramSocket fromSender = new DatagramSocket(13579);
		byte[] receivedData = new byte[200];
		int waitingFor = 0;
		ArrayList<Packet> received = new ArrayList<Packet>();
		boolean end = false;

		while (!end) {
			DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			fromSender.receive(receivedPacket);
			Packet packet = (Packet) Serializer.toObject(receivedPacket.getData());

			if (Math.random() > PROBABILITY) {
				System.out.println("Packet with sequence number " + packet.getSeq() + " received");

				if (packet.getSeq() == waitingFor && packet.isLast()) {

					waitingFor++;
					received.add(packet);

					System.out.println("###### Last packet received #####");

					end = true;

				} else if (packet.getSeq() == waitingFor){
					waitingFor++;
					received.add(packet);
					System.out.println("########## Success ##########");
				} else {
					System.out.println("Waiting for " + waitingFor + ", But " + packet.getSeq() + " received (disorder)");
				}

				Ack ackObject = new Ack(waitingFor, packet.getSendTime());

				byte[] ackBytes = Serializer.toBytes(ackObject);

				DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(), receivedPacket.getPort());

				fromSender.send(ackPacket);

				System.out.println("Sending ACK for seq " + waitingFor + "");
				System.out.println("Received Complete : " + (waitingFor - 1));

			} else {
				System.out.println("XXXXXXXX " + packet.getSeq() + " packet loss XXXXXXX");
			}

			System.out.println();
		}
		System.out.println("Finished transmission");
	}
	
}
