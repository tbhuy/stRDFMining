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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Huy Tran
 */
public class Config extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);

    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, UnsupportedEncodingException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (request.getParameter("job").equals("up")) {

            File file = new File(getServletContext().getRealPath("/") + "config");
            System.out.print(file.getAbsolutePath());
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(request.getParameter("host") + "\n");
            bw.write(request.getParameter("port") + "\n");
            bw.write(request.getParameter("storename") + "\n");
            bw.write(request.getParameter("supp") + "\n");
            bw.write(request.getParameter("conf") + "\n");
            bw.write(request.getParameter("num") + "\n");
            bw.write(request.getParameter("min") + "\n");

            bw.close();
            loadConfig();
            out.write("OK");
            out.close();

        } else if (request.getParameter("job").equals("down")) {
            loadConfig();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getServletContext().getRealPath("/") + "config"))));
            String line = "", l = "";
            while ((l = br.readLine()) != null) {
                line += l + "\n";
            }
            out.write(line);

            br.close();
            out.close();

        } else {
            loadConfig();

        }
    }

    private void loadConfig() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getServletContext().getRealPath("/") + "config"))));
        String line = "", l = "";
        ConfigManager.host = br.readLine();
        ConfigManager.port = Integer.parseInt(br.readLine());
        ConfigManager.store = br.readLine();
        ConfigManager.supp = Float.parseFloat(br.readLine());
        ConfigManager.conf = Float.parseFloat(br.readLine());
        ConfigManager.num = Integer.parseInt(br.readLine());
        ConfigManager.min = Integer.parseInt(br.readLine());

        br.close();
    }

}
