const BASE_URL = "http://localhost:8090";

const CREATE_PLAYER_URL = `${BASE_URL}/createPlayer`;

const GET_CAN_PLAY = `${BASE_URL}/canPlay`;
const GET_DRAW_CARD = `${BASE_URL}/drawCard`;
const GET_ROUND = `${BASE_URL}/round`;

const POST_CARD = `${BASE_URL}/postCard`;
const POST_PLAYER = `${BASE_URL}/player`;

let playerObject = {};
let userName = "";
let suitChosen = "";

let amountDrawn = 0;

var stompClient = null;

async function registerUser() {
  let registerButton = document.getElementById("register-player");
  let userNameInput = document.getElementById("user-id");
  wsConnect();
  userName = userNameInput.value;
  const response = await fetch(CREATE_PLAYER_URL, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userName),
  });
  try {
    const body = await response.json();

    if (body == null) {
    } else {
      playerObject = body;
      renderPlayerHand(playerObject.hand);
      registerButton.disabled = true;
      getPlayer();
      getRound();
    }
  } catch {
    renderMessage("This player name already exits uses another!");
  }
}

async function getPlayer() {
  const response = await fetch(POST_PLAYER, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userName),
  });
  try {
    const body = await response.json();
    playerObject = body;
    renderScores();
    renderDeck();
  } catch {}
}

async function sendCardToDB() {
  let response = await fetch(POST_CARD, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(playerObject),
  });
  try {
    const body = await response.json();
    playerObject = body;
    renderPlayerHand(playerObject.hand);
    renderScores();
    renderDeck();
  } catch {}
}

async function playCard(card) {
  const response = await fetch(GET_CAN_PLAY, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userName),
  });
  try {
    const canPlayStatus = await response.json();
    if (!canPlayStatus) {
      console.warn("It is not your turn!");
      renderMessage("It is not your turn ATM.");
      return;
    }

    if (!card.includes("8")) {
      if (!playerObject.card.includes(card.charAt(card.length - 1))) {
        renderMessage("Please choose a card of similar suit.");
        return;
      }
    }

    amountDrawn = 0;

    if (card.charAt(0) == "8") {
      renderMessage("Pick the next suit for the eight card.");
      let suits = ["H", "D", "S", "C"];
      let playerSuitSelection = document.getElementById("player-suit");
      playerSuitSelection.innerHTML = "";

      suits.forEach((suit) => {
        let input = document.createElement("input");
        input.value = suit;
        input.type = "button";
        input.id = `card`;
        input.onclick = function () {
          suitSelection(suit, card);
        };

        playerSuitSelection.appendChild(input);
      });
    } else {
      playerObject.card = card;
      playerObject.hand = playerObject.hand.filter(function (item) {
        return item !== card;
      });

      sendCardToDB();
      renderMessage("Your move was captured.");
    }
  } catch {}
}

function suitSelection(suit, card) {
  suitChosen = "8" + suit;
  playerObject.hand = playerObject.hand.filter(function (item) {
    return item !== card;
  });
  playerObject.card = suitChosen;
  sendCardToDB();
  document.getElementById("player-suit").innerHTML = "";
}

async function drawCard() {
  const res = await fetch(GET_CAN_PLAY, {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userName),
  });
  const canPlayStatus = await res.json();

  if (canPlayStatus) {
    const response = await fetch(GET_DRAW_CARD, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userName),
    });
    const player = await response.json();
    if (player) {
      renderPlayerHand(player.hand);
      if (
        player.hand.length == 0 ||
        player.hand.length == playerObject.hand.length
      ) {
        renderMessage("Opps No Card was Given!");
      } else {
        renderMessage(`Card drawn: ${player.hand[player.hand.length - 1]}`);
      }
      playerObject = player;
    } else {
      renderMessage("Oh no something went wrong!");
    }
  } else {
    amountDrawn++;
    shouldSkipTurn();
    renderMessage("You can not draw not your turn!");
  }
  renderDeck();
}

function shouldSkipTurn() {
  let shouldSkip = false;

  playerObject.hand.forEach((handCard) => {
    if (playerObject.card.includes("8") && handCard.includes("8")) {
      shouldSkip = false;
      return;
    }
    if (
      (playerObject.card.includes("H") && handCard.includes("H")) ||
      (playerObject.card.includes("D") && handCard.includes("D")) ||
      (playerObject.card.includes("S") && handCard.includes("S")) ||
      (playerObject.card.includes("C") && handCard.includes("C"))
    ) {
      shouldSkip = true;
    }
  });

  if (playerObject.deck.length == 0 && shouldSkip) {
    renderMessage("Deck is empty and you have no suit to play!");
    return shouldSkip;
  }

  if (amountDrawn >= 3) {
    renderMessage("You have maxed your number of turns!");
    return shouldSkip;
  }
  return shouldSkip;
}

async function getRound() {
  const response = await fetch(GET_ROUND);

  const roundValue = await response.json();

  document.getElementById("round").innerHTML = `Round: ${roundValue}`;
}

function renderPlayerHand(handArray) {
  let playerHandDiv = document.getElementById("player-hands");
  playerHandDiv.innerHTML = "";

  handArray.forEach((card) => {
    let input = document.createElement("input");
    input.value = card;
    input.type = "button";
    input.id = `card`;
    input.onclick = function () {
      playCard(card);
    };

    playerHandDiv.appendChild(input);
  });
}

function renderMessage(message) {
  let messagePara = document.getElementById("message");
  messagePara.innerHTML = "";
  messagePara.innerHTML = message;
}

function renderScores() {
  let playerScoresDiv = document.getElementById("player-scores-container");
  playerScoresDiv.innerHTML = "";

  let playerScoreParagraph = document.createElement("p");
  playerScoreParagraph.innerHTML = `ME: ${playerObject.score}`;
  playerScoreParagraph.id = "player-score";
  playerScoresDiv.appendChild(playerScoreParagraph);

  playerObject.otherPlayers.forEach((player) => {
    let playerScoreParagraph = document.createElement("p");
    playerScoreParagraph.innerHTML = `${JSON.parse(player.name)}: ${
      player.score
    }`;
    playerScoreParagraph.id = "player-score";
    playerScoresDiv.appendChild(playerScoreParagraph);
  });
}

function renderDeck() {
  let topCardPara = document.getElementById("top-card");
  let deckCountPara = document.getElementById("deck-count");
  topCardPara.innerHTML = playerObject.card;
  deckCountPara.innerHTML = playerObject.deck
    ? `Deck Count: ${playerObject.deck.length}`
    : "Deck Count: Not Now";
}

function wsConnect() {
  var socket = new SockJS("/crazy-eight-game-ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    console.log("Connected: " + frame);
    stompClient.subscribe("/topic/playerWS", function (player) {
      let parsedPlayer = JSON.parse(player.body);
      playerObject.card = parsedPlayer.card;
      playerObject.deck = parsedPlayer.deck;
      renderDeck();
      renderScores();
      if (parsedPlayer.name === playerObject.name) {
        if (playerObject.hand.length !== parsedPlayer.hand.length) {
          playerObject.hand = parsedPlayer.hand;
          renderPlayerHand(playerObject.hand);
        }
      }
      renderMessage("Updated player data from Web Socket");
    });
  });
}
