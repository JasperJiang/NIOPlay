package com.demo.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BioServerHandler implements Runnable{

    private Socket socket;

    public BioServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            int count = 0;
            String content = null;
            byte[] bytes = new byte[1024];
            while ((count = inputStream.read(bytes))!=-1){
                String line = new String(bytes, 0, count, "utf-8");
                System.out.println(line);
                content = line.trim().equalsIgnoreCase("time") ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) : "can't understand";
                outputStream.write(content.getBytes());
                //只是写到缓冲区，flush才会真正写
                outputStream.flush();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
