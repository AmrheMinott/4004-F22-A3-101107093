package crazyeight.gamelogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crazyeight.websocket.spring.model.Player;

public class GameLogicTest {

	private GameLogic gameLogic;
	private static final Logger LOGGER = LoggerFactory.getLogger(GameLogicTest.class);

	@BeforeEach
	public void before() {
		gameLogic = new GameLogic();
	}

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
		assertFalse(gameLogic.determineDirection("AH", true));
	}

	@Test
	public void givenTopCardAce_andDirectionFalse_whenDetermineDirection_assertTrue() {
		assertTrue(gameLogic.determineDirection("AH", false));
	}

	@Test
	public void givenTopCardNonAce_andDirectionFalse_whenDetermineDirection_assertTrue() {
		assertFalse(gameLogic.determineDirection("JH", false));
	}

	// @Test
	public void givenCurrentPlayer_assertTheRestOfPlayer() {
		Player p1 = new Player();
		Player p2 = new Player();
		p1.setName("P1");
		p2.setName("P2");

		assertEquals("P1", gameLogic.getOtherPlayers(new ArrayList<>(Arrays.asList(p1, p2))));
	}

	@Test
	public void givenItIsNotPlayersTurn_assertTheyCanNotPlay() {
		Player p1 = new Player();
		Player p2 = new Player();
		p1.setName("P1");
		p2.setName("P2");
		int currentPlayerIndex = 0;
		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2));

		assertEquals(false, gameLogic.canPlay("P2", players.get(currentPlayerIndex).getName()));
	}

	@Test
	public void givenItIsPlayersTurn_assertTheyCanPlay() {
		Player p1 = new Player();
		Player p2 = new Player();
		p1.setName("P1");
		p2.setName("P2");
		int currentPlayerIndex = 0;
		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2));

		assertEquals(true, gameLogic.canPlay("P1", players.get(currentPlayerIndex).getName()));
	}

	@Test
	public void givenMultiplePlayers_assertAccurateScoreOfPlayers() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("8H", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3));
		ArrayList<Player> list = gameLogic.updateAllPlayerScores(players);

		assertEquals(28, list.get(0).getScore());
		assertEquals(64, list.get(1).getScore());
		assertEquals(28, list.get(2).getScore());
	}

	@Test
	public void givenMultiPlayerRoundEnd_assertScoreOfPlayers() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		p1.setHand(new ArrayList<>(Arrays.asList()));
		p2.setHand(new ArrayList<>(Arrays.asList("8H", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		LOGGER.info("{} hand is {}.", p1.getName(), p1.getHand());
		LOGGER.info("{} hand is {}.", p2.getName(), p2.getHand());
		LOGGER.info("{} hand is {}.", p3.getName(), p3.getHand());

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3));
		ArrayList<Player> list = gameLogic.updateAllPlayerScores(players);

		assertEquals(0, list.get(0).getScore());
		assertEquals(64, list.get(1).getScore());
		assertEquals(28, list.get(2).getScore());

		LOGGER.info("{} score is {}.", p1.getName(), p1.getScore());
		LOGGER.info("{} score is {}.", p2.getName(), p2.getScore());
		LOGGER.info("{} score is {}.", p3.getName(), p3.getScore());
	}

	@Test
	public void givenPlayerOneDrawnCard_whenDrawCard_assertCardIsDrawn() {
		Player p1 = new Player();

		p1.setName("P1");

		assertNotNull(gameLogic.drawCard(p1.getName(), "P1"));
	}

	@Test
	public void givenPlayerOneDrawnThreeCards_whenDrawCard_assertCardIsDrawn() {
		Player p1 = new Player();
		int amtDrawn = 1;
		p1.setName("P1");

		String cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNull(cardDrawn);
		amtDrawn++;
	}

	@Test
	public void givenPlayerOne_andTwo_whenDrawCard_assertNullForCyclingPlayers() {
		Player p1 = new Player();
		Player p2 = new Player();

		int amtDrawn = 1;

		p1.setName("Player 1");
		p2.setName("Player 2");

		LOGGER.info("It is {} turn.", p1.getName());

		String cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p2.getName(), amtDrawn);
		LOGGER.info("{} attempted to Draw card on {} turn -> {}", p2.getName(), p1.getName(), cardDrawn);
		assertNull(cardDrawn);
		amtDrawn++;

		amtDrawn = 1;
		LOGGER.info("It is {} turn.", p2.getName());

		cardDrawn = gameLogic.drawCard(p2.getName(), p2.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p2.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p2.getName(), p2.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p2.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p2.getName(), p2.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p2.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p2.getName(), p2.getName(), amtDrawn);
		LOGGER.info("{} ran out of draws -> {}", p2.getName(), cardDrawn);
		assertNull(cardDrawn);
		amtDrawn++;

		amtDrawn = 1;
		LOGGER.info("It is {} turn.", p1.getName());
		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNotNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("{} - Drawn {}", p1.getName(), cardDrawn);
		assertNull(cardDrawn);
		amtDrawn++;

		cardDrawn = gameLogic.drawCard(p1.getName(), p2.getName(), amtDrawn);
		LOGGER.info("{} attempted to Draw card on {} turn -> {}", p2.getName(), p1.getName(), cardDrawn);
		assertNull(cardDrawn);
		amtDrawn++;
	}

	@Test
	public void givenEmptyDeck_whenDrawCard_assertNoCardIsDrawn() {
		Player p1 = new Player();
		int amtDrawn = 0;
		p1.setName("P1");

		gameLogic.setDeck(new ArrayList<>());

		String cardDrawn = gameLogic.drawCard(p1.getName(), p1.getName(), amtDrawn);
		LOGGER.info("Deck is empty so {} - Drew {}", p1.getName(), cardDrawn);
		assertNull(cardDrawn);
	}

	@Test
	public void givenPositionDirection_whenUserDeterminePlayer_assertIncrementOfOne() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3));
		int currentPlayerIndex = 0;
		boolean direction = true;
		String topCard = "6H";

		int nextPlayerIndex = gameLogic.determineCurrentPlayer(currentPlayerIndex, players, direction, topCard);

		assertEquals(1, nextPlayerIndex);
	}

	@Test
	public void givenPositionDirection_andQueenCard_whenUserDeterminePlayer_assertIncrementOfOne() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3));
		int currentPlayerIndex = 0;
		boolean direction = true;
		String topCard = "QH";

		LOGGER.info("{} is the Top Card.", topCard);

		int nextPlayerIndex = gameLogic.determineCurrentPlayer(currentPlayerIndex, players, direction, topCard);
		LOGGER.info("{} is the next player index.", nextPlayerIndex);

		assertEquals(2, nextPlayerIndex);
	}

	@Test
	public void givenNegativeDirection_andQueenCard_whenUserDeterminePlayer_assertIncrementOfOne() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		int currentPlayerIndex = 0;
		boolean direction = false;
		String topCard = "QH";
		LOGGER.info("{} is the Top Card.", topCard);

		int nextPlayerIndex = gameLogic.determineCurrentPlayer(currentPlayerIndex, players, direction, topCard);
		LOGGER.info("{} is the next player index.", nextPlayerIndex);

		assertEquals(2, nextPlayerIndex);
	}

