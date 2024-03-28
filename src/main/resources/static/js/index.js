let sockJs = null;
let stompClient = null;
let userId = null;
let cursorId = 0;
let category = null;
document.addEventListener("DOMContentLoaded", function () {
    category = document.getElementById("category-info").value;
    if (category === null) {
        const withPersonBtn = document.getElementById('with-person-btn');
        withPersonBtn.innerText = '매칭 취소';
        withPersonBtn.onclick = function () {
            stompClient.send("/app/match/cancel", {}, {}, function (error) {
                console.log('error', error);
            });
        };
    }
    userId = document.getElementById("userId").value;
    if (userId === null || userId === "") {
        console.log('userId is null or empty');
        return;
    }
    sockJs = new SockJS("/stomp");
    stompClient = Stomp.over(sockJs);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        /*
        stompClient.subscribe('/user/' + userId + '/match', function (message) {
            console.log('/user/{userId}/match: ', message);
        });
        destination 은 /user/{userId}/match 이지만, /user/match 로 subscribe 해야한다.
         */

        stompClient.subscribe('/user/match', function (frame) {
            location.href = "/reply/multi";
        });

        stompClient.subscribe('/user/match/join', function (frame) {
            console.log('/user/match/join: ', frame);
            frame = JSON.parse(frame.body);
            if (frame.statusCodeValue === 201) {
                alert('매칭 큐에 참가하였습니다.');
                const withPersonBtn = document.getElementById('with-person-btn');
                withPersonBtn.innerText = '매칭 취소';
                withPersonBtn.onclick = function () {
                    stompClient.send("/app/match/cancel", {}, {}, function (error) {
                        console.log('error', error);
                    });
                };
            } else if (frame.statusCodeValue === 204) {
                console.log('frame', frame);
                alert('매칭을 취소하였습니다.');
                const withPersonBtn = document.getElementById('with-person-btn');
                withPersonBtn.innerText = '사람과 함께';
                withPersonBtn.onclick = joinQueue;
            } else {
                alert('이미 매칭 큐에 참가하였습니다.')
            }
        });

        stompClient.send("/app/info", {}, {}, function (error) {
            console.log('debug', error);
        });
    });
});

function joinQueue() {
    let category = document.getElementById("category").value;
    stompClient.send("/app/match/join", {}, category, function (error) {
        console.log('error', error);
    });
}

function startStudy() {
    let category = document.getElementById("category").value;
    if (category === null || category === "카테고리 선택") {
        alert('학습 카테고리를 선택해주세요.');
        return;
    }
    location.href = "/reply/solo?category=" + category;
}

function getStudyHistory() {
    axios.get('/api/reply/' + category, {
        params: {
            cursorId: cursorId
        }
    }).then(function (response) {
        console.log(response);
        const problems = response.data;
        if (problems.length === 0) {
            alert('해당 카테고리에는 더 이상 문제가 존재하지 않습니다.');
            return;
        }
        const problemList = document.getElementById('problem-list');

        problems.forEach(function (problem) {
            problemList.appendChild(buildProblemItem(problem));
        });
        cursorId = problems[problems.length - 1].problemId;

    }).catch(function (error) {
        console.log(error);
        alert('카테고리가 유효하지 않습니다.')
    });
}