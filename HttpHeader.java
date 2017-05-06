/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Gianluca
 */
public class HttpHeader {
    
    private String [] tokens;
    private File in;
    String header = "HTTP/1.0 ";
    private int code;
    private String size;
    private FileInputStream config = null;
    
    
    public HttpHeader(String [] tokens) {
        this.tokens = tokens; 
        
        if(tokens[1].equals("") || tokens[1].equals("/")){
            in = new File("src/webserver/index.html"); // directorio por defecto en caso de no incluirlo
        }else{
            in = new File("src/webserver/" + tokens[1]);
        }
    }
    
    public String getHeader() {
        
        header = generateCode(); // if everything goes right "header" would contain // HTTP/1.0 200 OK 
        String [] tmp = header.split(" ");
        code = Integer.parseInt(tmp[1]);
        size = String.valueOf(in.length());
        
        
        header += "\r\nDate: ";
        header += new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(Calendar.getInstance().getTime());
        header += "\r\n";
        header += "Server: webServer de Gianluca";
     
        header += "\r\n";  
        
        if(code == 200){
            header += "Last-Modified: ";
            header += new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(in.lastModified());
            header += "\r\n"; 
            header += "Content-Length: " + in.length();
            header += "\r\n";
            header += "Content-Type: " + getFileType(tokens[1]);     
            header += "\r\n\r\n";  //Linea de Cabecera Vacia obligatoria
        }
        else if(code == 404){
            File error = new File("/home/gianluca/NetBeansProjects/WebServer/404/404.html");
            header += "Content-Length: " + error.length();
            header += "\r\n";
            header += "Content-Type: text/html";     
            header += "\r\n\r\n";  //Linea de Cabecera Vacia obligatoria
            
        }
        
        
        return header;
    }
    
    
    public String generateRequestHeader(int statusCode) {
        
        switch (statusCode)
          {
            case 200:
                header += "200 OK";
                break;
            case 304:
                header += "304 Not Modified";
                break;
            case 400:
                header += "400 Bad Request";
                break;
            case 403:
                header += "403 Forbidden";
                break;
            case 404:
                header += "404 Not Found";
                break;
          }
        
        return header;
    } 
    
    public String generateCode() {
        
        String protocol = tokens[2].toUpperCase(); // normal request peticion recibida GET /index.html HTTP/1.1
        
        if (!((protocol.equals("HTTP/1.1") || protocol.equals("HTTP/1.0")))){
              return generateRequestHeader(400);
        }

        if(!in.exists()) {
            return generateRequestHeader(404);
        } else if(!in.isFile() || in.isDirectory()) {
            //IN THIS CASE WE SHOULD LOOK IN THE CONFIG FILE IF WE CAN ACCESS THE
            //DIRECTORY THROUGH THE ALLOW FIELD
            return generateRequestHeader(403);
        } else { // if the file exists and it is not a directory it's OK  
            return generateRequestHeader(200);
        }
    }
    
    public String getFileType(String file){
        String tmp = file.substring(file.lastIndexOf("."));
        String type = tmp.substring(1);
        
        switch(type){
            case("html") : 
            case("htm" )  :
                type = "text/html"; break;
            case("txt") :
                type = "text/plain"; break;
            case("jpg" ) :
            case("jpeg") :   
                type = "image/jpg"; break;
            case("gif") :
                type = "image/gif"; break;
            default:
                type = "application/octet-stream"; break;
        }
        return type;
    }

    public int getCode(){
        return code;
    }
    public String getSize(){
        return size;
    }
  }
