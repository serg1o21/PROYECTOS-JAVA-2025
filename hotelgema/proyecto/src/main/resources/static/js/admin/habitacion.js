// ---------- ELEMENTOS DEL DOM ----------
const tablaHabitaciones = document.getElementById('tabla-habitacion');
const formHabitacion = document.getElementById('formHabitacion');
const modalHabitacionEl = document.getElementById('modalHabitacion');
const modalEliminarEl = document.getElementById('modalEliminar');
const selectCategoria = document.getElementById('categoriaId');

let idEliminar = null;
let modalHabitacion, modalEliminar;


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

async function getHabitaciones() {
    return await apiFetch('/habitacion');
}

async function getHabitacionById(id) {
    return await apiFetch(`/habitacion/${id}`);
}

async function saveHabitacion(data, id = null) {
    return await apiFetch(`/habitacion${id ? '/' + id : ''}`, {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
}

async function deleteHabitacion(id) {
    return await apiFetch(`/habitacion/${id}`, { method: 'DELETE' });
}

async function getCategorias() {
    return await apiFetch('/categoria');
}

// ---------- RENDER UI ----------
function renderHabitaciones(habitaciones) {
    tablaHabitaciones.innerHTML = '';
    habitaciones.forEach(hab => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${hab.idHabitacion}</td>
            <td>${hab.numero}</td>
            <td>${hab.categoria?.nombre ?? 'Sin categoría'}</td>
            <td>${hab.capacidad}</td>
            <td>S/. ${parseFloat(hab.precioNoche).toFixed(2)}</td>
            <td>${hab.estado ?? ''}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-warning me-1" onclick="handleEditar(${hab.idHabitacion})"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-sm btn-danger" onclick="handleEliminar(${hab.idHabitacion})"><i class="bi bi-trash"></i></button>
            </td>
        `;
        tablaHabitaciones.appendChild(fila);
    });
}

function renderCategoriasSelect(categorias, selectedId = null) {
    selectCategoria.innerHTML = '<option value="">Seleccione categoría</option>';
    categorias.forEach(cat => {
        const option = document.createElement('option');
        option.value = cat.id;
        option.textContent = cat.nombre;
        if (selectedId && selectedId === cat.id) {
            option.selected = true;
        }
        selectCategoria.appendChild(option);
    });
}

// ---------- MODAL HELPERS ----------
async function abrirModalHabitacion() {
    resetForm();
    document.getElementById("modalHabitacionLabel").textContent = "Nueva Habitación";

    const categorias = await getCategorias();
    if (categorias.success) {
        renderCategoriasSelect(categorias.data);
    }

    modalHabitacion.show();
}

function cerrarModalHabitacion() {
    modalHabitacion.hide();
}

function abrirModalEliminar(id) {
    idEliminar = id;
    modalEliminar.show();
}

function cerrarModalEliminar() {
    modalEliminar.hide();
}

// ---------- FORM HANDLERS ----------
function resetForm() {
    formHabitacion.reset();
    document.getElementById("habitacionId").value = "";
}

async function handleEditar(id) {
    const result = await getHabitacionById(id);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    const hab = result.data;

    const categorias = await getCategorias();
    if (categorias.success) {
        renderCategoriasSelect(categorias.data, hab.categoria?.id);
    }

    document.getElementById("habitacionId").value = hab.idHabitacion;
    document.getElementById("numero").value = hab.numero ?? "";
    document.getElementById("tipo").value = hab.tipo ?? "";
    document.getElementById("capacidad").value = hab.capacidad ?? "";
    document.getElementById("precioNoche").value = hab.precioNoche ?? "";
    document.getElementById("estado").value = hab.estado ?? "";

    document.getElementById("modalHabitacionLabel").textContent = "Editar Habitación";

    modalHabitacion.show();
}

async function handleEliminar(id) {
    abrirModalEliminar(id);
}

async function handleConfirmarEliminar() {
    if (!idEliminar) return;
    const result = await deleteHabitacion(idEliminar);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalEliminar();

    Swal.fire({
        title: "Eliminado",
        text: result.message ?? "Habitación eliminada correctamente",
        icon: "success",
        timer: 1500,
        showConfirmButton: false
    });

    await listarHabitaciones();
}

async function handleSubmit(e) {
    e.preventDefault();

    const id = document.getElementById("habitacionId").value;
    const data = {
        numero: document.getElementById("numero").value.trim(),
        tipo: document.getElementById("tipo").value.trim(),
        capacidad: parseInt(document.getElementById("capacidad").value),
        precioNoche: parseFloat(document.getElementById("precioNoche").value),
        estado: document.getElementById("estado").value.trim(),
        categoria: selectCategoria.value
            ? { id: parseInt(selectCategoria.value) }
            : null
    };

    const result = await saveHabitacion(data, id || null);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalHabitacion();

    Swal.fire({
        title: id ? "Habitación actualizada" : "Habitación creada",
        text: result.message ?? "Operación exitosa",
        icon: "success",
        timer: 2000,
        showConfirmButton: false
    });

    await listarHabitaciones();
}

// ---------- MAIN ----------
async function listarHabitaciones() {
    const result = await getHabitaciones();
    if (result.success) {
        renderHabitaciones(result.data);
    } else {
        console.error(result.message);
    }
}

// ---------- INIT ----------
document.addEventListener('DOMContentLoaded', async () => {
    modalHabitacion = new bootstrap.Modal(modalHabitacionEl);
    modalEliminar = new bootstrap.Modal(modalEliminarEl);

    await listarHabitaciones();

    formHabitacion.addEventListener('submit', handleSubmit);
    document.getElementById('btnConfirmarEliminar').addEventListener('click', handleConfirmarEliminar);
    document.querySelector('[data-bs-target="#modalHabitacion"]').addEventListener('click', abrirModalHabitacion);
});
