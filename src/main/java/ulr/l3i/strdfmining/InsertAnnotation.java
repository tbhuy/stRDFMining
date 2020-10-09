/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;

import eu.earthobservatory.org.StrabonEndpoint.client.SPARQLEndpoint;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Admin
 */
public class InsertAnnotation extends HttpServlet {
    private static String u = "endpoint";
    private static String p = "3ndpo1nt";
    private static String graph = "http://geminat.uni-lr.fr/strdfmining";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);

    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, UnsupportedEncodingException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String triple = request.getParameter("triple");
        SPARQLEndpoint endpoint = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, 
                ConfigManager.store+"/Store");
        endpoint.setUser(u);
        endpoint.setPassword(p);   
        Boolean resp = endpoint.store(triple, RDFFormat.TURTLE, new URL(graph));
        if (resp) 
           out.write("Store OK!");
        else 
           out.write("Store failed!");
            

    }
}
