/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package berkley.servidorThread;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author hermerson
 */
public class ServidorThread implements Runnable{
    
    private static ArrayList<Socket> clientes = new ArrayList();
    private static ArrayList<Integer> diferencas = new ArrayList();
    
    public ServidorThread(Socket cliente){
        //ADICIONAR O SOCKET DO CLIENTE NO ARRAYLIST
        this.clientes.add(cliente);
        //CHAMA O METODO INICIAR THREAD
        this.iniciarThread();
    }
    
    //O MÉTODO RUN SÓ CHAMA O MÉTODO pedirDiferenca().
    @Override
    public void run() {
        
        this.pedirDiferenca();
    }
    
    /*
        ESSE MÉTODO FAZ A LÓGICA DE PEDIR O TEMPO DO CLIENTE, DEPOIS DISSO ELE
        ELE ARMAZENA AS DIFERENÇAS EM UM ARRAYLIST QUE GUARDA AS DIFERENÇAS DE TEMPO
        DE CADA CLIENTE.
    
        DEPOIS QUE ELE PEGAR TODAS AS DIFERENÇAS ELE CHAMA O MÉTODO ajustarTempo();
    */
    private void pedirDiferenca(){
        
        Timestamp tempoDiferenca;
        int diferenca = 0;
        System.out.println("Pedir Diferença...");
        try{
            
            for(Socket cliente: this.clientes){
                
                ObjectOutputStream output = 
                        new ObjectOutputStream(cliente.getOutputStream());
               
                output.writeUTF("Diferença de tempo ?");
                output.flush();
                
                output.writeObject(new Timestamp(System.currentTimeMillis()));
                output.flush();
                
                ObjectInputStream input = 
                        new ObjectInputStream(cliente.getInputStream());
                tempoDiferenca = (Timestamp)input.readObject();
                diferenca = tempoDiferenca.getNanos()/100000;
                
                /*
                    Verificar se a diferença é muito grande, se não for grande 
                    adicionar a diferença na lista de diferenças, se for remove 
                    o cliente da lista de clientes e fecha a conexão dele.
                */
                
                if(verificarDiferenca(diferenca)){
                    this.diferencas.add(diferenca);
                }else{
                    cliente.close();
                    this.clientes.remove(cliente);
                }
                
            }
         
            this.ajustarTempo();
            
        }catch(Exception e){
            System.out.println("Pedir Diferença Error -> " + e);
        }
    }
    
    //VERIFICA SE A DIFERENCA É MUITO GRANDE.
    private boolean verificarDiferenca(int dif){
        //verifica se a diferença é maior do que 1 min.
        if(dif < 60000)
            return true;
        
        return false;
    }
    /*
        ESSE MÉTODO PECORRE O ARRAYLIST DE DIFERENÇAS COLETANDO TODAS AS DIFERENÇAS,
        DEPOIS ELE CALCULA A MÉDIA DE DIFERENÇAS DIVIDINDO OS TOTAIS DE DIFERENÇAS PELAS
        CONEXÕES MAIS O SERVIDOR.
    
        DEPOIS DISSO REMOVE TODOS AS DIFERENÇAS DO ARRAYLIST, PARA PRÓXIMAS CONEXÔES.
    */    
    private int calcularMedia(){
        
        System.out.println("Calcular Média...");
        
        int total = 0;
        
        for(Integer diferenca : this.diferencas){
            total += diferenca;
        }
        
        this.diferencas.removeAll(this.diferencas);
        
  
        return total/(this.clientes.size() + 1);
    }
    
    /*
        ESSE MÉTODO VAI VERIFICAR SE JÁ SE CONECTARAM MAIS DE 1 CLIENTE PEGANDO O TAMANHO DO
        ARRAYLIST, DEPOIS QUE OS DOIS CLIENTE TIVEREM CONECTADOS É CRIADA UM THREAD E CHAMADO
        O MÉTODO START() PARA QUE OS PROCESSO SEJAM INICIADOS.
    */
    private void iniciarThread(){
        System.out.println("Quantidade de clientes -> " + this.clientes.size());
        if(this.clientes.size() > 1){
            Thread th = new Thread(this);
            th.start();
        }
        
    }
    
    /*
        ESSE MÉTODO PRIMEIRO CHAMA O MÉTODO CalcularMedia(), DEPOIS RETORNA PARA
        CADA CLIENTE O VALOR DE TEMPO CORRETO, OU SEJA, O TEMPO ATUALIZADO.
    
        DEPOIS DISSO REMOVE TODOS OS CLIENTES DO ARRAYLIST PARA PRÓXIMAS CONEXÕES.
    */
    private void ajustarTempo(){
        
        System.out.println("Ajustar Tempo...");
        
        int media = this.calcularMedia();

        try{
            for(Socket cliente :  this.clientes){
            
                ObjectOutputStream output = 
                        new ObjectOutputStream(cliente.getOutputStream());
                
                Timestamp tempoCorreto =  
                        new Timestamp(System.currentTimeMillis());
                tempoCorreto.setNanos(tempoCorreto.getNanos() + media);
                
                output.writeObject(tempoCorreto);
                output.flush();
                
                
                cliente.close();
           
            }
            
            this.clientes.removeAll(this.clientes);
            
        }catch(Exception e){
            System.out.println("Ajusta Tempo Error -> " + e);
        }
        
        System.out.println("Tempos Ajustados!");
        
    }
    
}
