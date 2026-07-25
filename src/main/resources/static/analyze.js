function submitAnalysis(event) {
    event.preventDefault();

    const text = document.getElementById('messageInput').value.trim();
    const btn = document.getElementById('analyzeBtn');
    const resultBox = document.getElementById('resultBox');
    const loading = document.getElementById('loading');
    const errorBox = document.getElementById('errorBox');

    errorBox.style.display = 'none';
    resultBox.style.display = 'none';

    if (!text) {
        errorBox.textContent = 'Please paste a message to analyze.';
        errorBox.style.display = 'block';
        return;
    }

    btn.disabled = true;
    loading.style.display = 'block';

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
            loading.style.display = 'none';
            btn.disabled = false;

            if (!result) return;

            if (result.status !== 200) {
                errorBox.textContent = result.data.error || 'Something went wrong.';
                errorBox.style.display = 'block';
                return;
            }

            renderResult(result.data);
        })
        .catch(() => {
            loading.style.display = 'none';
            btn.disabled = false;
            errorBox.textContent = 'Network error. Please try again.';
            errorBox.style.display = 'block';
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

    let reasonsHtml = '<li style="color:#9aa7d1;">No red flags detected.</li>';
    if (data.reasons && data.reasons.length > 0) {
        reasonsHtml = data.reasons.map(r => `<li>${escapeHtml(r)}</li>`).join('');
    }

    let stepsHtml = '';
    if (data.nextSteps && data.nextSteps.length > 0) {
        stepsHtml = data.nextSteps.map(s => `<li>${escapeHtml(s)}</li>`).join('');
    }

    resultBox.className = 'verdict-card verdict-' + verdictClass;
    resultBox.innerHTML = `
        <span class="verdict-badge badge-${verdictClass}">${badgeLabel}</span>
        <span class="score-text">Risk Score: ${data.riskScore} / 100</span>
        <h3>Why this was flagged</h3>
        <ul>${reasonsHtml}</ul>
        <h3>What to do next</h3>
        <ul>${stepsHtml}</ul>
    `;
    resultBox.style.display = 'block';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}