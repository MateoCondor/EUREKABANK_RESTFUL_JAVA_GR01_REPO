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
8.  POST /transactions/transfer    → transferir entre cuentas
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
  { "id": 1, "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión de transferencia (%)" },
  { "id": 2, "key": "transfer.daily.limit",    "value": "5000.00", "description": "Límite diario de transferencias (USD)" },
  { "id": 3, "key": "withdraw.daily.limit",    "value": "2000.00", "description": "Límite diario de retiros (USD)" },
  { "id": 4, "key": "account.min.balance",     "value": "10.00",  "description": "Saldo mínimo requerido (USD)" }
]
```

---

### `GET /parameters/{key}` — Buscar por clave
```
GET /parameters/transfer.fee.percentage
```
**Response `200 OK`:**
```json
{ "id": 1, "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión de transferencia (%)" }
```

**Response `404 Not Found`:**
```json
{ "message": "Parameter not found: transfer.fee.percentage" }
```

---

### `POST /parameters` — Crear parámetro
**Request:**
```json
{
  "key": "transfer.fee.percentage",
  "value": "0.50",
  "description": "Comisión de transferencia (%)"
}
```

**Response `201 Created`:**
```json
{ "id": 1, "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión de transferencia (%)" }
```

**Errores posibles:**
```json
{ "message": "Parameter key is required" }                          // 400
{ "message": "Parameter value is required" }                        // 400
{ "message": "Parameter already exists with key: transfer.fee.percentage" } // 409
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
  "description": "Nueva comisión de transferencia"
}
```
**Response `200 OK`:** → objeto actualizado

---

### Parámetros del sistema disponibles

| Key | Efecto | Valor por defecto |
|-----|--------|-------------------|
| `transfer.fee.percentage` | % de comisión sobre cada transferencia. Ej: `"0.50"` = 0.5% | `"0"` (sin comisión) |
| `transfer.daily.limit` | Monto máximo transferible por día por cuenta | `"0"` (ilimitado) |
| `withdraw.daily.limit` | Monto máximo retirable por día por cuenta | `"0"` (ilimitado) |
| `account.min.balance` | Saldo mínimo que debe quedar tras un retiro | `"0"` (sin mínimo) |

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
{ "message": "Email is invalid" }                      // 400
{ "message": "Username is required" }                  // 400
{ "message": "Password is required" }                  // 400
{ "message": "Password must be at least 6 characters"} // 400
{ "message": "DNI already exists" }                    // 409
{ "message": "Username already exists" }               // 409
```

---

### `GET /clients` — Listar todos los clientes
**Response `200 OK`:**
```json
[
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
]
```

---

### `GET /clients/{id}` — Obtener cliente por ID
```
GET /clients/1
```
**Response `200 OK`:** → mismo objeto del `POST`

**Response `404 Not Found`:**
```json
{ "message": "Client not found" }
```

---

### `GET /clients/dni/{dni}` — Buscar por cédula
```
GET /clients/dni/0923456789
```
**Response `200 OK`:** → mismo objeto del `POST`

---

### `PUT /clients/{id}` — Actualizar datos del cliente
> Solo se actualizan los datos del cliente. El usuario (`username`/`password`) no se modifica aquí.

```
PUT /clients/1
```
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

---

### `DELETE /clients/{id}` — Eliminar cliente
```
DELETE /clients/1
```
**Response `200 OK`:**
```json
{ "message": "Client deleted" }
```

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

---

### `GET /accounts` — Listar todas las cuentas
**Response `200 OK`:** → array de cuentas

---

### `GET /accounts/{id}` — Obtener cuenta por ID
```
GET /accounts/1
```
**Response `200 OK`:** → objeto de cuenta

**Response `404 Not Found`:**
```json
{ "message": "Account not found" }
```

---

### `GET /accounts/client/{clientId}` — Cuentas de un cliente
```
GET /accounts/client/1
```
**Response `200 OK`:** → array de cuentas del cliente

---

### `GET /accounts/{id}/balance` — Consultar saldo
```
GET /accounts/1/balance
```
**Response `200 OK`:**
```json
{ "balance": 1250.75 }
```

**Response `400 Bad Request`** (cuenta inactiva):
```json
{ "message": "Account is not active" }
```

---

### `PUT /accounts/{id}/status` — Cambiar estado
```
PUT /accounts/1/status
```
**Request:**
```json
{ "status": "INACTIVE" }
```
> `status` puede ser: `ACTIVE` o `INACTIVE`

**Response `200 OK`:** → objeto de cuenta actualizado

---

## 5. 💸 Transactions

