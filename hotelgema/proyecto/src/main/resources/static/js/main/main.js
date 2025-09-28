document.getElementById("logoutBtn").addEventListener("click", async () => {
    await fetch("/auth/logout", { method: "POST" });
    sessionStorage.clear();
    window.location.href = "/";
});