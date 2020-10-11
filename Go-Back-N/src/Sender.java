import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class Sender {

	public static final int WINDOW_SIZE = 10;
	public static final int TIMER = 30;
	public static int lastSeq = 1000;

	public static void main(String[] args) throws Exception {

		int lastSent = 0;
		int waitingForAck = 0;

		System.out.println("Number of packets to send: " + lastSeq);

		DatagramSocket toReceiver = new DatagramSocket();
		InetAddress receiverAddress = InetAddress.getByName("localhost");
		ArrayList<Packet> sent = new ArrayList<Packet>();

		while (true) {
			while (lastSent - waitingForAck < WINDOW_SIZE && lastSent < lastSeq) {

				byte[] filePacketBytes = "ABCD".getBytes();
				long sendTime = System.currentTimeMillis();

				Packet packetObject = new Packet(lastSent, filePacketBytes, sendTime, (lastSent == lastSeq - 1) ? true : false);
				byte[] sendData = Serializer.toBytes(packetObject);

				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, 13579);

				System.out.println("Sending packet with sequence number " + lastSent);

				sent.add(packetObject);

				toReceiver.send(packet);

				lastSent++;

			}

			byte[] ackBytes = new byte[100];
			DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length);
			
			try {
				toReceiver.receive(ack);
				
				Ack ackObject = (Ack) Serializer.toObject(ack.getData());
				
				System.out.println("Received ACK for " + ackObject.getWaitingFor());

				long endTime = System.currentTimeMillis();
				System.out.println("elapsed time : " + (endTime - ackObject.getSendTime()));
				
				if(ackObject.getWaitingFor() == lastSeq){
					break;
				}
				
				waitingForAck = Math.max(waitingForAck, ackObject.getWaitingFor());

				toReceiver.setSoTimeout(TIMER);
				
			} catch (SocketTimeoutException e) {
				for (int i = waitingForAck; i < lastSent; i++) {
					
					byte[] sendData = Serializer.toBytes(sent.get(i));

					DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, 13579);
					
					toReceiver.send(packet);

					System.out.println("Resending packet with sequence number " + sent.get(i).getSeq());
				}
			}
			System.out.println();
		
		}
		
		System.out.println("Finished transmission");

	}

}
