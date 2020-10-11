import java.io.Serializable;


public class Ack implements Serializable{
	
	private int waitingFor;
	private long sendTime;

	public Ack(int waitingFor, long sendTime) {
		super();
		this.waitingFor = waitingFor;
		this.sendTime = sendTime;
	}

	public int getWaitingFor() {
		return waitingFor;
	}

	public void setWaitingFor(int waitingFor) {
		this.waitingFor = waitingFor;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

}
