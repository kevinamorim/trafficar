function validateRegistration() {
	document.register.hash.value = CryptoJS.SHA3(document.register.password.value);
	document.register.password.value = document.register.hash.value;
	document.register.submit();
}