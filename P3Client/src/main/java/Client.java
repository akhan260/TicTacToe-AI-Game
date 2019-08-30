import com.sun.corba.se.spi.activation.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Consumer;


public class Client extends Thread{

    //all the necessary things we need
    Socket socketClient;
    Scanner scanner;
    ObjectOutputStream out;
    ObjectInputStream in;
    int port;
    String ip;
    String test;
    private Consumer<Serializable> callback;
    ArrayList<ServerSocket> clients = new ArrayList<ServerSocket>();
    private int count = 1;

    GameInfo info;
    String temp;

    String data;



    //Client constructor
    Client(Consumer<Serializable> call, int portNum, String ipAd){
        port = portNum;
        callback = call;
        info = new GameInfo();
        ip = ipAd;
        data = "";

    }




    public void run() {
        try {
            //socketClient
            socketClient= new Socket("127.0.0.1",port);
            //incoming data and outgoing data
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);

            while(true) {
                System.out.print("start\n");
//                GameInfo check = (GameInfo) in.readObject(); //taking the incoming data and printing to the client
//                data = check.stateGame;
//                temp = data.toString();
                info = (GameInfo)(Serializable) in.readObject(); //taking the incoming data and printing to the client
                temp = info.data;
                System.out.print( temp + "\n");
                callback.accept(temp);//accepting the data
                 System.out.print("end\n");
//                }


            }
        }
        catch(Exception e) {}
    }

    //data sending to the server
    public void send(String s) {

        try {
            GameInfo temp5 = new GameInfo();
            temp5.data = s;
            out.writeObject(s);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
