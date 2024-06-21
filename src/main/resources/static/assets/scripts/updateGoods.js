let amount = document.querySelectorAll('.image-box').length;
let deletedImagesIndex = [];
getImage();
// 이미지 뜨게 하기
function getImage() {
    const fileInput = document.querySelectorAll('.goods-image-file');
    fileInput.forEach(e => {
        e.onchange = () => {
            const fileImage = e.parentNode.parentNode.querySelector('.file-image');
            const file = e.files[0];
            console.log(file);

            if (file.type.substring(0, 6) !== 'image/' ) return;

            const reader = new FileReader();
            reader.readAsDataURL(file);

            reader.onload = () => {
                fileImage.src = reader.result;
            }
        }
    });
    const imageDeleteButton = document.querySelectorAll('.image-delete-button');
    const goodsImages = document.querySelector('.goods-images');
    imageDeleteButton.forEach(e => {
        e.onclick = () => {
            if (amount === 1) return;
            const imageBox = e.parentNode;
            try {
                const imageIndex = imageBox.querySelector('.goods-image-get-index').value;
                deletedImagesIndex.push(imageIndex);
            } catch (e) {}

            imageBox.remove();
            amount--;
        }
    });
    const addImageButton = document.querySelector('.add-image-button');
    addImageButton.onclick = () => {
        if (amount === 5) return;
        goodsImages.insertAdjacentHTML('beforeend', `
        <div class="image-box">
                            <label class="show-image">
                                <img class="file-image" src="/assets/images/no-image.png" alt="">
                                <input hidden class="goods-image-file" accept=".jpg, .jpeg, .png" type="file">
                            </label>
                            <label class="image-delete-button"><input value="X" type="button"></label>
                        </div>
        `);
        getImage();
        amount++;
    };
}

const imageDeleteButton = document.querySelectorAll('.image-delete-button');
let firstButton = true;
imageDeleteButton.forEach(e => {
    if (firstButton === true) {
        e.parentNode.style.border = '1px solid #009fff';
        e.remove();
        firstButton = false;
    }
});

const updateGoodsForm = document.querySelector('.update-goods-info-form');

updateGoodsForm.onsubmit = (e) => {
    e.preventDefault();
    const files = document.querySelectorAll('.goods-image-file');
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("index", updateGoodsForm['index'].value);
    formData.append("title", updateGoodsForm['title'].value);
    formData.append("content", updateGoodsForm['content'].value);
    formData.append("price", updateGoodsForm['price'].value);
    formData.append("discount", updateGoodsForm['discount'].value);
    formData.append("stoke", updateGoodsForm['stoke'].value);
    formData.append("deletedImagesIndex", deletedImagesIndex);
    if (files != null) {
        for (let i = 0; i < files.length; i++) {
            formData.append("files", files[i].files[0]);
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
            alert('수정에 실패하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'success') {
            alert('굿즈 정보 수정에 성공하였습니다.');
            window.close();
            window.opener.location.reload();
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH', '/admin/updateGoodsInfo');
    xhr.send(formData);
}
