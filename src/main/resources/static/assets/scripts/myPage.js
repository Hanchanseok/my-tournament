// 내 댓글 삭제
function commentDelete(index) {
    if (!confirm('해당 댓글을 삭제하시겠습니까?')) {
        return null;
    }
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
            location.reload();
        } else if (responseObject['result'] === 'failure') {
            alert('댓글 삭제 실패');
        }

    }
    xhr.open('DELETE', '/tournament/ranking');
    xhr.send(formData);
}

// 내가 찜한 굿즈 삭제
const goodsIndexes = document.querySelectorAll('.goods-index');

goodsIndexes.forEach(e => {
    e.nextElementSibling.onclick = (t) => {
        t.preventDefault();
        if(!confirm('내 찜 목록에서 삭제하시겠습니까?')) return null;
        let goodsIndex = e.value;
        const xhr = new XMLHttpRequest();
        const formData = new FormData();
        formData.append("goodsIndex", goodsIndex);
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
                alert('위시리스트 삭제 완료');
                location.reload();
            } else if (responseObject['result'] === 'failure') {
                alert('위시리스트 삭제 실패');
            } else  {
                alert('서버가 알 수 없는 응답을 반환하였습니다. 잠시 후 다시 시도해 주세요.');
            }

        }
        xhr.open('DELETE', '/myPage/myWishlist');
        xhr.send(formData);
    };
});