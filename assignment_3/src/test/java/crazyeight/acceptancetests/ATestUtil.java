package crazyeight.acceptancetests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import crazyeight.websocket.spring.model.Player;

public class ATestUtil {

	static final int THREAD_SLEEP_TIME = 50;

	static final String REGISTER_PLAYER = "register-player";
	static final String USER_ID = "user-id";
	static final String DIRECTION = "direction";
	static final String CURRENT_PLAYER_ID = "current-player";
	static final String DRAW_CARD_BUTTON = "draw-card-button";

	static final String USER_1 = "user_1";
	static final String USER_2 = "user_2";
	static final String USER_3 = "user_3";
	static final String USER_4 = "user_4";

	static final String NO_CARD_GIVEN = "No Card Given";
	static final String PICK_THE_NEXT_SUIT_FOR_THE_EIGHT_CARD = "Pick the next suit for the eight card.";
	static final String UPDATED_PLAYER_DATA_FROM_WEB_SOCKET = "Updated player data from Web Socket";
	static final String PLEASE_CHOOSE_A_CARD_OF_SIMILAR_SUIT = "Please choose a card playable card.";
	static final String YOU_LOST_YOUR_TURN_DUE_TO_A_QUEEN = "You lost your turn due to a queen.";

	private static HttpClient client;
	private static HttpRequest request;

	public static void assertTextIsOnScreen(WebDriver driver, String textOnScreen) {
		assertTrue(hasText(driver, textOnScreen));
	}

	private static boolean hasText(WebDriver driver, final String searchKey) {
		final List<WebElement> result = driver.findElements(By.xpath("//*[contains(text(),'" + searchKey + "')]"));
		return result != null && result.size() > 0;
	}

	public static void rigGameWithPlayersData(ArrayList<Player> players)
			throws JsonProcessingException, IOException, InterruptedException {
		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(players);

		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/testSetPlayers"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).header("Content-Type", "application/json")
				.build();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	public static ArrayList<Player> getPlayersBackend() throws IOException, InterruptedException, JSONException {
		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/getPlayers")).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		Gson g = new Gson();
		return new ArrayList<>(Arrays.asList(g.fromJson(response.body(), Player[].class)));
	}

	public static void rigGameAfterDrawCard(ArrayList<Player> players)
			throws JsonProcessingException, IOException, InterruptedException {
		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(players);

		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/drawCardTest"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).header("Content-Type", "application/json")
				.build();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	public static void resetBackend() throws IOException, InterruptedException {
		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/reset")).GET().build();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	public static void resetCurrentPlayer(int currentPlayer) throws IOException, InterruptedException {
		var objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(currentPlayer);
		client = HttpClient.newHttpClient();
		request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8090/editCurrentPlayer"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).header("Content-Type", "application/json")
				.build();

		client.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
