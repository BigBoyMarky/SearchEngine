import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.sql.*;
import java.util.*;
import java.lang.*;

import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.w3c.dom.*;

public class Crawler
{
	Connection connection;
	int urlID;
	public Properties props;
	//Database database;

	public ArrayList<String> urlList;
	private String domain;
	private int NextURLID;
	private int NextURLIDScanned;
	private int urlIndex;
	private String root;
	//private int NextImageURLID;
	private int MaxURLs;

	Crawler(String d) {
		domain = d;
		MaxURLs = 100000;
		urlList = new ArrayList<String>();
		root = "https://www.cs.purdue.edu/";
		urlList.add(root);
		//urlList.add(root);
		urlID = 1;
	}

	void startCrawl() throws SQLException, IOException {

        //Clear database
		//System.out.println("Inside startCrawl");
		//System.out.println("NextURLIDScanned: " + NextURLIDScanned);

	    if(NextURLIDScanned > 0) {
	   		openConnection();
	        return;
	    }

	    System.out.println("Creating database......");
	    createDB();

	    NextURLID=0;
	    NextURLIDScanned = 0;
	    int urlID=0;

		//System.out.println("Starting to add urls to database......");
		/*System.out.println("urlList: ");
		urlList.toString();
		System.out.println("End of urlList");*/
		for(String url : urlList) {
			urlID = NextURLID;
			insertURLInDB(url, urlID);
			NextURLID++;
		}

		props.setProperty("crawler.urlID", "" + urlID);
		props.setProperty("crawler.NextURLID", "" + NextURLID);
		//System.out.println("urlID and NextURLID after setProperty: urlID: " + urlID + " " + NextURLID);
	     //setProperties();
	}

	public void crawl() throws SQLException {
	
		System.out.println("Starting the crawling");

		//readProperties();
		//createDB();


		while (NextURLID < MaxURLs) {//System.out.println(urlList.toString());
			//System.out.println("NextURLIDScanned: " + NextURLIDScanned);
			urlIndex = NextURLIDScanned;
	       	//String url1 = fetchURL("URLS");
	       	//System.out.println("urlList item: " + urlList.get(NextURLIDScanned));
	       	fetchURL(urlList.get(NextURLIDScanned));
			NextURLIDScanned++;

		}

	}


	public void readProperties() throws IOException {
      		props = new Properties();
      		FileInputStream in = new FileInputStream("database.properties");
      		props.load(in);
      		in.close();
	}

	public void openConnection() throws SQLException, IOException
	{
		String drivers = props.getProperty("jdbc.drivers");
      		if (drivers != null) System.setProperty("jdbc.drivers", drivers);

      		String url = props.getProperty("jdbc.url");
      		String username = props.getProperty("jdbc.username");
      		String password = props.getProperty("jdbc.password");

		connection = DriverManager.getConnection( url, username, password);
   	}

	public void createDB() throws SQLException, IOException {
			
			openConnection();

         	Statement stat = connection.createStatement();

		// Delete the table first if any
		try {
			stat.executeUpdate("DROP TABLE urltable");
			stat.executeUpdate("DROP TABLE wordtable");
			stat.executeUpdate("DROP TABLE imgtable");
		}
		catch (Exception e) {
		}

		// Create the table
        	stat.executeUpdate("CREATE TABLE urltable (urlid INT, url VARCHAR(512), description VARCHAR(200))");
        	stat.executeUpdate("CREATE TABLE wordtable (word VARCHAR(100), urlid INT)");
        	stat.executeUpdate("CREATE TABLE imgtable (url VARCHAR(512), urlid INT)");
	}

	public boolean urlInDB(String urlFound) throws SQLException, IOException {

        Statement stat = connection.createStatement();
        //PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM urltable WHERE url LIKE '%?%'");
		ResultSet result = stat.executeQuery( "SELECT * FROM urltable WHERE url LIKE '%" + urlFound + "%'");
		/*pstmt.setString(1, urlFound);
		ResultSet result = pstmt.executeQuery();*/

		if (result.next()) {
			stat.close();
	        	//System.out.println("URL "+urlFound+" already in DB");
			return true;
		}
		stat.close();
	       //System.out.println("URL "+urlFound+" not yet in DB");
		return false;
	}

	public void insertURLInDB(String url, int urlID) throws SQLException, IOException {

		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO urltable VALUES (?, ?, '')");
		pstmt.setInt(1, urlID);
		pstmt.setString(2, url);
		pstmt.executeUpdate();
		pstmt.close();

	}

