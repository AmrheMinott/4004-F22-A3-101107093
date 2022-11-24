package crazyeight.gamelogic;

import java.util.ArrayList;

import constants.CardFaces;

public class GameLogic {
	final int SCORE_TEN = 10;
	final int SCORE_FIFTY = 50;

	public int scoreRound(ArrayList<String> playerHand) {
		int finalScore = 0;
		if (playerHand.size() == 0) {
			return finalScore;
		}

		for (String card : playerHand) {
			if (card.contains(CardFaces.KING) || card.contains(CardFaces.QUEEN) || card.contains(CardFaces.JACK)) {
				finalScore += SCORE_TEN;
			} else if (card.contains(CardFaces.EIGHT)) {
				finalScore += SCORE_FIFTY;
			} else {
				finalScore += Integer.parseInt(Character.toString(card.charAt(0)));
			}
		}

		return finalScore;
	}

}
