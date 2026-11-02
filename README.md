# Informe de Proyecto - Parcial 1: Aplicación "ArgenBooking"

**Estudiante:** Gurianov Aleksandr  
**Curso:** ACN4AV - Aplicaciones Móviles  
**Profesor:** Sergio Daniel Medina  
**Repositorio GitHub:** https://github.com/aleksandrgurianov-lab/parcial-1-am-acn4a-gurianov

---

## 1. Descripción de la Pantalla Seleccionada
Para la entrega de este parcial, se ha desarrollado y perfeccionado la **Pantalla de Detalle de Alojamiento (Property Detail Screen)** dentro del ecosistema de la aplicación de reservas "ArgenBooking".

La aplicación está destinada a la gestión y reserva de lofts premium en Buenos Aires. La pantalla expone el inmueble **"Palermo Luxury Loft"**, permitiendo al usuario interactuar con un cotizador en tiempo de ejecución para calcular el precio total de su estadía de forma dinámica.

---

## 2. Actualizaciones y Correcciones Aplicadas (Revisión de Entrega)
En respuesta a las observaciones de la revisión anterior, se realizaron las siguientes mejoras técnicas y de estructuración:

*   **Configuración y Compilación Limpia:** Se reestructuró el proyecto desde cero, incluyendo la envoltura oficial de Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`) y un archivo `.gitignore` optimizado para garantizar la clonación y compilación inmediata en cualquier entorno informático.
*   **Registro de Componentes:** Se corrigió la declaración en `AndroidManifest.xml`, asegurando la correcta navegación entre pantallas (`MainActivity` y `DetailActivity`) sin cierres inesperados.
*   **Limpieza de Layouts XML:** Se eliminaron identificadores duplicados, se corrigieron las declaraciones de namespaces y se completaron todas las restricciones de alineación (`constraints`) en `ConstraintLayout`.
*   **Centralización de Recursos (`dimens.xml`):** Se extrajeron todos los márgenes, paddings (en `dp`) y tamaños de texto (en `sp`) al archivo `dimens.xml`, eliminando valores fijos ("hardcodeados") en los archivos de interfaz visual.

---

## 3. Flujo de Uso de la Pantalla (User Flow)
1.  **Navegación e Identidad:** Acceso a la pantalla principal y navegación hacia la pantalla de detalle a través de componentes interactivos y migas de pan ("INICIO / PALERMO LUXURY LOFT").
2.  **Exploración de Contenido Realista:** Visualización de la galería del inmueble, lista de servicios (Wi-Fi, Aire Acondicionado, Capacidad) y descripción detallada del alojamiento.
3.  **Cotización en Tiempo Real:** Selección del rango de fechas mediante el componente nativo `MaterialDatePicker`, actualizando automáticamente el número de noches y el monto total a abonar.
4.  **Confirmación y Generación Dinámica:** Al presionar "Reservar", se procesa la solicitud mediante lógica Java en tiempo de ejecución.

---

## 4. Justificación Técnica de Componentes Utilizados (Android XML)
*   **ConstraintLayout (Raíz):** Contenedor principal que garantiza un diseño responsivo y adaptable a diferentes resoluciones de pantalla.
*   **ScrollView & LinearLayout:** Organización estructural que permite el desplazamiento vertical fluido sin desbordamientos de contenido.
*   **TextView & EditText:** Representación de títulos, valores calculados y captura contextual de información.
*   **Button:** Elemento interactivo principal para la confirmación de reservas.

---

## 5. Comportamiento Dinámico y Eventos (Código Java)
*   **Navegación entre Actividades:** Control de flujo mediante `Intent` y cierre seguro de actividades con `finish()`.
*   **Inclusión Dinámica de Elementos UI:** Generación e inserción en tiempo de ejecución de un componente `TextView` con el resumen y estado de la reserva dentro del contenedor `dynamicInvoiceContainer`.
*   **Lógica de Cotizador (`MaterialDatePicker`):** Cálculo preciso de diferencia de días entre fechas seleccionadas y actualización dinámica de los costos.

---

## 6. Organización de Recursos y Buenas Prácticas
*   `strings.xml`: Centralización total de textos legibles en español.
*   `colors.xml`: Paleta corporativa (`bg_dark`, `orange_premium`).
*   `dimens.xml`: Estandarización de dimensiones y tipografías bajo lineamientos de Material Design.
*   **Historial de Git:** Repositorio estructurado y mantenido mediante la convención de **Conventional Commits** (`feat:`, `chore:`, `fix:`, `docs:`).