# 🏦 EUREKABANK — Guía de Endpoints REST

> **Base URL:** `http://localhost:8080/EUREKABANK_RESTFUL_JAVA_GR01/`  
> **Content-Type:** `application/json`  
> Todos los endpoints consumen y producen `application/json`.

---

## 🔁 Flujo de prueba recomendado

```
1.  POST /auth/login               → autenticarse con el admin inicial
2.  POST /parameters               → configurar parámetros del sistema (opcional)
3.  POST /clients                  → crear cliente (crea usuario automáticamente)
4.  POST /auth/login               → autenticarse con el usuario del cliente creado
5.  POST /accounts                 → crear cuenta bancaria para el cliente
6.  POST /transactions/deposit     → depositar saldo inicial
7.  POST /transactions/withdraw    → retirar
8.  POST /transactions/transfer    → transferir entre cuentas (soporta CREDIT/DEBIT)
9.  GET  /transactions/account/{id}→ ver historial de movimientos
```

---

## 1. 🔐 Auth

### `POST /auth/login`
Autentica a un usuario existente del sistema.

**Request:**
```json
{
  "username": "MONSTER",
  "password": "MONSTER9"
}
```
> El usuario `MONSTER` / `MONSTER9` es el admin creado automáticamente al arrancar el servidor.

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "MONSTER",
  "role": "ADMIN"
}
```

**Errores posibles:**
```json
{ "message": "Username and password are required" }  // 400
{ "message": "User not found" }                       // 404
{ "message": "User is inactive" }                     // 400
{ "message": "Invalid credentials" }                  // 400
```

---

## 2. ⚙️ Parameters

Módulo de configuración del sistema. Los parámetros son **opcionales**: si no están configurados, el sistema opera sin comisiones ni límites (`"0"` como default).

### `GET /parameters` — Listar todos los parámetros
**Response `200 OK`:**
```json
[
  { "id": 1, "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión genérica de transferencia (%)" },
  { "id": 2, "key": "transfer.credit.fee.percentage", "value": "0.25", "description": "Comisión para transferencias CREDIT (%)" },
  { "id": 3, "key": "transfer.debit.fee.percentage", "value": "1.00", "description": "Comisión para transferencias DEBIT (%)" }
]
```

---

### `GET /parameters/{key}` — Buscar por clave
```
GET /parameters/transfer.fee.percentage
```
**Response `200 OK`:**
```json
{ "id": 1, "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión genérica de transferencia (%)" }
```

---

### `POST /parameters` — Crear parámetro
**Request:**
```json
{
  "key": "transfer.fee.percentage",
  "value": "0.50",
  "description": "Comisión genérica de transferencia (%)"
}
```
**Response `201 Created`:**
```json
{ "id": 1, "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión genérica de transferencia (%)" }
```

---

### `PUT /parameters/{id}` — Actualizar valor
Solo se puede modificar `value` y `description`. La `key` es inmutable.

```
PUT /parameters/1
```
**Request:**
```json
{
  "value": "1.00",
  "description": "Nueva comisión genérica"
}
```
**Response `200 OK`:** → objeto actualizado

---

### Parámetros del sistema disponibles (incluyendo fallbacks)

El sistema utiliza un mecanismo de "fallback" (respaldo) para las transferencias. Si un parámetro específico de tipo (ej. `transfer.credit.fee.percentage`) no existe, buscará el genérico (`transfer.fee.percentage`). Si este tampoco existe, asumirá `0`.

| Key específica (Alta Prioridad) | Fallback (Baja Prioridad) | Efecto |
|---------------------------------|---------------------------|--------|
| `transfer.credit.fee.percentage`| `transfer.fee.percentage` | % de comisión cobrada al origen. |
| `transfer.debit.fee.percentage` | `transfer.fee.percentage` | % de comisión cobrada al origen. |
| `transfer.credit.daily.limit`   | `transfer.daily.limit`    | Monto máximo transferible por día. |
| `transfer.debit.daily.limit`    | `transfer.daily.limit`    | Monto máximo transferible por día. |
| (no aplica)                     | `withdraw.daily.limit`    | Monto máximo retirable por día. |
| (no aplica)                     | `account.min.balance`     | Saldo mínimo que debe quedar. |

---

## 3. 👤 Clients

> [!IMPORTANT]  
> Al crear un cliente se crea automáticamente su usuario de acceso al sistema. `username` y `password` son **obligatorios** en la creación.

### `POST /clients` — Crear cliente (+ usuario automático)
**Request:**
```json
{
  "name": "María Gómez",
  "dni": "0923456789",
  "email": "maria@gmail.com",
  "phone": "0981111111",
  "username": "mgomez",
  "password": "Segura123"
}
```
> `password` mínimo 6 caracteres. `username` debe ser único en el sistema.

**Response `201 Created`:**
```json
{
  "id": 1,
  "name": "María Gómez",
  "dni": "0923456789",
  "email": "maria@gmail.com",
  "phone": "0981111111",
  "status": "ACTIVE",
  "userId": 2,
  "username": "mgomez"
}
```

**Errores posibles:**
```json
{ "message": "Name is required" }                      // 400
{ "message": "Username is required" }                  // 400
{ "message": "Password must be at least 6 characters"} // 400
{ "message": "DNI already exists" }                    // 409
{ "message": "Username already exists" }               // 409
```

---

### `GET /clients` — Listar todos los clientes
**Response `200 OK`:** → array de objetos cliente.

### `GET /clients/{id}` — Obtener cliente por ID
**Response `200 OK`:** → objeto del cliente.

### `GET /clients/dni/{dni}` — Buscar por cédula
**Response `200 OK`:** → objeto del cliente.

### `PUT /clients/{id}` — Actualizar datos del cliente
> Solo se actualizan los datos del cliente. El usuario (`username`/`password`) no se modifica aquí.

**Request:**
```json
{
  "name": "María Carolina Gómez",
  "dni": "0923456789",
  "email": "mc.gomez@gmail.com",
  "phone": "0987654321"
}
```
**Response `200 OK`:** → objeto actualizado

### `DELETE /clients/{id}` — Eliminar cliente
**Response `200 OK`:** `{ "message": "Client deleted" }`

---

## 4. 🏛️ Accounts

### `POST /accounts` — Crear cuenta
**Request:**
```json
{
  "clientId": 1,
  "type": "SAVINGS"
}
```
> `type` puede ser: `SAVINGS` o `CHECKING`

**Response `201 Created`:**
```json
{
  "id": 1,
  "accountNumber": "483920174651",
  "balance": 0.00,
  "status": "ACTIVE",
  "type": "SAVINGS",
  "clientId": 1
}
```

### `GET /accounts` — Listar todas las cuentas
### `GET /accounts/{id}` — Obtener cuenta por ID
### `GET /accounts/client/{clientId}` — Cuentas de un cliente

### `GET /accounts/{id}/balance` — Consultar saldo
**Response `200 OK`:** `{ "balance": 1250.75 }`

### `PUT /accounts/{id}/status` — Cambiar estado
**Request:** `{ "status": "INACTIVE" }`
> `status` puede ser: `ACTIVE` o `INACTIVE`

---

## 5. 💸 Transactions

> [!IMPORTANT]  
> La cuenta debe estar en estado **`ACTIVE`**. El comportamiento de cada operación está regulado por los **parámetros del sistema** configurados en `/parameters`.

### `POST /transactions/deposit` — Depósito
**Request:**
```json
{
  "accountId": 1,
  "amount": 1000.00,
  "description": "Depósito inicial en efectivo"
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "type": "DEPOSIT",
  "transferType": null,
  "amount": 1000.00,
  "fee": 0.00,
  "date": "2026-05-15T12:00:00",
  "sourceAccountId": 1,
  "targetAccountId": null,
  "description": "Depósito inicial en efectivo"
}
```

---

### `POST /transactions/withdraw` — Retiro
**Request:**
```json
{
  "accountId": 1,
  "amount": 200.00,
  "description": "Retiro cajero automático"
}
```

**Response `201 Created`:**
```json
{
  "id": 2,
  "type": "WITHDRAW",
  "transferType": null,
  "amount": 200.00,
  "fee": 0.00,
  "date": "2026-05-15T12:05:00",
  "sourceAccountId": 1,
  "targetAccountId": null,
  "description": "Retiro cajero automático"
}
```

---

### `POST /transactions/transfer` — Transferencia
> La comisión se descuenta de la cuenta origen. El destino recibe exactamente el `amount` indicado.
> `transferType` es **obligatorio**. `CREDIT` (pagador empuja fondos) o `DEBIT` (beneficiario jala fondos).

**Request (Ejemplo CREDIT):**
```json
{
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "amount": 500.00,
  "transferType": "CREDIT",
  "description": "Pago de arriendo"
}
```

**Response `201 Created`:**
```json
{
  "id": 3,
  "type": "TRANSFER",
  "transferType": "CREDIT",
  "amount": 500.00,
  "fee": 2.50,
  "date": "2026-05-15T12:10:00",
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "description": "Pago de arriendo"
}
```
> `fee: 2.50` significa que se dedujo `502.50` de la cuenta origen (500 + 0.5% de comisión de `transfer.credit.fee.percentage`).

**Errores posibles:**
```json
{ "message": "Source and target account are required" }
{ "message": "Source and target accounts must be different" }
{ "message": "Transfer type is required (CREDIT or DEBIT)" }
{ "message": "Insufficient balance. Transfer requires 502.50 (amount: 500.00 + fee: 2.50)" }
{ "message": "Daily credit transfer limit exceeded. Limit: 5000.00, already transferred today: 4800.00" }
```

---

### `GET /transactions/account/{accountId}` — Historial de cuenta
Retorna todas las transacciones donde la cuenta aparece como origen **o** destino, ordenadas por fecha descendente.

```
GET /transactions/account/1
```

**Response `200 OK`:**
```json
[
  {
    "id": 3,
    "type": "TRANSFER",
    "transferType": "CREDIT",
    "amount": 500.00,
    "fee": 2.50,
    "date": "2026-05-15T12:10:00",
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "description": "Pago de arriendo"
  },
  {
    "id": 1,
    "type": "DEPOSIT",
    "transferType": null,
    "amount": 1000.00,
    "fee": 0.00,
    "date": "2026-05-15T12:00:00",
    "sourceAccountId": 1,
    "targetAccountId": null,
    "description": "Depósito inicial en efectivo"
  }
]
```

---

## 🧪 Escenario completo de prueba paso a paso

```
# 1. Login como admin
POST /auth/login
{ "username": "MONSTER", "password": "MONSTER9" }

