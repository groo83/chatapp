// 채팅방 목록을 저장하는 변수
let chatRooms = [];
let nickname;
var stompClient = null;
let firstMessageId = null;
let isLoading = false;
// WebSocket and STOMP setup
var droppedFile = null;
let messageQueue = [];
const accessToken = localStorage.getItem("accessToken");

// DOM elements
let chatMessages;
const nicknameElement = document.getElementById('nicknameElement');


document.addEventListener('DOMContentLoaded', function() {
  getMember();
});

async function getMember() {
  // 로컬 스토리지에서 JWT 토큰을 가져옴
  const jwtToken = localStorage.getItem('accessToken');
  if (jwtToken) {
    try {
        const response = await fetch('/api/member', {
           method: 'GET',
           headers: {
             'Authorization': `Bearer ${jwtToken}`
           }
        });


        if (!response.ok) {
            alert("인증 실패!");
            return;
        }

        const resJson = await response.json();
        const resData = resJson.data;
        let nicknameElement = document.getElementById("nicknameElement");
        console.log(resData)
        nickname = resData.nickname;
        nicknameElement.innerHTML = nickname;
        console.log('Main page loaded successfully');
    } catch (error) {
        console.error("Error:", error);
    }
  } else {
    alert('JWT 토큰이 없습니다.');
  }
}
// 페이지 로드 후 실행되는 함수
window.onload = function() {

    // 채팅방 목록 조회
    findChatRooms();
    // 채팅방 목록을 표시
    displayChatRooms();
}

// 플러스 버튼 클릭 시 호출되는 함수
async function createChatRoom() {
    const roomName = prompt("새로운 채팅방 이름을 입력하세요:");
    const token = localStorage.getItem('accessToken');
    if (roomName) {
        try {
            // 서버에 채팅방 생성 요청
            const response = await fetch('/api/chatroom', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({roomName: roomName})
            });

            if (response.ok) {
                const resJson = await response.json();
                console.dir(resJson);
                // 채팅방을 생성하고 목록에 추가
                 chatRooms.unshift({
                    chatRoomId: resJson.data.chatRoomId,
                    name: resJson.data.name,
                    nickname: resJson.data.nickname,
                });

                displayChatRooms();
            } else {
                alert("채팅방 생성에 실패했습니다." + response);
            }
        } catch (error) {
            alert("채팅방 생성에 실패했습니다." + error);
        }
    }
}

// 채팅방 목록을 HTML에 표시하는 함수
function displayChatRooms() {
    const chatRoomList = document.getElementById("chatRoomList");
    chatRoomList.innerHTML = ""; // 기존 목록 초기화
    console.dir(chatRooms);
    chatRooms.forEach((room, index) => {
        const listItem = document.createElement("li");
        listItem.className = "list-group-item d-flex justify-content-between align-items-center";
        listItem.innerHTML = `
            <div>
                <strong>${room.name}</strong>
                <small th:inline="text" class="text-muted">- 생성자: ${room.nickname}</small>
            </div>
            <button class="btn btn-sm btn-outline-info" onclick="enterChatRoom(${room.chatRoomId})">입장</button>
        `;
        chatRoomList.appendChild(listItem);
    });
}

// 채팅방 입장 함수
function enterChatRoom(roomId, firstMessageId) {
    getRecentMessages(roomId);
}

