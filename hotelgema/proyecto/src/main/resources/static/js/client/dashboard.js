document.addEventListener("DOMContentLoaded", () => {
    const detalleTipo = document.querySelector("#detalleTipo");
    const importeTotal = document.querySelector("#importeTotal");
    const habitacionesSeleccionadas = new Map();
    const hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "habitacionesSeleccionadas";
    document.getElementById("formRegistro").appendChild(hiddenInput);

    let ultimaFechaCheckin = "";
    let ultimaFechaCheckout = "";


    document.getElementById("form-disponibles").addEventListener("submit", async function (e) {
        e.preventDefault();

        const checkinActual = document.querySelector("input[name='checkin']").value;
        const checkoutActual = document.querySelector("input[name='checkout']").value;

        if ((checkinActual !== ultimaFechaCheckin || checkoutActual !== ultimaFechaCheckout)
            && habitacionesSeleccionadas.size > 0) {

            const confirmReset = confirm("Has cambiado el rango de fechas. Esto borrará tu selección actual. ¿Deseas continuar?");
            if (!confirmReset) return;

            habitacionesSeleccionadas.clear();
            actualizarDetalle();
        }

        ultimaFechaCheckin = checkinActual;
        ultimaFechaCheckout = checkoutActual;

        const form = new FormData(this);
        const params = new URLSearchParams(form);

        try {
            const response = await fetch(`/habitacion/disponibles?${params.toString()}`, {
                method: "GET",
                credentials: "include",
                headers: {
                    "X-Requested-With": "fetch",
                    "Accept": "application/json"
                }
            });

            if (!response.ok) throw new Error("Error " + response.status);

            const data = await response.json();
            const resultado = document.getElementById("resultado");
            resultado.innerHTML = "";

            if (data.length > 0) {
                const table = document.createElement("table");
                table.className = "table table-striped mt-3";

                const thead = document.createElement("thead");
                thead.innerHTML = `
                    <tr>
                        <th></th>
                        <th>ID</th>
                        <th>Número</th>
                        <th>Precio x noche</th>
                        <th>Tipo</th>
                    </tr>
                `;
                table.appendChild(thead);

                const tbody = document.createElement("tbody");
                data.forEach(h => {
                    const tr = document.createElement("tr");
                    const isChecked = habitacionesSeleccionadas.has(String(h.idHabitacion)) ? "checked" : "";

                    tr.innerHTML = `
                        <td><input type="checkbox" class="habitacion-check" 
                            data-id="${h.idHabitacion}" 
                            data-precio="${h.precioNoche}" 
                            data-categoria="${h.categoria.nombre}"
                            data-numero="${h.numero}"
                            ${isChecked}></td>
                        <td>${h.idHabitacion}</td>
                        <td>${h.numero}</td>
                        <td>${h.precioNoche}</td>
                        <td>${h.categoria.nombre}</td>
                    `;
                    tbody.appendChild(tr);
                });
                table.appendChild(tbody);
                resultado.appendChild(table);

                document.querySelectorAll(".habitacion-check").forEach(chk => {
                    chk.addEventListener("change", function () {
                        const id = String(this.dataset.id);
                        const precioRaw = this.dataset.precio;
                        const categoria = this.dataset.categoria;
                        const numero = this.dataset.numero;

                        if (this.checked) {
                            habitacionesSeleccionadas.set(id, {
                                precio: parseNumberLocale(precioRaw),
                                categoria,
                                numero
                            });
                        } else {
                            habitacionesSeleccionadas.delete(id);
                        }
                        actualizarDetalle();
                    });
                });

                actualizarDetalle();

            } else {
                resultado.textContent = "No hay habitaciones disponibles para el rango de fechas seleccionado.";
            }
        } catch (error) {
            console.error("Error al cargar disponibilidad:", error);
            alert("No se pudo consultar disponibilidad.");
        }
    });



    document.getElementById("formRegistro").addEventListener("submit", async function (e) {
        e.preventDefault();

        if (habitacionesSeleccionadas.size === 0) {
            alert("Debe seleccionar al menos una habitación para continuar.");
            return;
        }

        // Obtener fechas del formulario
        const checkIn = document.querySelector("input[name='checkin']").value;
        const checkOut = document.querySelector("input[name='checkout']").value;
        const noches = calcularNoches();

        // Construimos el objeto en el formato que espera el back
        const reservaPayload = {
            checkIn: checkIn,
            checkOut: checkOut,
            estado: "PENDIENTE", // o el estado que corresponda
            detalles: Array.from(habitacionesSeleccionadas.entries()).map(([id, datos]) => {
                const precioPorNoche = parseNumberLocale(datos.precio);
                const subtotal = precioPorNoche * noches;
                return {
                    habitacion: { idHabitacion: Number(id) },
                    precioNoche: precioPorNoche,
                    subtotal: subtotal
                };
            })
        };

        try {
            const response = await fetch("/reserva", {
                method: "POST",
                body: JSON.stringify(reservaPayload),
                credentials: "include",
                headers: {
                    "X-Requested-With": "fetch",
                    "Content-Type": "application/json"
                }
            });

            if (!response.ok) throw new Error(`Error ${response.status}`);
            const result = await response.json();

            if (result.success) {
                alert("✅ Reserva realizada con éxito");

            } else {
                alert("❌ No se pudo registrar la reserva: " + (result.message || "Error desconocido"));
            }
        } catch (error) {
            console.error("Error al guardar reserva:", error);
            alert("Ocurrió un error al registrar la reserva.");
        }
    });


    document.getElementById("tabMisReservas").addEventListener("click", () => {
        listarReservas();
    });

    function parseNumberLocale(value) {
        if (typeof value === "number") return value;
        if (!value) return 0;
        const s = String(value).trim();
        if (s.indexOf('.') > -1 && s.indexOf(',') > -1) {
            return Number(s.replace(/\./g, '').replace(',', '.'));
        }
        if (s.indexOf(',') > -1 && s.indexOf('.') === -1) {
            return Number(s.replace(',', '.'));
        }
        return Number(s);
    }

    function calcularNoches() {
        const checkinVal = document.querySelector("input[name='checkin']").value;
        const checkoutVal = document.querySelector("input[name='checkout']").value;
        if (!checkinVal || !checkoutVal) return 1;

        const [y1, m1, d1] = checkinVal.split('-').map(Number);
        const [y2, m2, d2] = checkoutVal.split('-').map(Number);
        const start = Date.UTC(y1, m1 - 1, d1);
        const end = Date.UTC(y2, m2 - 1, d2);
        const diffDays = Math.round((end - start) / (1000 * 60 * 60 * 24));
        return diffDays > 0 ? diffDays : 1;
    }
    function actualizarDetalle() {
        detalleTipo.innerHTML = "";

        if (habitacionesSeleccionadas.size === 0) {
            detalleTipo.innerHTML = "<li>-</li>";
            importeTotal.textContent = "S/. 0";
            hiddenInput.value = "";
            return;
        }

        const noches = calcularNoches();
        let total = 0;

        habitacionesSeleccionadas.forEach((datos, id) => {
            const precioPorNoche = parseNumberLocale(datos.precio);
            const subtotal = precioPorNoche * noches;
            total += subtotal;

            const li = document.createElement("li");
            li.classList.add("d-flex", "justify-content-between", "align-items-center", "mb-1");

            li.innerHTML = `
                <div>
                    <strong>${datos.categoria}</strong> - Habitación #${datos.numero}
                    <div class="small">S/. ${precioPorNoche.toFixed(2)} por noche — Subtotal: S/. ${subtotal.toFixed(2)}</div>
                </div>
                <button type="button" class="btn btn-sm btn-outline-danger btn-quitar" data-id="${id}">
                    Quitar
                </button>
            `;

            detalleTipo.appendChild(li);
        });

        importeTotal.textContent = `S/. ${total.toFixed(2)}`;
        hiddenInput.value = Array.from(habitacionesSeleccionadas.keys()).join(",");

        document.querySelectorAll(".btn-quitar").forEach(btn => {
            btn.addEventListener("click", function () {
                const id = String(this.dataset.id);
                habitacionesSeleccionadas.delete(id);
                const checkbox = document.querySelector(`.habitacion-check[data-id="${id}"]`);
                if (checkbox) checkbox.checked = false;
                actualizarDetalle();
            });
        });
    }

async function listarReservas() {
    try {
        const response = await fetch("/reserva", {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error(`Error ${response.status}`);

        const reservas = await response.json();
        const tbody = document.getElementById("tabla-reservas");
        tbody.innerHTML = "";

        reservas.data.forEach(reserva => {
            const total = reserva.detalles.reduce((acc, det) => acc + det.subtotal, 0);

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${reserva.idReserva}</td>
                <td>${reserva.checkIn}</td>
                <td>${reserva.checkOut}</td>
                <td>${reserva.detalles.map(det => `Hab. ${det.habitacion.numero}`).join(", ")}</td>
                <td>${reserva.estado}</td>
                <td>S/. ${total.toFixed(2)}</td>
            `;

            tbody.appendChild(tr);
        });

    } catch (error) {
        console.error("Error al cargar reservas:", error);
    }
}

});
