const tournamentIndex = document.querySelector('.tournament-index').value;
const loading = document.getElementById('loading');
const tournamentTitle = document.querySelector('.tournament-title');
const tournamentRound = document.querySelector('.tournament-round');

loadTournament();
function loadTournament() {
    const xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        loading.hide();
        if(xhr.status < 200 || xhr.status >= 300) {
            alert('알 수 없는 오류가 발생하였습니다.');
            return;
        }
        const responseObject = JSON.parse(xhr.responseText);
        const title = responseObject['tournamentTitle'];    // 불러온 토너먼트 제목
        const products = responseObject['products'];        // 불러온 토너먼트 요소들
        // 선택 부분
        const playContainer = document.querySelector('.play-container');
        
        products.sort(() => Math.random() - 0.5); // 요소를 랜덤으로 섞기
        console.log(products);
        tournamentTitle.innerText = title;  // 제목 설정

        // 맨 처음에는 데이터들을 화면에 나타내기
        playContainer.insertAdjacentHTML('beforeend',
            `<div class="product-box">
                <div class="product-select">
                    <input class="productIndex1" value="0" type="hidden">
                    <img class="product-img" src="/tournament/productThumbnail?index=${products[0].index}" alt="">
                </div>
                <span class="product-name">${products[0].name}</span>
            </div>
            <div class="product-box">
                <div class="product-select">
                    <input class="productIndex2" value="1" type="hidden">
                    <img class="product-img" src="/tournament/productThumbnail?index=${products[1].index}" alt="">
                </div>
                <span class="product-name">${products[1].name}</span>
            </div>`);

        // 선택하는
        let productSelect = document.querySelectorAll('.select');
        let i = 2;      // 인덱스
        let round8 = []; // 8강에 진출할 요소는?
        
        /*
        지난 이야기
        16강을 성공했으나 8강으로 넘어가는 과정을 생각하지 못했다.
        16강을 마쳐도 누르면 onclick이 작동한다. 빠져나가야 한다.
        
        1. 그냥 left, right 선택을 따로 빼네어 foreach 쓰지 않는다.
        2. foreach 빠져나갈 방법을 찾는다.
        3. 누를때 마다 선택받지 못한 product를 products 배열에서 공백처리후 16강 완료되면 뺀다. 그리고 그걸로 8강 시작
        * */

        console.log(productSelect);
        productSelect.forEach(e => {

            e.onclick = function round16() {
                // 왼쪽 선택시 왼쪽의 데이터가 8강 진출, 오른쪽도 마찬가지
                if (e.innerText === '선택1') {
                    let productIndex1 = document.querySelector('.productIndex1');
                    round8.push(products[productIndex1.value]);
                } else if (e.innerText === '선택2') {
                    let productIndex2 = document.querySelector('.productIndex2');
                    round8.push(products[productIndex2.value]);
                }
                console.log(i);
                console.log(round8);
                if (i === 16) {
                    return false;
                }

                playContainer.innerHTML = '';
                playContainer.insertAdjacentHTML('beforeend',
                    `<div class="product-box">
                <div class="product-select">
                    <input class="productIndex1" value="${i}" type="hidden">
                    <img class="product-img" src="/tournament/productThumbnail?index=${products[i].index}" alt="">
                </div>
                <span class="product-name">${products[i].name}</span>
            </div>
            <div class="product-box">
                <div class="product-select">
                    <input class="productIndex2" value="${i+1}" type="hidden">
                    <img class="product-img" src="/tournament/productThumbnail?index=${products[i+1].index}" alt="">
                </div>
                <span class="product-name">${products[i+1].name}</span>
            </div>`);
                i+=2;
            }
        });


    }
    xhr.open('GET', `/tournament/loadTournament?index=${tournamentIndex}`);
    xhr.send();
    loading.show();
}