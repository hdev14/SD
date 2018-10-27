/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package berkley.servidor;

import berkley.servidorThread.ServidorThread;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author hermerson
 */
public class Servidor {
    
    public static void main(String[] args) throws IOException {
       
        ServerSocket servidor = new ServerSocket(12345);
        while(true){
            Socket cliente = servidor.accept();
            ServidorThread st = new ServidorThread(cliente);
           
        }
    }
}
