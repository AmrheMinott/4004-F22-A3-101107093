package crazyeight.acceptancetests;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

	private static final int THREAD_SLEEP_TIME = 3000;

	private static final String USER_4 = "user_4";

	private static final String USER_3 = "user_3";

	private static final String USER_1 = "user_1";

	private static final String USER_2 = "user_2";

	private static final String REGISTER_PLAYER = "register-player";

	private static final String USER_ID = "user-id";

	private static final String CURRENT_PLAYER_ID = "current-player";

	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptanceTest.class);

	WebDriver driver_1;
	WebDriver driver_2;
	WebDriver driver_3;
	WebDriver driver_4;

	HashMap<String, WebDriver> map = new HashMap<>();

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
	}

	@AfterEach
	public void after() {
		for (String user : map.keySet()) {
			map.get(user).close();
		}
	}

	private HttpClient client;
	private HttpRequest request;

	@Test
	public void row41() throws InterruptedException, IOException {
		map.get(USER_1).get("http://localhost:8090/");
		map.get(USER_2).get("http://localhost:8090/");
		resetBackend();
		Thread.sleep(THREAD_SLEEP_TIME);
		LOGGER.info("Opening browsers for both players.");

		PlayerScore playerScore = new PlayerScore();
		playerScore.setName(USER_1);
		playerScore.setScore(0);

		ArrayList<Player> players = new ArrayList<>();

		Player player = new Player();
		player.setName(USER_1);
		player.setCard("KC");
		player.setScore(0);
		player.setRound(0);
		player.setHand(new ArrayList<>(Arrays.asList("3C", "4H", "8C")));
		player.setDeck(new ArrayList<>(Arrays.asList("4C", "QH", "KC", "AH", "AC", "AD", "AS")));
		player.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_2 = new Player();
		player_2.setName(USER_2);
		player_2.setCard("KC");
		player_2.setScore(0);
		player_2.setRound(0);
		player_2.setHand(new ArrayList<>(Arrays.asList("9H", "JH", "QC")));
		player_2.setDeck(new ArrayList<>(Arrays.asList("4C", "QH", "KC", "AH", "AC", "AD", "AS")));
		player_2.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		players.add(player);
		players.add(player_2);

		LOGGER.info("Regsitering {} in the game.", USER_1);

		map.get(USER_1).findElement(By.id(USER_ID)).sendKeys(USER_1);
		map.get(USER_1).findElement(By.id(REGISTER_PLAYER)).click();

		LOGGER.info("Regsitering {} in the game.", USER_2);

		map.get(USER_2).findElement(By.id(USER_ID)).sendKeys(USER_2);
		map.get(USER_2).findElement(By.id(REGISTER_PLAYER)).click();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		LOGGER.info("Confirming Socket connection opened and asserting current player is {}.", USER_1);

		assertEquals(String.format("Current Player: %s", USER_1),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_1),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());

		LOGGER.info("{} Playing 3C card.", USER_1);

		map.get(USER_1).findElement(By.className("3C")).click();
		Thread.sleep(THREAD_SLEEP_TIME);
		LOGGER.info("Asserting the current player has changed and is {}.", USER_2);

		assertEquals(String.format("Current Player: %s", USER_2),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_2),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());
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
		LOGGER.info("Opening browsers for both players.");

		PlayerScore playerScore = new PlayerScore();
		playerScore.setName(USER_1);
		playerScore.setScore(0);

		ArrayList<Player> players = new ArrayList<>();

		Player player = new Player();
		player.setName(USER_1);
		player.setCard(topCard);
		player.setScore(0);
		player.setRound(0);
		player.setHand(new ArrayList<>(Arrays.asList("AH", "4H", "8C")));
		player.setDeck(new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")));
		player.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_2 = new Player();
		player_2.setName(USER_2);
		player_2.setCard(topCard);
		player_2.setScore(0);
		player_2.setRound(0);
		player_2.setHand(new ArrayList<>(Arrays.asList("9H", "JH", "QC")));
		player_2.setDeck(new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")));
		player_2.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_3 = new Player();
		player_3.setName(USER_3);
		player_3.setCard(topCard);
		player_3.setScore(0);
		player_3.setRound(0);
		player_3.setHand(new ArrayList<>(Arrays.asList("9H", "JH", "QC")));
		player_3.setDeck(new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")));
		player_3.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		Player player_4 = new Player();
		player_4.setName(USER_4);
		player_4.setCard(topCard);
		player_4.setScore(0);
		player_4.setRound(0);
		player_4.setHand(new ArrayList<>(Arrays.asList("7H", "JH", "QC")));
		player_4.setDeck(new ArrayList<>(Arrays.asList("4C", "QH", topCard, "AH", "AC", "AD", "AS")));
		player_4.setOtherPlayersScore(new ArrayList<>(Arrays.asList(playerScore)));

		players.add(player);
		players.add(player_2);
		players.add(player_3);
		players.add(player_4);

		LOGGER.info("Regsitering {} in the game.", USER_1);

		map.get(USER_1).findElement(By.id(USER_ID)).sendKeys(USER_1);
		map.get(USER_1).findElement(By.id(REGISTER_PLAYER)).click();

		LOGGER.info("Regsitering {} in the game.", USER_2);

		map.get(USER_2).findElement(By.id(USER_ID)).sendKeys(USER_2);
		map.get(USER_2).findElement(By.id(REGISTER_PLAYER)).click();

		map.get(USER_3).findElement(By.id(USER_ID)).sendKeys(USER_3);
		map.get(USER_3).findElement(By.id(REGISTER_PLAYER)).click();

		map.get(USER_4).findElement(By.id(USER_ID)).sendKeys(USER_4);
		map.get(USER_4).findElement(By.id(REGISTER_PLAYER)).click();

		Thread.sleep(THREAD_SLEEP_TIME);

		rigGameWithPlayersData(players);

		Thread.sleep(THREAD_SLEEP_TIME);

		LOGGER.info("Confirming Socket connection opened and asserting current player is {}.", USER_1);

		assertEquals(String.format("Current Player: %s", USER_1),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_1),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());

		LOGGER.info("{} Playing AH card.", USER_1);
		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_1).findElement(By.className("AH")).click();

		LOGGER.info("Asserting the current player has changed and is {}.", USER_2);

		assertEquals(String.format("Current Player: %s", USER_4),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_4),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_4),
				map.get(USER_3).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_4),
				map.get(USER_4).findElement(By.id(CURRENT_PLAYER_ID)).getText());

		Thread.sleep(THREAD_SLEEP_TIME);
		map.get(USER_4).findElement(By.className("7H")).click();
		assertEquals(String.format("Current Player: %s", USER_3),
				map.get(USER_1).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_3),
				map.get(USER_2).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_3),
				map.get(USER_3).findElement(By.id(CURRENT_PLAYER_ID)).getText());
		assertEquals(String.format("Current Player: %s", USER_3),
				map.get(USER_4).findElement(By.id(CURRENT_PLAYER_ID)).getText());

		assertEquals("Current Direction: Left -> Right", map.get(USER_1).findElement(By.id(DIRECTION)).getText());
		assertEquals("Current Direction: Left -> Right", map.get(USER_2).findElement(By.id(DIRECTION)).getText());
		assertEquals("Current Direction: Left -> Right", map.get(USER_3).findElement(By.id(DIRECTION)).getText());
		assertEquals("Current Direction: Left -> Right", map.get(USER_4).findElement(By.id(DIRECTION)).getText());
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

	private boolean hasText(WebDriver driver, final String searchKey) {
		final List<WebElement> result = driver.findElements(By.xpath("//*[contains(text(),'" + searchKey + "')]"));
		return result != null && result.size() > 0;
	}
}
