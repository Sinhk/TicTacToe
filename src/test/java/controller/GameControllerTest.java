package controller;

import data.Game;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import repo.GameRepository;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author nilstes
 */
public class GameControllerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test(expected = NotFoundException.class)
    public void testThatPlayerCannotMoveIfGameDoesNotExist() {
        GameController c = new GameController(new GameRepository());
        c.move("player", "nonExistentGameId", 1, 2);
    }

    @Test(expected = ClientErrorException.class)
    public void testThatPlayerCannotMoveIfNotPlayersTurn() {
        GameController c = new GameController(new GameRepository());
        Game game = c.createGame("inviter", "invitee", 10);
        if(game.getTurn().equals("inviter")) {
            c.move(game.getGameId(), "invitee", 2, 3);
        } else {
            c.move(game.getGameId(), "inviter", 2, 3);
        }
    }
    
    @Test(expected = ClientErrorException.class)
    public void testThatPlayerCannotMoveToPositionOutOfRange() {
        GameController c = new GameController(new GameRepository());
        Game game = c.createGame("inviter", "invitee", 10);
        assertNotNull(game);
        c.move(game.getGameId(), game.getTurn(), 12, 21);
    }

    @Test
    public void testThatPlayerCannotMoveToOccupiedField() {
        GameController c = new GameController(new GameRepository());
        Game game = c.createGame("inviter", "invitee", 10);
        assertNotNull(game);
        c.move(game.getGameId(), game.getTurn(), 4, 5);
        exception.expect(ClientErrorException.class);
        c.move(game.getGameId(), game.getTurn(), 4, 5);
    }

    @Test
    public void testGetInvites() {
        GameController c = new GameController(new GameRepository());
        Game game = c.createGame("Bonnie", "Carl", 10);
        assertNotNull(game);
        List<Game> invites = c.getInvites("Carl");
        assertTrue(invites.contains(game));
    }

    @Test
    public void testDuplicateInvites() {
        GameController c = new GameController(new GameRepository());
        String un = "Carl";
        Game game = c.createGame("Bonnie", un, 10);
        Game game2 = c.createGame("Connie", un, 10);
        List<Game> invites = c.getInvites(un);
        assertEquals(2, invites.size());
        invites = c.getInvites(un);
        assertEquals(1, invites.size());
    }
}
