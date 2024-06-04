// 패스워드 변경
const passwordForm = document.getElementById('password-form');
const warningMessage = document.querySelector('.warning-message');

passwordForm.onsubmit = (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("password", passwordForm['password'].value);
    formData.append("passwordCheck", passwordForm['passwordCheck'].value);
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
            warningMessage.innerText = '비밀번호 변경에 실패하였습니다. 다시 시도해 주세요.';
            warningMessage.style.color = 'red';
        } else if (responseObject['result'] === 'failure_password_regex') {
            warningMessage.removeAttribute('hidden');
            warningMessage.innerText = '비밀번호 구성이 올바르지 않습니다.';
            warningMessage.style.color = 'red';
        } else if (responseObject['result'] === 'failure_password_check') {
            warningMessage.removeAttribute('hidden');
            warningMessage.innerText = '비밀번호가 맞지 않습니다. 다시 확인해 주세요.';
            warningMessage.style.color = 'red';
        } else if (responseObject['result'] === 'failure_current_password') {
            warningMessage.removeAttribute('hidden');
            warningMessage.innerText = '현재 비밀번호와 같습니다. 다른 비밀번호를 입력해주세요.';
            warningMessage.style.color = 'red';
        } else if (responseObject['result'] === 'success') {
            alert('비밀번호 변경 성공!');
            window.close();
            window.opener.location.reload();
        }

    }
    xhr.open('PATCH', '/myPage/updateMyPassword');
    xhr.send(formData);
};