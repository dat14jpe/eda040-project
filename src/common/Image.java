package common;

public class Image {
	private long timestamp, clientTime;
	private int cameraId;
	private byte[] data;
	private boolean motion;
	
	public Image(long t, int id, byte[] d, boolean m) {
		timestamp = t;
		cameraId = id;
		data = d;
		motion = m;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public int getCameraId() {
		return cameraId;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public boolean getMotion() {
		return motion;
	}
	
	public long getClientTime() {
	    return clientTime;
	}
	
	public void setClientTime(long clientTime) {
	    this.clientTime = clientTime;
	}
}
