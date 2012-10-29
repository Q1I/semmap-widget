package org.aksw.semmap.widget.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.message.DirectError;
import org.openid4java.message.Message;
import org.openid4java.message.ParameterList;
import org.openid4java.server.ServerException;
import org.openid4java.server.ServerManager;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	  // instantiate a ServerManager object
    public ServerManager manager = new ServerManager();

	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String identifier = null;
		String username = null;
		username = request.getParameter("openid_username");
		identifier = request.getParameter("openid_identifier");
		echo("user: " + username);
		echo("identifier: " + identifier);
		
		try {
			processRequest(request,response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		response.setStatus(HttpServletResponse.SC_OK);
//		response.getWriter().print(
//				"User: " + username + " identifier: " + identifier);
	}

	private void echo(String string) {
		System.out.println("[Login Server]: " + string);

	}

	public String processRequest(HttpServletRequest httpReq,
			HttpServletResponse httpResp) throws Exception {
		// extract the parameters from the request
		ParameterList request = new ParameterList(httpReq.getParameterMap());

		String mode = request.hasParameter("openid.mode") ? request
				.getParameterValue("openid.mode") : null;

				echo("Mode: "+mode);
		Message response;
		String responseText;
		mode = "associate";
		if ("associate".equals(mode)) {
			// --- process an association request ---
			response = manager.associationResponse(request);
			responseText = response.keyValueFormEncoding();
		} else if ("checkid_setup".equals(mode)
				|| "checkid_immediate".equals(mode)) {
			// interact with the user and obtain data needed to continue
			List userData = userInteraction(request);

			String userSelectedId = (String) userData.get(0);
			String userSelectedClaimedId = (String) userData.get(1);
			Boolean authenticatedAndApproved = (Boolean) userData.get(2);

			// --- process an authentication request ---
			response = manager.authResponse(request, userSelectedId,
					userSelectedClaimedId,
					authenticatedAndApproved.booleanValue());

			if (response instanceof DirectError)
				return directResponse(httpResp, response.keyValueFormEncoding());
			else {
				// caller will need to decide which of the following to use:

				// option1: GET HTTP-redirect to the return_to URL
				return response.getDestinationUrl(true);

				// option2: HTML FORM Redirection
				// RequestDispatcher dispatcher =
				// getServletContext().getRequestDispatcher("formredirection.jsp");
				// httpReq.setAttribute("prameterMap",
				// response.getParameterMap());
				// httpReq.setAttribute("destinationUrl",
				// response.getDestinationUrl(false));
				// dispatcher.forward(request, response);
				// return null;
			}
		} else if ("check_authentication".equals(mode)) {
			// --- processing a verification request ---
			response = manager.verify(request);
			responseText = response.keyValueFormEncoding();
		} else {
			// --- error response ---
			response = DirectError.createDirectError("Unknown request");
			responseText = response.keyValueFormEncoding();
		}

		// return the result to the user
		return responseText;
	}
	
	private List userInteraction(ParameterList request) throws ServerException
    {
        throw new ServerException("User-interaction not implemented.");
    }

    private String directResponse(HttpServletResponse httpResp, String response)
            throws IOException
    {
        ServletOutputStream os = httpResp.getOutputStream();
        os.write(response.getBytes());
        os.close();

        return null;
    }


}
