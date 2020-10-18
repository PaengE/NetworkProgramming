import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;


public class Receiver {

	// packet loss rate
	public static final double PROBABILITY = 0.1;

	public static void main(String[] args) throws Exception {
		
		DatagramSocket fromSender = new DatagramSocket(13579);
		byte[] receivedData = new byte[200];
		int waitingFor = 0;
		ArrayList<Packet> received = new ArrayList<Packet>();
		PriorityQueue<Packet> buffer = new PriorityQueue<Packet>(new Comparator<Packet>() {
			@Override
			public int compare(Packet o1, Packet o2) {
				return o1.getSeq() - o2.getSeq();
			}
		});
		boolean end = false;
		boolean isLast = false;

		while (!end) {
			// sender 로부터 packet 받는 부분, 바이트스트림을 패킷(object) 으로 변환
			DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			fromSender.receive(receivedPacket);
			Packet packet = (Packet) Serializer.toObject(receivedPacket.getData());

			// 1 - PROBABILITY 확률로 packet 을 받음
			if (Math.random() > PROBABILITY) {
				System.out.println("Packet with sequence number " + packet.getSeq() + " received");

				// 마지막 window 에서 loss가 없을 경우
				if (buffer.isEmpty() && packet.isLast()) {

					waitingFor++;
					received.add(packet);

					System.out.println("###### Last packet received #####");

					end = true;

				}
				// 기다리고 있는 packet 이 올 경우
				else if (packet.getSeq() == waitingFor){
					waitingFor++;
					received.add(packet);
					System.out.println("########## Success ##########");

					int tmp = 0;
					int bufSize = buffer.size();
					int seq = packet.getSeq() + 1;
					// 버퍼가 비어 있지 않으면 reassemble
					while (!buffer.isEmpty()) {
						if (tmp == 0) {
							System.out.print("Reassemble packet: ");
						}

						// 중간에 또 loss 가 존재하면 loss 바로 전 패킷까지만 재조립
						if (seq + tmp == buffer.peek().getSeq()) {
							System.out.print(buffer.peek().getSeq() + " ");

							waitingFor = buffer.peek().getSeq() + 1;
							isLast = buffer.peek().isLast();

							received.add(buffer.poll());

							tmp++;
							if (tmp == bufSize) {
								System.out.println();
							}
						} else {
							System.out.println();
							break;
						}
					}
				}
				// 기다리고 있는 패킷이 아닌 경우 버퍼에 저장함
				else {
					buffer.add(packet);
					System.out.println("Waiting for " + waitingFor + ", But " + packet.getSeq() + " received (buffered)");
				}

				
				// ACK 보내는 부분
				
				Ack ackObject = new Ack(waitingFor, packet.getSeq(), packet.getSendTime());

				byte[] ackBytes = Serializer.toBytes(ackObject);

				DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(), receivedPacket.getPort());

				fromSender.send(ackPacket);

				System.out.println("Sending ACK for " + packet.getSeq() + "");

				// 마지막 window 에서 loss 가 있을 경우의 종료 조건
				if (isLast) {
					end = true;
				}

			} else {	// PROBABILITY 확률로 packet 을 받지 않음
				System.out.println("XXXXXXXX " + packet.getSeq() + " packet loss XXXXXXX");
			}

			System.out.println();
		}
		System.out.println("Finished transmission");
	}
	
}
