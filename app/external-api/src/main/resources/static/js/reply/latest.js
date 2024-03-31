let cursorId = 0;

document.addEventListener("DOMContentLoaded", function () {
    const replyList = document.getElementById('reply-list');
    replyList.innerHTML = '';
    loadLatestReplies(cursorId);
});
function loadLatestReplies(cursorId) {
    axios.get('/api/reply', {
        params: {
            cursorId: cursorId,
        }
    }).then(function (response) {
        console.log(response);
        const replies = response.data.replyList;

        const replyList = document.getElementById('reply-list');
        if (replies.length === 0) {
            alert('더 이상 불러올 답변이 없습니다.');
        }

        for (let i = 0; i < replies.length; i++) {
            replyList.appendChild(buildReplyItem(replies[i]));
        }
    }).catch(function (error) {
        console.log(error);
        alert('답변을 불러오는데 실패했습니다.');
    });
}


function buildReplyItem(reply) {
    const li = document.createElement("li");
    li.className = "rounded-lg border bg-white bg-card text-card-foreground shadow-lg transform hover:scale-105 transition-transform duration-200 ease-in-out min-w-full";
    li.dataset.v0T = "card";

    const flexCol = document.createElement("div");
    flexCol.className = "flex flex-col space-y-1.5 p-6";
    li.appendChild(flexCol);

    const b = document.createElement("div");
    b.className = "flex flex-row items-center h-16";
    flexCol.appendChild(b);

    const spanImage = document.createElement("span");
    spanImage.className = "relative flex h-10 w-10 shrink-0 overflow-hidden rounded-full";
    b.appendChild(spanImage);

    const img = document.createElement("img");
    img.src = reply.userImage;
    img.alt = "작성자 이미지";
    spanImage.appendChild(img);

    const spanText = document.createElement("span");
    spanText.className = "flex h-full w-full items-center justify-start rounded-full bg-muted ml-2";
    spanText.textContent = reply.userName;
    b.appendChild(spanText);

    const a = document.createElement("div");
    a.className = "flex ml-4 h-16";
    flexCol.appendChild(a);

    const h3 = document.createElement("h3");
    h3.className = "text-xl font-semibold whitespace-normal leading-relaxed tracking-tight w-full";
    h3.textContent = reply.answer;
    a.appendChild(h3);

    const itemsEnd = document.createElement("div");
    itemsEnd.className = "items-center p-6 flex justify-end";
    li.appendChild(itemsEnd);

    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("width", "24");
    svg.setAttribute("height", "24");
    svg.setAttribute("viewBox", "0 0 24 24");
    svg.setAttribute("fill", "none");
    svg.setAttribute("stroke", "currentColor");
    svg.setAttribute("stroke-width", "2");
    svg.setAttribute("stroke-linecap", "round");
    svg.setAttribute("stroke-linejoin", "round");
    svg.className = "text-gray-400";
    itemsEnd.appendChild(svg);

    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
    path.setAttribute("d", "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z");
    svg.appendChild(path);

    const spanLike = document.createElement("span");
    spanLike.id = "likeCount" + reply.replyId;
    spanLike.className = "ml-1.5";
    spanLike.textContent = reply.likeCount;
    itemsEnd.appendChild(spanLike);

    li.addEventListener('click', function() {
        window.location.href = '/reply/' + reply.historyId;
    });

    return li;
}