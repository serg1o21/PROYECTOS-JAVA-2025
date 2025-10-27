// ---------- ELEMENTOS DEL DOM ----------
const tablaReservas = document.getElementById('tabla-reservas');
const formReserva = document.getElementById('formHabitacion'); // renombrable a formReserva si quieres
const modalReservaEl = document.getElementById('modalHabitacion'); 
const modalEliminarEl = document.getElementById('modalEliminar');

let idEliminar = null;
let modalReserva, modalEliminar;

// ---------- API ----------
async function apiFetch(url, options = {}) {
    try {
        const response = await fetch(url, options);
        if (!response.ok) throw new Error(`Error HTTP ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error("Error en fetch:", error);
        return { success: false, message: "Error en comunicación con el servidor" };
    }
}

async function getReservas() {
    return await apiFetch('/reserva');
}

async function getReservaById(id) {
    return await apiFetch(`/reserva/${id}`);
}

async function saveReserva(data, id = null) {
    return await apiFetch(`/reserva${id ? '/' + id : ''}`, {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
}

async function deleteReserva(id) {
    return await apiFetch(`/reserva/${id}`, { method: 'DELETE' });
}

async function actualizarEstadoReserva(id, estado) {
    try {
        const response = await fetch(`/reserva/${id}/estado`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ estado })
        });

        if (!response.ok) throw new Error(`Error HTTP ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error("Error al actualizar estado:", error);
        return { success: false, message: "Error al actualizar estado" };
    }
}

// ---------- RENDER UI ----------
function renderReservas(reservas) {
    tablaReservas.innerHTML = '';

    reservas.forEach(res => {
        const habitaciones = res.detalles?.map(det => det.habitacion.numero).join(', ') ?? '';
        const total = res.detalles?.reduce((acc, det) => acc + (det.subtotal ?? 0), 0).toFixed(2) ?? '0.00';
        const comprobanteHtml = res.urlComprobante
            ? `<a href="${res.urlComprobante}" target="_blank">Ver comprobante</a>`
            : 'Sin comprobante';

        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${res.idReserva}</td>
            <td>${res.checkIn ?? ''}</td>
            <td>${res.checkOut ?? ''}</td>
            <td>${habitaciones}</td>
            <td>${comprobanteHtml}</td>
            <td>S/. ${total}</td>
            <td>
                <select class="form-select form-select-sm estado-select" data-id="${res.idReserva}">
                    <option value="PENDIENTE" ${res.estado === 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
                    <option value="CONFIRMADA" ${res.estado === 'CONFIRMADA' ? 'selected' : ''}>Confirmada</option>
                    <option value="CANCELADA" ${res.estado === 'CANCELADA' ? 'selected' : ''}>Cancelada</option>
                </select>
            </td>
        `;
        tablaReservas.appendChild(fila);
    });
}

// ---------- MODALES ----------
function abrirModalReserva() {
    formReserva.reset();
    document.getElementById("modalHabitacionLabel").textContent = "Nueva Reserva";
    modalReserva.show();
}

function cerrarModalReserva() {
    modalReserva.hide();
}

function abrirModalEliminar(id) {
    idEliminar = id;
    modalEliminar.show();
}

function cerrarModalEliminar() {
    modalEliminar.hide();
}

// ---------- FORM HANDLERS ----------
async function handleEditarReserva(id) {
    const result = await getReservaById(id);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    const res = result.data;
    document.getElementById("habitacionId").value = res.id; 
    // Rellenar demás campos según tu modal
    document.getElementById("modalHabitacionLabel").textContent = "Editar Reserva";
    modalReserva.show();
}

async function handleConfirmarEliminar() {
    if (!idEliminar) return;
    const result = await deleteReserva(idEliminar);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalEliminar();

    Swal.fire({
        title: "Eliminado",
        text: result.message ?? "Reserva eliminada correctamente",
        icon: "success",
        timer: 1500,
        showConfirmButton: false
    });

    await listarReservas();
}

async function handleSubmitReserva(e) {
    e.preventDefault();

    const id = document.getElementById("habitacionId").value;
    const data = {
        checkIn: document.getElementById("checkIn")?.value,
        checkOut: document.getElementById("checkOut")?.value,
        habitaciones: [], // agregar lógica si seleccionas habitaciones
        comprobante: document.getElementById("comprobante")?.value,
        total: parseFloat(document.getElementById("total")?.value),
        estado: document.getElementById("estado")?.value
    };

    const result = await saveReserva(data, id || null);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalReserva();

    Swal.fire({
        title: id ? "Reserva actualizada" : "Reserva creada",
        text: result.message ?? "Operación exitosa",
        icon: "success",
        timer: 2000,
        showConfirmButton: false
    });

    await listarReservas();
}

// ---------- MAIN ----------
async function listarReservas() {
    const result = await getReservas();
    if (result.success) {
        renderReservas(result.data);

        // Delegación de eventos para selects de estado
        document.querySelectorAll('.estado-select').forEach(select => {
            select.addEventListener('change', async (e) => {
                const idReserva = e.target.dataset.id;
                const nuevoEstado = e.target.value;

                const resUpdate = await actualizarEstadoReserva(idReserva, nuevoEstado);
                if (resUpdate.success) {
                    Swal.fire({
                        title: "Actualizado",
                        text: "Estado de la reserva actualizado correctamente",
                        icon: "success",
                        timer: 1500,
                        showConfirmButton: false
                    });
                } else {
                    Swal.fire("Error", resUpdate.message, "error");
                }
            });
        });
    } else {
        console.error(result.message);
    }
}

// ---------- INIT ----------
document.addEventListener('DOMContentLoaded', async () => {
    modalReserva = new bootstrap.Modal(modalReservaEl);
    modalEliminar = new bootstrap.Modal(modalEliminarEl);

    await listarReservas();

    formReserva.addEventListener('submit', handleSubmitReserva);
    document.getElementById('btnConfirmarEliminar').addEventListener('click', handleConfirmarEliminar);
    document.querySelector('[data-bs-target="#modalHabitacion"]').addEventListener('click', abrirModalReserva);
});
