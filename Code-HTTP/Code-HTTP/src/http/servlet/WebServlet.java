package http.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class WebServlet extends HttpServlet {

    /**
     * A simple HTTP Servlet handling a form*/
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String name = req.getParameter("name");

        out.println("<HTML>");
        out.println("<HEAD><TITLE> What For </TITLE></HEAD>");
        out.println("<BODY><BIG> What For </BODY></BIG>");
        out.println("</HTML>");
        out.close();

        String someId = req.getParameter("someId");
        if (someId != null){
            out.println(someId);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doGet(req, res);
    }
}
