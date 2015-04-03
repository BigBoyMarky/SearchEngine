import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import util.HTMLFilter;
import java.util.*;

public class SearchPage extends HttpServlet {
    private String searchWord;
 public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Search</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h3>Search</h3>");
        String word = request.getParameter("word");
        if (word != null) {
            out.println("Search: ");
            out.println(" = " + HTMLFilter.filter(word) + "<br>");
        } else {
            out.println("No Parameters, Please enter some");
        }
        out.println("<P>");
        out.print("<form action=\"");
        out.print("SearchPage\" ");
        out.println("method=POST>");
        out.println("Search:");
        out.println("<input type=text size=20 name=word>");
        out.println("<br>");
        //out.println("Last Name:");
        //out.println("<input type=text size=20 name=lastname>");
        out.println("<br>");
        out.println("<input type=submit>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }
       /* response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Search Page</title>");
        //out.println("<link rel=\"stylesheet\" href=\"css\\styles.css\" type = \"text/css\" />");
        //out.println("<link rel = 'stylesheet' href = 'css/styles.css' type = 'text/css' />");
        out.println("<link rel = \"stylesheet\" type = \"text/css\" href = \"style.css\" />");
        out.println("</head>");
        out.println("<body bgcolor=grey>");
        out.println("<center>");
        out.println("<br>");
        out.println("<br>");
        out.println("<br>");
        out.println("<br>");
        out.println("<br>");
        out.println("<div id = \"search\">");
        //out.println("<div margin:auto height=200px>");
        out.println("<h1>Type your search input below</h1>");
        out.println("<form><input style = \"border:2px solid #373737\" id = \"searchinput\" type = \"text\" size = \"40\" name = searchword placeholder = \"Search......................\"></form>");
        out.println("</div>");
        out.println("</center>");
        out.println("</body>");
        out.println("</html>");*/
    
     /*   String search = request.getParameter("search");
        
        
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");

        if (firstName != null || lastName != null) {
            out.println("First Name:");
            out.println(" = " + HTMLFilter.filter(firstName) +
                         "<br>");
            out.println("Last Name:");
            out.println(" = " + HTMLFilter.filter(lastName));
        } else {
            out.println("No Parameters, Please enter some");
        }

        out.println("<P>");
        out.print("<form action=\"");
        out.print("myapp\" ");
        out.println("method=POST>");
        out.println("First Name:");
        out.println("<input type=text size=20 name=firstname>");
        out.println("<br>");
        out.println("Last Name:");
        out.println("<input type=text size=20 name=lastname>");
        out.println("<br>");
        out.println("<input type=submit>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }*/

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        /*response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Results</title>");
        //out.println("<link rel=\"stylesheet\" href=\"css\\styles.css\" type = \"text/css\" />");
        //out.println("<link rel = 'stylesheet' href = 'css/styles.css' type = 'text/css' />");
        out.println("<link rel = \"stylesheet\" type = \"text/css\" href = \"style.css\" />");
        out.println("</head>");
        out.println("<body bgcolor=grey>");
        out.println("<center>");
        out.println("<h1>Search results</h1>");
        out.println("<div>");
        out.println("</div>");






        out.println("</div>");
        out.println("</center>");
        out.println("</body>");
        out.println("</html>");*/

        /*response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        searchWord = request.getParameter("searchword");
        System.out.println(searchWord);*/
        doGet(request, response);
    }

}

