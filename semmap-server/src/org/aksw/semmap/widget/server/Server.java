package org.aksw.semmap.widget.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet implementation class Server
 */
public class Server extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private int count = 0;

	private String property;
	private String resourceUri;
	private String userId;
	private String rating = null;
	private ByteArrayInputStream fis = null;
	private byte[] data = null;

	
	private static final String DEFAULT_TEMP_DIR = ".";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Server() {
		super();
		// TODO Auto-generated constructor stub
		echo("Start Server");
	}

	public void echo(String string) {
		System.out.println("[Server]: " + string);

	}

	private File getTempDir() {
		return new File(DEFAULT_TEMP_DIR);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		echo("doGet: " + request.toString());
		// // Set a cookie for the user, so that the counter does not increate
		// // everytime the user press refresh
		// HttpSession session = request.getSession(true);
		// // Set the session valid for 5 secs
		// session.setMaxInactiveInterval(5);
		// response.setContentType("text/plain");
		// PrintWriter out = response.getWriter();
		// if (session.isNew()) {
		// count++;
		// }
		// out.println("This site has been accessed " + count + " times.");

		String service = request.getParameter("service");
		echo("Req: " + service);
		String resp = null;
		if (service == null)
			resp = "no param!";
		else if (service.equals("test")) // Userdata
			resp = "done";

		String callback = request.getParameter("jsonpCallback");
		echo("callback: "+callback);
		if(callback == null)
			resp +=" no callback!";
		else
			resp = "jsonpCallback ({ "+'"'+"html"+'"'+":"+'"'+getWidget()+'"'+"} );";
		echo(request.toString());
		echo(resp);
		// CORS
		// resp.addHeader("Access-Control-Allow-Origin",
		// "http://localhost:8080");
//		response.addHeader("Access-Control-Allow-Origin", "*");

		response.setContentType("text/javascript");

		response.getWriter().write(resp);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		echo("Post: " + request.toString());
		echo("ConenType: " + request.getContentType());
		echo("Path: " + request.getContextPath());

		String name, filePath;

		echo("####Upload Servlet: Receiving formular");
		// process only multipart requests
		if (ServletFileUpload.isMultipartContent(request)) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(request);
				echo("Number of items: " + items.size());
				int i = 0;
				for (FileItem fileItem : items) {
					String itemName = fileItem.getFieldName();
					echo(">> "+i+".item: "+ itemName);
					
					if (itemName.equals("property"))
						property = fileItem.getString();
					if (itemName.equals("userId"))
						userId = fileItem.getString();
					if (itemName.equals("resourceUri"))
						resourceUri = fileItem.getString();
					if (itemName.equals("rating"))
						rating = fileItem.getString();
					
					echo("FieldName: " + fileItem.getFieldName());
					echo("Name: " + fileItem.getName());
					echo("Type: "
							+ fileItem.getContentType());

					if (itemName.equals("file")) {
						// Inputstream
						data = fileItem.get();
						fis = new ByteArrayInputStream(	data);
						echo("file into inputstram!");
						
											}
					
					i++;
				}
				
				// Insert into db
				// File
				PreparedStatement ps = null;
				try {
					DBTool db = new DBTool(getResourceDir()
							+ "/resources/db_settings.ini");
					Connection conn = db.getConnection(); // establish
															// connection
					String query = "replace into file(resource, user, property,value) values (?, ?, ?, ?)";
					conn.setAutoCommit(false);

					echo("data length: "+data.length);
					
					// File DB insert
					ps = conn.prepareStatement(query);
					ps.setString(1, resourceUri);
					ps.setString(2, userId);
					ps.setString(3, property);
					ps.setBinaryStream(4, fis, (int) data.length);
					ps.executeUpdate();
					conn.commit();
					echo("db commit");
					
					// Rating
					if(rating != null){
						
					query = "replace into property(resource, user, property,value) values (?, ?, ?, ?)";
					ps = conn.prepareStatement(query);
					ps.setString(1, resourceUri);
					ps.setString(2, userId);
					ps.setString(3, "rating");
					ps.setString(4,rating);
					ps.executeUpdate();
					conn.commit();
					echo("Ratingdb commit!");
					}
					
					// Ok response message
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter()
							.print("File was added to database!");
					response.flushBuffer();
					
				} catch(Exception e){
					echo("ERROR: "+e.getMessage());
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
				finally {
					ps.close();
					fis.close();
				}

				echo(">> Info:");
				echo("user: "+userId);
				echo("resource: "+resourceUri);
				echo("property: "+property);
				if(rating !=null)
					echo("rating: "+rating);
				
			} catch (Exception e) {
				
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occurred while adding template : "
								+ e.getMessage());
			}

		} else {			
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
		}
		
		

		
		echo("####Upload Servlet: Receiving formular done");
	}

	private String getWidget(){
		File file = new File(getResourceDir()+"/resources/widget.html");
	    int ch;
	    StringBuffer strContent = new StringBuffer("");
	    FileInputStream fin = null;
	    try {
	      fin = new FileInputStream(file);
	      while ((ch = fin.read()) != -1)
	        strContent.append((char) ch);
	      fin.close();
	    } catch (Exception e) {
	      System.out.println(e);
	    }
		echo(strContent.toString());

		return strContent.toString().replaceAll("\\s", " ");

	}
	
	private String getResourceDir() {
		String prefix = getServletContext().getRealPath("");
		if (!prefix.endsWith("/")) {
			prefix += '/';
		}
		// resourcePath = prefix+"WEB-INF/classes/";
		String resourcePath = prefix + "WEB-INF/";
//		echo("prefix: " + prefix);
//		echo("resourcePath: " + resourcePath);

		return resourcePath;
	}
}
