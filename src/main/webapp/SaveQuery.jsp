<%-- 
    Document   : SaveQuery
    Created on : Nov 10, 2016, 3:58:16 AM
    Author     : Huy Tran
--%>

<%@page import="java.io.FileWriter"%>
<%@page import="java.io.StringReader"%>
<%@page import="java.io.BufferedWriter"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
                                        BufferedWriter outs = new BufferedWriter(new FileWriter(getServletContext().getRealPath("/").replace('\\', '/')+"KB/Query/"+request.getParameter("fn")));
					StringReader text = new StringReader(request.getParameter("text"));
					int i;
					boolean cr = false;
					String lineend = "\r\n";
					
					while ((i = text.read()) >= 0) {
						if (i == '\r') cr = true;
						else if (i == '\n') {
							outs.write(lineend);
							cr = false;
						}
						else if (cr) {
							outs.write(lineend);
							cr = false;
						}
						else {
							outs.write(i);
							cr = false;
						}
					}
					outs.flush();
					outs.close();
                                        out.write("Done");
            %>