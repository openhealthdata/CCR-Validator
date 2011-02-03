/*
 * Copyright 2010 OpenHealthData, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhealthdata.validator;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.*;

import 	 java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidatorServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * 
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		validator = new ValidationManager();
		//TODO covert this to a dynamic runtime allocation
	}
	
	ValidationManager validator;
	// TODO Set to NUll in production environment
	// THIS MUST BE CHANGED FOR YOUR IMPLEMENTATION if running in NAT'ed environment see README.txt
	// applicationBase should be in the form of:   http://{server}:{port}/{webappcontext}
	// :{port} is not needed if on port 80.
	static String applicationBase = null;
	
	// TODO This does not work in a NAT'ed environment
	public static void setApplicationBase(HttpServletRequest req) {
	    if (applicationBase == null) {
	        applicationBase = req.getScheme() + "://" + req.getServerName() +
	                getPort(req) + req.getContextPath();
	    }
	}

	private static String getPort(HttpServletRequest req) {
	    if ("http".equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 80 ||
	            "https".equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 443 ) {
	        return (":" + req.getServerPort());
	    } else {
	        return "";
	    }
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	/**
	 * Accept a POST request with a file upload to be validated
	 * Response is an XML file with an internal XSL reference
	 */
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response)
	throws ServletException, IOException
	{
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		setApplicationBase(request);
		
		
		//Check that we have a file upload request
		boolean isMultipart = FileUpload.isMultipartContent(request);
		if (isMultipart){
			// Create a new file upload handler
			DiskFileUpload upload = new DiskFileUpload();
			// Parse the request
			List items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				logger.log(Level.SEVERE, "Exception processing upload file request", e);
				e.printStackTrace();
			}
			
			// Process the uploaded items
			Iterator iter = items.iterator();
			
			FileItem item = (FileItem) iter.next();
			StringBuffer sb = new StringBuffer();
			if (!item.isFormField()) {
				String testFileName = item.getName();
				testFileName  = testFileName.replace('\\','/');
				
				String [] path = testFileName.split("/");
				testFileName = path[path.length-1];
				
				String[] fName = item.getName().split("\\|/");
				String fileName = "tmpValidationFile.tmp";
				if (fName.length>0){
					fileName = fName[fName.length-1];
				}
				File uploadedFile = new File(fileName);
				try {
					item.write(uploadedFile);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Exception processing uploaded file item", e);
					//e.printStackTrace();
				}
				//Add XSL Reference to result XML
				// To convert to a RESTful interface, this XSL reference could be removed
				String xslRef = "<?xml-stylesheet type=\"text/xsl\" href=\""+applicationBase+"/XSLT/validationTest.xsl\"?>";
				String xmlRef = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
				String replaceStr = "<\\?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"\\?>";
				String valRes = validator.validateToString(uploadedFile);
				String result = valRes.replaceFirst(replaceStr, xmlRef+xslRef);
				//Add to response stream
				sb.append( result );
				//TODO Convert to save XML files
				uploadedFile.delete();
			}
			out.print(sb.toString());
		}//END if (isMultipart)    
		out.close();
	}
}


