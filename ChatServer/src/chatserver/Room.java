/*
 * Bu sınıf, sohbet odalarının temsilini sağlar.
 */
package chatserver;

import java.util.ArrayList;

public class Room {
    String roomName; // Oda adı
    ArrayList<Client> clients = new ArrayList<>(); // Oda içindeki istemcilerin listesi
    

    public Room(String roomName) {
        this.roomName = roomName;
    }
}
