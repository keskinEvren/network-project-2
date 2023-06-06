package chatserver;

import Message.Message;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bir sohbet sunucusunda bir istemciyi temsil eder.
 *
 * Client sınıfı, sunucu ile belirli bir istemci arasındaki iletişimi yönetmekten sorumludur.
 */
public class Client {
    int id; // İstemci için benzersiz kimlik numarası
    String name = "Default"; 
    ArrayList<String> Users; // İstemciye bağlı diğer sohbet katılımcılarının listesi
    Socket soket; 
    ObjectOutputStream sOutput; 
    ObjectInputStream sInput; 
    ClientListener listenThread; 

    public Client(Socket gelenSoket, int id) {
        this.soket = gelenSoket; 
        this.id = id; 
        Users = new ArrayList<>(); // Sohbet katılımcıları için boş bir liste oluşturur
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream()); // Soketten çıkış akışı oluşturur
            this.sInput = new ObjectInputStream(this.soket.getInputStream()); // Soketten giriş akışı oluşturur
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
        }
        this.listenThread = new ClientListener(this); 
    }

    /**
     * İstemciye bir ileti gönderir.
     *
     * @param message 
     */
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message); // İletiyi çıkış akışına yazar
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }
}
