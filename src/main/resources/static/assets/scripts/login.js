const loginForm = document.getElementById('login-form');
const warning = document.querySelector('.warning');

loginForm.onsubmit = (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", loginForm['email'].value);
    formData.append("password", loginForm['password'].value);
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
            warning.hide();
            location.href = '/';
        } else if (responseObject['result'] === "failure") {
            warning.show();
        } else if (responseObject['result'] === "failure_is_deleted") {
            warning.innerText = '해당 계정은 탈퇴된 계정입니다.';
            warning.show();
        } else if (responseObject['result'] === "failure_is_suspended") {
            warning.innerText = '해당 계정은 정지된 계정입니다.';
            warning.show();
        } else if (responseObject['result'] === "failure_password") {
            warning.innerText = '비밀번호가 맞지 않습니다.';
            warning.show();
        } else if (responseObject['result'] === "failure_email") {
            warning.innerText = '해당 계정은 없는 계정입니다.';
            warning.show();
        }

    }
    xhr.open('POST', '/user/login');
    xhr.send(formData);
}