async function getRecentMessages(roomId, firstMessageId = null) {
    var param = '';
    if (firstMessageId) {
        param = '?firstId=' + firstMessageId;
    }
    try {
        // 서버에서 채팅방 상세 정보 가져오기
        const response = await fetch(`/api/chatroom/${roomId}/messages${param}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            },
        });

        if (response.ok) {
            const roomData = await response.json();
            console.dir(roomData);

            if(firstMessageId) {
                addPagingMessages(roomData.data.messages);
                return;
            }

            displayChatRoom(roomData.data);
            connectWebSocket(roomData.data.chatRoomId);

            //loadUserList(roomId);
            loadChatRoomScript();

        } else {
            alert("채팅방 정보를 불러오는 데 실패했습니다.");
        }
    } catch (error) {
        alert("채팅방 정보를 불러오는 데 실패했습니다: " + error);
    }
}

// 채팅방의 상세 정보를 표시하는 함수
function displayChatRoom(roomData) {
    const chatContainer = document.querySelector(".chat-container");
    chatContainer.innerHTML = `

        <div class="chat-header">
            <span class="back-btn" onclick="exitChatRoom()">&larr; 뒤로</span>
            <h3>${roomData.name}</h3>
            <div class="user-list" id="userList">
            </div>
        </div>
        <div class="chat-messages" id="chatMessages">
        </div>
        <!-- File Drop Area -->
        <div class="file-drop-area" id="dropArea">Drag and drop a file here or click to upload</div>

        <!-- Input Section -->
        <div class="chat-input">
            <div class="input-group">
                <input type="text" id="chatInput" class="form-control" placeholder="메시지를 입력하세요...">
                <button id="sendButton" class="btn btn-primary" type="button">
                    Send
                </button>
            </div>
        </div>
        <input type="hidden" id="roomId" value="${roomData.chatRoomId}">

    `;
    chatMessages = document.getElementById('chatMessages');

    parseMessages(roomData.messages);

    document.getElementById('chatRoomListContainer').style.display = 'none';
    document.getElementById('chatContainer').style.display = 'flex';

    // 스크롤 하단 설정
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

async function findChatRooms() {
    try {
        const response = await fetch('/api/chatroom', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            const resJson = await response.json();
            console.dir(resJson);
            // 채팅방을 생성하고 목록에 추가
            resList = resJson.data;
            resList.forEach((room) => {
                chatRooms.push({
                    chatRoomId: room.chatRoomId,
                    name: room.name,
                    nickname: room.nickname,
                });
            });
            displayChatRooms();
        } else {
            alert("채팅방 조회에 실패했습니다." + response);
        }
    } catch (error) {
        alert("채팅방 조회에 실패했습니다." + error);
    }
}

function loadChatRoomScript() {
    const scriptName = "chatroom.js"
    // 최초에만 로딩
    if (isScriptLoaded(scriptName)) {
        return;
    }
    let script = document.createElement("script");
    script.src = "/js/" + scriptName;  // 로드할 자바스크립트 파일 경로
    script.type = "text/javascript";
    script.onload = function() {
        console.log("스크립트가 로드되었습니다!");
    };
    document.body.appendChild(script);
}

// Display received message
function showMessage(message, chatPageArea) {
    var chatMessageElement = document.createElement('div');
    chatMessageElement.className = `chat-message ${
                                        message.type === "enter"
                                            ? "message-system"
                                            : message.sender === nicknameElement.innerHTML.trim()
                                            ? "message-sent"
                                            : "message-received"
                                   }`;
    chatMessageElement.innerHTML = `
        <div class="message-content">

            ${message.type !== "enter" ? `<small class="d-block">${message.sender}</small>` : ''}

            ${message.fileUrl ? `<a href="${message.fileUrl}" id="downloadLink" download>${message.fileName}</a>` : ''}
            ${message.content ? `<p>${message.content}</p>` : ''}
        </div>
    `;

    if (chatPageArea) {
        chatPageArea.appendChild(chatMessageElement);
        return;
    }

    chatMessages.appendChild(chatMessageElement);
}


function connectWebSocket(roomId) {

    var socket = new WebSocket(`ws://localhost:8080/ws-stomp`);
    stompClient = Stomp.over(socket);

    if (!accessToken) {
        alert("인증 정보가 없습니다. 다시 로그인 해주세요.");
        window.location.href = "login.html";
        return;
    }

    stompClient.connect({ 'Authorization': `Bearer ${accessToken}` }, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe(`/topic/${roomId}/messages`, function (message) {
            var chatMessage = JSON.parse(message.body);
            showMessage(chatMessage);
        });

        stompClient.subscribe(`/topic/${roomId}/users`, function (response) {
            updateUserList(JSON.parse(response.body));
        });

        joinChat(roomId);
    }, function (error) {
        console.error("WebSocket connection error:", error);
        stompClient = null; // 에러 발생 시 초기화
    });

}




// Send message to server
function sendMessage(content, sender, roomId) {
    var message = {
        sender: sender,
        content: content,
        type: "message",
        roomId: roomId,
    };
    const accessToken = localStorage.getItem("accessToken");
    stompClient.send("/app/chat", { 'Authorization': `Bearer ${accessToken}` }, JSON.stringify(message));
}

// Send file to server
function sendFile(file, sender, roomId) {
    var formData = new FormData();
    formData.append("file", file);
    formData.append("sender", sender);
    const accessToken = localStorage.getItem("accessToken");

    fetch('/files/upload', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${accessToken}`
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            return response.text();
        } else {
            throw new Error("File upload failed with status: " + response.status);
        }
    })
    .then(fileName => {
        if (fileName) {
            var message = {
                sender: sender,
                content: null,
                fileName: fileName,
                roomId: roomId,
                fileUrl: null,
                type: "file"
            };

            stompClient.send("/app/chat", { 'Authorization': `Bearer ${accessToken}` }, JSON.stringify(message));
        }
    })
    .catch(error => console.error("Error uploading file:", error));
}


function joinChat(roomId) {
    var nickname = nicknameElement.innerHTML.trim();
    if (nickname && stompClient.connected) {
        stompClient.send(`/app/${roomId}/join`, { 'Authorization': `Bearer ${accessToken}` }, nickname);
    }
}

function leaveChat() {
    if (nickname) {
        stompClient.send(`/app/${roomId}/leave`, { 'Authorization': `Bearer ${accessToken}` }, nickname);
    }
}

function updateUserList(users) {
    const userList = document.getElementById("userList");
    userList.innerHTML = users.join(", ");
}

function isScriptLoaded(scriptName) {
    return Array.from(document.scripts).some(script => script.src.includes(scriptName));
}

function exitChatRoom() {
    document.getElementById('chatContainer').style.display = 'none';
    document.getElementById('chatRoomListContainer').style.display = 'block';
}


// 이전 메시지 불러오기 (스크롤 시 호출)
async function loadOlderMessages(roomId) {
    if (isLoading) return;
    isLoading = true;

    getRecentMessages(roomId, firstMessageId);

    isLoading = false;
}

function addPagingMessages(messages) {
    const chatPageArea = document.createElement('div');


    parseMessages(messages, chatPageArea);
    // 스크롤 paging
    chatMessages.prepend(chatPageArea);
}

function parseMessages(messages, chatPageArea) {
    if(messages.length > 0){
        firstMessageId = messages[0].messageId;
    }

    messages.forEach(message => {
        showMessage(message, chatPageArea);
    });
}