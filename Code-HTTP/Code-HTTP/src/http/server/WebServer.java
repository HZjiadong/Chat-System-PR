///A Simple Web Server (WebServer.java)

package http.server;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   * @see WebServer#getClass()
   * @see WebServer#
   * @see WebServer#
   * @see WebServer#
   * @see WebServer#
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(8888);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String uri = "";
        String command = "";
        String postData = "";
        byte[] byteData = null;
        Map<String, String> map = new HashMap<String,String>();
        String str = ".";
        str = in.readLine();
        if(str != null && !str.equals("")) {
          System.out.println(str);
          String[] request = str.split(" ");
          command = request[0];
          uri = request[1];
          String[] lines = null;
          str = in.readLine();
          System.out.println(str);

          while (str != null && !str.equals("")) {
            lines = str.split(": ");
            map.put(lines[0], lines[1]); // enregistrer tous les headers
            str = in.readLine();
            System.out.println(str);
          }

          if(map.containsKey("Content-Length")) {
            int cL = Integer.valueOf(map.get("Content-Length"));
            char[]  buffer      = new char[cL];

            in.read(buffer, 0, cL);

            postData = new String(buffer, 0, buffer.length);
            System.out.println(postData);

            Charset cs = Charset.forName("UTF-8");
            CharBuffer cb = CharBuffer.allocate(buffer.length);
            cb.put(buffer);
            cb.flip();
            ByteBuffer bb = cs.encode(cb);
            byteData = bb.array();
          }
        }


        


        // Send the response
        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}