# 2. Configurar parámetros del sistema (Fallback para genérico vs específico)
POST /parameters  →  { "key": "transfer.fee.percentage", "value": "0.10", "description": "Fallback comisión" }
POST /parameters  →  { "key": "transfer.debit.fee.percentage", "value": "1.00", "description": "Comisión DEBIT 1%" }

# 3. Crear cliente María (crea automáticamente usuario 'mgomez')
POST /clients
{ "name": "María Gómez", "dni": "0923456789", "email": "maria@gmail.com",
  "phone": "0981111111", "username": "mgomez", "password": "Segura123" }
→ clientId: 1, userId: 2

# 4. Crear cliente Carlos (crea automáticamente usuario 'clopez')
POST /clients
{ "name": "Carlos López", "dni": "1734567890", "email": "carlos@gmail.com",
  "phone": "0982222222", "username": "clopez", "password": "Segura456" }
→ clientId: 2, userId: 3

# 5. Crear cuentas
POST /accounts  →  { "clientId": 1, "type": "SAVINGS" }   → accountId: 1
POST /accounts  →  { "clientId": 2, "type": "CHECKING" }  → accountId: 2

# 6. Depositar saldo inicial en cuenta de María
POST /transactions/deposit
{ "accountId": 1, "amount": 1000.00, "description": "Sueldo mensual" }

