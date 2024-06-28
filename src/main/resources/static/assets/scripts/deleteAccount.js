const deleteMyAccountForm = document.querySelector('.delete-my-account-form');

deleteMyAccountForm.onsubmit = (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
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
            alert('알 수 없는 이유로, 회원탈퇴에 실패하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'success') {
            alert('그 동안 이용해 주셔서 감사합니다.');
            window.close();
            window.opener.location.href='/';
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }
        
    }
    xhr.open('DELETE', '/myPage/deleteMyAccount');
    xhr.send(formData);
}