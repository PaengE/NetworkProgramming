import java.io.Serializable;

// ACK class
public class Ack implements Serializable{
	
	private int waitingFor;
	private int sequence;
	private long sendTime;

	public Ack(int waitingFor, int sequence, long sendTime) {
		super();
		this.waitingFor = waitingFor;
		this.sequence = sequence;
		this.sendTime = sendTime;
	}

	public int getWaitingFor() {
		return waitingFor;
	}

	public void setWaitingFor(int waitingFor) {
		this.waitingFor = waitingFor;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

}
