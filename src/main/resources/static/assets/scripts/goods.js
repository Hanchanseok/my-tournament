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