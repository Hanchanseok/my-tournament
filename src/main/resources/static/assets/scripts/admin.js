const recognizeBtn = document.querySelectorAll('.recognize-button');

// 해당 인덱스의 대회 승인 전환
recognizeBtn.forEach(t => {
    t.onclick = (e) => {
        e.preventDefault();

        const index = t.parentNode.parentNode.querySelector('.tournamentIndex').innerText;

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
});


// 해당 유저 계정 정지 기능
const suspendBtn = document.querySelectorAll('.suspend-button');

suspendBtn.forEach(t => {
    t.onclick = (e) => {
        e.preventDefault();

        const email = t.parentNode.parentNode.querySelector('.email').innerText;
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
})