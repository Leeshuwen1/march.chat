package ru.mulyukin.otus.march.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.in;

public class ClientApplycation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(in);
        try (
                Socket socket = new Socket("localhost", 8189);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Подключились к серверу");
            new Thread(() -> {
                try {
                    while (true) {
                        String inMessage = inputStream.readUTF();
                        System.out.println(inMessage);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
            while (true) {
                String message = scanner.nextLine();
                outputStream.writeUTF(message);
                if(message.startsWith("/exit")){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
