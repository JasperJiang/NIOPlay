package com.demo.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端
 */
public class BioServer {

    ServerSocket serverSocket = null;

    public BioServer() {
        try {
            //new了socket
            serverSocket = new ServerSocket(9090);
            TimeServerHandlerExecutorPool timeServerHandlerExecutorPool=new TimeServerHandlerExecutorPool(50,1000);
            while (true){
                //阻塞的，只要有一个客户端来连接就会执行
                final Socket socket = serverSocket.accept();
                System.out.println("客户端" + socket.getRemoteSocketAddress().toString() + "来连接了");
//                new Thread(new BioServerHandler(socket)).start();
                timeServerHandlerExecutorPool.execute(new BioServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start(){

    }

    public static void main(String[] args) {

        new BioServer().start();

    }

}
