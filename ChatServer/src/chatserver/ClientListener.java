/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import Message.Message;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * İstemci olaylarını dinlemek için bir thread sınıfıdır.
 * @author Evren
 */
public class ClientListener extends Thread {

    Client TheClient;

    ClientListener(Client TheClient) {
        this.TheClient = TheClient;
    }

    @Override
    public void run() {
        while (TheClient.soket.isConnected()) {
            try {
                try {
                    Message msg = (Message) TheClient.sInput.readObject();

                    switch (msg.type) {
                        case JoinServer:
                            TheClient.name = msg.content.toString();
                            Server.bcastUsers();
                            System.out.println("User " + TheClient.name + " has joined the server...");
                            break;
                        case CreateRoom:
                            Room r = new Room((String) msg.content);
                            Server.rooms.add(r);
                            r.clients.add(TheClient);
                            Server.bcastRooms();
                            System.out.println(TheClient.name + " has create a room named : " + r.roomName);
                            break;
                        case JoinRoom:
                            ArrayList<String> roomUsers = new ArrayList<>();
                            ArrayList<Object> aboutRoom = new ArrayList<>();
                            Room room = Server.findRoom((String) msg.content);
                            aboutRoom.add(room.roomName);
                            room.clients.add(TheClient);
                            for (Client client : room.clients) {
                                roomUsers.add(client.name);
                            }
                            aboutRoom.add(roomUsers);
                            Message msgClients = new Message(Message.Message_Type.JoinRoom);
                            msgClients.content = aboutRoom;
                            for (Client client : room.clients) {
                                Server.Send(client, msgClients);
                            }
                            System.out.println("User " + TheClient.name + " is joining the room named " + room.roomName);
                            break;
                        case ReturnRoomsNames:
                            ArrayList<String> theRoomList = new ArrayList<>();
                            for (Room theRoom : Server.rooms) {
                                theRoomList.add(theRoom.roomName);
                            }
                            Message roomListToNewy = new Message(Message.Message_Type.ReturnRoomsNames);
                            roomListToNewy.content = theRoomList;
                            Server.Send(TheClient, roomListToNewy);
                            break;
                        case ShowRoomUsers:
                            Room roomUsrs = Server.findRoom((String)msg.content); 
                            Server.bcRoomUsers(roomUsrs, TheClient);
                            break;
                        case UpdateRoomUsers:
                            ArrayList<String> updateList = (ArrayList<String>) msg.content;
                            ArrayList<Object> updateRoomList = new ArrayList<>();
                            Room updatedRoom = Server.findRoom(updateList.get(0));
                            updatedRoom.clients.remove(Server.findClient(updateList.get(1)));
                            updateRoomList.add(updatedRoom.roomName);
                            ArrayList<String> updatedNames = new ArrayList<>();
                            for (Client theClient : updatedRoom.clients) {
                                updatedNames.add(theClient.name);
                            }
                            updateRoomList.add(updatedNames);
                            Message updateR = new Message(Message.Message_Type.UpdateRoomUsers);
                            updateR.content = updateRoomList;
                            for (Client client : updatedRoom.clients) {
                                Server.Send(client, updateR);
                            }
                            for (Client theClient : Server.Clients) {
                                Server.bcRoomUsers(updatedRoom, TheClient);
                            }
                            break;
                        case RoomMessage:
                            ArrayList<String> roomMsg = (ArrayList<String>) msg.content;
                            Server.bcRoomMsg(roomMsg);
                            break;
                        case CreatePrivateChat:
                            TheClient.Users.add((String) msg.content);
                            Client client = Server.findClient((String) msg.content);
                            Message privateMsg = new Message(Message.Message_Type.CreatePrivateChat);
                            privateMsg.content = TheClient.name;
                            Server.Send(client, privateMsg);
                            break;
                        case PrivateMessage:
                            Client c = Server.findClient(((ArrayList<String>) msg.content).get(0));
                            Server.Send(c, msg);
                            break;
                        case SendFile:
                            Client sendedClient = Server.findClient((String)(((ArrayList<Object>) msg.content).get(0)));
                            Server.Send(sendedClient, msg);
                            break;
                    }

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Server.Clients.remove(TheClient);

            }
        }
    }
}

class NewClientListener extends Thread {

    @Override
    public void run() {
        while (!Server.serverSocket.isClosed()) {
            try {
                System.out.println("Waiting For User...");

                Socket clientSocket = Server.serverSocket.accept();

                System.out.println("Client come...");

                Client newClient = new Client(clientSocket, Server.IdClient);

                Server.IdClient++;
                Server.Clients.add(newClient);
                newClient.listenThread.start();
                
            } catch (IOException ex) {
                Logger.getLogger(NewClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
