# Carpeta de Fotos

Coloca aquí las imágenes que quieras usar en la aplicación.

## Cómo usar una foto en el Hero

1. Copia tu imagen a esta carpeta, por ejemplo: `mascota-hero.jpg`
2. Abre `frontend/src/LandingPage.jsx`
3. Busca la sección del hero (`{/* HERO */}`) y localiza este comentario:
   ```jsx
   {/* <img src="/fotos/mascota-hero.jpg" alt="Mascota" className="animal-img" /> */}
   ```
4. Quita los comentarios (`{/* */}`) de esa línea y ajusta el nombre del archivo si es diferente.
5. Puedes dejar o eliminar la línea del `<div className="animal-placeholder">` debajo.

## Cómo usar fotos en otras partes de la app

Para usar cualquier imagen de esta carpeta en cualquier componente JSX:

```jsx
<img src="/fotos/nombre-de-tu-archivo.jpg" alt="Descripción" />
```

La ruta `/fotos/...` funciona porque esta carpeta está dentro de `public/`, lo que significa que Vite la sirve directamente sin necesitar imports.

## Formatos recomendados

- `.jpg` / `.jpeg` — fotos e imágenes de mascotas
- `.png` — imágenes con fondo transparente
- `.webp` — mejor compresión, recomendado para performance

## Tamaños sugeridos

| Uso            | Tamaño recomendado |
|----------------|--------------------|
| Hero (tarjeta) | 480 × 480 px       |
| Cards          | 400 × 300 px       |
| Avatares       | 200 × 200 px       |
