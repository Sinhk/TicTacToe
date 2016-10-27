package controller;

import data.Opponents;
import data.Session;
import repo.SessionRepository;

import javax.ws.rs.NotAuthorizedException;
import java.util.Collection;
import java.util.Date;

/**
 * @author nilstes
 */
public class SessionController {
    
    private SessionRepository repo;

    public SessionController(SessionRepository repo) {
        this.repo = repo;
    }
    
    public Session createSession(String userName) {
        if (repo.existsSession(userName)) {
            throw new NotAuthorizedException("Duplicate login");
        }
        Session session = new Session();
        session.setUserName(userName);
        session.setLoggedOn(new Date());
        repo.addSession(session);
        return session;
    }

    public void removeSession(String userName) {
        // todo
    }
    
    public Opponents getPossibleOpponents(String myUserName) {
        Collection<String> names = repo.getAllUserNames();
        Opponents ops = new Opponents();
        for (String name : names) {
            if (!name.equals(myUserName))
                ops.addUserName(name);
        }
        return ops;
    }
}