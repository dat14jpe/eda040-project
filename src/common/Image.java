package common;

public class Image {
	private long timestamp;
	private int cameraId;
	private byte[] data;
	
	public Image(long t, int id, byte[] d) {
		timestamp = t;
		cameraId = id;
		data = d;
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
}
