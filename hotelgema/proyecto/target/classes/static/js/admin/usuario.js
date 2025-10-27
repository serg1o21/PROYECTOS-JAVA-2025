// ---------- VARIABLES GLOBALES ----------
const tablaUsuarios = document.getElementById('tabla-usuario');
const formUsuario = document.getElementById('formUsuario');
const modalUsuarioEl = document.getElementById('modalUsuario');
let modalUsuario;
let esEdicion = false; // 🔹 Indicador de creación vs edición

// ---------- FETCH API ----------
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

async function getUsuarios() {
    return await apiFetch('/usuario');
}

async function getUsuarioById(id) {
    return await apiFetch(`/usuario/${id}`);
}

async function saveUsuario(data, id = null) {
    return await apiFetch(`/usuario${id ? '/' + id : ''}`, {
        method: id ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
}

// ---------- RENDER UI ----------
function renderUsuarios(usuarios) {
    tablaUsuarios.innerHTML = '';
    usuarios.forEach(usu => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${usu.ndoc}</td>
            <td>${usu.tipodoc}</td>
            <td>${usu.nombre}</td>
            <td>${usu.apellido}</td>
            <td>${usu.fecnacimiento}</td>
            <td>${usu.genero}</td>
            <td>${usu.rol}</td>
            <td>${usu.correo}</td>
            <td>${usu.usuario}</td>
            <td>${usu.estado ? 'habilitado' : 'deshabilitado'}</td>
            <td class="text-center">
                <button class="btn btn-sm btn-warning me-1" onclick="handleEditar(${usu.ndoc})">
                    <i class="bi bi-pencil"></i>
                </button>
            </td>
        `;
        tablaUsuarios.appendChild(fila);
    });
}

// ---------- MODAL ----------
async function abrirmodalUsuario() {
    resetForm();
    esEdicion = false; // 🔹 Creación
    document.getElementById("modalUsuarioLabel").textContent = "Nuevo Usuario";
    modalUsuario.show();
}

function cerrarModalUsuario() {
    modalUsuario.hide();
}

function resetForm() {
    formUsuario.reset();
    document.getElementById("ndoc").value = "";
}

// ---------- EDITAR ----------
async function handleEditar(id) {
    const result = await getUsuarioById(id);
    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }
    const usuario = result.data;

    esEdicion = true; // 🔹 Edición

    document.getElementById("ndoc").value = usuario.ndoc ?? "";
    document.getElementById("tipodoc").value = usuario.tipodoc ?? "";
    document.getElementById("nombre").value = usuario.nombre ?? "";
    document.getElementById("apellido").value = usuario.apellido ?? "";
    document.getElementById("fecnacimiento").value = usuario.fecnacimiento ?? "";
    document.getElementById("genero").value = usuario.genero ?? "";
    document.getElementById("rol").value = usuario.rol ?? "";
    document.getElementById("correo").value = usuario.correo ?? "";
    document.getElementById("usuario").value = usuario.usuario ?? "";
    document.getElementById("clave").value = ""; // Nunca mostrar la clave real
    document.getElementById("estado").value = usuario.estado ? "true" : "false";

    document.getElementById("modalUsuarioLabel").textContent = "Editar Usuario";
    modalUsuario.show();
}

// ---------- SUBMIT ----------
async function handleSubmit(e) {
    e.preventDefault();

    const id = esEdicion ? document.getElementById("ndoc").value : null;

    const data = {
        ndoc: document.getElementById("ndoc").value.trim(),
        tipodoc: document.getElementById("tipodoc").value.trim(),
        nombre: document.getElementById("nombre").value.trim(),
        apellido: document.getElementById("apellido").value.trim(),
        fecnacimiento: document.getElementById("fecnacimiento").value,
        genero: document.getElementById("genero").value.trim(),
        rol: document.getElementById("rol").value.trim(),
        correo: document.getElementById("correo").value.trim(),
        usuario: document.getElementById("usuario").value.trim(),
        clave: document.getElementById("clave").value.trim() || null,
        estado: document.getElementById("estado").value === "true"
    };

    const result = await saveUsuario(data, id);

    if (!result.success) {
        Swal.fire("Error", result.message, "error");
        return;
    }

    cerrarModalUsuario();

    Swal.fire({
        title: esEdicion ? "Usuario actualizado" : "Usuario creado",
        text: result.message ?? "Operación exitosa",
        icon: "success",
        timer: 2000,
        showConfirmButton: false
    });

    await listarUsuarios();
}

// ---------- LISTAR ----------
async function listarUsuarios() {
    const result = await getUsuarios();
    if (result.success) {
        renderUsuarios(result.data);
    } else {
        console.error(result.message);
    }
}

// ---------- INIT ----------
document.addEventListener('DOMContentLoaded', async () => {
    modalUsuario = new bootstrap.Modal(modalUsuarioEl);
    await listarUsuarios();

    formUsuario.addEventListener('submit', handleSubmit);
    document.querySelector('[data-bs-target="#modalUsuario"]').addEventListener('click', abrirmodalUsuario);
});
