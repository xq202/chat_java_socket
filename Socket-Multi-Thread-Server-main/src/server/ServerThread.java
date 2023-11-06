/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
import java.io.IOException;
import java.net.*;

/**
 *
 * @author Admin
 */
public class ServerThread implements Runnable {

    private DatagramSocket socketOfServerSend;
    private DatagramSocket socketOfServerReceive;
    private int clientNumber;
    private int port;
//    private BufferedReader is;
//    private BufferedWriter os;
    private DatagramPacket DP;
    private InetAddress serverAddress;
    private boolean isClosed;

//    public BufferedReader getIs() {
//        return is;
//    }

//    public BufferedWriter getOs() {
//        return os;
//    }

    public int getClientNumber() {
        return clientNumber;
    }

    public ServerThread(DatagramSocket socketOfServer, int clientNumber,int port) {
        this.socketOfServerReceive = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        isClosed = false;
        this.port = port;
        System.out.println("listener: " + port);
    }
    public void endClient(){
        this.isClosed = true;
        Server.serverThreadBus.remove(clientNumber);
        System.out.println(this.clientNumber+" đã thoát");
        Server.serverThreadBus.sendOnlineList();
        Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.clientNumber+" đã thoát---");
    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
//            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
//            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            serverAddress = InetAddress.getByName("localhost");
            System.out.println("Khời động luông mới thành công, ID là: " + clientNumber);
            socketOfServerSend = new DatagramSocket();
            write("get-id" + "," + this.clientNumber);
            System.out.println("get-id" + "," + this.clientNumber);
            Server.serverThreadBus.sendOnlineList();
            Server.serverThreadBus.mutilCastSend("global-message"+","+"---Client "+this.clientNumber+" đã đăng nhập---");
            System.out.println("global-message"+","+"---Client "+this.clientNumber+" đã đăng nhập---");
            String message;
            byte[] data = new byte[1024];
            DP = new DatagramPacket(data,data.length);
            while (!isClosed) {
                socketOfServerReceive.receive(DP);
                message = new String(DP.getData(),0,DP.getLength());
                if (message == null || message=="") {
                    break;
                }
                else System.out.println(message);
                if(message.equals("end")){
                    System.out.println("endClient");
                    endClient();
                    break;
                }
                String[] messageSplit = message.split(",");
                if(messageSplit[0].equals("send-to-global")){
                    Server.serverThreadBus.boardCast(this.getClientNumber(),"global-message"+","+"Client "+messageSplit[2]+": "+messageSplit[1]);
                }
                if(messageSplit[0].equals("send-to-person")){
                    Server.serverThreadBus.sendMessageToPersion(Integer.parseInt(messageSplit[3]),"Client "+ messageSplit[2]+" (tới bạn): "+messageSplit[1]);
                }
            }
        } catch (IOException e) {
            endClient();
        }
    }
    public void write(String message) throws IOException{
        byte[] data = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, port+1);
        socketOfServerSend.send(sendPacket);
        System.out.println("send :`" + message + "` to port: " + (port+1));
    }
}
