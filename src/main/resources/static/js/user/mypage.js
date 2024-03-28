let userId = null;
let cursorId = 0;

document.addEventListener("DOMContentLoaded", function () {
    userId = document.getElementById("userId").value;

    loadMoreMyReplies();
});

function linkGithubRepo() {
    const repoName = prompt("Github repository 명을 입력해주세요. (예: CS_Study)");
    if (repoName === undefined)
        return;
    axios.patch('/api/users/github-repo', {
        githubRepo: repoName
    }).then(function (response) {
        console.log(response);
        alert('GitHub Repository Name 이 등록되었습니다.');
    }).catch(function (error) {
        console.log(error);
        alert('GitHub Repository Name 등록에 실패했습니다.');
    });
}

function loadMoreMyReplies() {
    axios.get('/api/reply/user', {
        params: {
            userId: userId,
            cursorId: cursorId
        }
    }).then(function (response) {
        console.log(response);
        const replies = response.data;
        if (replies.length === 0) {
            if (cursorId !== 0) {
                alert('더 이상 불러올 답변이 존재하지 않습니다.');
            }
            return;
        }
        const replyList = document.getElementById('reply-list');

        replies.forEach(function (reply) {
            replyList.appendChild(buildReplyItem(reply));
        });
        cursorId = replies[replies.length - 1].replyId;
    }).catch(function (error) {
        console.log(error);
        alert('답변을 불러오는데 실패했습니다.');
    });
}

function buildReplyItem(reply) {
    let question = reply.question;
    if (question.length > 20)
        question = question.substring(0, 20) + '...';
    let answer = reply.answer;
    if (answer.length > 40)
        answer = answer.substring(0, 40) + '...';

    const liElement = document.createElement('li');
    liElement.className = 'rounded-lg bg-white p-6 shadow';
    liElement.className += ' cursor-pointer';

    const divOne = document.createElement('div');
    divOne.className = 'h-10 w-full rounded bg-gray-200 flex items-center align-middle';

    const spanOne = document.createElement('span');
    spanOne.className = 'mx-3';

    spanOne.textContent = question;
    divOne.appendChild(spanOne);

    const divTwo = document.createElement('div');
    divTwo.className = 'mt-4 pt-3 h-40 rounded-md bg-gray-200 items-center justify-center';

    const spanTwo = document.createElement('span');
    spanTwo.className = 'mx-3';
    spanTwo.textContent = answer;
    divTwo.appendChild(spanTwo);

    liElement.appendChild(divOne);
    liElement.appendChild(divTwo);

    liElement.addEventListener('click', function() {
        window.location.href = '/reply/' + reply.replyId;
    });

    return liElement;
}