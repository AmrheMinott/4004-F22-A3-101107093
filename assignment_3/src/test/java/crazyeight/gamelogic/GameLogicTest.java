package crazyeight.gamelogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

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
}
