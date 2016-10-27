package controller;

import data.Opponents;
import data.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import repo.SessionRepository;

import javax.ws.rs.NotAuthorizedException;
import java.util.Collection;

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

    @Test
    public void testListingOtherPlayersToInvite() {
        String un = "Bob Kare";
        sc.createSession(un);
        sc.createSession("Torgunn");
        sc.createSession("Tore Lars");
        Opponents opponents = sc.getPossibleOpponents(un);
        Collection<String> names = repo.getAllUserNames();
        for (String s : opponents.getUserNames()) {
            assertTrue(names.contains(s));
            assertNotEquals(un, s);
        }

    }
}
