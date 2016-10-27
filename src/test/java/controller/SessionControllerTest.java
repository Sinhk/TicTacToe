package controller;

import data.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import repo.SessionRepository;

import javax.ws.rs.NotAuthorizedException;

import static org.junit.Assert.*;

/**
 * @author nilstes
 */
public class SessionControllerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private SessionRepository repo;
    private SessionController sc;


    @Before
    public void setUp() throws Exception {
        repo = new SessionRepository();
        sc = new SessionController(repo);

    }

    @Test
    public void testThatUserCanLogOnWithOnlyUsername() {
        String un = "Per";
        Session session = sc.createSession(un);
        assertNotNull(session);
        assertTrue(repo.existsSession(un));
    }

    @Test
    public void testDuplicateLogin() {
        String un = "Kalle";
        sc.createSession(un);
        exception.expect(NotAuthorizedException.class);
        Session session = sc.createSession(un);
        assertNull(session);
    }
}
