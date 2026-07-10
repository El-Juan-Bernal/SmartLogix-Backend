// Trae el perfil completo (nombre, apellido, telefono, imagenPerfil, direcciones)
// desde ms_usuario, usando el mismo id que devuelve el login de ms_idp.
// Se usa justo después de loginService(), porque el login solo trae los
// datos de autenticación (id, username, email), no el perfil.
export const obtenerPerfilPorAuthId = async (authId) => {
    const response = await fetch(`/api/usuarios/auth/${authId}`);

  if (response.status === 404) {
    // El usuario existe en ms_idp pero todavía no completó su perfil en ms_usuario.
    // No es un error: simplemente no hay datos de perfil aún.
    return null;
  }

  if (!response.ok) {
    throw new Error('Error al obtener el perfil del usuario');
  }

  return response.json();
};

