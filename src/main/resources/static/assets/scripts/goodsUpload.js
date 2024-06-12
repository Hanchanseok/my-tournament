/****** 파일 갯수 +- ******/
let goodsImagesDiv = document.querySelector('.goods-images');
let addImageButton = document.querySelectorAll('.add-image-button');
let deleteImageButton = document.querySelectorAll('.delete-image-button');
let count = 0;

addImageButton.forEach(e => {
    e.onclick = () => {
        if (count === 4) return;
        goodsImagesDiv.insertAdjacentHTML('beforeend', `
                <div class="image-box">
                    <label><input required class="goods-image-file" accept=".jpg, .jpeg, .png" type="file"></label>
                </div>
        `);
        count++;
    };
});

deleteImageButton.forEach(e => {
    e.onclick = () => {
        if (count === 0) return;
        let imageBox = document.querySelectorAll('.image-box');
        goodsImagesDiv.removeChild(imageBox[imageBox.length-1]);
        count--;
    }
});


/***************** 업로드 **********************/
const goodsUploadForm = document.querySelector('.goods-upload-form');

goodsUploadForm.onsubmit = (e) => {
    e.preventDefault();
    const goodsImageFile = document.querySelectorAll('.goods-image-file');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('title', goodsUploadForm['title'].value);
    formData.append('content', goodsUploadForm['content'].value);
    formData.append('price', goodsUploadForm['price'].value);
    formData.append('stoke', goodsUploadForm['stoke'].value);
    for (let i = 0; i < goodsImageFile.length; i++) {
        formData.append('files', goodsImageFile[i].files[0]);
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
            alert('업로드에 실패하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'failure_not_manager') {
            alert('관리자 계정으로만 굿즈를 업로드 할 수 있습니다.');
        } else if (responseObject['result'] === 'failure_image_length_over') {
            alert('이미지 파일은 최소 1개, 최대 5개까지 가능합니다.');
        } else if (responseObject['result'] === 'success') {
            alert('굿즈 업로드에 성공하였습니다.');
            location.href = '/store/';
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }
        
    }
    xhr.open('POST', '/store/upload');
    xhr.send(formData);
}