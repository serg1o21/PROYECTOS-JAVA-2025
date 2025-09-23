// ---------- ELEMENTOS DEL DOM ----------
const tablaCategorias = document.getElementById('tabla-categorias');
const formCategoria = document.getElementById('formCategoria');
const modalCategoriaEl = document.getElementById('modalCategoria');
const modalEliminarEl = document.getElementById('modalEliminar');

let idEliminar = null;
let modalCategoria, modalEliminar;

// ---------- API CALLS ----------
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

async function getCategorias() {
    return await apiFetch('/categoria');
}

async function getCategoriaById(id) {
    return await apiFetch(`/categoria/${id}`);
}

async function saveCategoria(data, id = null) {
    return await apiFetch(`/categoria${id ? '/' + id : ''}`, {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
}

async function deleteCategoria(id) {
    return await apiFetch(`/categoria/${id}`, { method: 'DELETE' });
}

// ---------- RENDER UI ----------
function renderCategorias(categorias) {
    tablaCategorias.innerHTML = '';
    categorias.forEach(cat => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${cat.id}</td>
            <td>${cat.nombre}</td>
            <td>${cat.descripcion ?? ''}</td>
            <td>S/. ${cat.precio?.toFixed(2) ?? '0.00'}</td>
            <td>${cat.aforo ?? ''}</td>
            <td>${cat.beneficios ?? ''}</td>
            <td>
                ${cat.imagen
                ? `<img src="data:image/jpeg;base64,${cat.imagen}" alt="img" style="width:60px;height:60px;object-fit:cover">`
                : '<span class="text-muted">Sin imagen</span>'}
            </td>
            <td class="text-center">
                <button class="btn btn-sm btn-warning me-1" onclick="handleEditar(${cat.id})"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-sm btn-danger" onclick="handleEliminar(${cat.id})"><i class="bi bi-trash"></i></button>
            </td>
        `;
        tablaCategorias.appendChild(fila);
    });
}

// ---------- MODAL HELPERS ----------
function abrirModalCategoria() {
    resetForm();
    document.getElementById("modalCategoriaLabel").textContent = "Nueva Categoría";
    modalCategoria.show();
}

function cerrarModalCategoria() {
    modalCategoria.hide();
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
    formCategoria.reset();
    document.getElementById("categoriaId").value = "";
}

async function handleEditar(id) {
    const result = await getCategoriaById(id);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    const categoria = result.data;
    document.getElementById("categoriaId").value = categoria.id;
    document.getElementById("nombre").value = categoria.nombre ?? "";
    document.getElementById("descripcion").value = categoria.descripcion ?? "";
    document.getElementById("precio").value = categoria.precio ?? "";
    document.getElementById("aforo").value = categoria.aforo ?? "";
    document.getElementById("beneficios").value = categoria.beneficios ?? "";
    document.getElementById("modalCategoriaLabel").textContent = "Editar Categoría";

    modalCategoria.show();
}

async function handleEliminar(id) {
    abrirModalEliminar(id);
}

async function handleConfirmarEliminar() {
    if (!idEliminar) return;
    const result = await deleteCategoria(idEliminar);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalEliminar();

    Swal.fire({
        title: "Eliminado",
        text: result.message ?? "Categoría eliminada correctamente",
        icon: "success",
        timer: 1500,
        showConfirmButton: false
    });

    await listarCategorias();
}

async function handleSubmit(e) {
    e.preventDefault();

    const id = document.getElementById("categoriaId").value;
    const data = {
        nombre: document.getElementById("nombre").value.trim(),
        descripcion: document.getElementById("descripcion").value.trim(),
        precio: parseFloat(document.getElementById("precio").value),
        aforo: document.getElementById("aforo").value.trim(),
        beneficios: document.getElementById("beneficios").value.trim()
    };

    const result = await saveCategoria(data, id || null);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalCategoria();

    Swal.fire({
        title: id ? "Categoría actualizada" : "Categoría creada",
        text: result.message ?? "Operación exitosa",
        icon: "success",
        timer: 2000,
        showConfirmButton: false
    });

    await listarCategorias();
}

// ---------- MAIN ----------
async function listarCategorias() {
    const result = await getCategorias();
    if (result.success) {
        renderCategorias(result.data);
    } else {
        console.error(result.message);
    }
}

// ---------- INIT ----------
document.addEventListener('DOMContentLoaded', async () => {
    modalCategoria = new bootstrap.Modal(modalCategoriaEl);
    modalEliminar = new bootstrap.Modal(modalEliminarEl);

    await listarCategorias();

    formCategoria.addEventListener('submit', handleSubmit);
    document.getElementById('btnConfirmarEliminar').addEventListener('click', handleConfirmarEliminar);
    document.querySelector('[data-bs-target="#modalCategoria"]').addEventListener('click', abrirModalCategoria);
});
