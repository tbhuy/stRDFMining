/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;

import de.fuberlin.wiwiss.d2rq.CommandLineTool;
import d2rq.dump_rdf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Admin
 */
public class RDFDump extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);

    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        //reponse handle
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        //Parameters
        String fn = request.getParameter("fn");
        String view = request.getParameter("view");
        String mapping = request.getParameter("mapping");
        String file = getServletContext().getRealPath("") + "/" + Math.abs(mapping.hashCode()) + ".ttl";
        String dest = getServletContext().getRealPath("") + "/KB/Store/" + fn;
        //Write mapping content to disk
        FileWriter filewriter = new FileWriter(file);
        filewriter.write(mapping);
        filewriter.close();
        
        // Dump with D2RQ
        String command = "-out " + dest + " " + file;
        new dump_rdf().process(command.split(" "));

        //Read result   
        File f = new File(dest);
        if (f.exists()) {
            
            if (view.equals("true")) {
                BufferedReader reader = new BufferedReader(new FileReader(dest));
                //BufferedReader br = new InputStreamReader(new FileInputStream(txtFilePath));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                out.println(sb.toString());
            } else {
                out.write("Dump OK.");
            }
        }
        else
        {
            out.write("Dump Failed.");
        }
    }

}
