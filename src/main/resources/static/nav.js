document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.getElementById('navToggle');
    const navbar = document.getElementById('navbar');
    if (toggle && navbar) {
        toggle.addEventListener('click', () => {
            const isOpen = navbar.classList.toggle('nav-open');
            toggle.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        });
    }

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            const overlay = document.getElementById('detailOverlay');
            if (overlay && overlay.classList.contains('show')) {
                closeDetail();
            }
        }
    });
});

function initMatrixBackground() {
    const canvas = document.getElementById('matrixCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    function resizeCanvas() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    resizeCanvas();
    window.addEventListener('resize', resizeCanvas);

    const chars = '0123456789';
    const fontSize = 16;
    let drops = new Array(Math.floor(canvas.width / fontSize)).fill(1);

    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    function draw() {
        ctx.fillStyle = 'rgba(10, 14, 39, 0.1)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        ctx.font = fontSize + 'px monospace';
        for (let i = 0; i < drops.length; i++) {
            ctx.fillStyle = '#4ade80';
            ctx.fillText(chars[Math.floor(Math.random() * chars.length)], i * fontSize, drops[i] * fontSize);
            if (drops[i] * fontSize > canvas.height && Math.random() > 0.98) drops[i] = 0;
            drops[i]++;
        }
    }
    setInterval(draw, 60);
}