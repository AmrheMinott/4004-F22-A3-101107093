package crazyeight.gamelogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import crazyeight.websocket.spring.model.Player;

public class GameLogicTest {

	private GameLogic gameLogic = new GameLogic();

	@Test
	public void givenEmptyHand_whenScoreRound_assertScoreOfZero() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList());

		assertEquals(0, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenKing_whenScoreRound_assertScoreOfTen() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("KH"));

		assertEquals(10, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenQueen_whenScoreRound_assertScoreOfTen() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("QH"));

		assertEquals(10, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenJack_whenScoreRound_assertScoreOfTen() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("JH"));

		assertEquals(10, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenAnEightOfSomeThing_whenScoreRound_assertScoreOfTen() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("8H"));

		assertEquals(50, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenAnNonSpecialCardOfSomeThing_whenScoreRound_assertScoreOfTen() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("7H"));

		assertEquals(7, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenAnAceCardOfSomeThing_whenScoreRound_assertScoreOfOne() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("AH"));

		assertEquals(1, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenAHandOfCardAces_whenScoreRound_assertScore() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("AH", "AC", "AD", "AS"));

		assertEquals(4, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenAHandOfSevens_whenScoreRound_assertScore() {
		ArrayList<String> playerHand = new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S"));

		assertEquals(28, gameLogic.scoreRound(playerHand));
	}

	@Test
	public void givenTopCardAce_andDirectionTrue_whenDetermineDirection_assertFalse() {
		assertEquals(false, gameLogic.determineDirection("AH", true));
	}

	@Test
	public void givenTopCardAce_andDirectionFalse_whenDetermineDirection_assertTrue() {
		assertEquals(true, gameLogic.determineDirection("AH", false));
	}

	@Test
	public void givenTopCardNonAce_andDirectionFalse_whenDetermineDirection_assertTrue() {
		assertEquals(false, gameLogic.determineDirection("JH", false));
	}

	@Test
	public void givenCurrentPlayer_assertTheRestOfPlayer() {
		Player p1 = new Player();
		Player p2 = new Player();
		p1.setName("P1");
		p2.setName("P2");

		assertEquals("P1", gameLogic.getOtherPlayers("P2", new ArrayList<>(Arrays.asList(p1, p2))).get(0).getName());
	}

	@Test
	public void givenItIsNotPlayersTurn_assertTheyCanNotPlay() {
		Player p1 = new Player();
		Player p2 = new Player();
		p1.setName("P1");
		p2.setName("P2");
		int currentPlayerIndex = 0;
		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2));

		assertEquals(false, gameLogic.canPlay("P2", currentPlayerIndex, players));

	}

	@Test
	public void givenItIsPlayersTurn_assertTheyCanPlay() {
		Player p1 = new Player();
		Player p2 = new Player();
		p1.setName("P1");
		p2.setName("P2");
		int currentPlayerIndex = 0;
		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2));

		assertEquals(true, gameLogic.canPlay("P1", currentPlayerIndex, players));

	}
}
