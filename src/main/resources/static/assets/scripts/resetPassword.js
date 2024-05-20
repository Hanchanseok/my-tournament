const resetPasswordForm = document.getElementById('resetPassword-form');
const emailSend = resetPasswordForm['emailSend'];
const emailVerify = resetPasswordForm['emailVerify'];
const warningEmail = document.querySelector('.email-message');
const warningEmailCode = document.querySelector('.emailCode-message');
const warningRegister = document.querySelector('.register-message');
const loading = document.getElementById('loading');

emailSend.onclick = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", resetPasswordForm['email'].value);
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
            resetPasswordForm['emailCode'].enable();
            resetPasswordForm['email'].disable();
            resetPasswordForm['emailCode'].focus();
            resetPasswordForm['emailSalt'].value = responseObject['salt'];
        } else if (responseObject['result'] === 'failure') {
            warningEmail.show();
        } else if (responseObject['result'] === 'failure_none_register_email') {
            warningEmail.innerText = '등록되지 않은 이메일입니다.';
            warningEmail.show();
        }

    }
    xhr.open('POST', '/user/resetPasswordEmail');
    xhr.send(formData);
    loading.show();
}

emailVerify.onclick = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", resetPasswordForm['email'].value);
    formData.append("salt", resetPasswordForm['emailSalt'].value);
    formData.append("code", resetPasswordForm['emailCode'].value);
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
            resetPasswordForm['password'].enable();
            resetPasswordForm['password'].focus();
            resetPasswordForm['passwordCheck'].enable();
            resetPasswordForm['nickname'].enable();
            resetPasswordForm['emailCode'].disable();
        } else if (responseObject['result'] === "failure") {
            warningEmailCode.show();
        } else if (responseObject['result'] === "failure_expired") {
            warningEmailCode.innerText = '만료된 코드입니다.';
            warningEmailCode.show();
        }

    }
    xhr.open('PATCH', '/user/resetPasswordEmail');
    xhr.send(formData);
    loading.show();
}

resetPasswordForm.onsubmit = (e) => {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", resetPasswordForm['email'].value);
    formData.append("salt", resetPasswordForm['emailSalt'].value);
    formData.append("code", resetPasswordForm['emailCode'].value);
    formData.append("password", resetPasswordForm['password'].value);
    formData.append("passwordCheck", resetPasswordForm['passwordCheck'].value);
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
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
        } else if (responseObject['result'] === "failure_email_auth_verified") {
            warningRegister.innerText = '이메일 인증이 완료되지 않았습니다.';
            warningRegister.show();
        }
    }
    xhr.open('PATCH', '/user/resetPassword');
    xhr.send(formData);
}