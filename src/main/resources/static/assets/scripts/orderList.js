const shoppingCart = document.querySelector('#shopping-cart');
const orderList = shoppingCart.querySelector('.order-list');
const orderListCount = shoppingCart.querySelector('.order-list-count');

// 화면의 오른쪽 아래 미결재 주문 리스트 뜨게 하기
goodsOrderList();
function goodsOrderList() {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if(xhr.status < 200 || xhr.status >= 300) {
            return;
        }
        try {
            const responseObject = JSON.parse(xhr.responseText);
            const goodsOrder = responseObject['goodsOrder'];    // 미결재 주문들 불러옴
            if (goodsOrder.length > 0) {    // 주문들이 0개 이상일 경우, 알림 뜨게 함
                orderListCount.innerText = '';
                orderListCount.classList.add('show');
                orderListCount.innerText = `${goodsOrder.length}`;
            }
            // orderList.innerHTML = '';
            // 주문 목록들 나열
            for (let i = 0; i < goodsOrder.length; i++) {
                orderList.insertAdjacentHTML('beforeend', `
                <a href=/store/purchaseOrder?index=${goodsOrder[i].index}>${goodsOrder[i].title} ${goodsOrder[i].amount}개</a>
                `)
            }

        }catch (e) {

        }

    }
    xhr.open('GET', '/store/orderList');
    xhr.send();
}
