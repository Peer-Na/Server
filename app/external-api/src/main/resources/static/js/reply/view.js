let problemId = null;
document.addEventListener('DOMContentLoaded', function () {
    problemId = document.getElementById('problemId').value;

    document.querySelectorAll('.like-button').forEach(function(button) {
        button.addEventListener('click', function() {
            const replyId = this.getAttribute('data-reply-id');
            likeReply(replyId);
        });
    });

    /*
    좋아요 취소 기능 봉인
    document.querySelectorAll('.dislike-button').forEach(function(button) {
        button.addEventListener('click', function() {
            const replyId = this.getAttribute('data-reply-id');
            disLikeReply(replyId);
        });
    });
     */
});

function openOthersReply() {
    window.open('/reply/others?problemId=' + problemId);
}

function likeReply(replyId) {
    axios.post('/api/replies/likey?replyId=' + replyId,
    ).then(function (response) {
        console.log(response);
        const likeCountElement = document.getElementById('like-count-' + replyId);
        likeCountElement.innerText = (parseInt(likeCountElement.innerText) + 1).toString();
        const likeIcon = document.getElementById('like-icon-' + replyId);
        likeIcon.attributes.fill.value = 'red';
    }).catch(function (error) {
        console.log(error);
        alert('이미 좋아요를 누른 답변입니다.');
    });
}
/*
좋아요 취소 기능 봉인
function disLikeReply(replyId) {
    axios.delete('/api/replies/likey?replyId=' + replyId,
    ).then(function (response) {
        console.log(response);
        const likeCountElement = document.getElementById('like-count-' + replyId);
        likeCountElement.innerText = (parseInt(likeCountElement.innerText) - 1).toString();
        const dislikeCountElement = document.getElementById('dislike-count-' + replyId);
        dislikeCountElement.innerText = (parseInt(dislikeCountElement.innerText) - 1).toString();
        const likeButton = document.getElementById('like-btn-' + replyId);
        likeButton.style.display = 'block';
        const dislikeButton = document.getElementById('dislike-btn-' + replyId);
        dislikeButton.style.display = 'none';
    }).catch(function (error) {
        console.log(error);
        alert('이미 좋아요를 취소한 답변입니다.');
    });
}
 */
