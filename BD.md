# Base de datos de Páginas de Villa Serena

---

## 1. Resumen del caso

**Páginas de Villa Serena** es una cadena de librerías de Elena Ruiz con 3 tiendas: Centro, Ribera (en Aldeaverde) y Universidad.

Actualmente llevan el control de stock, empleados y ventas con hojas de cálculo y notas en papel. Esto les causa varios problemas:

- No saben el stock real que hay en cada tienda ni cuándo se hizo el último recuento.
- En las hojas tienen autores mezclados en la misma celda y editoriales repetidas.
- Cuando cambia el precio de un libro en el catálogo, se alteran los totales de pedidos antiguos y las cuentas no cuadran.

La base de datos relacional debe solucionar esto:

1. Controlar el stock exacto por tienda y la fecha de recuento.
2. Organizar libros (por ISBN), editoriales y autores (distinguiendo autor principal o colaborador).
3. Gestionar empleados por tienda y clientes (socios y no socios).
4. Registrar los pedidos guardando el precio real cobrado de cada libro en ese momento.
5. Permitir consultas rápidas sobre ventas, facturación y disponibilidad.

---

## 2. Análisis del caso

### 2.1 Entidades y atributos

| Entidad      | Atributos                                                                        | Notas del caso                               |
| ------------ | -------------------------------------------------------------------------------- | -------------------------------------------- |
| Tienda       | id, nombre, direccion, ciudad, telefono                                          | 3 tiendas físicas (§1)                       |
| Editorial    | id, nombre, pais, telefono_contacto                                              | Una editorial por libro (§2)                 |
| Autor        | id, nombre, nacionalidad, anio_nacimiento                                        | Autores de los libros (§2)                   |
| Libro        | isbn, titulo, anio_publicacion, num_paginas, precio_catalogo, editorial_id       | Clave ISBN de 13 cifras (§2)                 |
| Empleado     | id, dni, nombre, apellidos, cargo, fecha_contratacion, email, tienda_id          | Trabaja en una sola tienda (§4)              |
| Cliente      | id, nombre_completo, email, telefono, es_socio, fecha_alta                       | Email único (§5)                             |
| Pedido       | id, numero_pedido, fecha, tienda_id, empleado_id, cliente_id, forma_pago, estado | Efectivo, tarjeta o bizum (§6)               |
| Libro_Autor  | libro_isbn, autor_id, rol                                                        | Intermedia con rol (principal o colaborador) |
| Inventario   | tienda_id, libro_isbn, stock, fecha_ultimo_recuento                              | Intermedia con stock y fecha recuento        |
| Linea_Pedido | id, pedido_id, libro_isbn, cantidad, precio_cobrado                              | Intermedia con cantidad y precio cobrado     |

### 2.2 Relaciones

| Relación          | Tipo | Cómo se resuelve   | Explicación                                           |
| ----------------- | ---- | ------------------ | ----------------------------------------------------- |
| Editorial – Libro | 1:N  | libro.editorial_id | Una editorial publica varios libros                   |
| Libro – Autor     | N:M  | Tabla libro_autor  | Libros con varios autores y autores con varios libros |
| Tienda – Libro    | N:M  | Tabla inventario   | Stock de libros por tienda                            |
| Tienda – Empleado | 1:N  | empleado.tienda_id | Cada empleado pertenece a una sola tienda             |
| Tienda – Pedido   | 1:N  | pedido.tienda_id   | Cada venta se hace en una tienda                      |
| Empleado – Pedido | 1:N  | pedido.empleado_id | Un empleado atiende varios pedidos                    |
| Cliente – Pedido  | 1:N  | pedido.cliente_id  | Un cliente hace varios pedidos                        |
| Pedido – Libro    | N:M  | Tabla linea_pedido | Un pedido incluye varios libros                       |

### 2.3 Datos descartados

| Dato                              | Motivo                                                        |
| --------------------------------- | ------------------------------------------------------------- |
| Total del ticket                  | Se calcula en la consulta sumando cantidad \* precio_cobrado  |
| Historial de tiendas del empleado | Elena indicó que solo necesita que figure en su tienda actual |
| Autores juntos en una celda       | Se separan en la tabla intermedia libro_autor                 |

---

## 3. Reglas de negocio

1. Cada tienda tiene un nombre único y su propio teléfono.
2. Cada libro se identifica por su código ISBN de 13 dígitos.
3. Un libro pertenece a una sola editorial.
4. Un libro puede tener varios autores, indicando si es autor principal o colaborador.
5. Cada empleado trabaja en una sola tienda. DNI y email son únicos.
6. Los cargos válidos son: librero, cajero y encargado.
7. El email del cliente es único.
8. El stock no puede ser negativo y registra la fecha del último recuento.
9. La línea de pedido guarda el precio cobrado en el momento de la venta.
10. Todo pedido tiene tienda, empleado y cliente asociados.
11. Pagos admitidos: efectivo, tarjeta o bizum. Estados: preparado, entregado o cancelado.

---

## 4. Diagrama entidad-relación

![Diagrama entidad-relación](Diagrama.png)

### Tabla de relaciones

| Relación          | Tipo | Cómo se resuelve                 |
| ----------------- | ---- | -------------------------------- |
| editorial – libro | 1:N  | Clave foránea libro.editorial_id |
| libro – autor     | N:M  | Tabla intermedia libro_autor     |
| tienda – libro    | N:M  | Tabla intermedia inventario      |
| tienda – empleado | 1:N  | Clave foránea empleado.tienda_id |
| tienda – pedido   | 1:N  | Clave foránea pedido.tienda_id   |
| empleado – pedido | 1:N  | Clave foránea pedido.empleado_id |
| cliente – pedido  | 1:N  | Clave foránea pedido.cliente_id  |
| pedido – libro    | N:M  | Tabla intermedia linea_pedido    |

---

## 5. Modelo lógico

- **tienda** (id [PK], nombre [UQ], direccion, ciudad, telefono)
- **editorial** (id [PK], nombre [UQ], pais, telefono_contacto)
- **autor** (id [PK], nombre, nacionalidad, anio_nacimiento)
- **libro** (isbn [PK], titulo, anio_publicacion, num_paginas, precio_catalogo, editorial_id [FK -> editorial.id])
- **empleado** (id [PK], dni [UQ], nombre, apellidos, cargo, fecha_contratacion, email [UQ], tienda_id [FK -> tienda.id])
- **cliente** (id [PK], nombre_completo, email [UQ], telefono, es_socio, fecha_alta)
- **libro_autor** (libro_isbn [PK, FK -> libro.isbn], autor_id [PK, FK -> autor.id], rol)
- **inventario** (tienda_id [PK, FK -> tienda.id], libro_isbn [PK, FK -> libro.isbn], stock, fecha_ultimo_recuento)
- **pedido** (id [PK], numero_pedido [UQ], fecha, tienda_id [FK -> tienda.id], empleado_id [FK -> empleado.id], cliente_id [FK -> cliente.id], forma_pago, estado)
- **linea_pedido** (id [PK], pedido_id [FK -> pedido.id], libro_isbn [FK -> libro.isbn], cantidad, precio_cobrado)

---
