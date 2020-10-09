/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ulr.l3i.strdfmining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Admin
 */
public class Query extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);

    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, UnsupportedEncodingException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String req = StringEscapeUtils.unescapeJavaScript(request.getParameter("q"));
        String format = request.getParameter("f").toLowerCase();
        String algo = StringEscapeUtils.unescapeJavaScript(request.getParameter("algo"));
        String result = "";
        if (algo.equals("none")) {

            try {
                result = QueryProcessor.getMD5(req + format);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
            }

            File file = new File(getServletContext().getRealPath("") + "/KB/Cache/" + result);
            if (file.exists()) {
                String line = "", l = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                while ((l = br.readLine()) != null) {
                   line+=l+"\n";
                }
                 out.write(line);

                br.close();
                out.close();

            } else {
                String resp = "";
                if (format.equals("json")) {
                    resp = QueryProcessor.queryJSON(req);

                } else if (format.equals("csv")) {
                    resp = QueryProcessor.queryCSVQ(req);

                }
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(resp);
                bw.close();
                out.write(resp);
                 out.close();
            }
        } else if (algo.equals("apriori")) {
            String param = request.getParameter("param").toLowerCase();
            String s = AprioriWeb.getRuleInstancesJSON(Integer.parseInt(param));

            out.print(s.replaceAll("'", ""));
             out.close();

        }

    }
}
