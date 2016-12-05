package server;

import java.io.IOException;
import java.io.OutputStream;
//import java.nio.ByteBuffer;

import common.*;

public class Protocol {
    public static void writePacket(OutputStream out, int mode, long timestamp, Image image) throws IOException {
        int packetType = Constants.PACKET_S2C;
        out.write(packetType);
        out.write(mode);
        final byte[] buffer = new byte[8];

        for (int i = 1; i <= 8; ++i) {
            buffer[i - 1] = (byte)(timestamp >> (8 * (8 - i)));
        }
        //ByteBuffer.wrap(buffer).putLong(timestamp);
        out.write(buffer, 0, 8);

        byte[] imageData = image.getData();
        int dataLength = imageData.length;
        for (int i = 1; i <= 4; ++i) {
            buffer[i - 1] = (byte)(dataLength >> (8 * (4 - i)));
        }
        //ByteBuffer.wrap(buffer).putInt(dataLength);
        out.write(buffer, 0, 4);

        out.write(imageData, 0, dataLength);
    }
}
