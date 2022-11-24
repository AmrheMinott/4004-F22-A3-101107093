package crazyeight.gamelogic;

import java.util.ArrayList;

import constants.CardFaces;

public class GameLogic {
	final int SCORE_TEN = 10;

	public int scoreRound(ArrayList<String> playerHand) {
		int finalScore = 0;
		if (playerHand.size() == 0) {
			return finalScore;
		}
		
		for (String card : playerHand) {
			if (card.contains(CardFaces.KING)) {
				finalScore += SCORE_TEN;
			}
		}

		return finalScore;
	}

}
