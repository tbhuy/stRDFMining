/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Admin
 */
public class CacheCheck extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);

    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, UnsupportedEncodingException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String query = request.getParameter("query");
        String result = "";
        try {
            result = QueryProcessor.getMD5(query + "json");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CacheCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = new File(getServletContext().getRealPath("") + "/KB/Cache/" + result);
        if(file.exists())
            out.write("Cached: "+result);
        else
            out.write("Not cached");

    }
}
