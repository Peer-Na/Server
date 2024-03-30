let sockJs = null;
let stompClient = null;
let userId = null;
let lastChatId = null;
let chatList = null;
let chatInput = null;
let loading = false;

let targetId = null;
let pendingFrames = [];


const processFrame = (frame) => {
    const messageId = parseInt(frame.headers['message-id'].split('-')[1]);
    if (targetId == null) {
        targetId = messageId;
    }
    if (targetId === messageId) {
        // 처리할 message-id와 동일한 경우
        updateLastMessage(frame.body);
        targetId++;
        pendingFrames.sort((a, b) => a.messageId - b.messageId);
        if (pendingFrames.length) {
            console.log('debug: pendingFrames[0]', pendingFrames[0]);
        }
        while (pendingFrames.length && pendingFrames[0].messageId === targetId) {
            // 처리할 message-id가 대기 목록에 있는 경우
            const pendingFrame = pendingFrames[0];
            updateLastMessage(pendingFrame.body);
            pendingFrames.shift();
            targetId++;
        }
    } else {
        frame.messageId = messageId;
        pendingFrames.push(frame);
    }
};

document.addEventListener("DOMContentLoaded", function () {
    chatList = document.getElementById("chat-list");

    chatInput = document.getElementById('chat-input');
    chatInput.addEventListener('keyup', function (event) {
        if (event.key === 'Enter') {
            sendMessageToGPT();
        }
    });

    sockJs = new SockJS("/stomp");
    stompClient = Stomp.over(sockJs);
    userId = document.getElementById("userId").value;
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/user/gpt', function (frame) {
            if (!loading) {
                loading = true;
                createGPTChatCard();
            }
            processFrame(frame);
        });
    });
});

function createGPTChatCard() {
    const time = new Date();
    if (lastChatId == null) {
        lastChatId = 0;
    }
    pendingFrames = [];

    const li = document.createElement("li");
    li.className = "flex items-end justify-between bg-[#1E1E2D] text-white p-3 my-2 rounded-lg";

    const p = document.createElement("p");
    p.id = 'chat' + ++lastChatId;
    p.className = "flex-row";
    p.innerText = 'Dr.GPT: \n';
    li.appendChild(p);

    const span = document.createElement("span");
    span.className = "ml-2 text-xs flex";
    // span.innerText = time.getHours() + ":" + time.getMinutes();
    span.innerText = time.toLocaleString(undefined, {hour: 'numeric', minute: 'numeric'});

    li.appendChild(span);

    chatList.appendChild(li);
}

function createUserChatCard(content) {
    const time = new Date();
    if (lastChatId == null) {
        lastChatId = 0;
    }
    pendingFrames = [];

    const li = document.createElement("li");
    li.className = "flex items-end justify-end bg-[#1E1E2D] text-white p-3 my-2 rounded-lg";

    const p = document.createElement("p");
    p.className = "flex";
    p.innerText = 'User:\n' + content;
    li.appendChild(p);

    const span = document.createElement("span");
    span.className = "flex ml-2 text-xs";
    // span.innerText = time.getHours() + ":" + time.getMinutes();
    span.innerText = time.toLocaleString(undefined, {hour: 'numeric', minute: 'numeric'});

    li.appendChild(span);

    chatList.appendChild(li);
}

function updateLastMessage(message) {
    if (message === '\n-----END MESSAGE-----\n') {
        setTimeout(() => {
            loading = false;
        }, 500);
        return;
    }

    const lastChatContent = document.getElementById('chat' + lastChatId);
    lastChatContent.innerText += message;
}

function sendMessageToGPT() {
    if (chatInput.value === '') {
        return;
    }
    const lastGPTMessage = document.getElementById('chat' + lastChatId).innerText;

    let request = {message:chatInput.value, lastGPTMessage:lastGPTMessage};
    console.log(request);
    stompClient.send("/app/gpt/message", {}, JSON.stringify(request), function (error) {
        console.log('debug', error);
    });
    createUserChatCard(chatInput.value);
    chatInput.value = '';
}