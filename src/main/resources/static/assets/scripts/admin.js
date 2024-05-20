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