import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import util.HTMLFilter;
import java.util.*;
import java.sql.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchPage extends HttpServlet {

 public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {

        String searchwords = request.getParameter("word");

        String words[] = searchwords.split(" ");

        PrintWriter out = response.getWriter();
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CRAWLER", "root", "password");

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Search Results</title>");
            out.println("<link rel=\"stylesheet\" href=\"css/searchpagestyles.css\" type = \"text/css\" />");
            out.println("</head>");
            out.println("<body>");
            out.println("<br>");
            out.println(" <a href = \"index.html\"><button id = \"homebutton\">Home</button></a>");
            out.println("<center>");

                    //<form action = "SearchPage" method = "GET"><input id = "searchinput" type = "text" name = "word" placeholder = "Search......................"></form>
            
            out.println("<form action = \"SearchPage\" method = \"GET\"><input id = \"searchinput\" type = \"text\" name = \"word\" placeholder = \"Search again.....\"> <button id = \"searchbutton\" action = \"SearchPage\" method = \"GET\">Search</button></form>");
            
            
            out.println("<h1>Search results for: " + searchwords + "</h1>");
            out.println("<br>");
            out.println("</center>");
            
            out.println("<hr>");
            //out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            


            ArrayList<Integer> intersectingURLIDs = new ArrayList<Integer>();

            for (int i = 0; i < words.length; i++) {

                out.println("<h1>Search Results for " + words[i] + "</h1>");

                try { 
                    Statement stat = connection.createStatement();
                    ResultSet result;

                    result = stat.executeQuery("SELECT * FROM wordtable WHERE word LIKE '%" + words[i] + "%'");
                    

                    ResultSet imgResult;
                    ResultSet urlResult;

                    int urlID;
                    String imgLink = "";
                    String urlLink = "";
                    String description = "";

                    while (result.next()) {
                        urlID = result.getInt("urlid");
                        //out.println("urlID" + urlID); // comment this out later
                        stat = connection.createStatement();
                        imgResult = stat.executeQuery("SELECT * FROM imgtable WHERE urlid=" + urlID + " ORDER BY RAND() LIMIT 1");
                        if (imgResult.next()) {
                            imgLink = imgResult.getString("url");
                            if (imgLink.contains("logo.svg") || imgLink.contains("brand.svg")) {
                                stat = connection.createStatement();
                                imgResult = stat.executeQuery("SELECT * FROM imgtable WHERE urlid=" + urlID + " ORDER BY RAND() LIMIT 1");
                                if (imgResult.next()) {
                                    imgLink = imgResult.getString("url");    
                                }
                            }
                        }
                        out.println("<img width=\"100\" height=\"100\" src = \"" + imgLink + "\" style = \"background-color:grey;\">");
                        
                        stat = connection.createStatement();
                        urlResult = stat.executeQuery("SELECT * FROM urltable WHERE urlid=" + urlID);
                        if (urlResult.next()) {
                           urlLink = urlResult.getString("url");
                           description = urlResult.getString("description");
                        }
                        out.println("<a href = \"" + urlLink + "\">" + urlLink + "</a>");
                        out.println("<br>");
                        out.println("<p>" + description + ".....</p>");
                        out.println("<br>");
                        //out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                        out.println("<br>");

                        intersectingURLIDs.add(urlID);

                        //urlID++;
                        

                    }

                    stat.close();
                } catch(Exception e) {
                    out.println(e.getMessage());
                }
            }

            Collections.sort(intersectingURLIDs);
            List<Integer> finalList = new ArrayList<Integer>();
            
            for (int i = 0; i < intersectingURLIDs.size() - 1; i++) {
                if (intersectingURLIDs.get(i) == intersectingURLIDs.get(i + 1)) {
                    finalList.add(intersectingURLIDs.get(i));
                    i++;
                }
            }

            if (finalList.size() > 1) {
                out.println("<h1>Search Results for " + searchwords + "</h1>");
            }

            Statement stat;
            ResultSet imgResult;
            ResultSet urlResult;

            try {

                //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CRAWLER", "root", "password");
                
                for (int i = 0; i < finalList.size(); i++) {

                    int urlID = finalList.get(i);
                    //out.println(urlID);
                    String imgLink = "";
                    String urlLink = "";
                    String description = "";


                    stat = connection.createStatement();
                    imgResult = stat.executeQuery("SELECT * FROM imgtable WHERE urlid=" + urlID + " ORDER BY RAND() LIMIT 1");
                    if (imgResult.next()) {
                        imgLink = imgResult.getString("url");
                        if (imgLink.contains("logo.svg") || imgLink.contains("brand.svg")) {
                            stat = connection.createStatement();
                            imgResult = stat.executeQuery("SELECT * FROM imgtable WHERE urlid=" + urlID + " ORDER BY RAND() LIMIT 1");
                            if (imgResult.next()) {
                                imgLink = imgResult.getString("url");    
                            }
                        }
                    }
                    out.println("<img width=\"100\" height=\"100\" src = \"" + imgLink + "\" style = \"background-color:grey;\">");
                    stat = connection.createStatement();
                    urlResult = stat.executeQuery("SELECT * FROM urltable WHERE urlid=" + urlID);
                    if (urlResult.next()) {
                       urlLink = urlResult.getString("url");
                       description = urlResult.getString("description");
                    }

                    out.println("<a href = \"" + urlLink + "\">" + urlLink + "</a>");
                    out.println("<br>");
                    out.println("<p>" + description + ".....</p>");
                    out.println("<br>");
                    //out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    out.println("<br>");


                }
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        } catch(Exception e) { // catch for initial connection to database

        }



        out.println("</body>");
        out.println("</html>");

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        doGet(request, response);
    }

}

