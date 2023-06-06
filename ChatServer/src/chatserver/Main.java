/*
 * Bu sınıf, chat sunucusunu başlatan ana sınıftır.
 */
package chatserver;

public class Main {
    public static void main(String[] args) {
        // TODO code application logic here

        // 2000 numaralı port üzerinde chat sunucusunu başlat
        Server.Start(2000);
    }
}
