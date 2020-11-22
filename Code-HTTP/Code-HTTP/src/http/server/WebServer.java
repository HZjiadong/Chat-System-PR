///A Simple Web Server (WebServer.java)

package http.server;


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
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


        switch(command) {
          case "GET":
            get(remote, out, uri);
            break;
          case "HEAD":


//          case "HEAD":
//            head(out, uri);
//            break;
//          case "PUT":
//            put(remote, out, uri, byteData);
//            break;
//          case "POST":
//            post(out, uri, map, byteData);
//            break;
//          case "DELETE":
//            delete(out,uri);
//            break;
          default:
            try {
              requestHandler(out,501);
            } catch (Exception e) {
              System.out.println(e);
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

  private void requestHandler(PrintWriter out, int stat, String contentType) {
    switch(String.valueOf(stat)) {
      case "100":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Continue");
        out.println("Content-Type: " + contentType);
        out.println("Server: Bot");
        out.println("");
        break;
      case "200":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " OK");
        out.println("Content-Type: " + contentType);
        out.println("Server: Bot");
        out.println("");
        break;
      case "403":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Forbidden");
        out.println("Content-Type: " + contentType);
        out.println("Server: Bot");
        out.println("");
        out.println("<html>");
        out.println("<head><title>403 Forbidden</title></head>");
        out.println("<body><h1>403 Forbidden</h1>");
        out.println("<p>Access is forbidden to the requested page.</p></body>");
        out.println("</html>");
        break;
      case "404":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Found");
        out.println("Content-Type: " + contentType);
        out.println("Server: Bot");
        out.println("");
        out.println("<html>");
        out.println("<head><title>404 Not Found</title></head>");
        out.println("<body><h1>404 Not Found</h1>");
        out.println("<p>The requested URL was not found on this server.</p></body>");
        out.println("</html>");
        break;
    }
    out.flush();
  }

  private void requestHandler(PrintWriter out, int stat) {

    switch(String.valueOf(stat)) {
      case "100":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Continue");
        out.println("Server: Bot");
        out.println("");
        break;
      case "204":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " No Content");
        out.println("Server: Bot");
        out.println("");
        break;
      case "201":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Created");
        out.println("Server: Bot");
        out.println("");
        break;
      case "403":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Forbidden");
        out.println("Server: Bot");
        out.println("");
        out.println("<html>");
        out.println("<head><title>403 Forbidden</title></head>");
        out.println("<body><h1>403 Forbidden</h1>");
        out.println("<p>Access is forbidden to the requested page.</p></body>");
        out.println("</html>");
        break;
      case "404":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Found");
        out.println("Server: Bot");
        out.println("");
        out.println("<html>");
        out.println("<head><title>404 Not Found</title></head>");
        out.println("<body><h1>404 Not Found</h1>");
        out.println("<p>The requested URL was not found on this server.</p></body>");
        out.println("</html>");
        break;
      case "500":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Internal Server Error");
        out.println("Server: Bot");
        out.println("");
        break;
      case "501":
        out.println("HTTP/1.1 " + String.valueOf(stat) + " Not Implemented");
        out.println("Server: Bot");
        out.println("");
        break;
    }
    out.flush();
  }

  private void get(Socket remote, PrintWriter out, String uri) throws IOException {
    String path = ClassLoader.getSystemResource("").toString().substring(6);
    path = path.substring(0, path.length()-1) + (uri.indexOf("?")==-1 ? uri : uri.substring(0, uri.indexOf("?")));
    File file = new File(path);
    System.out.println(path); //Les fichiers se trouvent sous /bin

    if(!uri.contains("?")) {
      if (uri.equals("/")) {
        requestHandler(out,200,"text/html");
        out.println("<link rel=\"icon\" href=\"data:;base64,=\">"); //ignorer favicon.ico
        // Send the HTML page
        out.println("<H1>Welcome to the Ultra Mini-WebServer</H1>");
        out.flush();

      } else {
        String type = getContentType(file);
        System.out.println(type);

      }
    }

  }

  public void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException {
    res.setContentType("text/html");
    PrintWriter out = res.getWriter();
    out.println("<HTML>");
    out.println("<HEAD><TITLE> What For </TITLE></HEAD>");
    out.println("<BODY><BIG> What For </BODY></BIG>");
    out.println("</HTML>");
    out.close();
  }

  public String getContentType(File file) {

    String fileName = file.getName();
    String type = ".txt";

    if (fileName.endsWith(".html") || fileName.endsWith(".htm") || fileName.endsWith(".txt")) {
      type = "text/html";
    } else if (fileName.endsWith(".mp4")) {
      type = "video/mp4";
    } else if (fileName.endsWith(".png")) {
      type = "image/png";
    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
      type = "image/jpg";
    } else if (fileName.endsWith(".mp3")) {
      type = "audio/mp3";
    } else if (fileName.endsWith(".avi")) {
      type = "video/x-msvideo";
    } else if (fileName.endsWith(".css")) {
      type = "text/css";
    } else if (fileName.endsWith(".pdf")) {
      type = "application/pdf";
    } else if (fileName.endsWith(".odt")) {
      type = "application/vnd.oasis.opendocument.text";
    } else if (fileName.endsWith(".json")) {
      type = "application/json";
    }

    return type;
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
