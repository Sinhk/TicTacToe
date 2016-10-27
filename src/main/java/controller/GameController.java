package controller;

import data.Game;
import data.Position;
import data.Square;
import repo.GameRepository;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author nilstes
 */
public class GameController {

    private static final Logger log = Logger.getLogger(GameController.class.getName());
    private static Random rnd = new SecureRandom();

    private GameRepository repo;

    public GameController(GameRepository repo) {
        this.repo = repo;
    }

    public Game createGame(String inviter, String invitee, int squares) {
        String gameId = new BigInteger(128, rnd).toString(36);
        Game game = new Game(gameId, inviter, invitee, squares);
        game.setTurn(rnd.nextBoolean() ? inviter : invitee);
        repo.addGame(game);

        // Robot game?
        if (game.getTurn().equals("")) {
            game.setInviteAccepted(true);
            game.addMove(squares / 2, squares / 2, ""); // Middle of board
        }
        return game;
    }

    public Game move(String gameId, String player, int x, int y) {
        Game game = getGame(gameId);

        // Right turn?
        if (!player.equals(game.getTurn())) {
            log.warning("Wrong turn");
            throw new ClientErrorException("Ikke denne spillerens tur", Response.Status.BAD_REQUEST);
        }

        // Out of range?
        if (x < 0 || y < 0 || x >= game.getSquares() || y >= game.getSquares()) {
            log.log(Level.WARNING, "Position out of range: {0},{1}", new Object[]{x, y});
            throw new ClientErrorException("Posisjon er ugyldig: " + x + "," + y, Response.Status.BAD_REQUEST);
        }

        //Occupied?
        if (game.getBoard()[y][x] != Square.e) {
            log.log(Level.INFO, "Position occupied: {0}, {1}", new int[]{x, y});
            throw new ClientErrorException("Position occupied: " + x + "," + y, Status.BAD_REQUEST);
        }

        GameAI ai = new GameAI(game);

        // Make move
        game.addMove(x, y, player);
        if (ai.isWin(x, y)) {
            game.setWinner(player);
        }

        // Robot move?
        if (game.getWinner() == null && game.getNumMoves() < game.getSquares() * game.getSquares() && game.getTurn().equals("")) {
            List<Position> positions = ai.getBestMoves();
            if (positions.size() == 0) {
                throw new ServerErrorException("Couldn't find move", Status.INTERNAL_SERVER_ERROR);
            }
            Position pos = positions.get(rnd.nextInt(positions.size()));
            game.addMove(pos.x(), pos.y(), "");
            if (ai.isWin(pos.x(), pos.y())) {
                game.setWinner("");
            }
        }

        return game;
    }

    public Game getGame(String gameId) {
        Game game = repo.getGame(gameId);
        if (game == null) {
            log.log(Level.WARNING, "Game not found: {0}", gameId);
            throw new NotFoundException("Spill med id " + gameId + " ble ikke funnet");
        }
        return game;
    }

    public List<Game> getInvites(String userName) {
        Iterable<Game> games = repo.getAllGames();
        List<Game> invites = new ArrayList<>();

        for (Game game : games) {
            if (game.getInvitee().equals(userName) && !game.isInviteAccepted()) {
                invites.add(game);
            }
        }
        return invites;
    }
}

