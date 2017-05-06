/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

/**
 *
 * @author gianluca
 */
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
//** Thread que atiende una conexiÃ³n 
public class ThreadServer extends Thread {
    private Socket socket;
    private File fileToSend;
    //private final String contentDir = "src/webserver/";
    private final String contentDir = "/home/gianluca/NetBeansProjects/WebServer/src/webserver/";
    
    
    public ThreadServer(Socket s) {
        this.socket = s;
    }
    
    @Override
    public void run() {
        try {
            // Establecemos el canal de entrada
            BufferedReader sEntrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Establecemos el canal de salida
            //PrintWriter sSalida = new PrintWriter(socket.getOutputStream(), true);
            OutputStream sSalida = socket.getOutputStream();
            //WEB SERVER CODE    
            processPetition(sEntrada, sSalida);
            
                
            sSalida.close();
            sEntrada.close();
                
        } catch (SocketTimeoutException e) {
            System.err.println("30 segs sin recibir nada");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
           // Cerramos el socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void processPetition(BufferedReader input, OutputStream output) {
          
        String request; // peticion HTTP
        ArrayList <String> list = new ArrayList<>();
        String [] tokens; 
    
        
        try {
            //RECEIVE PETITION
            request = input.readLine();
            System.out.println("peticion recibida " + request);
            
            String mod = null;//para guardarlo
            String line;
            
//            while(!(request.equals("\t\n"))){
//                if(request.contains("If-Modified-Since")){
//                    mod = request;
//                    System.out.println("MODIFICATOOOOO");
//                    break;
//                }
//                line = request;
//            }
//            
            
            //HANDLE PETITION
            tokens = request.split(" ");        //peticion HTTP troceada
            String tmp = tokens[0].toUpperCase(); //contains the method GET/HEAD
            HttpHeader header = new HttpHeader(tokens);
            
                        
            fileToSend = new File(contentDir + tokens[1]);

            if(tmp.equals("GET")) {
 
                
                output.write(header.getHeader().getBytes());
                System.out.println(header.getHeader());              
                sendFile(output);
                
                if(header.getCode() == 200){
                    logAccess (request,currDateStr(), header,header.getCode(),header.getSize());
                }else{
                    logError (request,currDateStr(), header,header.getCode());
                }
                
                
            }else if(tmp.equals("HEAD")){ 
                output.write(header.getHeader().getBytes());
                System.out.println(header.getHeader());                
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendFile(OutputStream output) throws IOException {     
        FileInputStream in = null;
        int bytesRead;
        
        try { 
            if(fileToSend.exists() && !fileToSend.isDirectory()){
            
                in = new FileInputStream(fileToSend.getPath());  
            }else if(fileToSend.isDirectory()){
                
                in = new FileInputStream(listDirectory().getPath());
               
            }else if(!fileToSend.exists()){
                
                File notFound = new File("/home/gianluca/NetBeansProjects/WebServer/404/404.html");
                in = new FileInputStream(notFound.getPath());
            } 
            
            while ((bytesRead = in.read()) != -1) {
                
                 output.write(bytesRead); 
            }
            output.flush();
        }
        
        catch (IOException e) {
            System.err.println(e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    
     private void logAccess(String request, String recDate, HttpHeader head,int code,String size) {
        File fh;

        try {

            fh = new File("/home/gianluca/NetBeansProjects/WebServer/log/logaccess.txt");
           
            FileWriter fw = new FileWriter(fh,true);
            BufferedWriter w = new BufferedWriter(fw);
            w.write(request + "\n");
            w.write(String.valueOf(socket.getInetAddress())+ "\n");
            w.write(recDate+ "\n");
            w.write(String.valueOf(code)+ "\n");
            w.write("File Size in bytes: " + size);
            w.write("\n"+"################################################## \n");
            w.close();
            

          }
        catch (SecurityException | IOException e)
          {
            e.printStackTrace();
          }

      }

    private void logError(String request, String recDate, HttpHeader head,int code) throws IOException {
        File fh;

        try{
            fh = new File("/home/gianluca/NetBeansProjects/WebServer/log/logerror.txt");
            
            
            FileWriter fw = new FileWriter(fh,true);
            BufferedWriter w = new BufferedWriter(fw);
            
            w.write(request + "\n");
            w.write(String.valueOf(socket.getInetAddress())+ "\n");
            w.write(recDate+ "\n");
            w.write(String.valueOf(code)+ "\n");
            w.write("\n"+"################################################## \n");
            w.close();
            w.close();
          }
        catch (IOException | SecurityException ex)
          {
            System.out.println(ex.getMessage());
          }

      }
    private String currDateStr () {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(Calendar.getInstance().getTime());
    }
    
    private File listDirectory() throws IOException{
        System.out.print("aqui es un directorio");
        
        Properties config = new Properties();
        InputStream configFile = new FileInputStream("/home/gianluca/NetBeansProjects/WebServer/conf/config.html");
        config.load(configFile);
        //we should see if the allow field is active
        File folder = new File("/home/gianluca/NetBeansProjects");
        File toShow = new File("/home/gianluca/NetBeansProjects/WebServer/conf/config.html"); //in this html file the name of the files to list are
                                                                                                      //printed
        FileWriter fw = new FileWriter(toShow,true);                                          
        BufferedWriter w = new BufferedWriter(fw);
                
        File[] listOfFiles = folder.listFiles();
               
        for(File file : listOfFiles){
            w.write("<!DOCTYPE html>\n<html>\n<body>\n<ul>\n<li><a href=\""+file.getName()+"\">"+file.getName()+"</a></li>\n</ul>\n</body>\n<html>");
        }
        return toShow;
    }
}

