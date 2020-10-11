import java.io.Serializable;


public class Packet implements Serializable {

	public int seq;
	public byte[] data;
	public long sendTime;
	public boolean last;

	public Packet(int seq, byte[] data, long sendTime, boolean last) {
		super();
		this.seq = seq;
		this.data = data;
		this.sendTime = sendTime;
		this.last = last;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}


}
