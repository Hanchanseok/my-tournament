// 페이징 눌렀을 시 아래로 스크롤
const url = new URL(window.location.href);  // url 객체

const urlParams = url.searchParams;         // 해당 주소에서 파라미터들 가져오기
console.log(urlParams.get("index"));        // ?index=1 일 경우 1
console.log(urlParams.get("page"));         // ?page=2 일 경우 2

if (urlParams.get("page") != null) {
    window.scrollTo(0, document.body.scrollHeight);
}


const commentForm = document.getElementById('comment-form');
// 댓글 작성
try {
    commentForm.onsubmit = (e) => {
        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append('content', commentForm['content'].value);
        formData.append('tournamentIndex', commentForm['tournamentIndex'].value);
        xhr.onreadystatechange = function() {
            if(xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if(xhr.status < 200 || xhr.status >= 300) {
                alert('알 수 없는 오류가 발생하였습니다.');
                return;
            }
            const responseObject = JSON.parse(xhr.responseText);
            if (responseObject['result'] === 'failure') {
                alert('댓글 작성에 실패하였습니다.');
            } else if (responseObject['result'] === 'success') {
                location.reload();
            }

        }
        xhr.open('POST', '/tournament/ranking');
        xhr.send(formData);
    }
} catch (e) {
    console.log('비로그인 상태이므로 form 누락');
}

// 댓글 수정
const updateButton = document.querySelectorAll('.update-button');
const updateForm = document.getElementById('update-comment-form');

updateForm.onclick = (e) => {
    if (e.target !== e.currentTarget) return; // 부모 요소만 클릭되도록
    // 수정 취소를 누르면 form 이 사라진다.
    updateForm.style.display='none';
}

updateButton.forEach(btn => {
    btn.onclick = () => {
        const index = btn.parentNode.querySelector('.comment-index').value; // 댓글 인덱스
        const content = btn.parentNode.parentNode.querySelector('.comment-content').innerText;  // 댓글 내용
        updateForm.style.display='flex';    // 수정 누를 시 form 나타남
        updateForm['content'].value = content;  // 해당 버튼에 속한 댓글 내용을 textarea 에

        updateForm.onsubmit = (e) => {
            e.preventDefault();
            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            formData.append('index', index);
            formData.append('content', updateForm['content'].value);
            xhr.onreadystatechange = function() {
                if(xhr.readyState !== XMLHttpRequest.DONE) {
                    return;
                }
                if(xhr.status < 200 || xhr.status >= 300) {
                    alert('알 수 없는 오류가 발생하였습니다.');
                    return;
                }
                const responseObject = JSON.parse(xhr.responseText);
                if (responseObject['result'] === 'failure') {
                    alert('댓글 수정에 실패하였습니다.');
                } else if (responseObject['result'] === 'success') {
                    location.reload();
                }

            }
            xhr.open('PATCH', '/tournament/ranking');
            xhr.send(formData);
        }
    }
});

// 댓글 삭제
const deleteButton = document.querySelectorAll('.delete-button');

deleteButton.forEach(btn => {
    btn.onclick = () => {
        if (!confirm('댓글을 삭제하시겠습니까?')) {
            return;
        }
        const index = btn.parentNode.querySelector('.comment-index').value;
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("index", index);
        xhr.onreadystatechange = function() {
            if(xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if(xhr.status < 200 || xhr.status >= 300) {
                alert('알 수 없는 오류가 발생하였습니다.');
                return;
            }
            const responseObject = JSON.parse(xhr.responseText);
            if (responseObject['result'] === 'failure') {
                alert('댓글 삭제에 실패하였습니다.');
            } else if (responseObject['result'] === 'success') {
                location.reload();
            }

        }
        xhr.open('DELETE', '/tournament/ranking');
        xhr.send(formData);
    };
});


// 댓글 신고
const reportButton = document.querySelectorAll('.report-button');

reportButton.forEach(btn => {
    btn.onclick = () => {
        if (!confirm('댓글을 신고하시겠습니까?')) {
            return;
        }
        const index = btn.parentNode.querySelector('.comment-index').value;
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("index", index);
        xhr.onreadystatechange = function() {
            if(xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            if(xhr.status < 200 || xhr.status >= 300) {
                alert('알 수 없는 오류가 발생하였습니다.');
                return;
            }
            const responseObject = JSON.parse(xhr.responseText);
            if (responseObject['result'] === 'failure') {
                alert('댓글 신고에 실패하였습니다.');
            } else if (responseObject['result'] === 'failure_report_own_comment') {
                alert('본인의 댓글은 신고할 수 없습니다.');
            } else if (responseObject['result'] === 'success') {
                alert('신고 완료');
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }

        }
        xhr.open('PATCH', '/tournament/ranking/report');
        xhr.send(formData);
    };
});