//	@Test
	public void givenPlayerPlacesTwoCard_whenUserDeterminePlayer_assertTwoCardsAdded() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		int currentPlayerIndex = 0;

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7D", "7C", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		LOGGER.info("{} placed 2H on the top.", p1.getName());
		p1.setCard("2H");

		LOGGER.info("{} attempts to draw 2 cards.", p2.getName());

		LOGGER.info("{} has {} cards before.", p2.getName(), p2.getHand().size());
		gameLogic.addTwoCardToPlayer(players.get(currentPlayerIndex + 1), "2H");
		LOGGER.info("{} has {} cards after.", p2.getName(), p2.getHand().size());

		assertEquals(6, p2.getHand().size());
	}

	@Test
	public void givenPlayerPlacesTwoCard_andDeckPartialFull_whenDrawsCard_assertOneCardsAdded() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList("AH")));
		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		int currentPlayerIndex = 0;

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		LOGGER.info("{} placed 2H on the top.", p1.getName());
		p1.setCard("2H");

		LOGGER.info("{} attempts to draw 2 cards.", p2.getName());
		LOGGER.info("{} has {} cards before.", p2.getName(), p2.getHand().size());
		gameLogic.addTwoCardToPlayer(players.get(currentPlayerIndex + 1), "2H");
		LOGGER.info("{} has {} cards after.", p2.getName(), p2.getHand().size());

		assertEquals(2, p2.getHand().size());
	}

	@Test
	public void givenPlayerPlacesTwoCard_andDeckEmpty_whenDrawsCard_assertZeroCardsAdded() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		LOGGER.info("{} is Initialised.", p1.getName());
		LOGGER.info("{} is Initialised.", p2.getName());
		LOGGER.info("{} is Initialised.", p3.getName());

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P2");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList()));
		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		int currentPlayerIndex = 0;

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		LOGGER.info("{} placed 2H on the top.", p1.getName());
		p1.setCard("2H");

		LOGGER.info("{} attempts to draw 2 cards.", p2.getName());
		LOGGER.info("{} has {} cards before.", p2.getName(), p2.getHand().size());
		gameLogic.addTwoCardToPlayer(players.get(currentPlayerIndex + 1), "2H");
		LOGGER.info("{} has {} cards after.", p2.getName(), p2.getHand().size());

		assertEquals(4, p2.getHand().size());
	}

	@Test
	public void givenPlayerHasNoPlayableCard_andOverTheDrawLimit_whenCheckingIfCanSkip_assertTrue() {

		Player p1 = new Player();
		p1.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "9S", "7S")));
		p1.setCard("9D");

		boolean value = gameLogic.shouldSkipPlayer(p1, 4);

		assertTrue(value);
	}

	@Test
	public void givenPlayerHasEightCard_andOverTheDrawLimit_whenCheckingIfCanSkip_assertFalse() {

		Player p1 = new Player();
		p1.setHand(new ArrayList<>(Arrays.asList("7H", "8C", "9S", "7S")));
		p1.setCard("9D");

		boolean value = gameLogic.shouldSkipPlayer(p1, 4);

		assertFalse(value);
	}

	@Test
	public void givenPlayerIsUnderTheDrawLimit_whenCheckingIfCanSkip_assertFalse() {

		Player p1 = new Player();
		p1.setHand(new ArrayList<>(Arrays.asList("7H", "8C", "9S", "7S")));
		p1.setCard("9D");

		boolean value = gameLogic.shouldSkipPlayer(p1, 3);

		assertFalse(value);
	}

	@Test
	public void givenPlayerHasNoPlayableCard_andUnderLimit_andDeckIsEmpty_whenCheckingIfCanSkip_assertTrue() {

		Player p1 = new Player();
		p1.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "9S", "7S")));
		p1.setCard("9D");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList()));
		boolean value = gameLogic.shouldSkipPlayer(p1, 2);

		assertTrue(value);
	}

	@Test
	public void givenPlayerWithEmptyHand_whenRoundEnds_assertRoundIncrease() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList()));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		gameLogic.handleRoundCompletion(players, "2H");

		assertEquals(2, gameLogic.getRound());

	}

	@Test
	public void givenPlayerWithEmptyHand_whenRoundEnds_assertPlayersHandRest() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList()));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		gameLogic.handleRoundCompletion(players, "2H");

		assertEquals(5, p1.getHand().size());
		assertEquals(5, p2.getHand().size());
		assertEquals(5, p3.getHand().size());
		assertEquals(5, p4.getHand().size());

	}
	
	@Test
	public void givenPlayerWith10Card_whenRoundEnds_assertGameContinues() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));

		p1.setHand(new ArrayList<>(Arrays.asList("7S", "10D", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7S", "7C", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7S", "7C", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7S", "7C", "7D", "7S")));

		assertFalse(gameLogic.handleRoundCompletion(players, "10H"));
	}

	@Test
	public void givenPlayerWithEmptyHand_whenRoundEnds_assertPlayersScore() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList()));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "7C", "7D", "7S")));

		gameLogic.handleRoundCompletion(players, "2H");

		assertEquals(23, p1.getScore());
		assertEquals(0, p2.getScore());
		assertEquals(28, p3.getScore());
		assertEquals(28, p4.getScore());
	}

	@Test
	public void givenNoPlayersCanPlay_andDeckEmpty_whenRoundEnds_assertRoundHasChanged() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList()));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "3H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7H", "3H", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "3H", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "3H", "7D", "7S")));

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		gameLogic.handleRoundCompletion(players, "2C");

		assertEquals(2, gameLogic.getRound());
	}

	@Test
	public void givenAtLeastPlayersCanPlay_andDeckEmpty_whenRoundEnds_assertRoundHasNotChanged() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList()));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7H", "10C", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		gameLogic.handleRoundCompletion(players, "2C");

		assertEquals(1, gameLogic.getRound());
	}

	@Test
	public void givenAtLeastOneEightCard_andDeckEmpty_whenRoundEnds_assertRoundHasNotChanged() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList()));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "2H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7H", "8H", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		gameLogic.handleRoundCompletion(players, "2C");

		assertEquals(1, gameLogic.getRound());
	}

	@Test
	public void givenNoPlayerCanPlay_andDeckEmpty_whenRoundEnds_assertScoreUpdated() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		gameLogic.setDeck(new ArrayList<>(Arrays.asList()));

		p1.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));
		p2.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));
		p3.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));
		p4.setHand(new ArrayList<>(Arrays.asList("7H", "7H", "7D", "7S")));

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));
		gameLogic.handleRoundCompletion(players, "2C");

		assertEquals(28, p1.getScore());
		assertEquals(28, p2.getScore());
		assertEquals(28, p3.getScore());
		assertEquals(28, p4.getScore());
	}

	@Test
	public void givenPlayerWithOver100Point_whenDeterminingWinner_assertPlayerNameWithLowestPoints() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		p1.setScore(100);
		p2.setScore(2);
		p3.setScore(4);
		p4.setScore(88);

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));

		assertEquals("P2", gameLogic.determineWinner(players));
	}
	
	@Test
	public void givenNoPlayerOver100Point_whenDeterminingWinner_assertNullPlayer() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player p3 = new Player();
		Player p4 = new Player();

		p1.setName("P1");
		p2.setName("P2");
		p3.setName("P3");
		p4.setName("P4");

		p1.setScore(10);
		p2.setScore(2);
		p3.setScore(4);
		p4.setScore(88);

		ArrayList<Player> players = new ArrayList<>(Arrays.asList(p1, p2, p3, p4));

		assertNull(gameLogic.determineWinner(players));
	}
}
