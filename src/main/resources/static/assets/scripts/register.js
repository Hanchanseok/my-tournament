const registerForm = document.getElementById('register-form');
const emailSend = registerForm['emailSend'];
const emailVerify = registerForm['emailVerify'];
const warningEmail = document.querySelector('.email-message');
const warningEmailCode = document.querySelector('.emailCode-message');
const warningRegister = document.querySelector('.register-message');
const loading = document.getElementById('loading');

emailSend.onclick = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", registerForm['email'].value);
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        loading.hide();
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('알 수 없는 오류가 발생하였습니다.');
            return;
        }
        const responseObject = JSON.parse(xhr.responseText);
        if (responseObject['result'] === 'success') {
            warningEmail.hide();
            registerForm['emailCode'].enable();
            registerForm['email'].disable();
            registerForm['emailCode'].focus();
            registerForm['emailSalt'].value = responseObject['salt'];
        } else if (responseObject['result'] === 'failure') {
            warningEmail.show();
        } else if (responseObject['result'] === 'failure_duplicate_email') {
            warningEmail.innerText = '해당 이메일은 이미 등록이 되어 있습니다.';
            warningEmail.show();
        }

    }
    xhr.open('POST', '/user/registerEmail');
    xhr.send(formData);
    loading.show();
};

emailVerify.onclick = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", registerForm['email'].value);
    formData.append("salt", registerForm['emailSalt'].value);
    formData.append("code", registerForm['emailCode'].value);
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        loading.hide();
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('알 수 없는 오류가 발생하였습니다.');
            return;
        }
        const responseObject = JSON.parse(xhr.responseText);
        if (responseObject['result'] === "success") {
            warningEmailCode.hide();
            registerForm['password'].enable();
            registerForm['password'].focus();
            registerForm['passwordCheck'].enable();
            registerForm['nickname'].enable();
            registerForm['emailCode'].disable();
        } else if (responseObject['result'] === "failure") {
            warningEmailCode.show();
        } else if (responseObject['result'] === "failure_expired") {
            warningEmailCode.innerText = '만료된 코드입니다.';
            warningEmailCode.show();
        }
    }
    xhr.open('PATCH', '/user/registerEmail');
    xhr.send(formData);
    loading.show();
}

registerForm.onsubmit = (e) => {
    e.preventDefault();
    
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", registerForm['email'].value);
    formData.append("salt", registerForm['emailSalt'].value);
    formData.append("code", registerForm['emailCode'].value);
    formData.append("password", registerForm['password'].value);
    formData.append("passwordCheck", registerForm['passwordCheck'].value);
    formData.append("nickname", registerForm['nickname'].value);
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        loading.hide();
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('알 수 없는 오류가 발생하였습니다.');
            return;
        }
        const responseObject = JSON.parse(xhr.responseText);
        if (responseObject['result'] === "success") {
            warningRegister.hide();
            location.href = '/user/login';
        } else if (responseObject['result'] === "failure") {
            warningRegister.show();
        } else if (responseObject['result'] === "failure_password_regex") {
            warningRegister.innerText = '비밀번호의 구성이 잘못되었습니다.';
            warningRegister.show();
        } else if (responseObject['result'] === "failure_password_check") {
            warningRegister.innerText = '비밀번호가 맞지 않습니다.';
            warningRegister.show();
        } else if (responseObject['result'] === "failure_nickname_regex") {
            warningRegister.innerText = '잘못된 닉네임 입니다.';
            warningRegister.show();
        } else if (responseObject['result'] === "failure_email_auth_verified") {
            warningRegister.innerText = '이메일 인증이 완료되지 않았습니다.';
            warningRegister.show();
        } else if (responseObject['result'] === "failure_duplicate_nickname") {
            warningRegister.innerText = '이미 사용중인 닉네임 입니다.';
            warningRegister.show();
        }
        
    }
    xhr.open('POST', '/user/register');
    xhr.send(formData);
    loading.show();
}