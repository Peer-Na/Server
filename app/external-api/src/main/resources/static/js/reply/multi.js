let cursorId = 0;let category = null;let isFetching = false; // 현재 데이터를 로딩 중인지 여부let problemId = -1;let submitButton = null;let exampleAnswerBtn = null;let othersAnswerBtn = null;let chatInput = null;let chatInputBtn = null;let sockJs = null;let stompClient = null;let roomId = null;document.addEventListener("DOMContentLoaded", function () {    category = document.getElementById('category').innerText;    submitButton = document.getElementById('submit-button');    exampleAnswerBtn = document.getElementById('example-answer');    othersAnswerBtn = document.getElementById('others-answer');    chatList = document.getElementById("chat-list");    chatInput = document.getElementById('chat-input');    chatInputBtn = document.getElementById('chat-input-button');    roomId = document.getElementById('room-id').innerText;    chatInput.addEventListener('keypress', function (event) {        if (event.key === 'Enter') {            chatInputBtn.focus();            const content = chatInput.value.toString();            stompClient.send("/app/room/" + roomId, {}, content, function (error) {                console.log('debug', error);            });            chatInput.value = '';            chatInput.focus();        }    });    loadNewProblems();    sockJs = new SockJS("/stomp");    stompClient = Stomp.over(sockJs);    stompClient.connect({}, function (frame) {        console.log('Connected: ' + frame);        stompClient.subscribe('/room/' + roomId, function (frame) {            console.log('/room/' + roomId + ': ', frame);            createUserChatCard(frame.body);        });        stompClient.subscribe('/room/' + roomId + '/answer', function (frame) {            const body = JSON.parse(frame.body);            console.log('/room/' + roomId + '/answer: ', body);            createReplyCard(body);        });        stompClient.subscribe('/room/' + roomId + '/problem', function (frame) {            const body = JSON.parse(frame.body);            console.log('/room/' + roomId + 'problem: ', body);            changeProblem(body);        });        setTimeout(function () {            stompClient.send("/app/room/" + roomId + '/enter', {}, {}, function (error) {                console.log('debug', error);            });        }, 1000);    });});function loadNewProblems() {    console.log("Loading New Problems...");    isFetching = true; // 데이터 로딩 중으로 표시    axios.get('/api/problem/' + category, {        params: {            cursorId: cursorId        }    }).then(function (response) {        console.log(response);        const problems = response.data.problems;        if (problems.length === 0) {            alert('해당 카테고리에는 더 이상 문제가 존재하지 않습니다.');            return;        }        const problemList = document.getElementById('problem-list');        problems.forEach(function (problem) {            problemList.appendChild(buildProblemItem(problem));        });        cursorId = problems[problems.length - 1].problemId;    }).catch(function (error) {        console.log(error);        alert('카테고리가 유효하지 않습니다.')    });    isFetching = false; // 데이터 로딩 완료로 표시}function changeProblem(body) {    problemId = body.problemId;    document.getElementById('question').innerText = body.question;    createUserChatCard(body.message);}function submitReply() {    axios.post('/api/reply/person', {        problemId: problemId,        answer: document.getElementById('answer').value    }).then(function (response) {        console.log(response);        if (response.status === 201) {            alert('답안이 제출되었습니다.');            submitButton.innerText = '답안 수정';            submitButton.setAttribute('onclick', 'updateReply()');            submitButton                .className = "text-gray-900 bg-gradient-to-r from-red-200 via-red-300 to-yellow-200 hover:bg-gradient-to-bl focus:ring-4 focus:outline-none focus:ring-red-100 dark:focus:ring-red-400 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2";            exampleAnswerBtn                .disabled = !exampleAnswerBtn.disabled;            exampleAnswerBtn                .classList.remove('cursor-not-allowed')            othersAnswerBtn                .disabled = !othersAnswerBtn.disabled;            othersAnswerBtn                .classList.remove('cursor-not-allowed')            updateExampleAnswer();            const userReply = document.getElementById('user-reply');            userReply.style.display = 'none';        }    }).catch(function (error) {        console.log(error);        alert('답안 제출에 실패했습니다.');    });    loadKeywords();}function updateReply() {    axios.patch('/api/reply', {        problemId: problemId,        answer: document.getElementById('answer').value    }).then(function (response) {        console.log(response);        alert('답안이 수정되었습니다.');    }).catch(function (error) {        console.log(error);        alert('답안 수정에 실패했습니다.');    });}function loadKeywords() {    axios.get('/api/problem/keyword?problemId=' + problemId)        .then(function (response) {            console.log(response);            const keywords = response.data.keywords;            const keywordList = document.getElementById('keyword-list');            keywordList.innerHTML = '';            for (let i = 0; i < keywords.length; i++) {                const keywordItem = document.createElement('li');                keywordItem.setAttribute('class', 'inline-flex items-center justify-center whitespace-nowrap text-sm font-medium ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary hover:bg-primary/90 h-10 bg-gradient-to-r from-purple-400 to-purple-600 text-white rounded-2xl px-6 py-2 transition-all duration-200 ease-in-out shadow-md hover:shadow-lg');                keywordItem.innerText = keywords[i];                keywordList.appendChild(keywordItem);            }        }).catch(function (error) {        console.log(error);        alert('키워드를 불러오는데 실패했습니다.');    });}function createSvgElement() {    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');    svg.setAttribute('class', 'w-4 h-4 ms-3 rtl:rotate-180 text-gray-500 dark:text-gray-400');    svg.setAttribute('aria-hidden', 'true');    svg.setAttribute('fill', 'none');    svg.setAttribute('viewBox', '0 0 14 10');    svg.setAttribute('xmlns', 'http://www.w3.org/2000/svg');    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');    path.setAttribute('stroke', 'currentColor');    path.setAttribute('stroke-linecap', 'round');    path.setAttribute('stroke-linejoin', 'round');    path.setAttribute('stroke-width', '2');    path.setAttribute('d', 'M1 5h12m0 0L9 1m4 4L9 9');    svg.appendChild(path);    return svg;}function buildProblemItem(problem) {    const problemItem = document.createElement('li');    const input = document.createElement('input');    input.addEventListener('click', () => selectProblem(problem));    input.setAttribute('type', 'button');    input.setAttribute('id', 'problem-' + problem.problemId);    input.setAttribute('name', 'problem');    input.setAttribute('value', 'problem-' + problem.problemId);    input.setAttribute('class', 'hidden peer');    const label = document.createElement('label');    label.setAttribute('for', 'problem-' + problem.problemId);    label.setAttribute('class', 'inline-flex items-center justify-between w-full p-5 text-gray-900 bg-white border border-gray-200 rounded-lg cursor-pointer dark:hover:text-gray-300 dark:border-gray-500 dark:peer-checked:text-blue-500 peer-checked:border-blue-600 peer-checked:text-blue-600 hover:text-gray-900 hover:bg-gray-100 dark:text-white dark:bg-gray-600 dark:hover:bg-gray-500');    const div = document.createElement('div');    div.setAttribute('class', 'block');    const question = document.createElement('div');    question.setAttribute('class', 'w-full text-lg font-semibold');    question.innerText = problem.question;    const category = document.createElement('div');    category.setAttribute('class', 'w-full text-gray-500 dark:text-gray-400');    category.innerText = problem.category;    div.appendChild(question);    div.appendChild(category);    label.appendChild(div);    label.appendChild(createSvgElement());    problemItem.appendChild(input);    problemItem.appendChild(label);    return problemItem;}function selectProblem(problem) {    problemId = problem.problemId;    document.getElementById('question').innerText = problem.question;    stompClient.send("/app/room/" + roomId + "/problem", {}, JSON.stringify({        problemId: problemId    }, function (error) {        console.log('debug', error);    }));    submitButton        .innerText = '답안 제출'    submitButton        .setAttribute('onclick', 'submitReply()');    submitButton        .setAttribute('class', 'text-white bg-gradient-to-r from-red-400 via-red-500 to-red-600 hover:bg-gradient-to-br focus:ring-4 focus:outline-none focus:ring-red-300 dark:focus:ring-red-800 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 mb-2');    stompClient.send("/app/room/" + roomId + "/problem", {}, problemId, function (error) {        console.log('debug', error);    });}function openOthersReply() {    window.open('/reply/others?problemId=' + problemId);}function updateExampleAnswer() {    axios.get('/api/problem/answer?problemId=' + problemId)        .then(function (response) {            console.log(response);            const exampleAnswerContent = document.getElementById('example-answer-content');            exampleAnswerContent.innerText = response.data.answer;        }).catch(function (error) {        console.log(error);        alert('예시 답안을 불러오는데 실패했습니다.');    });}function createUserChatCard(content) {    const time = new Date();    pendingFrames = [];    const li = document.createElement("li");    li.className = "flex items-end justify-end bg-[#1E1E2D] text-white p-3 my-2 rounded-lg";    const p = document.createElement("p");    p.className = "flex";    p.innerText = content;    li.appendChild(p);    const span = document.createElement("span");    span.className = "flex ml-2 text-xs";    span.innerText = time.toLocaleString(undefined, {hour: 'numeric', minute: 'numeric'});    li.appendChild(span);    chatList.appendChild(li);}function createReplyCard(reply) {    var outerli = document.createElement('li');    var flexDiv = document.createElement('div');    flexDiv.className = 'flex flex-row items-center h-16';    var span1 = document.createElement('span');    span1.className = 'relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full ml-5';    var img = document.createElement('img');    img.alt = '작성자 이미지';    img.src = reply.userImage;    span1.appendChild(img);    var span2 = document.createElement('span');    span2.className = 'flex h-full w-full items-center justify-start rounded-full bg-muted ml-2';    span2.textContent = reply.userName;    flexDiv.appendChild(span1);    flexDiv.appendChild(span2);    var pDiv = document.createElement('div');    pDiv.className = 'p-6';    var innerDiv = document.createElement('div');    innerDiv.className = 'flex-1';    var container = document.createElement('div');    container.className = 'container flex flex-col gap-4 px-4 md:gap-10 lg:px-6';    var spaceDiv = document.createElement('div');    spaceDiv.className = 'space-y-4';    var textarea = document.createElement('textarea');    textarea.id = 'answer';    textarea.disabled = true;    textarea.rows = '10';    textarea.className = 'text-xl flex w-full rounded-md border border-input bg-background ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 p-4 h-full min-h-[400px]'    textarea.placeholder = 'Enter your answer here...';    textarea.textContent = reply.answer;    spaceDiv.appendChild(textarea);    container.appendChild(spaceDiv);    innerDiv.appendChild(container);    pDiv.appendChild(innerDiv);    outerli.appendChild(flexDiv);    outerli.appendChild(pDiv);    const replyList = document.getElementById('reply-list');    replyList.appendChild(outerli);}