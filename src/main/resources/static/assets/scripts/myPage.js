// 내 댓글 삭제
function commentDelete(index) {
    if (!confirm('해당 댓글을 삭제하시겠습니까?')) {
        return null;
    }
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
        if (responseObject['result'] === 'success') {
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('댓글 삭제 실패');
        }

    }
    xhr.open('DELETE', '/tournament/ranking');
    xhr.send(formData);
}