> [!IMPORTANT]  
> La cuenta debe estar en estado **`ACTIVE`**. El comportamiento de cada operación puede estar regulado por los **parámetros del sistema** configurados en `/parameters`.

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
  "amount": 1000.00,
  "fee": 0.00,
  "date": "2026-05-15T12:00:00",
  "sourceAccountId": 1,
  "targetAccountId": null,
  "description": "Depósito inicial en efectivo"
}
```

**Errores posibles:**
```json
{ "message": "Account not found" }               // 404
{ "message": "Account is not active" }            // 400
{ "message": "Amount must be greater than zero" } // 400
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
  "amount": 200.00,
  "fee": 0.00,
  "date": "2026-05-15T12:05:00",
  "sourceAccountId": 1,
  "targetAccountId": null,
  "description": "Retiro cajero automático"
}
```

**Errores posibles:**
```json
{ "message": "Insufficient balance" }                                    // 400
{ "message": "Withdrawal would leave balance below the required minimum of 10.00" } // 400
{ "message": "Daily withdraw limit exceeded. Limit: 2000.00, already withdrawn today: 1900.00" } // 400
{ "message": "Account is not active" }                                   // 400
```

---

### `POST /transactions/transfer` — Transferencia
> La comisión se descuenta de la cuenta origen. El destino recibe exactamente el `amount` indicado.

**Request:**
```json
{
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "amount": 500.00,
  "description": "Pago de arriendo"
}
```

**Response `201 Created`:**
```json
{
  "id": 3,
  "type": "TRANSFER",
  "amount": 500.00,
  "fee": 2.50,
  "date": "2026-05-15T12:10:00",
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "description": "Pago de arriendo"
}
```
> `fee: 2.50` significa que se dedujo `502.50` de la cuenta origen (500 + 0.5% de comisión).

**Errores posibles:**
```json
{ "message": "Source and target account are required" }                              // 400
{ "message": "Source and target accounts must be different" }                        // 400
{ "message": "Insufficient balance. Transfer requires 502.50 (amount: 500.00 + fee: 2.50)" } // 400
{ "message": "Daily transfer limit exceeded. Limit: 5000.00, already transferred today: 4800.00" } // 400
{ "message": "Account not found" }                                                   // 404
{ "message": "Account is not active" }                                               // 400
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
    "amount": 500.00,
    "fee": 2.50,
    "date": "2026-05-15T12:10:00",
    "sourceAccountId": 1,
    "targetAccountId": 2,
    "description": "Pago de arriendo"
  },
  {
    "id": 2,
    "type": "WITHDRAW",
    "amount": 200.00,
    "fee": 0.00,
    "date": "2026-05-15T12:05:00",
    "sourceAccountId": 1,
    "targetAccountId": null,
    "description": "Retiro cajero automático"
  },
  {
    "id": 1,
    "type": "DEPOSIT",
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

# 2. Configurar parámetros del sistema
POST /parameters  →  { "key": "transfer.fee.percentage", "value": "0.50", "description": "Comisión transferencia %" }
POST /parameters  →  { "key": "transfer.daily.limit",    "value": "5000.00", "description": "Límite diario transferencias" }
POST /parameters  →  { "key": "withdraw.daily.limit",    "value": "2000.00", "description": "Límite diario retiros" }
POST /parameters  →  { "key": "account.min.balance",     "value": "10.00",   "description": "Saldo mínimo requerido" }

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

# 7. Verificar saldo
GET /accounts/1/balance  →  { "balance": 1000.00 }

# 8. Retirar
POST /transactions/withdraw
{ "accountId": 1, "amount": 300.00, "description": "Gastos varios" }

# 9. Transferir (con 0.5% de comisión → fee = 1.00)
POST /transactions/transfer
{ "sourceAccountId": 1, "targetAccountId": 2, "amount": 200.00, "description": "Pago cuota" }
# María pierde: 200 + 1.00 (fee) = 201.00

# 10. Verificar saldos finales
GET /accounts/1/balance  →  { "balance": 499.00 }
GET /accounts/2/balance  →  { "balance": 200.00 }

# 11. Ver historial completo de María
GET /transactions/account/1

# 12. Login como cliente (para probar acceso con usuario de cliente)
POST /auth/login
{ "username": "mgomez", "password": "Segura123" }
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

---

## 📋 Resumen de endpoints

| Módulo | Método | Path | Descripción |
|--------|--------|------|-------------|
| **Auth** | POST | `/auth/login` | Login de usuario |
| **Parameters** | GET | `/parameters` | Listar parámetros |
| | GET | `/parameters/{key}` | Buscar por clave |
| | POST | `/parameters` | Crear parámetro |
| | PUT | `/parameters/{id}` | Actualizar valor |
| **Clients** | GET | `/clients` | Listar clientes |
| | GET | `/clients/{id}` | Obtener por ID |
| | GET | `/clients/dni/{dni}` | Buscar por cédula |
| | POST | `/clients` | Crear cliente + usuario |
| | PUT | `/clients/{id}` | Actualizar cliente |
| | DELETE | `/clients/{id}` | Eliminar cliente |
| **Accounts** | GET | `/accounts` | Listar cuentas |
| | GET | `/accounts/{id}` | Obtener por ID |
| | GET | `/accounts/client/{clientId}` | Cuentas de un cliente |
| | GET | `/accounts/{id}/balance` | Consultar saldo |
| | POST | `/accounts` | Crear cuenta |
| | PUT | `/accounts/{id}/status` | Cambiar estado |
| **Transactions** | POST | `/transactions/deposit` | Depósito |
| | POST | `/transactions/withdraw` | Retiro |
| | POST | `/transactions/transfer` | Transferencia con comisión |
| | GET | `/transactions/account/{accountId}` | Historial de cuenta |
