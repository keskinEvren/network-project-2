/*
 * Bu kod bir sohbet uygulamasının istemci tarafı için yazılmıştır.
 */

package chatapp;

import Message.Message;
import static chatapp.Client.sInput;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Bu sınıf, sunucudan gelen mesajları dinlemek için bir thread'dir.
 */
class ServerListener extends Thread {

    @Override
    public void run() {
        while (Client.socket.isConnected()) {
            try {
                // Sunucudan gelen mesajı okuyoruz
                Message received = (Message) (sInput.readObject());

                switch (received.type) {

                    case CreateRoom:
                        // Sunucudan gelen odalar listesini alıyoruz ve güncelliyoruz
                        Login.Login.getRooms((ArrayList<String>) received.content);
                        System.out.println("Rooms list reached from server...");
                        break;
                    case JoinRoom:
                        // Sunucudan gelen bir odaya katılma mesajı aldık
                        // İlgili odayı buluyoruz
                        ArrayList<Object> aboutRoom = (ArrayList<Object>) received.content;
                        ChatRoom cr = Login.Login.findChatRoom((String) aboutRoom.get(0));
                        break;
                    case JoinServer:
                        Thread.sleep(100);
                        // Sunucuya bağlandıktan sonra kullanıcı listesini alıyoruz
                        Login.Login.getUsers((ArrayList<String>) received.content);
                        break;
                    
                    case UpdateRoomUsers:
                        // Bir odaya yeni bir kullanıcı katıldığında kullanıcı listesini güncelliyoruz
                        ArrayList<Object> updatedRoomList = (ArrayList<Object>) received.content;
                        ChatRoom updatedRoom = Login.Login.findChatRoom((String) updatedRoomList.get(0));
                        break;
                    case ReturnRoomsNames:
                        // Odaların isimlerini güncelliyoruz
                        Login.Login.getRooms((ArrayList<String>) received.content);
                        System.out.println("Rooms List Refreshed!");
                        break;
                    case RoomMessage:
                        // Bir odaya ait bir mesaj aldık
                        ArrayList<String> chatRoomMsg = (ArrayList<String>) received.content;
                        ChatRoom chatRm = Login.Login.findChatRoom(chatRoomMsg.get(1));
                        chatRm.getMessage(chatRoomMsg.get(0), chatRoomMsg.get(2));
                        break;
                    case CreatePrivateChat:
                        // Özel bir sohbet oluşturuldu
                        Login.Login.openPrivChat((String) received.content);
                        break;
                    case PrivateMessage:
                        // Özel bir mesaj aldık
                        String chatMate = ((ArrayList<String>) received.content).get(0);
                        String txtMsg = ((ArrayList<String>) received.content).get(1);
                        Login.Login.findPrivChat(chatMate).getMessage(txtMsg);
                        break;
                    case SendFile:
                        // Dosya alındı
                        ArrayList<Object> fileArr = (ArrayList<Object>) received.content;
                        String file = System.getProperty("user.home") + "/Downloads/" + Login.Login.userName + (String) fileArr.get(1);
                        byte[] mybytearray = (byte[]) fileArr.get(2);
                        OutputStream os = new FileOutputStream(file);
                        os.write(mybytearray);
                        System.out.println((String) fileArr.get(1) + " geldi.");
                        os.close();
                        break;
                }

            } catch (IOException | ClassNotFoundException ex) {
                // Hata durumunda hatayı loglayın ve döngüyü kırın
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

public class Client {

    public static Socket socket;
    public static ObjectInputStream sInput;
    public static ObjectOutputStream sOutput;
    public static ServerListener listenMe;

    public static void Start(String ip, int port, String userName) {
        try {
            // Sunucuya bağlanıyoruz
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new ServerListener();
            Client.listenMe.start();

            // Sunucuya katılma mesajı gönderiyoruz
            Message msg = new Message(Message.Message_Type.JoinServer);
            msg.content = userName;
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Stop() {
        try {
            if (Client.socket != null) {
                // İstemciyi durduruyoruz
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();
                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {
        // Mesajı konsola yazdırma
        System.out.println(msg);
    }

    // Mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.flush();
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
