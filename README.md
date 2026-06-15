# MindCare AI - Backend API

Esta es la API Backend para el proyecto **MindCare AI**, construida con **Node.js**, **Express**, **MongoDB (Mongoose)** y la **API de Gemini (Google Generative AI)** para brindar apoyo emocional empático e interactivo.

## Características

- **Conexión a MongoDB**: Persistencia de datos mediante esquemas y modelos de Mongoose.
- **Autenticación Segura**: Registro e inicio de sesión de usuarios usando contraseñas cifradas (`bcryptjs`) y tokens `JWT`.
- **Sesiones de Chat**: Persistencia de sesiones de chat y mensajes con roles configurados (`user` y `assistant`).
- **Integración con Gemini AI**: Generación de respuestas inteligentes, adaptativas y análisis de señales de crisis emocional en tiempo real.
- **Paginación Eficiente**: Endpoint de recuperación de mensajes optimizado para retornar solo los últimos 50 mensajes de una sesión.

---

## Requisitos Previos

Asegúrate de tener instalado:
- [Node.js](https://nodejs.org/) (Versión 18 o superior recomendada)
- [MongoDB](https://www.mongodb.com/try/download/community) (Local o una instancia en la nube como MongoDB Atlas)

---

## Configuración del Proyecto

1. **Clonar el proyecto** (si no lo has hecho ya):
   ```bash
   git clone https://github.com/NessXx18/ExamenFinalApp.git
   cd ExamenFinalApp
   ```

2. **Instalar dependencias**:
   ```bash
   npm install
   ```

3. **Configurar Variables de Entorno**:
   Crea un archivo `.env` en la raíz del proyecto basándote en `.env.example`:
   ```bash
   cp .env.example .env
   ```
   Abre el archivo `.env` y define tus credenciales:
   ```env
   PORT=3000
   JWT_SECRET=tu_secreto_jwt_super_seguro
   GEMINI_API_KEY=tu_api_key_de_gemini
   MONGODB_URI=mongodb://127.0.0.1:27017/mindcare
   ```

4. **Ejecutar el servidor**:
   - Modo desarrollo (con recarga automática):
     ```bash
     npm run dev
     ```
   - Modo producción:
     ```bash
     npm start
     ```

---

## Rutas de la API

### Autenticación (`/api/auth`)
- `POST /register`: Registra un nuevo usuario. Valida el formato del correo y contraseñas de al menos 8 caracteres.
- `POST /login`: Inicia sesión y retorna un token JWT.

### Chats (`/api/chats`)
- `GET /sessions`: Retorna todas las sesiones de chat del usuario autenticado.
- `POST /sessions`: Crea una nueva sesión de chat.
- `GET /sessions/:sessionId/messages`: Obtiene los últimos 50 mensajes de una sesión específica (paginado).
- `POST /sessions/:sessionId/messages`: Envía un mensaje a la IA y guarda tanto el mensaje enviado como la respuesta de Gemini.

---

## Guía de Solución de Problemas Git

Si al intentar hacer push te encuentras con el error:
> `fatal: 'origin' does not appear to be a git repository`
> `fatal: Could not read from remote repository.`

Esto ocurre porque tu repositorio local no tiene configurado un servidor remoto llamado `origin`. Sigue estos pasos exactos para solucionarlo y subir tus cambios:

1. **Asegúrate de inicializar Git** (si es un repositorio nuevo):
   ```bash
   git init
   ```

2. **Agrega el repositorio remoto de GitHub con el nombre `origin`**:
   ```bash
   git remote add origin https://github.com/NessXx18/ExamenFinalApp.git
   ```

3. **Verifica que se haya agregado correctamente**:
   ```bash
   git remote -v
   ```
   *Deberías ver la URL de tu repositorio listada tanto para (fetch) como para (push).*

4. **Si el remoto ya existía pero tenía una URL incorrecta**, puedes corregirlo con:
   ```bash
   git remote set-url origin https://github.com/NessXx18/ExamenFinalApp.git
   ```

5. **Asegúrate de que estás en la rama `main`**:
   ```bash
   git branch -M main
   ```

6. **Haz push de tus cambios estableciendo el upstream**:
   ```bash
   git push -u origin main
   ```
