let goodsImagesDiv = document.querySelector('.goods-images');
let addImageButton = document.querySelectorAll('.add-image-button');
let deleteImageButton = document.querySelectorAll('.delete-image-button');
let count = 0;

addImageButton.forEach(e => {
    e.addEventListener('click' , function add(){
        if (count === 4) return;
        goodsImagesDiv.insertAdjacentHTML('beforeend', `
                <div class="image-box">
                    <input required id="goods-image-file" accept=".jpg, .jpeg, .png" type="file">
                </div>
        `);
        count++;
    });
});

deleteImageButton.forEach(e => {
    e.onclick = () => {
        if (count === 0) return;
        let imageBox = document.querySelectorAll('.image-box');
        goodsImagesDiv.removeChild(imageBox[imageBox.length-1]);
        count--;
    }
})