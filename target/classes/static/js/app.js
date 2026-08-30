document.querySelectorAll('form').forEach(form => form.addEventListener('submit', () => {
	const button = form.querySelector('button[type="submit"]');
	const code = form.querySelector('[data-otp-value]');
	if (code) code.value = [...form.querySelectorAll('[data-otp-input]')].map(input => input.value).join('');
	if (button) button.disabled = true;
}));

if (window.lucide) window.lucide.createIcons();

document.querySelectorAll('[data-password-toggle]').forEach(toggle => toggle.addEventListener('click', () => {
	const input = document.getElementById(toggle.dataset.passwordToggle);
	if (!input) return;
	const visible = input.type === 'text';
	input.type = visible ? 'password' : 'text';
	toggle.setAttribute('aria-label', visible ? 'Show password' : 'Hide password');
	toggle.innerHTML = `<i data-lucide="${visible ? 'eye' : 'eye-off'}"></i>`;
	if (window.lucide) window.lucide.createIcons();
}));

const codeInputs = [...document.querySelectorAll('[data-otp-input]')];
codeInputs.forEach((input, index) => {
	input.addEventListener('input', () => {
		input.value = input.value.replace(/\D/g, '').slice(-1);
		if (input.value && codeInputs[index + 1]) codeInputs[index + 1].focus();
	});
	input.addEventListener('keydown', event => {
		if (event.key === 'Backspace' && !input.value && codeInputs[index - 1]) codeInputs[index - 1].focus();
	});
	input.addEventListener('paste', event => {
		const pasted = (event.clipboardData || window.clipboardData).getData('text').replace(/\D/g, '').slice(0, codeInputs.length);
		if (!pasted) return;
		event.preventDefault();
		codeInputs.forEach((field, fieldIndex) => { field.value = pasted[fieldIndex] || ''; });
	});
});

const password = document.querySelector('[data-password-strength]');
if (password) password.addEventListener('input', () => {
	const value = password.value;
	const checks = { length: value.length >= 10, upper: /[A-Z]/.test(value), number: /\d/.test(value), special: /[^A-Za-z0-9]/.test(value) };
	const meter = document.querySelector('.auth-meter');
	if (meter) meter.dataset.strength = Object.values(checks).filter(Boolean).length;
	Object.entries(checks).forEach(([name, valid]) => {
		const item = document.querySelector(`[data-requirement="${name}"]`);
		if (item) item.classList.toggle('valid', valid);
	});
});
