package crazyeight.websocket.spring.model;

import java.util.ArrayList;

public class Player {

	private ArrayList<String> deck;
	private ArrayList<String> hand;
	private ArrayList<Player> otherPlayers;

	private String card;
	private String name;
	private String winner;

	private int round;
	private int score;

	public ArrayList<String> getDeck() {
		return deck;
	}

	public void setDeck(ArrayList<String> deck) {
		this.deck = deck;
	}

	public ArrayList<String> getHand() {
		return hand;
	}

	public void setHand(ArrayList<String> hand) {
		this.hand = hand;
	}

	public ArrayList<Player> getOtherPlayers() {
		return otherPlayers;
	}

	public void setOtherPlayers(ArrayList<Player> otherPlayers) {
		this.otherPlayers = otherPlayers;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Top card " + card + " hand " + hand.toString();
	}

}