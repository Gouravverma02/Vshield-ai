function loadHistory() {
    fetch('/api/history')
        .then(res => {
            if (res.status === 401) {
                window.location.href = 'login.html';
                return null;
            }
            return res.json();
        })
        .then(records => {
            if (!records) return;
            renderList(records);
        })
        .catch(() => {
            document.getElementById('historyList').innerHTML =
                '<div class="empty-state">Could not load history. Please refresh.</div>';
        });
}

function renderList(records) {
    const listEl = document.getElementById('historyList');

    if (records.length === 0) {
        listEl.innerHTML = `
            <div class="empty-state">
                <p>No checks yet.</p>
                <p style="margin-top:6px;">Analyze your first message to get started.</p>
                <a href="analyze.html" class="analyze-btn" style="display:inline-block; text-decoration:none; margin-top:16px; width:auto; padding:12px 28px;">Analyze a Message</a>
            </div>
        `;
        return;
    }

    listEl.innerHTML = records.map(r => {
        const verdictClass = r.verdict.toLowerCase();
        const badgeEmoji = { SAFE: '🟢', SUSPICIOUS: '🟡', DANGEROUS: '🔴' }[r.verdict] || '';
        const date = new Date(r.createdAt).toLocaleString();

        return `
            <div class="history-item" onclick="loadDetail(${r.id})">
                <div class="history-item-top">
                    <span class="verdict-badge badge-${verdictClass}">${badgeEmoji} ${r.verdict}</span>
                    <span class="history-date">${date}</span>
                </div>
                <div class="history-preview">${escapeHtml(r.textPreview)}</div>
            </div>
        `;
    }).join('');
}

function loadDetail(id) {
    fetch('/api/history/' + id)
        .then(res => {
            if (res.status === 401) {
                window.location.href = 'login.html';
                return null;
            }
            return res.json();
        })
        .then(data => {
            if (!data) return;
            renderDetail(data);
        });
}

function renderDetail(data) {
    const overlay = document.getElementById('detailOverlay');
    const box = document.getElementById('detailBox');
    const verdictClass = data.verdict.toLowerCase();
    const badgeLabel = { SAFE: '🟢 SAFE', SUSPICIOUS: '🟡 SUSPICIOUS', DANGEROUS: '🔴 DANGEROUS' }[data.verdict] || data.verdict;
    const date = new Date(data.createdAt).toLocaleString();

    let reasonsHtml = '<li style="color:#9aa7d1;">No red flags detected.</li>';
    if (data.reasons && data.reasons.length > 0) {
        reasonsHtml = data.reasons.map(r => `<li>${escapeHtml(r)}</li>`).join('');
    }

    let stepsHtml = '';
    if (data.nextSteps && data.nextSteps.length > 0) {
        stepsHtml = data.nextSteps.map(s => `<li>${escapeHtml(s)}</li>`).join('');
    }

    box.className = 'verdict-card verdict-' + verdictClass;
    box.innerHTML = `
        <button class="close-btn" onclick="closeDetail()">&times;</button>
        <span class="verdict-badge badge-${verdictClass}">${badgeLabel}</span>
        <span class="score-text">Risk Score: ${data.riskScore} / 100</span>
        <h3 style="clear:both;">Original message</h3>
        <p style="color:#d4d9ec; font-size:13px; background:rgba(0,0,0,0.2); padding:10px; border-radius:6px;">${escapeHtml(data.originalText)}</p>
        <h3>Why this was flagged</h3>
        <ul>${reasonsHtml}</ul>
        <h3>What to do next</h3>
        <ul>${stepsHtml}</ul>
        <p style="margin-top:16px; color:#6b7280; font-size:12px;">Checked on ${date}</p>
    `;
    overlay.style.display = 'flex';
}

function closeDetail() {
    document.getElementById('detailOverlay').style.display = 'none';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}