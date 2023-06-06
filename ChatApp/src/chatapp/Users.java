/*
 * Bu sınıf, kullanıcı bilgilerini temsil eden bir sınıftır.
 */
package chatapp;

/**
 *
 * Bu sınıf, kullanıcı adı ve kullanıcı kimliği bilgilerini içerir.
 */
public class Users {

    String username;
    Integer userID;

    /**
     * Kullanıcı sınıfı için yapılandırıcı.
     *
     * @param username Kullanıcının kullanıcı adı
     * @param userID Kullanıcının kimliği
     */
    public Users(String username, Integer userID) {
        this.username = username;
        this.userID = userID;
    }

}
