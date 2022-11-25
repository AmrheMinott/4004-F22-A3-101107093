const BASE_URL = "http://localhost:8090";

const CREATE_PLAYER_URL = `${BASE_URL}/createPlayer`;

const GET_PLAYER = `${BASE_URL}/player`;
const GET_CAN_PLAY = `${BASE_URL}/canPlay`;


let playerObject = {};
let userName = "";

async function registerUser() {
  let registerButton = document.getElementById("register-player");
  let userNameInput = document.getElementById("user-id");

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
    alert("This player name already exits uses another!");
  }
}

async function getPlayer() {
  const response = await fetch(GET_PLAYER, {
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
    if (canPlayStatus) {
      playerObject.card = card;
      playerObject.hand = playerObject.hand.filter(function (item) {
        return item !== card;
      });

      sendCardToDB(card);
    } else {
      console.warn("It is not your turn!");
    }
  } catch {}
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

/*
(function () {
  function select(str) {
    return document.querySelector(str);
  }

  function alertMessage(message, type) {
    let alerts = select("#alerts");

    let el = document.createElement("p");
    el.innerHTML = message;
    el.classList.add(type);
    alerts.append(el);
    setTimeout(() => (alerts.innerHTML = ""), 5000);
  }

  function drawChat(chatUsername) {
    return `<div class="row text-center">\n  <h3>Chat with ${chatUsername}</h3>\n</div>\n<div id="chat_messages_${chatUsername}" class="row"></div>\n<br/>\n<div class="row">\n  <div class="col-md-4"><textarea id="chat_input_${chatUsername}"></textarea></div>\n  <div class="col-md-1"><input type="button" id="chat_send_to_${chatUsername}" value="Send"/></div>\n</div>`;
  }

  function getChat(chatList, chatName) {
    let chatRoom = chatList.querySelector(`#chat_${chatName}`);
    if (chatRoom === null) {
      let el = document.createElement("div");
      el.id = `chat_${chatName}`;
      el.innerHTML = drawChat(chatName);
      el.classList.add("row");
      chatList.append(el);
      return el;
    } else {
      return chatRoom;
    }
  }

  // this is where I can end the round
  function clickSendButton(chatRoom, toWhom, stompClient, username) {
    chatRoom.querySelector(`#chat_send_to_${toWhom}`).addEventListener(
      "click",
      () => {
        let msgInput = chatRoom.querySelector(`#chat_input_${toWhom}`);
        let msg = msgInput.value;

        if (msg && msg !== "") {
          stompClientSendMessage(
            stompClient,
            "/app/message",
            JSON.stringify({
              toWhom: toWhom,
              fromWho: username,
              message: msg,
            })
          );
          let messages = chatRoom.querySelector(`#chat_messages_${toWhom}`);
          messages.innerHTML += `<div class="row"><div class="col-md-1">Me:</div><div class="col-md-8">${msg}</div></div>`;
          msgInput.value = "";
        } else {
          alertMessage(
            `Message to user [${toWhom}] cannot be empty !!!`,
            "bg-danger"
          );
        }
      },
      true
    );
  }

  function displayMessage(
    chatList,
    stompClient,
    username,
    { fromWho, message }
  ) {
    let chatRoom = getChat(chatList, fromWho);
    let messages = chatRoom.querySelector(`#chat_messages_${fromWho}`);
    messages.innerHTML += `<div class="row"><div class="col-md-1">${fromWho}:</div><div class="col-md-8">${message}</div></div>`;

    clickSendButton(chatRoom, fromWho, stompClient, username);
  }

  function displayUserList(userList, chatList, username, stompClient) {
    const lis =
      userList.length === 0
        ? "It looks like you are the only one in the chat room !!!"
        : userList.reduce(
            (acc, item) =>
              `${acc}<li id="user_${item}"><a href="#chat_${item}">${item}</a></a></li>`,
            ""
          );

    select("#chat_user_list").innerHTML = `<ul>${lis}</ul>`;

    userList.forEach((item) =>
      select(`#chat_user_list #user_${item}`).addEventListener(
        "click",
        () => {
          clickSendButton(getChat(chatList, item), item, stompClient, username);
        },
        true
      )
    );
  }

  function stompSubscribe(stompClient, endpoint, callback) {
    //8
    stompClient.subscribe(endpoint, callback);
    return stompClient;
  }

  function stompClientSendMessage(stompClient, endpoint, message) {
    // 9
    stompClient.send(endpoint, {}, message);
    return stompClient;
  }

  function disconnect(
    stompClient,
    username,
    connectBtn,
    disconnectBtn,
    clicked = false
  ) {
    connectBtn.disabled = false;
    disconnectBtn.disabled = true;
    if (clicked) {
      stompClientSendMessage(stompClient, "/app/unregister", username);
    }
    stompClient.disconnect(); //6-1
  }

  function connect(username) {
    //1-1
    return new Promise((resolve, reject) => {
      let stompClient = Stomp.over(new SockJS("/websocket-chat"));
      stompClient.connect({}, (frame) => resolve(stompClient));
    });
  }

  //To guarantee that our page is completely loaded before we execute anything
  window.addEventListener("load", function (event) {
    let chatUsersList = [];
    let chatList = select("#chat_list");
    let connectButton = select("#webchat_connect");
    let disconnectButton = select("#webchat_disconnect");

    connectButton.addEventListener(
      "click",
      () => {
        let username = select("#webchat_username").value;

        if (username == null || username === "") {
          alertMessage("Name cannot be empty!!!", "bg-danger");
        } else {
          connect(username) //1
            .then((stompClient) =>
              stompSubscribe(stompClient, "/user/queue/newMember", (data) => {
                //2
                console.log("1111 -> data.body", data.body);
                chatUsersList = JSON.parse(data.body);
                if (chatUsersList.length > 0) {
                  displayUserList(
                    chatUsersList.filter((x) => x != username),
                    chatList,
                    username,
                    stompClient
                  );
                } else {
                  alertMessage("Username already exists!!!", "bg-danger");
                  disconnect(
                    stompClient,
                    username,
                    connectButton,
                    disconnectButton
                  );
                }
              })
            )// CREATE nested thens that get the data from the server for each data point
            .then((stompClient) =>
              stompSubscribe(stompClient, "/topic/newMember", (data) => {
                // 3
                console.log("2222 -> data.body", data.body);
                chatUsersList.push(data.body);
                displayUserList(
                  chatUsersList.filter((x) => x != username),
                  chatList,
                  username,
                  stompClient
                );
              })
            )
            .then((stompClient) =>
              stompClientSendMessage(stompClient, "/app/register", username)
            ) // 4
            .then((stompClient) =>
              stompSubscribe(stompClient, `/user/${username}/msg`, (data) => {
                console.log("3333 -> data.body", data.body);
                displayMessage(
                  chatList,
                  stompClient,
                  username,
                  JSON.parse(data.body)
                );
              })
            )
            .then((stompClient) => {
              //5
              connectButton.disabled = true;
              disconnectButton.disabled = false;
              disconnectButton.addEventListener(
                "click",
                () =>
                  disconnect(
                    stompClient,
                    username,
                    connectButton,
                    disconnectButton,
                    true
                  ),
                true
              ); // 6
              return stompClient;
            })
            .then((stompClient) =>
              stompSubscribe(stompClient, "/topic/disconnectedUser", (data) => {
                // 7
                console.log("4444 -> data.body", data.body);
                const userWhoLeft = data.body;
                chatUsersList = chatUsersList.filter((x) => x != userWhoLeft);
                displayUserList(
                  chatUsersList.filter((x) => x != username),
                  chatList,
                  username,
                  stompClient
                );
                alertMessage(
                  `User [${userWhoLeft}] left the chat room!!!`,
                  "bg-success"
                );
              })
            );
        }
      },
      true
    );
  });
})();
*/
