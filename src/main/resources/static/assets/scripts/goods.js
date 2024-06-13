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
            alert('주문을 완료하였습니다.');
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('POST', '/store/order');
    xhr.send(formData);
}