package server;

import java.io.IOException;
import java.io.OutputStream;
import common.Image;

public class Protocol {
    public static void writePacket(OutputStream out, int mode, long timestamp, Image image) throws IOException {
        int packetType = 1; // server to client
        out.write(packetType);
        out.write(mode);
        byte[] buffer = new byte[8];

        // Write timestamp.
        for (int i = 1; i <= 8; ++i) {
            buffer[i - 1] = (byte) (timestamp >> (8 - i) * 8);
        }
        out.write(buffer, 0, 8);

        // Write data (image) length.
        byte[] imageData = image.getData();
        for (int i = 1; i <= 4; ++i) {
            buffer[i - 1] = (byte) (imageData.length >> (4 - i) * 8);
        }
        out.write(buffer, 0, 4);

        out.write(imageData);
    }
}
