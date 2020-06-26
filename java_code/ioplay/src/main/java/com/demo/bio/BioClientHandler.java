package com.demo.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BioClientHandler implements Runnable{

    private Socket socket;

    public BioClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            int count = 0;
            byte[] bytes = new byte[1024];

            while ((count = inputStream.read(bytes)) != -1) {
                System.out.println("收到消息: " + new String(bytes, 0, count, "utf-8"));
                System.out.println("请发送消息：");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
