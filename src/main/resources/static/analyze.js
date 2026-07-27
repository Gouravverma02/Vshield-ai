document.addEventListener('DOMContentLoaded', () => {
    const textarea = document.getElementById('messageInput');
    const charCount = document.getElementById('charCount');
    if (textarea && charCount) {
        textarea.addEventListener('input', () => {
            const len = textarea.value.length;
            charCount.textContent = len + ' / 5000 characters';
            charCount.style.color = len > 5000 ? 'var(--danger)' : 'var(--text-muted)';
        });
    }
});

function submitAnalysis(event) {
    event.preventDefault();

    const text = document.getElementById('messageInput').value.trim();
    const btn = document.getElementById('analyzeBtn');
    const resultBox = document.getElementById('resultBox');
    const loadingBox = document.getElementById('loadingBox');
    const errorBox = document.getElementById('errorBox');

    errorBox.classList.remove('show');
    resultBox.classList.remove('show');

    if (!text) {
        errorBox.textContent = 'Please paste a message to analyze.';
        errorBox.classList.add('show');
        return;
    }

    if (text.length > 5000) {
        errorBox.textContent = 'Message is too long. Please keep it under 5000 characters.';
        errorBox.classList.add('show');
        return;
    }

    btn.disabled = true;
    loadingBox.classList.add('show');

    fetch('/api/analyze', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text })
    })
        .then(res => {
            if (res.status === 401) {
                window.location.href = 'login.html';
                return null;
            }
            return res.json().then(data => ({ status: res.status, data }));
        })
        .then(result => {
            loadingBox.classList.remove('show');
            btn.disabled = false;

            if (!result) return;

            if (result.status !== 200) {
                errorBox.textContent = result.data.error || 'Something went wrong. Please try again.';
                errorBox.classList.add('show');
                return;
            }

            renderResult(result.data);
        })
        .catch(() => {
            loadingBox.classList.remove('show');
            btn.disabled = false;
            errorBox.textContent = 'Network error. Please check your connection and try again.';
            errorBox.classList.add('show');
        });
}

function renderResult(data) {
    const resultBox = document.getElementById('resultBox');
    const verdict = data.verdict;
    const verdictClass = verdict.toLowerCase();

    const badgeLabel = {
        SAFE: '🟢 SAFE',
        SUSPICIOUS: '🟡 SUSPICIOUS',
        DANGEROUS: '🔴 DANGEROUS'
    }[verdict] || verdict;

    let reasonsHtml = '<li style="color:var(--text-secondary);">No red flags detected.</li>';
    if (data.reasons && data.reasons.length > 0) {
        reasonsHtml = data.reasons.map(r => `<li>${escapeHtml(r)}</li>`).join('');
    }

    let stepsHtml = '';
    if (data.nextSteps && data.nextSteps.length > 0) {
        stepsHtml = data.nextSteps.map(s => `<li>${escapeHtml(s)}</li>`).join('');
    }

    resultBox.className = 'verdict-card result-inline verdict-' + verdictClass;
    resultBox.innerHTML = `
        <span class="verdict-badge badge-${verdictClass}">${badgeLabel}</span>
        <span class="score-text">Risk Score: ${data.riskScore} / 100</span>
        <h3>Why this was flagged</h3>
        <ul>${reasonsHtml}</ul>
        <h3>What to do next</h3>
        <ul>${stepsHtml}</ul>
    `;
    resultBox.classList.add('show');
    resultBox.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}