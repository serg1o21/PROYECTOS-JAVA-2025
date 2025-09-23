document.getElementById("logoutBtn").addEventListener("click", async () => {
    await fetch("/usuario/logout", { method: "POST" });
    sessionStorage.clear();
    window.location.href = "/";
});