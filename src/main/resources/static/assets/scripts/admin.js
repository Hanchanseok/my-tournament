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
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
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
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH', '/admin/accounts');
    xhr.send(formData);
}

// 해당 유저를 관리자로
function patchAdmin(email) {
    if(!confirm('해당 유저에게 관리자 권한을 부여하시겠습니까?')) return;
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
            alert('관리자로 전환 완료!');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('관리자 권한을 부여하지 못했습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'failure_deleted_email') {
            alert('탈퇴한 계정이므로 관리자 권한을 부여할 수 없습니다.');
        } else if (responseObject['result'] === 'failure_suspended_email') {
            alert('정지된 계정이므로 관리자 권한을 부여할 수 없습니다.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH', '/admin/patchAdmin');
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
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
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
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('DELETE', '/admin/reportedComments');
    xhr.send(formData);
}

// 굿즈 판매여부 전환
function changeSale(index) {
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
            alert('판매 여부 전환 완료!');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('알 수 없는 이유로 판매 여부 전환에 실패하였습니다. 다시 시도해 주세요.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH', '/admin/changeGoodsSale');
    xhr.send(formData);
}

// 굿즈 주문 취소(관리자)
function cancelOrder(index) {
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
            alert('주문을 취소하였습니다.');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('알 수 없는 이유로 주문 취소에 실패하였습니다. 다시 시도해 주세요.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }
    }
    xhr.open('DELETE', '/admin/goodsOrder');
    xhr.send(formData);
}

// 신고 받은 리뷰 문제없음 (isReported를 false로)
function reviewNa(index) {
    if (!confirm('해당 리뷰가 정말 문제가 없습니까?')) {
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
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH', '/admin/goodsReview');
    xhr.send(formData);
}


// 신고 받은 리뷰 삭제
function reviewDelete(index) {
    if (!confirm('해당 리뷰를 삭제하시겠습니까?')) {
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
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('DELETE', '/admin/goodsReview');
    xhr.send(formData);
}

// 해당 굿즈 삭제
function deleteGoods(index) {
    if (!confirm('해당 굿즈를 삭제하시겠습니까?')) {
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
            alert('굿즈 삭제 완료');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('굿즈 삭제 실패');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('DELETE', '/admin/deleteGoods');
    xhr.send(formData);
}

// 토너먼트 삭제
function deleteTournament(index) {
    if (!confirm('해당 토너먼트를 삭제하시겠습니까?')) {
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
            alert('토너먼트 삭제 완료');
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('토너먼트 삭제 실패');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('DELETE', '/admin/deleteTournament');
    xhr.send(formData);
}