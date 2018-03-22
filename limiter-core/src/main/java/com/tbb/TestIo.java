package com.tbb;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: tubingbing
 * @Date: 2017/3/22
 * @Time: 12:47
 */
public class TestIo {

    public static void main(String[] args) {
         try {
             ServerSocket serverSocket = new ServerSocket(8099);
             while(true) {
                 Socket socket = serverSocket.accept();

                 InputStream in = socket.getInputStream();
                 byte[] bytes = new byte[1024];
                 StringBuilder sb = new StringBuilder();
                 if (in.read(bytes) > 0) {
                     sb.append(new String(bytes,0,bytes.length));
                 }
                 System.out.println(sb.toString());

                 OutputStream out = socket.getOutputStream();
                 String s ="<html><body>"+sb.toString()+"</body></html>";
                 out.write(s.getBytes());
                 //out.write("\n".getBytes());
                 //out.write("测试".getBytes());
                 out.flush();
                 out.close();
             }
         }catch (IOException e){
            e.printStackTrace();
         }



    }
}
