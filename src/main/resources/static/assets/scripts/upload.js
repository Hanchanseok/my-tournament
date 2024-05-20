// 이미지파일 input들
const fileInput = document.querySelectorAll('.file');

// 이미지 뜨게 하기
fileInput.forEach(e => {
   e.onchange = () => {
       const fileImage = e.parentNode.parentNode.querySelector('.file-image');
       const file = e.files[0];
       
       if (file.type.substring(0, 6) !== 'image/' ) return;
       
       const reader = new FileReader();
       reader.readAsDataURL(file);
       
       reader.onload = () => {
           fileImage.src = reader.result;
       }
   }
});


/************** 대회 업로드 ***************/
const uploadForm = document.getElementById('upload-form');
const files = document.querySelectorAll('.file');
const productName = document.querySelectorAll('.product-name');

uploadForm.onsubmit = (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("email", uploadForm['email'].value);
    formData.append("title", uploadForm['title'].value);
    formData.append("content", uploadForm['content'].value);
    // 파일들과 이름들 넣기
    for (let i = 0; i < files.length; i++) {
        formData.append("files", files[i].files[0]);
        formData.append("productNames", productName[i].value);
        // 만약 썸네일 부분 체크되어 있다면 해당 파일을 썸네일로
        if (files[i].parentNode.parentNode.querySelector('.thumbnail-select').checked) {
            formData.append("tournamentThumbnail", files[i].files[0]);
        }
    }
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
            alert('업로드에 실패하였습니다.');
        } else if (responseObject['result'] === 'success') {
            location.href = '/';
        }

        
    }
    xhr.open('POST', '/tournament/upload');
    xhr.send(formData);
}
