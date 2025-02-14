const chatInput = document.getElementById('chatInput');
const sendButton = document.getElementById('sendButton');
const dropArea = document.getElementById('dropArea');
const roomIdInput = document.getElementById('roomId');

// Handle drag & drop
dropArea.addEventListener('dragover', function (event) {
    event.preventDefault();
    dropArea.classList.add('dragover');
});

dropArea.addEventListener('dragleave', function () {
    dropArea.classList.remove('dragover');
});

dropArea.addEventListener('drop', function (event) {
    event.preventDefault();
    dropArea.classList.remove('dragover');
    droppedFile = event.dataTransfer.files[0];
    if (droppedFile) {
        chatInput.value = `File: ${droppedFile.name}`;
    }
});

// Send message or file
sendButton.addEventListener('click', async function (event) {
//    var nickname = nicknameInput.value.trim();
//    if (!nickname) {
//        alert("Please enter your nickname.");
//        return;
//    }


    var message = chatInput.value.trim();
    var roomId = roomIdInput.value.trim();
    var nickname = nicknameElement.innerHTML.trim();

    if (droppedFile) {
        sendFile(droppedFile, nickname, roomId);
    } else if (message) {
        sendMessage(message, nickname, roomId);
    }

    // Clear input
    chatInput.value = '';
    droppedFile = null;
});

// Handle Enter key for message sending
chatInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        sendButton.click();
    }
});

// Allow clicking to upload files
dropArea.addEventListener('click', () => {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.style.display = 'none';
    fileInput.addEventListener('change', (e) => {
        droppedFile = e.target.files[0];
        if (droppedFile) {
            event.preventDefault();
            chatInput.value = `File: ${droppedFile.name}`;

        }
    });
    fileInput.click();
});

// todo 추후 진행
/*

document.getElementById("downloadLink").addEventListener("click", async function(event) {
    event.preventDefault(); // 기본 동작 방지

    const accessToken = localStorage.getItem("accessToken");
    const url = "/files/download/일빵빵_총복습(1강-200강).pdf";

    try {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) throw new Error("다운로드 실패");

        // Blob을 사용해 파일 다운로드 처리
        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = downloadUrl;
        a.download = "일빵빵_총복습(1강-200강).pdf";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    } catch (error) {
        console.error("다운로드 오류:", error);
    }
});*/