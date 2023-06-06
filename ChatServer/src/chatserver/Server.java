/*
 * Bu sınıf, chat sunucusunun işlevlerini gerçekleştiren sınıftır.
 */
package chatserver;

import Message.Message;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Server {

    public static ServerSocket serverSocket; // Sunucu soketi
    public static int IdClient = 0; // İstemci kimlik numarası
    public static int port = 0; // Sunucu port numarası
    public static ArrayList<Room> rooms = new ArrayList<>(); // Oda listesi
    public static NewClientListener runThread; // İstemci dinleme iş parçacığı
    public static ArrayList<Client> Clients = new ArrayList<>(); // İstemci listesi

    public static void Start(int openport) {
        try {
            Server.port = openport;
            Server.serverSocket = new ServerSocket(Server.port);
            Server.runThread = new NewClientListener();
            Server.runThread.start();
            String start1 = "SERVER IS RUNNING!";

            JOptionPane.showMessageDialog(null, "SERVER STARTED!", "Server Info", 1);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "SERVER NOT STARTED!", "Server Info", 1);
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Send(Client cl, Message msg) {
        try {
            cl.sOutput.flush();
            cl.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void bcastUsers() {
        ArrayList<String> clientList = new ArrayList<>();

        for (Client client : Clients) {
            clientList.add(client.name);
        }
        Message msg = new Message(Message.Message_Type.JoinServer);
        msg.content = clientList;
        for (Client client : Clients) {
            Send(client, msg);
        }
    }

    public static void bcastRooms() {
        ArrayList<String> roomList = new ArrayList<>();

        for (Room room : rooms) {
            roomList.add(room.roomName);
        }
        Message msg = new Message(Message.Message_Type.CreateRoom);
        msg.content = roomList;
        for (Client client : Clients) {
            Send(client, msg);
        }
    }

    public static Room findRoom(String rName) {
        for (Room room : rooms) {
            if (rName.equalsIgnoreCase(room.roomName)) {
                return room;
            }
        }
        return null;
    }

    public static void bcRoomMsg(ArrayList<String> msg) {
        Room theRoom = Server.findRoom(msg.get(1));
        Message theMsg = new Message(Message.Message_Type.RoomMessage);
        theMsg.content = msg;
        for (Client client : theRoom.clients) {
            if (!client.name.equalsIgnoreCase(msg.get(0))) {
                Send(client, theMsg);
            }
        }
    }

    public static void bcRoomUsers(Room room, Client c) {
        ArrayList<String> roomUsrs = new ArrayList<>();
        for (Client client : room.clients) {
            roomUsrs.add(client.name);
        }
        Message theMsg = new Message(Message.Message_Type.ShowRoomUsers);
        theMsg.content = roomUsrs;
        Send(c, theMsg);
    }

    public static Client findClient(String userName) {
        for (Client client : Clients) {
            if (userName.equalsIgnoreCase(client.name)) {
                return client;
            }
        }
        return null;
    }
}
