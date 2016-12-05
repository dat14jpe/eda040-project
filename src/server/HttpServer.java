package server;

/*import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;*/
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import common.Image;

public class HttpServer implements Runnable {
    private Monitor monitor;
    private int port;

    public HttpServer(Monitor monitor, int port) {
        this.monitor = monitor;
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            //serverSocket.setReuseAddress(true);
            System.out.println("Camera HTTP server on port " + port);
            Image lastImage = null;
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    InputStream in = clientSocket.getInputStream();
                    OutputStream out = clientSocket.getOutputStream();

                    // Read request.
                    String request, inputLine;
                    request = inputLine = readln(in);
                    while (inputLine != null && !inputLine.equals("")) {
                        inputLine = readln(in);
                    }

                    // See that it is a GET request.
                    if (null != request && !request.startsWith("GET ")) {
                        writeln(out, "HTTP/1.0 501 Method not implemented");
                        writeln(out, "");
                        clientSocket.close();
                        continue;
                    }

                    // Send JPEG image or HTML index (depending on request).
                    // - Since this is extra functionality, we will default to
                    // just showing the image directly if there is no index.html.
                    final String indexPath = "web/index.html";
                    final boolean indexExists = false;//new File(indexPath).isFile();
                    writeln(out, "HTTP/1.0 200 OK");
                    if (!indexExists || request.substring(4, 10).equals("/image")) {
                        Image image = monitor.getImage(lastImage);
                        lastImage = image;
                        byte[] imageData = image.getData();
                        writeln(out, "Content-Length: " + imageData.length);
                        writeln(out, "Content-Type: image/jpeg");
                        writeln(out, "Cache-Control: no-store");
                        writeln(out, "");
                        out.write(imageData);
                    } else {
                        String content = readTextFile("web/index.html");
                        writeln(out, "Content-Length: " + content.length());
                        writeln(out, "Content-Type: text/html; charset=utf-8");
                        writeln(out, "");
                        writeln(out, content);
                    }

                    out.flush();
                    clientSocket.close();
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readln(InputStream in) throws IOException {
        boolean done = false;
        String result = "";

        while(!done) {
            int ch = in.read();        // Read
            if (ch <= 0 || ch == 10) {
                // Something < 0 means end of data (closed socket)
                // ASCII 10 (line feed) means end of line
                done = true;
            }
            else if (ch >= ' ') {
                result += (char)ch;
            }
        }

        return result;
    }

    private void writeln(OutputStream out, String s) throws IOException {
        final byte[] CRLF = { '\r', '\n' };
        out.write(s.getBytes());
        out.write(CRLF);
    }

    private static String readTextFile(String fileName) throws IOException {
        return "";
        /*InputStream is = new FileInputStream(fileName);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while(line != null){
           sb.append(line).append("\r\n");
           line = buf.readLine();
        }

        buf.close();
        return sb.toString();*/
    }
}
