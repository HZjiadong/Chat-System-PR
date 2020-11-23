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

        String accountIdStr = req.getParameter("accoundIdStr");
        int accoundId = Integer.parseInt(accountIdStr);
        if (accoundId != 0){
            out.println("Id can't be zero!");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doGet(req, res);
    }
    
    public void AuthentificationBasedOnUserName(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{

        String name = req.getRemoteUser();
        PrintWriter out = res.getWriter();
        if (name == null){
            out.println("The Server Administrator should protect this resource");
        } else {
            String [] items = req.getParameterValues("item");
            if (items != null) {
                for (int i =0; i < items.length; i++) {
                    addItemToCart(name, items[i]);
                }
            }
        }
        if (name == null){
            out.println("The Server Administrator should protect this page");
        } else {
            String[] items = getItemsFromCart(name);
        }
    }

    private String[] getItemsFromCart(String name) {
        return new String[0];
    }

    private void addItemToCart(String name, String item) {
    }


    public void AuthentificationBasedOnCookies(HttpServletRequest req, HttpServletResponse res) throws ServletException,IOException{
        Cookie cookie = new Cookie("ID","999");
        res.addCookie(cookie);

        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            for(int i = 0; i<cookies.length; i++){
                String name = cookies[i].getName();
                String value = cookies[i].getValue();
            }
        }
    }
}
