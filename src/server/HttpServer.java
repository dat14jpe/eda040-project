package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpServer implements Runnable {
	private Monitor monitor;
	
	public HttpServer(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public void run() {
		try {
			final int portNumber = 7654;
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Camera HTTP server on port " + portNumber);
			while (true) {
				Socket clientSocket = serverSocket.accept(); 
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				OutputStream out = clientSocket.getOutputStream();
				
				// Read request.
				String request, inputLine;
				request = inputLine = in.readLine();
				while (inputLine != null && !inputLine.equals("")) {
					inputLine = in.readLine();
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
				final boolean indexExists = new File(indexPath).isFile();
				writeln(out, "HTTP/1.0 200 OK");
				if (!indexExists || request.substring(4, 10).equals("/image")) {
					byte[] imageData = monitor.getImage();
					writeln(out, "Content-Length: " + imageData.length);
					writeln(out, "Content-Type: image/jpeg");
					writeln(out, "Cache-Control: no-store");
					writeln(out, "");
					out.write(imageData);
				} else {
					String content = new String(Files.readAllBytes(Paths.get("web/index.html")));
					writeln(out, "Content-Length: " + content.length());
					writeln(out, "Content-Type: text/html; charset=utf-8");
					writeln(out, "");
					writeln(out, content);
				}
				
				out.flush();
				clientSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeln(OutputStream out, String s) throws IOException {
		//out.write((s + "\r\n").getBytes(Charset.forName("UTF-8")));
		final byte[] CRLF = {'\r', '\n'};
		out.write(s.getBytes());
		out.write(CRLF);
	}
}
