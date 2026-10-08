# Base de datos de Páginas de Villa Serena

---

## 1. Resumen del caso

**Páginas de Villa Serena** es una cadena de librerías fundada por Elena Ruiz que cuenta actualmente con tres tiendas físicas: _Centro_, _Ribera_ (en la localidad vecina de Aldeaverde) y _Universidad_.

### El problema actual

El negocio gestiona sus operaciones de forma descentralizada mediante hojas de cálculo individuales para cada tienda y notas en papel. Este sistema genera importantes problemas en el día a día:

- **Descontrol del inventario:** Descuadres de stock entre tiendas (libros que constan en una sede pero físicamente están en otra), imposibilidad de conocer el stock real en tiempo global y desconocimiento de la fecha del último recuento físico.
- **Inconsistencias en el catálogo:** Autores múltiples mezclados en una misma celda de texto y editoriales repetidas continuamente sin normalizar.
- **Pérdida de trazabilidad en ventas:** Al variar los precios de catálogo a lo largo del tiempo, los pedidos antiguos recalculan sus importes de forma incorrecta, descuadrando el historial de facturación.

### Necesidades del nuevo sistema

La base de datos relacional debe centralizar y unificar la gestión del negocio para:

1. **Controlar el stock en tiempo real:** Conocer la cantidad exacta de copias de cada libro por tienda y su fecha de último recuento.
2. **Normalizar el catálogo:** Registrar libros (identificados por ISBN de 13 dígitos), editoriales y autores (soportando coautorías y roles como autor principal o colaborador).
3. **Gestionar personal y clientes:** Asignar cada empleado a su tienda física y registrar a clientes socios y habituales.
4. **Garantizar la integridad de las ventas:** Registrar los pedidos asociando tienda, empleado, cliente, fecha, método de pago, estado y líneas de detalle con la cantidad y el **precio real cobrado** en el momento de la venta.
5. **Responder a consultas de negocio:** Permitir conocer la facturación por tienda, los libros más vendidos, la disponibilidad entre sedes y el rendimiento de los empleados.

---