# 7. Transferencia CREDIT (Usa fallback transfer.fee.percentage -> 0.1% -> Fee = 0.50)
POST /transactions/transfer
{ "sourceAccountId": 1, "targetAccountId": 2, "amount": 500.00, "transferType": "CREDIT" }
# Saldo María = 1000.00 - 500.50 = 499.50

# 8. Transferencia DEBIT (Usa específico transfer.debit.fee.percentage -> 1% -> Fee = 1.00)
POST /transactions/transfer
{ "sourceAccountId": 1, "targetAccountId": 2, "amount": 100.00, "transferType": "DEBIT" }
# Saldo María = 499.50 - 101.00 = 398.50

# 9. Verificar saldos finales
GET /accounts/1/balance  →  { "balance": 398.50 }
GET /accounts/2/balance  →  { "balance": 600.00 }
```

---

## ⚠️ Códigos HTTP de respuesta

| Código | Significado |
|--------|-------------|
| `200 OK` | Consulta o actualización exitosa |
| `201 Created` | Recurso creado correctamente |
| `400 Bad Request` | Datos inválidos, saldo insuficiente, cuenta/usuario inactivo, límite excedido |
| `404 Not Found` | Cuenta, cliente, usuario o parámetro no encontrado |
| `409 Conflict` | DNI, username o clave de parámetro ya existente |
| `500 Internal Server Error` | Parámetro requerido mal configurado (valor no numérico) |
