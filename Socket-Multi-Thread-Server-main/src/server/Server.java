/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Admin
 */
public class Server {
    public static int clientNumber = 0;
    public static int port = 7775;
    public static int portClient = 7777;
    public static InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static ThreadPoolExecutor executor;
    public Server() {
    }

    public static volatile ServerThreadBus serverThreadBus;
    public static DatagramSocket socketOfServer;
    public static void addThread(int port) throws SocketException {

        DatagramSocket socketOfServer = new DatagramSocket(port);
        ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++,port);
        serverThreadBus.add(serverThread);
        System.out.println("Số thread đang chạy là: "+serverThreadBus.getLength());
        executor.execute(serverThread);
    }

    public static void main(String[] args) throws SocketException {
//        DatagramSocket listener = null;
        serverThreadBus = new ServerThreadBus();
        System.out.println("Server is waiting to accept user...");

        executor = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );
        socketOfServer = new DatagramSocket(port);
        byte[] data = new byte[1024];
        DatagramPacket DP = new DatagramPacket(data,data.length);
        String mess;
        try {
            while (true) {
                socketOfServer.receive(DP);
                mess = new String(DP.getData(),0,DP.getLength());
                if(mess.equals("connected")){
                    String resp = String.valueOf(portClient);
                    byte[] d = resp.getBytes();
                    DatagramPacket sendPort = new DatagramPacket(d,d.length,DP.getAddress(),DP.getPort());
                    socketOfServer.send(sendPort);
                    System.out.println("send port: "+portClient + " to client");
                    addThread(portClient);
                    portClient+=2;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
