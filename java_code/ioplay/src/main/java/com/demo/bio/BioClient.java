package com.demo.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 服务端
 */
public class BioClient {

    public static void main(String[] args) {

        Socket socket = null;
        OutputStream outputStream = null;
        try {
            socket = new Socket("127.0.0.1", 9090);
            new Thread(new BioClientHandler(socket)).start();
            outputStream = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            System.out.println("input:");
            while (true){
                String s = scanner.nextLine();
                if (s.trim().equals("exit")){
                    break;
                }
                outputStream.write(s.getBytes());
                outputStream.flush();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
