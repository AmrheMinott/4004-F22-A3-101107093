package crazyeight.acceptancetests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crazyeight.websocket.spring.model.Player;
import crazyeight.websocket.spring.model.PlayerScore;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AcceptanceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptanceTest.class);

	private ArrayList<Player> players;
	private WebDriver driver_1;
	private WebDriver driver_2;
	private WebDriver driver_3;
	private WebDriver driver_4;

	private HashMap<String, WebDriver> map = new HashMap<>();

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() throws IOException, InterruptedException {
		driver_1 = new ChromeDriver();
		driver_2 = new ChromeDriver();
		driver_3 = new ChromeDriver();
		driver_4 = new ChromeDriver();

		map.put(ATestUtil.USER_1, driver_1);
		map.put(ATestUtil.USER_2, driver_2);
		map.put(ATestUtil.USER_3, driver_3);
		map.put(ATestUtil.USER_4, driver_4);

		ATestUtil.resetBackend();

		players = new ArrayList<>();
	}

	@AfterEach
	public void afterEach() {
		for (String user : map.keySet()) {
			map.get(user).close();
		}
		players.clear();
	}

	// @Test
	public void row41() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initTwoPlayers("KC", new ArrayList<>(Arrays.asList("4C", "QH", "KC", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3C", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfTwoPlayers(ATestUtil.USER_1);

		map.get(ATestUtil.USER_1).findElement(By.className("3C")).click();
		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfTwoPlayers(ATestUtil.USER_2);
	}

	// @Test
	public void row43() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KH";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("AH", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_1).findElement(By.className("AH")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_4).findElement(By.className("7H")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_3);

		assertCurrentDirection("Current Direction: Left -> Right");
	}

	// @Test
	public void row44() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("QC", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_1).findElement(By.className("QC")).click();
		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_3);
		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_2), ATestUtil.YOU_LOST_YOUR_TURN_DUE_TO_A_QUEEN);
	}

	// @Test
	public void row45() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "4C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("QC", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("3C", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.resetCurrentPlayer(3);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_4).findElement(By.className("3C")).click();
		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_1);
	}

	// @Test
	public void row47() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KH";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("AH", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("7H", "JH", "QC")), new ArrayList<>(Arrays.asList("AH", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.resetCurrentPlayer(3);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_4).findElement(By.className("AH")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_3);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_3).findElement(By.className("7H")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_2);

		assertCurrentDirection("Current Direction: Left -> Right");
	}

	// @Test
	public void row48() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("QC", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.resetCurrentPlayer(3);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_4).findElement(By.className("QC")).click();
		assertCurrentPlayerViaSeleniumOfFourPlayers(ATestUtil.USER_2);
		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.YOU_LOST_YOUR_TURN_DUE_TO_A_QUEEN);
	}

	// @Test
	public void row51() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("KH", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("KH")).click();

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.PLEASE_CHOOSE_A_CARD_OF_SIMILAR_SUIT);
	}

	// @Test
	public void row52() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("7C", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("7C")).click();

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.UPDATED_PLAYER_DATA_FROM_WEB_SOCKET);
	}

	// @Test
	public void row53() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("8H", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("8H")).click();

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.PICK_THE_NEXT_SUIT_FOR_THE_EIGHT_CARD);
	}

	// @Test
	public void row54() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("5S", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("5S")).click();

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.PLEASE_CHOOSE_A_CARD_OF_SIMILAR_SUIT);
	}