	public void insertWordInDB(String word, int urlID) throws SQLException, IOException {

		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO wordtable VALUES (?, ?)");
		pstmt.setString(1, word);
		pstmt.setInt(2, urlID);
		pstmt.executeUpdate();
		pstmt.close();

	}

	public void insertImgInDB(String imgurl, int urlID) throws SQLException, IOException {

		PreparedStatement pstmt = connection.prepareStatement("INSERT INTO imgtable VALUES (?, ?)");
		pstmt.setString(1, imgurl);
		pstmt.setInt(2, urlID);
		pstmt.executeUpdate();
		pstmt.close();

	}

	public String makeAbsoluteURL(String url, String parentURL) {
		if (url.indexOf(":")<0) {
			// the protocol part is already there.
			return url;
		}

		if (url.length() > 0 && url.charAt(0) == '/') {
			// It starts with '/'. Add only host part.
			int posHost = url.indexOf("://");
			if (posHost <0) {
				return url;
			}
			int posAfterHost = url.indexOf("/", posHost+3);
			if (posAfterHost < 0) {
				posAfterHost = url.length();
			}
			String hostPart = url.substring(0, posAfterHost);
			return hostPart + "/" + url;
		} 

		// URL start with a char different than "/"
		int pos = parentURL.lastIndexOf("/");
		int posHost = parentURL.indexOf("://");
		if (posHost <0) {
			return url;
		}

		return "";
		
		
		

	}
	public void updateUrlDescription(int id, String description) throws SQLException {
		PreparedStatement pstmt = connection.prepareStatement("UPDATE urltable SET description = ? WHERE urlid = ?");
		pstmt.setString(1, description);
		pstmt.setInt(2, id);
		pstmt.executeUpdate();
	}
	
