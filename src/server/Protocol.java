package server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import common.Image;

public class Protocol {
    public static void writePacket(OutputStream out, int mode, long timestamp, Image image) throws IOException {
        int packetType = Monitor.PACKET_S2C;
        out.write(packetType);
        out.write(mode);
        final byte[] buffer = new byte[8];

        ByteBuffer.wrap(buffer).putLong(timestamp);
        out.write(buffer, 0, 8);

        byte[] imageData = image.getData();
        int dataLength = imageData.length;
        ByteBuffer.wrap(buffer).putInt(dataLength);
        out.write(buffer, 0, 4);
        
        //System.out.println("Sent " + imageData.length + " image bytes.");

        out.write(imageData, 0, dataLength);
    }
}
