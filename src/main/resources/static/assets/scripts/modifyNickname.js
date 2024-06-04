// 닉네임 중복 검사 및 닉네임 변경
const nicknameForm = document.getElementById('nickname-form');
const warningMessage = document.querySelector('.warning-message');
// 닉네임 중복 검사
nicknameForm['nicknameSend'].onclick = (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('nickname', nicknameForm['nickname'].value);
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
            warningMessage.removeAttribute('hidden');
            warningMessage.innerText = '알맞은 닉네임을 입력해주세요.';
            warningMessage.style.color = 'red';
        } else if (responseObject['result'] === 'failure_duplicate_nickname') {
            warningMessage.removeAttribute('hidden');
            warningMessage.innerText = '중복되는 닉네임이 있습니다. 다른 닉네임으로 설정해주세요.';
            warningMessage.style.color = 'red';
        } else if (responseObject['result'] === 'success') {
            warningMessage.removeAttribute('hidden');
            warningMessage.innerText = '사용하셔도 좋은 닉네임 입니다.';
            warningMessage.style.color = 'green';
            nicknameForm['nickname'].disabled = true;
        }
    }
    xhr.open('POST', '/myPage/updateMyNickname');
    xhr.send(formData);
}

// 닉네임 변경
nicknameForm.onsubmit = (e) => {
    if (!(warningMessage.innerText === '사용하셔도 좋은 닉네임 입니다.')) {
        alert('닉네임 중복 검사를 완료해 주세요.');
        return null;
    }
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('nickname', nicknameForm['nickname'].value);
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
            alert('닉네임 변경에 실패하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'failure_duplicate_nickname') {
            alert('중복되는 닉네임을 입력하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'success') {
            alert('닉네임 변경에 성공하였습니다.');
            window.close();
            window.opener.location.reload();
        }
    }
    xhr.open('PATCH', '/myPage/updateMyNickname');
    xhr.send(formData);
}