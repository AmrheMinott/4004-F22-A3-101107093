package crazyeight.acceptancetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import crazyeight.websocket.spring.model.Player;
import crazyeight.websocket.spring.model.PlayerScore;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AcceptanceTest {

	private static final String DIRECTION = "direction";

	private static final int THREAD_SLEEP_TIME = 500;

	private static final String USER_4 = "user_4";

	private static final String USER_3 = "user_3";

	private static final String USER_1 = "user_1";

	private static final String USER_2 = "user_2";

	private static final String REGISTER_PLAYER = "register-player";

	private static final String USER_ID = "user-id";

	private static final String CURRENT_PLAYER_ID = "current-player";

	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptanceTest.class);

	private HttpClient client;
	private HttpRequest request;
	private ArrayList<Player> players;
	private WebDriver driver_1;
	private WebDriver driver_2;
	private WebDriver driver_3;
	private WebDriver driver_4;

	private HashMap<String, WebDriver> map = new HashMap<>();

	@BeforeAll
	static void setupAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void before() {
		driver_1 = new ChromeDriver();
		driver_2 = new ChromeDriver();
		driver_3 = new ChromeDriver();
		driver_4 = new ChromeDriver();

		map.put(USER_1, driver_1);
		map.put(USER_2, driver_2);
		map.put(USER_3, driver_3);
		map.put(USER_4, driver_4);

		players = new ArrayList<>();
	}

	@AfterEach
	public void after() {
		for (String user : map.keySet()) {
			map.get(user).close();
		}
		players.clear();
	}

	@Test
	public void row41() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initTwoPlayers("KC", new ArrayList<>(Arrays.asList("4C", "QH", "KC", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("3C", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")));

		registerViaSeleniumTwoPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfTwoPlayers(USER_1);

		LOGGER.info("{} Playing 3C card.", USER_1);

		map.get(USER_1).findElement(By.className("3C")).click();
		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfTwoPlayers(USER_2);
	}

	@Test
	public void row43() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "KH";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("AH", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_1);

		LOGGER.info("{} Playing AH card.", USER_1);
		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_1).findElement(By.className("AH")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_4);

		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_4).findElement(By.className("7H")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_3);

		assertCurrentDirection("Current Direction: Left -> Right");
	}

	@Test
	public void row44() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("QC", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_1);

		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_1).findElement(By.className("QC")).click();
		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_3);
		assertTextIsOnScreenWithQueenCard(map.get(USER_2));
	}

	@Test
	public void row45() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "4C";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("QC", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("3C", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		resetCurrentPlayer(3);

		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_4);

		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_4).findElement(By.className("3C")).click();
		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_1);
	}

	@Test
	public void row47() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "KH";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("AH", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("7H", "JH", "QC")), new ArrayList<>(Arrays.asList("AH", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		resetCurrentPlayer(3);

		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_4);

		LOGGER.info("{} Playing AH card.", USER_4);
		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_4).findElement(By.className("AH")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_3);

		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_3).findElement(By.className("7H")).click();

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_2);

		assertCurrentDirection("Current Direction: Left -> Right");
	}

	@Test
	public void row48() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("4C", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("QC", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		resetCurrentPlayer(3);

		Thread.sleep(THREAD_SLEEP_TIME);

		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_4);

		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_4).findElement(By.className("QC")).click();
		assertCurrentPlayerViaSeleniumOfFourPlayers(USER_2);
		assertTextIsOnScreenWithQueenCard(map.get(USER_1));
	}

	@Test
	public void row51() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("KH", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		map.get(USER_1).findElement(By.className("KH")).click();

		Thread.sleep(THREAD_SLEEP_TIME);

		assertTextIsOnScreenAfterPlayCard(map.get(USER_1), "Please choose a card of similar suit.");
	}

	@Test
	public void row52() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		map.get(USER_3).get("http://localhost:8090/");
		map.get(USER_4).get("http://localhost:8090/");

		String topCard = "KC";

		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);

		initFourPlayers(topCard, new ArrayList<>(Arrays.asList("JH", "QH", "AS", "AH", "AC", "AD", "AS")),
				new ArrayList<>(Arrays.asList("7C", "4H", "8C")), new ArrayList<>(Arrays.asList("9H", "JH", "QC")),
				new ArrayList<>(Arrays.asList("9H", "JH", "QC")), new ArrayList<>(Arrays.asList("7H", "JH", "QC")));

		registerViaSeleniumFourPlayers();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		map.get(USER_1).findElement(By.className("7C")).click();

		Thread.sleep(THREAD_SLEEP_TIME);

		assertTextIsOnScreenAfterPlayCard(map.get(USER_1), "Updated player data from Web Socket");
	}

	private void assertTextIsOnScreenWithQueenCard(WebDriver driver) {
		assertTrue(hasText(driver, "You lost your turn due to a queen."));
	}

	private void assertTextIsOnScreenAfterPlayCard(WebDriver driver, String textOnScreen) {
		assertTrue(hasText(driver, textOnScreen));
	}

	private void assertCurrentPlayerViaSeleniumOfTwoPlayers(String player) {
		assertEquals(String.format("Current Player: %s", player),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());
	}

	private void registerViaSeleniumTwoPlayers() {
		map.get(USER_1).findElement(By.id(USER_ID)).sendKeys(USER_1);
		map.get(USER_1).findElement(By.id(REGISTER_PLAYER)).click();

		map.get(USER_2).findElement(By.id(USER_ID)).sendKeys(USER_2);
		map.get(USER_2).findElement(By.id(REGISTER_PLAYER)).click();
	}

	private void assertCurrentDirection(String direction) {
		assertEquals(direction, map.get(USER_1).findElement(By.id(DIRECTION)).getText());
		assertEquals(direction, map.get(USER_2).findElement(By.id(DIRECTION)).getText());
		assertEquals(direction, map.get(USER_3).findElement(By.id(DIRECTION)).getText());
		assertEquals(direction, map.get(USER_4).findElement(By.id(DIRECTION)).getText());
	}

	private void assertCurrentPlayerViaSeleniumOfFourPlayers(String player) {
		assertEquals(String.format("Current Player: %s", player),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(USER_3).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", player),
				map.get(USER_4).findElement(By.id(CURRENT_PLAYER_ID)).getText());
	}

	private void registerViaSeleniumFourPlayers() {
		map.get(USER_1).findElement(By.id(USER_ID)).sendKeys(USER_1);
		map.get(USER_1).findElement(By.id(REGISTER_PLAYER)).click();

		map.get(USER_2).findElement(By.id(USER_ID)).sendKeys(USER_2);
		map.get(USER_2).findElement(By.id(REGISTER_PLAYER)).click();

		map.get(USER_3).findElement(By.id(USER_ID)).sendKeys(USER_3);
		map.get(USER_3).findElement(By.id(REGISTER_PLAYER)).click();

		map.get(USER_4).findElement(By.id(USER_ID)).sendKeys(USER_4);
		map.get(USER_4).findElement(By.id(REGISTER_PLAYER)).click();
	}

	private void initTwoPlayers(String topCard, ArrayList<String> deck, ArrayList<String> hand_1,
			ArrayList<String> hand_2) {
		PlayerScore playerScore = new PlayerScore();
		playerScore.setName(USER_1);
		playerScore.setScore(0);

		Player player_1 = new Player();
		player_1.setName(USER_1);
		player_1.setCard(topCard);
		player_1.setScore(0);
		player_1.setRound(0);
		player_1.setHand(hand_1);
		player_1.setDeck(deck);
		player_1.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_2 = new Player();
		player_2.setName(USER_2);
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
		playerScore.setName(USER_1);
		playerScore.setScore(0);

		Player player_1 = new Player();
		player_1.setName(USER_1);
		player_1.setCard(topCard);
		player_1.setScore(0);
		player_1.setRound(0);
		player_1.setHand(hand_1);
		player_1.setDeck(deck);
		player_1.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_2 = new Player();
		player_2.setName(USER_2);
		player_2.setCard(topCard);
		player_2.setScore(0);
		player_2.setRound(0);
		player_2.setHand(hand_2);
		player_2.setDeck(deck);
		player_2.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_3 = new Player();
		player_3.setName(USER_3);
		player_3.setCard(topCard);
		player_3.setScore(0);
		player_3.setRound(0);
		player_3.setHand(hand_3);
		player_3.setDeck(deck);
		player_3.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_4 = new Player();
		player_4.setName(USER_4);
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

	private void rigGameWithPlayersData(ArrayList<Player> players)
			throws JsonProcessingException, IOException, InterruptedException {
		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(players);

		LOGGER.info("Attempting to rig game.");

		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/testSetPlayers"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).header("Content-Type", "application/json")
				.build();

		client.send(request, HttpResponse.BodyHandlers.ofString());

		LOGGER.info("Game Data rigged.");
	}

	private void resetBackend() throws IOException, InterruptedException {
		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/reset")).GET().build();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private void resetCurrentPlayer(int currentPlayer) throws IOException, InterruptedException {
		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(currentPlayer);
		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/editCurrentPlayer"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).header("Content-Type", "application/json")
				.build();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private boolean hasText(WebDriver driver, final String searchKey) {
		final List<WebElement> result = driver.findElements(By.xpath("//*[contains(text(),'" + searchKey + "')]"));
		return result != null && result.size() > 0;
	}
}
