function showError(elementId, message) {
    const el = document.getElementById(elementId);
    el.textContent = message;
    el.style.display = 'block';
}

function hideError(elementId) {
    document.getElementById(elementId).style.display = 'none';
}

function handleSignup(event) {
    event.preventDefault();
    hideError('errorBox');

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.textContent = 'Creating account...';

    fetch('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })
        .then(res => res.json().then(data => ({ status: res.status, data })))
        .then(({ status, data }) => {
            if (status === 201) {
                window.location.href = 'dashboard.html';
            } else {
                showError('errorBox', data.error || 'Signup failed');
                btn.disabled = false;
                btn.textContent = 'Sign Up';
            }
        })
        .catch(() => {
            showError('errorBox', 'Something went wrong. Please try again.');
            btn.disabled = false;
            btn.textContent = 'Sign Up';
        });
}

function handleLogin(event) {
    event.preventDefault();
    hideError('errorBox');

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.textContent = 'Logging in...';

    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })
        .then(res => res.json().then(data => ({ status: res.status, data })))
        .then(({ status, data }) => {
            if (status === 200) {
                window.location.href = 'dashboard.html';
            } else {
                showError('errorBox', data.error || 'Login failed');
                btn.disabled = false;
                btn.textContent = 'Log In';
            }
        })
        .catch(() => {
            showError('errorBox', 'Something went wrong. Please try again.');
            btn.disabled = false;
            btn.textContent = 'Log In';
        });
}

function requireAuth() {
    return fetch('/api/auth/me')
        .then(res => {
            if (!res.ok) {
                window.location.href = 'login.html';
                return null;
            }
            return res.json();
        });
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = 'login.html');
}