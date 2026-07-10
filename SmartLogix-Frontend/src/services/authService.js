// La ruta base de autenticación (Vite la redirigirá a http://localhost:8080/api/v1/auth)
const API_URL = '/api/auth';

export const loginService = async (credenciales) => {
  // Mapeamos 'correo' del front a 'email' para el backend
  const payload = {
    email: credenciales.correo,
    password: credenciales.password
  };

  const response = await fetch(`${API_URL}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error('Credenciales incorrectas');
  }

  return response.json(); 
};

export const registerService = async (datos) => {
  // Enviamos los campos exactos que espera nuestro nuevo DTO en el backend
  const payload = {
    username: datos.username,
    email: datos.correo, // Seguimos mapeando correo a email
    password: datos.password
  };

  const response = await fetch(`${API_URL}/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    if (response.status === 409) {
      throw new Error('El correo o usuario ya está registrado');
    }
    throw new Error('Error al crear la cuenta');
  }

  return response.json(); 
};

