// 이미지파일 input들
const fileInput = document.querySelector('.file');
try {

    // 이미지 뜨게 하기
    fileInput.onchange = () => {
        const fileImage = fileInput.parentNode.querySelector('.file-image');
        const file = fileInput.files[0];

        if (file.type.substring(0, 6) !== 'image/' ) return;
        console.log(file);
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
            fileImage.src = reader.result;
        }
    };
} catch (e) {

}


try {
    const modifyTournamentForm = document.querySelector(".modify-tournament-form");

    modifyTournamentForm.onsubmit = (e) => {
        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("index", modifyTournamentForm['index'].value);
        formData.append("title", modifyTournamentForm['title'].value);
        formData.append("content", modifyTournamentForm['content'].value);
        formData.append("file", fileInput.files[0]);
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
                alert('수정에 실패하였습니다.');
            } else if (responseObject['result'] === 'success') {
                location.href = `/myPage/myTournament/product?index=${modifyTournamentForm['index'].value}`;
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }

        }
        xhr.open('POST', '/myPage/myTournament/modify');
        xhr.send(formData);
    }
} catch (e) {

}

// 토너먼트 요소 수정
try {
    const modifyProductForm = document.querySelector('.modify-product-form');

    modifyProductForm.onsubmit = (e) => {
        e.preventDefault();
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("index", modifyProductForm['index'].value);
        formData.append("tournamentIndex", modifyProductForm['tournamentIndex'].value);
        formData.append("name", modifyProductForm['name'].value);
        formData.append("file", fileInput.files[0]);
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
                alert('수정에 실패하였습니다.');
            } else if (responseObject['result'] === 'success') {
                location.href = `/myPage/myTournament/product?index=${modifyProductForm['tournamentIndex'].value}`;
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }

        }
        xhr.open('POST', '/myPage/myTournament/product/modify');
        xhr.send(formData);
    }
} catch (e) {

}

// 닫기 버튼
try {
    const closeButton = document.querySelector('.close-button');
    closeButton.onclick = () => {
        window.close();
        window.opener.location.reload();
    }
} catch (e) {

}