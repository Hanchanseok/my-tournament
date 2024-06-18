const purchaseOrderForm = document.querySelector('.purchase-order-form');

// 주문 취소하기
const cancelOrderButton = document.querySelector('.cancel-order-button');
cancelOrderButton.addEventListener('click', cancelOrder);
function cancelOrder(e) {
    if (!confirm('주문을 취소하시겠습니까? 저장된 주문 내역은 다시 불러올 수 없습니다.')) {
        return;
    }
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("index", purchaseOrderForm['orderIndex'].value);
    formData.append("amount", purchaseOrderForm['orderAmount'].value);
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
            alert('알 수 없는 이유로, 주문 취소에 실패하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'failure_wrong_user') {
            alert('권한이 없습니다. 로그인 후 다시 시도하십시오.');
            location.href = '/user/login';
        } else if (responseObject['result'] === 'success') {
            alert('주문을 취소하였습니다.');
            location.href = '/store/';
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('DELETE', '/store/order');
    xhr.send(formData);
}

// 할인 내역 버튼 클릭시 할인 내역 확인
const discountDetailButton = document.querySelector('.discount-detail-button');
const discountDetail = document.querySelector('.discount-detail');
discountDetailButton.onclick = () => {
    discountDetail.classList.toggle('show');
}


// 결제하기
purchaseOrderForm.onsubmit = (e) => {
    e.preventDefault();
    const checkAgree = document.querySelector('.check-agree');
    if (!checkAgree.checked) {
        checkAgree.nextElementSibling.style.color = 'red';
        return;
    }
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append("index", purchaseOrderForm['orderIndex'].value);
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
            alert('알 수 없는 이유로, 결제에 실패하였습니다. 다시 시도해 주세요.');
        } else if (responseObject['result'] === 'success') {
            alert('결제 완료! 제품을 기다려주십시오.');
            location.href = '/store/';
        } else {
            alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
        }

    }
    xhr.open('PATCH', '/store/purchaseOrder');
    xhr.send(formData);
}