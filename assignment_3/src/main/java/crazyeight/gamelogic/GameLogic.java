package crazyeight.gamelogic;

import java.util.ArrayList;

public class GameLogic {

	public int scoreRound(ArrayList<String> playerHand) {
		if (playerHand.size() == 0) {
			return 0;
		}

		return Integer.MAX_VALUE;
	}

}
