/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;

import eu.earthobservatory.org.StrabonEndpoint.client.SPARQLEndpoint;
import java.io.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Admin
 */
public class StoreManager extends HttpServlet {

    private static String u = "endpoint";
    private static String p = "3ndpo1nt";
    private static String graph = "http://geminat.uni-lr.fr/strdfmining";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);

    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String type = request.getParameter("type");

        if (type.equals("update")) {
            SPARQLEndpoint endpoint = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, ConfigManager.store+"/Update");
            String query = request.getParameter("query");
            System.out.println(query);
            endpoint.setUser("endpoint");
            endpoint.setPassword("3ndpo1nt");
            Boolean resp = endpoint.update(query);
            if (resp) {
                out.write("Update OK!");
            } else {
                out.write("Update failed!");
            }

        } else {
            SPARQLEndpoint endpoint = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, ConfigManager.store+"/Store");
            String format = request.getParameter("format");
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            String data = request.getParameter("data");
            String url = request.getParameter("url");
            URL namedGraph = new URL(graph);
            Boolean resp = false;

            if (!(user.equals(u) && pass.equals(p))) {
                System.out.println("Auth failed");
                out.write("Authentification failed!");
                return;
            } else if (data.equals("")) {
                endpoint.setUser(user);
                endpoint.setPassword(pass);
                URL url2 = new File(url).toURI().toURL();
                resp = endpoint.store(url2, RDFFormat.valueOf(format), namedGraph);
                System.out.println("Storing data from file: " + url2);

            } else {
                resp = endpoint.store(data, RDFFormat.valueOf(format), namedGraph);
                System.out.println("Storing data from input");

            }
            if (resp) {
                out.write("Store OK!");
            } else {
                out.write("Store failed!");
            }
        }
    }

}
