///A Simple Web Server (WebServer.java)

package http.server;


import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
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

    private Object Syetem;

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
            int conLen = Integer.valueOf(map.get("Content-Length"));
            char[]  buffer      = new char[conLen];

            in.read(buffer, 0, conLen);

            postData = new String(buffer, 0, buffer.length);
            System.out.println(postData);

            Charset charset = Charset.forName("UTF-8");
            CharBuffer charbuffer = CharBuffer.allocate(buffer.length);
            charbuffer.put(buffer);
            charbuffer.flip();
            ByteBuffer bytebuffer = charset.encode(charbuffer);
            byteData = bytebuffer.array();
          }
        }

        switch(command){

            case "GET":
                get( remote, out, uri);
                break;

            case "HEAD":
                head( out, uri);
                break;

            case "PUT":
                put( remote, out, uri, byteData);
                break;

            case "POST":
                post( out, uri, map, byteData);
                break;

            case "DELETE":
                delete( out, uri);
                break;

            default:
                try{
                    requestHandler( out, 501);
                } catch (Exception e){
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

    private void get(Socket remote, PrintWriter out, String uri) {
      try{
          String path = ClassLoader.getSystemResource("").toString().substring(6);
          path = path.substring(0, path.length()-1) + (uri.indexOf("?")==-1 ? uri : uri.substring(0, uri.indexOf("?")));
          File file = new File(path);
          System.out.println(path); //basic files in catalogue "/bin"

          //uri contains "?"
          if(!uri.contains("?")){
              //uri contains "/"
              if (uri.equals("/")) {
                  requestHandler(out,200,"text/html");
                  out.println("<link rel=\"icon\" href=\"data:;base64,=\">");
                  out.println("<H1>Welcome to our WebServer</H1>");
                  out.flush();
              }
              else{
                  //other type of normal file sources in discussion with html form
                  String type = getContentType(file);
                  System.out.println(type);

                  if(file.isFile() && file.exists() && !file.canRead()){
                      requestHandler(out,403,getContentType(file));
                  }
                  else{
                      //text file source, need to be further discussion
                      if(file.isFile() && file.exists()){
                          //test/html files in consideration
                          if(type.equals("text/html")){
                              requestHandler(out,200,"text/html");
                              out.println("<link rel=\"icon\" href=\"data:;base64,=\">");
                              String encoding="GBK";
                              InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
                              BufferedReader bufferedReader = new BufferedReader(read);
                              String lineTxt = null;
                              while((lineTxt = bufferedReader.readLine()) != null){
                                  out.println(lineTxt);
                              }
                              read.close();
                              out.flush();
                          }//images sources with certains form
                          // png images
                          else if(type.equals("image/png")){
                              try {
                                  //read images form source documents
                                  BufferedImage image = ImageIO.read(new File(path));
                                  requestHandler(out,200,"image/png");
                                  out.flush();
                                  ByteArrayOutputStream pngForms = new ByteArrayOutputStream();
                                  ImageIO.write(image,"png", pngForms);
                                  pngForms.flush();
                                  byte[] imageByte = pngForms.toByteArray();
                                  pngForms.close();
                                  remote.getOutputStream().write(imageByte);
                              } catch (IIOException e) {
                                  requestHandler(out,404,"image/png");
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          }
                          //jpg images
                          else if(type.equals("image/jpg")){
                              try {
                                  //read images form source documents
                                  BufferedImage image = ImageIO.read(new File(path));
                                  requestHandler(out,200,"image/jpg");
                                  out.flush();
                                  ByteArrayOutputStream jpgForms = new ByteArrayOutputStream();
                                  ImageIO.write(image,"jpg", jpgForms);
                                  jpgForms.flush();
                                  byte[] imageByte = jpgForms.toByteArray();
                                  jpgForms.close();
                                  remote.getOutputStream().write(imageByte);
                              } catch (IIOException e) {
                                  requestHandler(out,404,"image/jpg");
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          }
                          //svg images
                          else if(type.equals("image/svg")){
                              try {
                                  //read images form source documents
                                  BufferedImage image = ImageIO.read(new File(path));
                                  requestHandler(out,200,"image/svg");
                                  out.flush();
                                  ByteArrayOutputStream svgForms = new ByteArrayOutputStream();
                                  ImageIO.write(image,"svg", svgForms);
                                  svgForms.flush();
                                  byte[] imageByte = svgForms.toByteArray();
                                  svgForms.close();
                                  remote.getOutputStream().write(imageByte);
                              } catch (IIOException e) {
                                  requestHandler(out,404,"image/svg");
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          }
                          //not in any types above, with purely byteflow I/O
                          else {
                              BufferedInputStream inFlow = new BufferedInputStream(new FileInputStream(file));
                              BufferedOutputStream outFlow = new BufferedOutputStream(remote.getOutputStream());
                              requestHandler(out,200,type);

                              byte[] buffer = new byte[1024];

                              int nbRead;
                              while((nbRead = inFlow.read(buffer)) != -1) {
                                  outFlow.write(buffer, 0, nbRead);
                              }
                              inFlow.close();
                              outFlow.flush();
                          }
                      }
                      //message error
                      else if(file.isFile()) {
                          requestHandler(out,403,type);
                      }
                      else {
                          requestHandler(out,404,type);
                      }
                  }
              }

          }//GET with parameters
          else if(uri.contains("Adder.html?") && !uri.endsWith("?") && file.exists() && file.isFile()){
              requestHandler(out,200,"text/html");
              out.println("<link rel=\"icon\" href=\"data:;base64,=\">");
              Map<String, String> map = new HashMap<String,String>();
              String lineParameter = null;
              String[] parameters = null;
              lineParameter = uri.substring(uri.indexOf("?")+1);
              parameters = lineParameter.split("&");
              for(String s : parameters)
                  map.put(s.split("=")[0], s.split("=")[1]);
              int result = 0;
              for(String key:map.keySet()){
                  if((key.equals("first")||key.equals("second")) && !map.get(key).isEmpty()) {
                      result += Integer.parseInt(map.get(key));
                  }
              }

              out.println(result);
              out.flush();
          }
          //message error
          else{
              requestHandler( out, 404, "text/html");
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
    }

    private void delete(PrintWriter out, String uri) {
    }

    private void post(PrintWriter out, String uri, Map<String, String> map, byte[] byteData) {
    }

    private void put(Socket remote, PrintWriter out, String uri, byte[] byteData) {
    }

    private void head(PrintWriter out, String uri) {
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
