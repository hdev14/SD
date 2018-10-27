/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package berkley.cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * @author hermerson
 */
public class Cliente2 {
    public static void main(String[] args) throws Exception {
        
        //VARIAVEIS UTILIZADAS
        Timestamp tempoCliente = new Timestamp(System.currentTimeMillis());
        Socket servidor = new Socket("127.0.0.1", 12345);
        Scanner tela = new Scanner(System.in);
        String mensagem = "";
        
        System.out.println("Esperando resposta do Servidor...");
        
        //RECEBE A ORDEM DO SERVIDOR PARA MANDA A DIFERENÇA
        ObjectInputStream input = 
                new ObjectInputStream(servidor.getInputStream());
        mensagem = input.readUTF();
        
        System.out.println(mensagem);
        
        //RECEBE O TEMPO DO SERVIDOR
        Timestamp tempoServidor = (Timestamp)input.readObject();
        
        System.out.println("Tempo Cliente -> " + tempoCliente.toString());
        System.out.println("Tempo Servidor -> " + tempoServidor.toString());
        
        //CALCULA A DIFERENÇA OBS(USA A FUNÇÃO DA LIB MATH PRA QUE O VALOR VENHA ABSOLUTO
        Timestamp tempoDiferenca = new Timestamp(Math.abs(tempoCliente.getTime() - tempoServidor.getTime()));
        
        //IMPRIME A DIFERENÇA, COMO A RESPOSTA É MUITO RAPIDA O TEMPO DE DIFERENÇA E EM MILISEG
        //A CADA  1 MILISEGUNDOS SÃO 1 MILHÃO DE NANOSEGUNDOS
        System.out.println("Diferença -> " + tempoDiferenca.getNanos()/1000000 + "milisecs");
        
        //ENVIA O TEMPO COM A DIFERENÇA PARA O SERVIDOR
        ObjectOutputStream output = new ObjectOutputStream(servidor.getOutputStream());
        output.writeObject(tempoDiferenca);
        
        //RECEBE O TEMPO CORRETO
        input = new ObjectInputStream(servidor.getInputStream());
        Timestamp tempoCorreto =  (Timestamp)input.readObject();
        
        System.out.println("Tempo Sincronizado -> " + tempoCorreto.toString());
        
        
    }
}