   	public void fetchURL(String urlScanned) {
		try {
			URL url = new URL(urlScanned);
			System.out.println("urlscanned="+urlScanned+" url.path="+url.getPath());

			if (!url.getHost().equals("www." + domain)) {
				System.out.println("url outside of domain: " + url.getHost());
				return;
			}

			// open reader for URL
			InputStreamReader in = new InputStreamReader(url.openStream());

			// read contents into string builder
			StringBuilder input = new StringBuilder();
			int ch;
			while ((ch = in.read()) != -1) {
         			input.append((char) ch);
			}

 			// search for all occurrences of pattern
			String patternString =  "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";
			Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(input);

			StringBuilder page = new StringBuilder();
			int c;
			while ((c = in.read()) != -1) {
				page.append((char) c);
			}

			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				String match = input.substring(start, end);
				String urlFound = matcher.group(1).replaceAll("\"", "");

				if (urlInDB(urlFound)) {
					continue;
				}

				else {	
					urlFound = (new URL(new URL(urlScanned), urlFound)).toString();
					//urlFound = makeAbsoluteURL(urlFound, urlScanned);
					URL newURL = new URL(urlFound);
						//urlList.add(urlFound);
					if (!urlFound.contains("cs.purdue.edu")) {
						System.out.println("url outside of domain: " + urlFound);
						continue;
					}
					else if (!urlInDB(urlFound) && !urlFound.contains("mail") && !urlScanned.contains("mail") && urlFound.length() > 0) {
						//urlList.add(urlFound);
						System.out.println(urlFound);
						urlList.add(newURL.toString());
						insertURLInDB(urlFound, urlID);
						NextURLID++;





						//////////////////////////// KIND OF WORKING DESCRIPTION GRABBER ////////////////////////////////////////////
						/*Document doc = Jsoup.connect(urlFound).get();
						String content = doc.toString();
						String finalDescription = "";
						if (content.contains("<h1>")) {
							content = content.substring(content.indexOf("<h1>")+ 4);
							finalDescription = finalDescription + content.substring(0, content.indexOf("</h1>"));
							content = content.substring(content.indexOf("</h1>") + 5);
						}
						else if (content.contains("<h2>")) {
							content = content.substring(content.indexOf("<h2>") + 4);
							finalDescription = finalDescription + content.substring(0, content.indexOf("</h2>"));
							content = content.substring(content.indexOf("</h2>") + 5);
						}
						else if (content.contains("<h3>")) {
							content = content.substring(content.indexOf("<h3>") + 4);
							finalDescription = finalDescription + content.substring(0, content.indexOf("</h3>"));
							content = content.substring(content.indexOf("</h3>") + 5);	
						}
						else if (content.contains("<h4>")) {
							content = content.substring(content.indexOf("<h4>") + 4);
							finalDescription = finalDescription + content.substring(0, content.indexOf("</h4>"));
							content = content.substring(content.indexOf("</h4>") + 5);	
						}
						if (content.contains("<p>")) {
							content = content.substring(content.indexOf("<p>") + 3);
							finalDescription = finalDescription + content.substring(0, content.indexOf("</p>"));
							content = content.substring(content.indexOf("</p>") + 4);
						}
					
						System.out.println(content.substring(0, 100));*/


						// getting the description for the urltable
						Document doc = Jsoup.connect(urlFound).get();
						Elements header1Elements = doc.getElementsByTag("h1");
						Elements header2Elements = doc.getElementsByTag("h2");
						Elements header3Elements = doc.getElementsByTag("h3");
						Elements header4Elements = doc.getElementsByTag("h4");
						Elements paragraphElements = doc.getElementsByTag("p");
						String finalDescription = "";

						for(Element header : header1Elements) {
							finalDescription = finalDescription + header.text() + " ";
						}

						for(Element header : header2Elements) {
							finalDescription = finalDescription + header.text() + " ";
						}

						for(Element header : header3Elements) {
							finalDescription = finalDescription + header.text() + " ";	
						}

						for(Element header : header4Elements) {
							finalDescription = finalDescription + header.text() + " ";
						}

						for(Element paragraph : paragraphElements) {
							finalDescription = finalDescription + " " + paragraph.text();
						}


						//System.out.println(finalDescription);
						
						if (finalDescription.length() < 100) {
							finalDescription = finalDescription + " " + doc.text();
						}




						if (finalDescription.length() > 100) {
							updateUrlDescription(urlID, finalDescription.substring(0, 100));
						}
						else {
							updateUrlDescription(urlID, finalDescription);
						}




/////////////////////////////////////////////////INSERT INTO WORD TABLE /////////////////////////////////////////////////////////////////

						String content = doc.text();
						List<String>words = Arrays.asList(content.split("\\P{Alpha}+"));
						ArrayList<String>wordsAlreadyInDB = new ArrayList<String>();
						for(int i = 1; i < words.size(); i++) {
							if (!wordsAlreadyInDB.contains(words.get(i))) {
								wordsAlreadyInDB.add(words.get(i));
								insertWordInDB(words.get(i), urlID);
								//System.out.println(words.get(i));
							}
						}

						//System.out.println(words.toString());

/////////////////////////////////////////////////INSERT IMAGE SOURCE INTO IMG TABLE /////////////////////////////////////////////////////////////////
// select * from imgtable where urlid=? order by rand() limit 1
						Elements imageElements = doc.select("img");
						for (Element img : imageElements) {
							String imgsrc = img.attr("src");
							if (!imgsrc.contains("http")) {
								/*System.out.println("URLID: " + urlID);
								System.out.println("ROOT: " + root);
								System.out.println("URLFOUND: " + urlFound);
								System.out.println("IMAGE SOURCE: " + imgsrc);*/
								if (imgsrc.contains("logo.svg")) {
									imgsrc = "https://www.cs.purdue.edu/images/logo.svg";
								} else if (imgsrc.contains("brand.svg")) {
									imgsrc = "https://www.cs.purdue.edu/images/brand.svg";
								}
								else if (imgsrc.startsWith("images/")) {
									imgsrc = "../" + imgsrc;
									imgsrc = urlFound + "/" + imgsrc;
								}
								else {
									imgsrc = urlFound + "/" + imgsrc;
								}
							}
							//System.out.println("NEW IMAGE SOURCE: " + imgsrc);
							insertImgInDB(imgsrc, urlID);
						}

						urlID++;

					}
				
				}

    				//System.out.println(match);
 			}

		}
  		catch (Exception e)
  		{
   			System.out.println("Invalid URL");
  		}
	}

   	public static void main(String[] args) {

		Crawler crawler = new Crawler("cs.purdue.edu");

		try {
			crawler.readProperties();
			String root = crawler.props.getProperty("crawler.root");
			crawler.createDB();
			//crawler.fetchURL(root);


			crawler.startCrawl();
			crawler.crawl();

		}
		catch( Exception e) {
         		e.printStackTrace();
		}
		System.out.println("\n\n\n\n\n\n\n===========================CONTENTS OF URLLIST============================\n\n\n\n\n\n\n");
		System.out.println(crawler.urlList.toString());
	}
}