//	@Test
	public void row58() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initOnePlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3H")));

		registerPlayerViaSelenium(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(0).setHand(new ArrayList<>(Arrays.asList("3H", "6C")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("6C")).click();
	}

//	@Test
	public void row59() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initOnePlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3H")));

		registerPlayerViaSelenium(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(0).setHand(new ArrayList<>(Arrays.asList("3H", "6D", "5C")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("5C")).click();
	}

//	@Test
	public void row60() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initOnePlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3H")));

		registerPlayerViaSelenium(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(0).setHand(new ArrayList<>(Arrays.asList("3H", "6D", "5C", "7H")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
	}

//	@Test
	public void row61() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initOnePlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3H")));

		registerPlayerViaSelenium(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.NO_CARD_GIVEN);
		players.get(0).setHand(new ArrayList<>(Arrays.asList("3H", "6D", "5S", "4H")));

		ATestUtil.rigGameAfterDrawCard(players);
	}

//	@Test
	public void row62() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initOnePlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3H")));

		registerPlayerViaSelenium(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(0).setHand(new ArrayList<>(Arrays.asList("3H", "6D", "8H")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("8H")).click();

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.assertTextIsOnScreen(map.get(ATestUtil.USER_1), ATestUtil.PICK_THE_NEXT_SUIT_FOR_THE_EIGHT_CARD);

		map.get(ATestUtil.USER_1).findElement(By.className("H")).click();
	}

//	@Test
	public void row63() throws InterruptedException, IOException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initOnePlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("KS", "3C")));

		registerPlayerViaSelenium(ATestUtil.USER_1);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(0).setHand(new ArrayList<>(Arrays.asList("KS", "3C", "6C")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("6C")).click();
	}

//	@Test
	public void row67() throws InterruptedException, IOException, JSONException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initTwoPlayers(topCard, new ArrayList<>(Arrays.asList("KH", "10C")), new ArrayList<>(Arrays.asList("JH", "2C")),
				new ArrayList<>(Arrays.asList("4H")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_1).findElement(By.className("2C")).click();

		map.get(ATestUtil.USER_2).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(1).setHand(new ArrayList<>(Arrays.asList("4H", "9D")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_2).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();

		players.get(1).setHand(new ArrayList<>(Arrays.asList("4H", "9D", "6C")));

		ATestUtil.rigGameAfterDrawCard(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);
		map.get(ATestUtil.USER_2).findElement(By.className("6C")).click();
	}

//	@Test
	public void row78() throws InterruptedException, IOException, JSONException {
		map.get(ATestUtil.USER_1).get("http://localhost:8090/");
		map.get(ATestUtil.USER_2).get("http://localhost:8090/");
		map.get(ATestUtil.USER_3).get("http://localhost:8090/");
		map.get(ATestUtil.USER_4).get("http://localhost:8090/");

		String topCard = "7C";

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH")), new ArrayList<>(Arrays.asList("AS")),
				new ArrayList<>(Arrays.asList()), new ArrayList<>(Arrays.asList("8H", "JH", "6H", "KH", "KS")),
				new ArrayList<>(Arrays.asList("8C", "8D", "2D")));

		registerPlayerViaSelenium(ATestUtil.USER_1);
		registerPlayerViaSelenium(ATestUtil.USER_2);
		registerPlayerViaSelenium(ATestUtil.USER_3);
		registerPlayerViaSelenium(ATestUtil.USER_4);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.rigGameWithPlayersData(players);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		ATestUtil.resetCurrentPlayer(2);

		Thread.sleep(ATestUtil.THREAD_SLEEP_TIME);

		map.get(ATestUtil.USER_3).findElement(By.id(ATestUtil.DRAW_CARD_BUTTON)).click();
		ArrayList<Integer> expectedScore = new ArrayList<>(Arrays.asList(1, 0, 86, 102));
		ArrayList<Player> list = ATestUtil.getPlayersBackend();

		for (int i = 0; i < 4; i++) {
			assertEquals(expectedScore.get(i), list.get(i).getScore());
		}
	}

	private void assertCurrentPlayerViaSeleniumOfTwoPlayers(String player) {
		assertEquals(String.format("Current Player: %s", player),
				map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(ATestUtil.USER_2).findElement(By.id(ATestUtil.CURRENT_PLAYER_ID)).getText());
	}

	private void registerPlayerViaSelenium(String playerName) {
		map.get(playerName).findElement(By.id(ATestUtil.USER_ID)).sendKeys(playerName);
		map.get(playerName).findElement(By.id(ATestUtil.REGISTER_PLAYER)).click();
	}

	private void assertCurrentDirection(String direction) {
		assertEquals(direction, map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.DIRECTION)).getText());
		assertEquals(direction, map.get(ATestUtil.USER_2).findElement(By.id(ATestUtil.DIRECTION)).getText());
		assertEquals(direction, map.get(ATestUtil.USER_3).findElement(By.id(ATestUtil.DIRECTION)).getText());
		assertEquals(direction, map.get(ATestUtil.USER_4).findElement(By.id(ATestUtil.DIRECTION)).getText());
	}

	private void assertCurrentPlayerViaSeleniumOfFourPlayers(String player) {
		assertEquals(String.format("Current Player: %s", player),
				map.get(ATestUtil.USER_1).findElement(By.id(ATestUtil.CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(ATestUtil.USER_2).findElement(By.id(ATestUtil.CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(ATestUtil.USER_3).findElement(By.id(ATestUtil.CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(ATestUtil.USER_4).findElement(By.id(ATestUtil.CURRENT_PLAYER_ID)).getText());
	}

	private void initOnePlayers(String topCard, ArrayList<String> deck, ArrayList<String> hand_1) {
		PlayerScore playerScore = new PlayerScore();
		playerScore.setName(ATestUtil.USER_1);
		playerScore.setScore(0);

		Player player_1 = new Player();
		player_1.setName(ATestUtil.USER_1);
		player_1.setCard(topCard);
		player_1.setScore(0);
		player_1.setRound(0);
		player_1.setHand(hand_1);
		player_1.setDeck(deck);
		player_1.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		players.add(player_1);
	}

	private void initTwoPlayers(String topCard, ArrayList<String> deck, ArrayList<String> hand_1,
			ArrayList<String> hand_2) {
		PlayerScore playerScore = new PlayerScore();
		playerScore.setName(ATestUtil.USER_1);
		playerScore.setScore(0);

		Player player_1 = new Player();
		player_1.setName(ATestUtil.USER_1);
		player_1.setCard(topCard);
		player_1.setScore(0);
		player_1.setRound(0);
		player_1.setHand(hand_1);
		player_1.setDeck(deck);
		player_1.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_2 = new Player();
		player_2.setName(ATestUtil.USER_2);
		player_2.setCard(topCard);
		player_2.setScore(0);
		player_2.setRound(0);
		player_2.setHand(hand_2);
		player_2.setDeck(deck);
		player_2.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		players.add(player_1);
		players.add(player_2);
	}

	private void initFourPlayers(String topCard, ArrayList<String> deck, ArrayList<String> hand_1,
			ArrayList<String> hand_2, ArrayList<String> hand_3, ArrayList<String> hand_4) {
		PlayerScore playerScore = new PlayerScore();
		playerScore.setName(ATestUtil.USER_1);
		playerScore.setScore(0);

		Player player_1 = new Player();
		player_1.setName(ATestUtil.USER_1);
		player_1.setCard(topCard);
		player_1.setScore(0);
		player_1.setRound(0);
		player_1.setHand(hand_1);
		player_1.setDeck(deck);
		player_1.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_2 = new Player();
		player_2.setName(ATestUtil.USER_2);
		player_2.setCard(topCard);
		player_2.setScore(0);
		player_2.setRound(0);
		player_2.setHand(hand_2);
		player_2.setDeck(deck);
		player_2.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_3 = new Player();
		player_3.setName(ATestUtil.USER_3);
		player_3.setCard(topCard);
		player_3.setScore(0);
		player_3.setRound(0);
		player_3.setHand(hand_3);
		player_3.setDeck(deck);
		player_3.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_4 = new Player();
		player_4.setName(ATestUtil.USER_4);
		player_4.setCard(topCard);
		player_4.setScore(0);
		player_4.setRound(0);
		player_4.setHand(hand_4);
		player_4.setDeck(hand_4);
		player_4.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		players.add(player_1);
		players.add(player_2);
		players.add(player_3);
		players.add(player_4);

	}

}
