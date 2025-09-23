document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formLogin");
    const alertaLogin = document.getElementById("alertaLogin");
    const alertaTextoLogin = document.getElementById("alertaTextoLogin");

    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        alertaLogin.classList.add("d-none");

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(form.action, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            const result = await response.json();

            if (response.ok) {
                mostrarAlerta("success", "Login exitoso");
                if (result.redirectUrl) {
                    window.location.href = result.redirectUrl;
                } else {
                    window.location.href = "/"; // fallback si no hay redirect
                }
            } else {
                mostrarAlerta("danger", result.error || "Usuario o clave incorrecta");
            }
        } catch (err) {
            console.error(err);
            mostrarAlerta("danger", "No se pudo conectar al servidor");
        }

        setTimeout(() => {
            alertaLogin.classList.add("d-none");
        }, 4000);
    });

    function mostrarAlerta(tipo, mensaje) {
        alertaLogin.classList.remove("alert-success", "alert-danger");
        alertaLogin.classList.add(`alert-${tipo}`);
        alertaTextoLogin.innerHTML = mensaje;
        alertaLogin.classList.remove("d-none");
    }
});
