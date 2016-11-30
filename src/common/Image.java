package common;

public class Image {
	private long timestamp;
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
}
