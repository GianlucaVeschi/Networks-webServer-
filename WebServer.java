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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Properties;
//** Ejemplo que implementa un servidor MULTIHILO de eco usando TCP. */
public class WebServer{
     public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Formato: ServidorTCP <puerto>");
            System.exit(-1);
        }
        //GET index.html HTTP/1.1 
        //If-Modified-Since: Fri, 10 Jan 2014 13:03:32 GMT
        ServerSocket socketServer = null; //donde eschuca el servidor
        Socket socket = null;
        try {
            //creamos el socket del servidor
            socketServer = new ServerSocket(Integer.parseInt(argv[0]));
            //establecemos un timeout de 30 s
            socketServer.setSoTimeout(60000);
            while (true) { //BUCLE infinito
                //esperamos posibles conexiones
                socket = socketServer.accept();
                //creamos un objecto de nuestra clase ThreadServer y
                //pasamos la nueva conexion al constructor
                ThreadServer threadS = new ThreadServer(socket);
//                De esta manera, la conexión es procesada con este
//                nuevo socket en un hilo de ejecución independiente, quedando el socket
//                servidor preparado para recibir nuevas peticiones de otros clientes
                
                //luego iniciamos su ejecucion con el metodo start (metodo de clase Thread)
                threadS.start();
            }
        } catch (SocketTimeoutException e) {
            System.err.println("60 segs sin recibir nada");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                //cerramos el socket del servidor
                socketServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
