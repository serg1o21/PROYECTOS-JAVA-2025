document.addEventListener("DOMContentLoaded", () => {
    const botonesReservar = document.querySelectorAll(".btn__reservar");

    botonesReservar.forEach((btn) => {
        btn.addEventListener("click", async (e) => {
            e.preventDefault();
            
            const contenedor = btn.closest(".row");
            const habitacion = contenedor.querySelector("h2")?.textContent || "";
            const precio = contenedor.querySelector(".fs-4 span")?.textContent || "";

            sessionStorage.setItem("habitacionSeleccionada", habitacion);
            sessionStorage.setItem("precioSeleccionado", precio);

            try {
                const res = await fetch("/usuario/check");

                if (!res.ok) {
                    window.location.href = "/login";
                    return;
                }
                const data = await res.json();
                const roles = data.roles || [];
                if (roles.includes("ROLE_CLIENTE")) {
                    window.location.href = "/cliente/dashboard";
                } else if (roles.includes("ROLE_ADMIN")) {
                    window.location.href = "/admin/dashboard";
                } else {
                    console.warn("Usuario sin rol válido. Redirigiendo a login.");
                    window.location.href = "/login";
                }
            } catch (err) {
                console.error("Error verificando sesión:", err);
                window.location.href = "/login";
            }
        });
    });
});
