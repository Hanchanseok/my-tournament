// 캐러셀
const carouselPrev = document.querySelector('.carousel-prev');
const carouselNext = document.querySelector('.carousel-next');
const carousel = document.querySelector('.carousel'); // 움직일 케러셀
let index = 0;
const carouselCount = document.querySelectorAll('.carousel-image').length-1;

carouselPrev.onclick = (e) => {
    if (index === 0) return;
    index -= 1;
    carousel.style.transform = `translate3d(-${600 * index}px , 0, 0)`;
};

carouselNext.onclick = () => {
    if (index === carouselCount) return;
    index += 1;
    carousel.style.transform = `translate3d(-${600 * index}px , 0, 0)`;
}

// 주소 api
const buyForm = document.querySelector('.buy-form');
const addressFinder = document.querySelector('.address-finder');

addressFinder.hide = function () {
    this.classList.remove('-visible');
}

addressFinder.onclick = function () {
    addressFinder.hide();
}

addressFinder.show = function () {
    new daum.Postcode({
        width: '100%',
        height: '100%',
        oncomplete: function(data) {
            buyForm['addressPostal'].value = data['zonecode'];
            buyForm['addressPrimary'].value = data['address'];
            buyForm['addressSecondary'].focus();
            addressFinder.hide();
        }
    }).embed(addressFinder.querySelector(':scope > .dialog'));
    this.classList.add('-visible');
}

buyForm['findAddress'].onclick = function () {
    addressFinder.show();
}

// 수량 조절
const decreaseButton = document.querySelector('.decrease-button');
const increaseButton = document.querySelector('.increase-button');
const resetButton = document.querySelector('.reset-button');
const amountInput = document.querySelector('.amount-input');
const goodsStoke = document.querySelector('.goods-stoke').innerText;
const goodsPrice = document.querySelector('.goods-price').innerText;
const selectedPrice = document.querySelector('.selected-price');

decreaseButton.onclick = () => {
    let amount = +amountInput.value;
    if (amount === 1) return;
    amount -= 1
    selectedPrice.innerText = '';
    selectedPrice.innerText = +goodsPrice * amount + ' 원';
    amountInput.value = amount;
}

increaseButton.onclick = () => {
    let amount = +amountInput.value;
    if (amount === +goodsStoke) return;
    amount += 1
    selectedPrice.innerText = '';
    selectedPrice.innerText = +goodsPrice * amount + ' 원';
    amountInput.value = amount;
}

resetButton.onclick = () => {
    let amount = 1;
    selectedPrice.innerText = '';
    selectedPrice.innerText = +goodsPrice * amount + ' 원';
    amountInput.value = 1;
}

/***************** 주문 *******************/

buyForm.onsubmit = (e) => {
    e.preventDefault();
    if (buyForm['addressPostal'].value === '' || buyForm['addressPrimary'].value === '') {
        alert('우편번호 찾기를 완료해 주세요.');
        return;
    }
    if (buyForm['addressSecondary'].value === '') {
        alert('상세한 주소를 작성해 주십시오.');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("goodsIndex", buyForm['goodsIndex'].value);
    formData.append("amount", buyForm['amount'].value);
    formData.append("price", +buyForm['amount'].value * +goodsPrice);
    formData.append("addressPostal", buyForm['addressPostal'].value);
    formData.append("addressPrimary", buyForm['addressPrimary'].value);
    formData.append("addressSecondary", buyForm['addressSecondary'].value);
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('알 수 없는 오류가 발생하였습니다.');
            return;
        }
        const responseObject = JSON.parse(xhr.responseText);
        if (responseObject['result'] === 'failure_stoke_over') {
            alert('해당 제품은 품절되었습니다. 재고가 들어올 때 까지 기다려주세요.');
        } else if (responseObject['result'] === 'failure_wrong_price') {
            alert('최종 금액이 맞지 않습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'failure_none_sale') {
            alert('판매가 중단된 제품입니다.');
        } else if (responseObject['result'] === 'failure_none_login') {
            alert('권한이 없습니다. 로그인 후 주문해주시길 바랍니다.');
            location.href = '/user/login';
        } else if (responseObject['result'] === 'failure') {
            alert('알 수 없는 이유로 주문을 하지 못하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'success') {
            location.href = `/store/purchaseOrder?index=${responseObject['orderIndex']}`
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('POST', '/store/order');
    xhr.send(formData);
}


// 저장된 주소 넣기 및 저장된 내 주소 삭제
const savedAddress = document.querySelectorAll('.saved-address');

savedAddress.forEach(e => {
    const addressPostal = e.nextElementSibling;
    const addressPrimary = addressPostal.nextElementSibling;
    const addressSecondary = addressPrimary.nextElementSibling;
    const addressIndex = addressSecondary.nextElementSibling;
    const deleteButton = addressIndex.nextElementSibling;
    e.onclick = () => {
        buyForm['addressPostal'].value = addressPostal.value;
        buyForm['addressPrimary'].value = addressPrimary.value;
        buyForm['addressSecondary'].value = addressSecondary.value;
    }

    deleteButton.onclick = () => {
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("addressIndex", addressIndex.value);
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
                alert('알 수 없는 이유로, 주소 삭제에 실패하였습니다. 다시 시도해 주세요.');
            } else if (responseObject['result'] === 'success') {
                location.reload();
            } else {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }
        }
        xhr.open('DELETE', '/store/myAddress');
        xhr.send(formData);
    }
});

// 찜하기
const wishlistButton = document.querySelector('.wishlist-button');

wishlistButton.onclick = (e) => {
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("goodsIndex", buyForm['goodsIndex'].value);
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
            alert('알 수 없는 이유로, 제품을 위시리스트에 등록하지 못했습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'success') {
            alert('찜하기 완료, 마이페이지에서 확인해 주세요.');
        } else if (responseObject['result'] === 'failure_none_login') {
            alert('로그인이 필요한 서비스입니다. 로그인 페이지로 이동합니다.');
            location.href = '/user/login'
        } else if (responseObject['result'] === 'failure_already_wishlist') {
            alert('이미 찜한 제품입니다. 마이페이지에서 확인해 주세요.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('POST', '/store/wishlist');
    xhr.send(formData);
}