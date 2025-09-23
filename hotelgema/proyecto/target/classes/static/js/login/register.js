document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formRegistro");
    const alerta = document.getElementById("alertaRegistro");
    const alertaTexto = document.getElementById("alertaTextoRegistro");

    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        limpiarErrores();
        alerta.classList.add("d-none");

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
                // ✅ Registro exitoso
                mostrarAlerta("success", result.message || "Usuario registrado correctamente");
                form.reset();
            } else {
                // ❌ Error de validación o error de negocio
                if (result.detalles && typeof result.detalles === "object") {
                    mostrarErroresCampo(result.detalles);
                    mostrarAlerta("danger", "Revisa los campos marcados en rojo.");
                } else if (result.detalles && typeof result.detalles === "string") {
                    // Ej: { error: "Violación de integridad", detalles: "Usuario ya existe" }
                    mostrarAlerta("danger", result.detalles);
                } else {
                    mostrarAlerta("danger", result.error || "Error en el registro");
                }
            }
        } catch (err) {
            console.error("Error en la petición:", err);
            mostrarAlerta("danger", "No se pudo conectar al servidor");
        }

        setTimeout(() => {
            alerta.classList.add("d-none");
        }, 4000);
    });

    function mostrarAlerta(tipo, mensaje) {
        alerta.classList.remove("alert-success", "alert-danger");
        alerta.classList.add(`alert-${tipo}`);
        alertaTexto.innerHTML = mensaje;
        alerta.classList.remove("d-none");
    }

    function mostrarErroresCampo(errores) {
        Object.entries(errores).forEach(([campo, mensaje]) => {
            const input = form.querySelector(`[name="${campo}"]`);
            if (input) {
                input.classList.add("is-invalid");

                let errorDiv = input.parentElement.querySelector(".invalid-feedback");
                if (!errorDiv) {
                    errorDiv = document.createElement("div");
                    errorDiv.classList.add("invalid-feedback");
                    input.parentElement.appendChild(errorDiv);
                }
                errorDiv.textContent = mensaje;
            }
        });
    }

    function limpiarErrores() {
        form.querySelectorAll(".is-invalid").forEach(input => input.classList.remove("is-invalid"));
        form.querySelectorAll(".invalid-feedback").forEach(div => div.remove());
    }
});