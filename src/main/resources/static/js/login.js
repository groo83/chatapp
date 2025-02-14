document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // 기본 폼 제출 동작 방지

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // 서버로 보낼 데이터
    const requestData = {
        email: email,
        password: password,
    };

    try {
        const response = await fetch("/api/auth/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestData),
        });


        if (!response.ok) {
            alert("로그인 실패!");
            return;
        }

        const resJson = await response.json();
        const resData = resJson.data;
        console.dir(resJson);
        // todo XSS 공격을 방지하기 위해 토큰 값 검증 (예: base64 형식 검사 등)
        //if (isValidJWT(accessToken)) {
        localStorage.setItem("accessToken", resData.accessToken);
        localStorage.setItem("refreshToken", resData.refreshToken);
        //} else {
        //    alert("토큰 형식이 잘못되었습니다.");
        //}
        const data = btoa(JSON.stringify({ nickname: resData.nickname }));

        window.location.href = "main";
    } catch (error) {
        console.error("Error:", error);
    }
});

// JWT의 형식을 검증하는 함수
function isValidJWT(token) {

}
