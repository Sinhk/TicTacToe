package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nilstes
 */
public class Opponents implements Serializable {
    List<String> userNames = new ArrayList<>();

    public Opponents() {
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public void addUserName(String userName) {
        this.userNames.add(userName);
    }
}
