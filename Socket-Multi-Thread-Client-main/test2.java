import java.net.*;
import java.io.*;

public class UDPServer {
    public static void main(String[] args) {
        DatagramSocket serverSocket = null;

        try {
            serverSocket = new DatagramSocket(7777); // Mở cổng 7777

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket); // Nhận tin nhắn từ máy khách

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if(receivedMessage.isEmpty()) break;
                System.out.println("Received from client: " + receivedMessage);

                // Xử lý tin nhắn tại đây (ví dụ: đảo ngược tin nhắn)
                String responseMessage = new StringBuilder(receivedMessage).reverse().toString();
                byte[] sendData = responseMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(sendPacket); // Gửi tin nhắn trả lời tới máy khách
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }
}
