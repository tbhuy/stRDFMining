package ulr.l3i.strdfmining;

import eu.earthobservatory.org.StrabonEndpoint.client.EndpointResult;
import eu.earthobservatory.org.StrabonEndpoint.client.SPARQLEndpoint;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openrdf.query.resultio.stSPARQLQueryResultFormat;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author huy
 */
public class QueryProcessor {



    public static String queryCSV(String req) throws IOException {
        SPARQLEndpoint ep = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, ConfigManager.store+"/Query");
        System.out.print(ep.getConnectionURL());
        EndpointResult response = ep.query(req, stSPARQLQueryResultFormat.TSV);
        return response.getResponse().replaceAll("\t", ",");

    }

    //remove special characters
    //normalizing
    public static String queryCSVQ(String req) throws IOException {
        SPARQLEndpoint ep = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, ConfigManager.store+"/Query");
        //System.out.print(ep.getConnectionURL());
        EndpointResult response = ep.query(req, stSPARQLQueryResultFormat.TSV);
        //  System.out.print(response.getResponse());
        String ss[] = response.getResponse().split("\n");
        String var[] = ss[0].split("\t");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < var.length; i++) {
            sb.append("," + var[i]);
        }
        sb.delete(0, 1);
        sb.append("\n");

        for (int i = 1; i < ss.length; i++) {
            System.out.print(i);
            JSONObject obj = new JSONObject();
            String sss[] = ss[i].split("\t");
            String str = "";
            for (int j = 0; j < var.length; j++) {
                String s = new String(sss[j].getBytes("ISO-8859-1"), "UTF-8");
                if (s.indexOf("^") > 0) {
                    s = s.substring(0, s.indexOf("^"));
                }

                s = s.replaceAll("\"", "");
                s = s.replaceAll("'", "_");

                str = str + ",\"" + s + "\"";

            }
            sb.append(str.substring(1) + "\n");

        }
        return sb.toString();

    }

    // Since Strabon does not support DumpKB nor export in RDF format..
    public static void DumpKB(String fn) throws IOException {
        File f = new File(fn);
        FileWriter fw = new FileWriter(f);
        SPARQLEndpoint ep = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, ConfigManager.store+"/Query");
        System.out.print(ep.getConnectionURL());
        EndpointResult response = ep.query("Select * where {?s ?p ?o.}", stSPARQLQueryResultFormat.TSV);
        String s[]=response.getResponse().split("\n");
        // Remove the first line (s,p,o)
        for(int i=1;i<s.length;i++)
        {
           String ss[]=s[i].split("\t");
           
        fw.write("<"+ss[0]+"> <"+ss[1]+"> <"+ss[2]+">. \n");
        }
        fw.close();

    }

  
    public static String queryJSON(String req) throws IOException {

        SPARQLEndpoint ep = new SPARQLEndpoint(ConfigManager.host, ConfigManager.port, ConfigManager.store+"/Query");
        //System.out.print(ep.getConnectionURL());
        EndpointResult response = ep.query(req, stSPARQLQueryResultFormat.TSV);

        //   System.out.print(response.getResponse());
        String ss[] = response.getResponse().split("\n");
        String var[] = ss[0].split("\t");

        JSONArray json = new JSONArray();

        for (int i = 1; i < ss.length; i++) {
            //System.out.print(i);
            JSONObject obj = new JSONObject();
            obj.put("id", i);
            String sss[] = ss[i].split("\t");
            
            for (int j = 0; j < var.length; j++) {
                String s = new String(sss[j].getBytes("ISO-8859-1"), "UTF-8");
                if (s.indexOf("^") > 0) {
                    s = s.substring(0, s.indexOf("^"));
                }
                s = s.replaceAll("\"", "");
                obj.put(var[j], s);
            }
           // System.out.print(i);
            json.add(obj);

        }
        return json.toJSONString();

    }

    public static String getMD5(String req) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String req2 = req.toLowerCase();
        String hash = req2.substring(req2.indexOf("select"));
        hash = hash.replaceAll("\\s+", "");
        hash = hash.replaceAll("\\r|\\n", "");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest(hash.trim().getBytes("UTF-8"));
        return new String(Hex.encodeHex(md5));
    }

}
