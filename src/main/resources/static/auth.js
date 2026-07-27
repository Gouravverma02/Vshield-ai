function showError(message) {
    const el = document.getElementById('errorBox');
    el.textContent = message;
    el.classList.add('show');
}

function hideError() {
    const el = document.getElementById('errorBox');
    el.classList.remove('show');
    el.textContent = '';
}

function setLoading(isLoading, btn, loadingText, defaultText) {
    const loadingBox = document.getElementById('loadingBox');
    btn.disabled = isLoading;
    if (loadingBox) loadingBox.classList.toggle('show', isLoading);
    btn.textContent = isLoading ? loadingText : defaultText;
}

function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function handleSignup(event) {
    event.preventDefault();
    hideError();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const btn = document.getElementById('submitBtn');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');

    if (emailError) emailError.classList.remove('show');
    if (passwordError) passwordError.classList.remove('show');
    document.getElementById('email').classList.remove('invalid');
    document.getElementById('password').classList.remove('invalid');

    let hasError = false;
    if (!validateEmail(email)) {
        if (emailError) { emailError.textContent = 'Please enter a valid email address.'; emailError.classList.add('show'); }
        document.getElementById('email').classList.add('invalid');
        hasError = true;
    }
    if (password.length < 8) {
        if (passwordError) { passwordError.textContent = 'Password must be at least 8 characters.'; passwordError.classList.add('show'); }
        document.getElementById('password').classList.add('invalid');
        hasError = true;
    }
    if (hasError) return;

    setLoading(true, btn, 'Creating account...', 'Sign Up');

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
                setLoading(false, btn, 'Creating account...', 'Sign Up');
                showError(data.error || 'Signup failed. Please try again.');
            }
        })
        .catch(() => {
            setLoading(false, btn, 'Creating account...', 'Sign Up');
            showError('Network error. Please check your connection and try again.');
        });
}

function handleLogin(event) {
    event.preventDefault();
    hideError();

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const btn = document.getElementById('submitBtn');

    if (!email || !password) {
        showError('Please enter both email and password.');
        return;
    }

    setLoading(true, btn, 'Logging in...', 'Log In');

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
                setLoading(false, btn, 'Logging in...', 'Log In');
                showError(data.error || 'Login failed. Please try again.');
            }
        })
        .catch(() => {
            setLoading(false, btn, 'Logging in...', 'Log In');
            showError('Network error. Please check your connection and try again.');
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
        })
        .catch(() => {
            window.location.href = 'login.html';
            return null;
        });
}

function logout() {
    fetch('/api/auth/logout', { method: 'POST' })
        .then(() => window.location.href = 'login.html')
        .catch(() => window.location.href = 'login.html');
}