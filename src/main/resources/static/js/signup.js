// 비밀번호 확인 유효성 검사
function validatePassword() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        document.getElementById('passwordError').textContent = "비밀번호가 일치하지 않습니다.";
        document.getElementById('submitBtn').disabled = true;
    } else {
        document.getElementById('passwordError').textContent = "";
        document.getElementById('submitBtn').disabled = false;
    }
}

document.getElementById("signupForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // 기본 폼 제출 동작 방지

    const email = document.getElementById("email").value;
    const nickname = document.getElementById("nickname").value;
    const password = document.getElementById("password").value;
    const errorMessageElement = document.getElementById("errorMessage");

    // 서버로 보낼 데이터
    const requestData = {
        email: email,
        password: password,
        nickname: nickname,
    };

    try {
        const response = await fetch("/api/auth/signup", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify(requestData),
        });

        if (response.ok) {
            // 회원가입 성공
            alert("Registration successful! Redirecting to login page...");
            window.location.href = "/login";
        } else {
            // 에러 처리
            const errorData = await response.json();
            errorMessageElement.textContent = errorData.message || "An error occurred. Please try again.";
        }
    } catch (error) {
        errorMessageElement.textContent = "Network error. Please try again later.";
        console.error("Error:", error);
    }
});
