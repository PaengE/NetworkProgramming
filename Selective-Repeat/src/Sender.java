import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;


public class Sender {

	// window size
	public static final int WINDOW_SIZE = 10;
	// udp timeout timer
	public static final int TIMER = 100;
	// packet count
	public static int lastSeq = 1000;

	public static void main(String[] args) throws Exception {

		int lastSent = 0;
		int waitingForAck = 0;

		System.out.println("Number of packets to send: " + lastSeq);

		DatagramSocket toReceiver = new DatagramSocket();
		InetAddress receiverAddress = InetAddress.getByName("localhost");
		ArrayList<Packet> sent = new ArrayList<Packet>();

		while (true) {

			// window size 만큼 packet 을 보냄
			while (lastSent - waitingForAck < WINDOW_SIZE && lastSent < lastSeq) {

				byte[] filePacketBytes = "ABCD".getBytes();
				long sendTime = System.currentTimeMillis();

				// packet 생성 및 ByteStream 으로 변환
				Packet packetObject = new Packet(lastSent, filePacketBytes, sendTime, (lastSent == lastSeq - 1) ? true : false);
				byte[] sendData = Serializer.toBytes(packetObject);

				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, 13579);

				System.out.println("Sending packet with sequence number " + lastSent);

				sent.add(packetObject);

				toReceiver.send(packet);

				// 마지막으로 보낸 packet sequence 증가
				lastSent++;

			}
			System.out.println();

			byte[] ackBytes = new byte[100];
			DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length);


			try {	// ACK 을 받아 처리하는 부분
				toReceiver.setSoTimeout(TIMER);

				toReceiver.receive(ack);
				
				Ack ackObject = (Ack) Serializer.toObject(ack.getData());

				System.out.println("waitingForAck = " + waitingForAck);
				System.out.println("Received ACK for " + ackObject.getSequence() + "");


				long endTime = System.currentTimeMillis();
				System.out.println("elapsed time : " + (endTime - ackObject.getSendTime()));
				
				// 종료조건
				if(ackObject.getWaitingFor() == lastSeq){
					break;
				}
				
				waitingForAck = Math.max(waitingForAck, ackObject.getWaitingFor());

			} catch (SocketTimeoutException e) {

				// loss 된 패킷을 보냄

				byte[] sendData = Serializer.toBytes(sent.get(waitingForAck));

				DatagramPacket packet = new DatagramPacket(sendData, sendData.length, receiverAddress, 13579);

				toReceiver.send(packet);

				System.out.println("Resending packet with sequence number " + sent.get(waitingForAck).getSeq());

			}

		}
		
		System.out.println("\nFinished transmission");

	}

}
