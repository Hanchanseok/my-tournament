// 해당 인덱스의 대회 승인 전환
function recognize(index) {
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
            alert('승인여부 전환 완료!');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('토너먼트 승인 여부를 전환하지 못하였습니다.');
        }

    }
    xhr.open('PATCH', '/admin/recognize');
    xhr.send(formData);
}


// 해당 유저 계정 정지 기능
function suspend(email) {
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", email);
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
            alert('정지여부 전환 완료!');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('해당 계정은 없는 계정이거나 관리자 계정일 수 있습니다.');
        }

    }
    xhr.open('PATCH', '/admin/accounts');
    xhr.send(formData);
}


// 신고 받은 댓글 문제없음 (isReported를 false로)
function commentNa(index) {
    if (!confirm('해당 댓글이 정말 문제가 없습니까?')) {
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
            alert('무혐의 처리 완료!');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('무혐의 처리에 실패하였습니다.');
        }

    }
    xhr.open('PATCH', '/admin/reportedComments');
    xhr.send(formData);
}


// 신고 받은 댓글 삭제
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
    xhr.open('DELETE', '/admin/reportedComments');
    xhr.send(formData);
}
