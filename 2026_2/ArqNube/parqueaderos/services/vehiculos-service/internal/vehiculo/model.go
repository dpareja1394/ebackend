package vehiculo

import (
	"fmt"
	"regexp"
	"strings"
	"time"
	"unicode/utf8"

	"github.com/google/uuid"
)

type TipoVehiculo string

const (
	Automovil   TipoVehiculo = "AUTOMOVIL"
	Motocicleta TipoVehiculo = "MOTOCICLETA"
	Bicicleta   TipoVehiculo = "BICICLETA"
)

func (t TipoVehiculo) valido() bool {
	return t == Automovil || t == Motocicleta || t == Bicicleta
}

// Vehiculo es un vehículo registrado en un parqueadero (contracts/vehiculos-service.openapi.yaml).
type Vehiculo struct {
	ID                   uuid.UUID    `json:"id"`
	Placa                string       `json:"placa"`
	Tipo                 TipoVehiculo `json:"tipo"`
	Marca                *string      `json:"marca"`
	Color                string       `json:"color"`
	PropietarioNombre    string       `json:"propietarioNombre"`
	PropietarioDocumento string       `json:"propietarioDocumento"`
	ParqueaderoID        uuid.UUID    `json:"parqueaderoId"`
	EspacioID            *uuid.UUID   `json:"espacioId"`
	EspacioCodigo        *string      `json:"espacioCodigo"`
	CreadoEn             time.Time    `json:"creadoEn"`
	ActualizadoEn        time.Time    `json:"actualizadoEn"`
}

// VehiculoRequest son los datos editables de un vehículo.
type VehiculoRequest struct {
	Placa                string       `json:"placa"`
	Tipo                 TipoVehiculo `json:"tipo"`
	Marca                string       `json:"marca"`
	Color                string       `json:"color"`
	PropietarioNombre    string       `json:"propietarioNombre"`
	PropietarioDocumento string       `json:"propietarioDocumento"`
}

// Parqueadero es lo que este servicio necesita saber de un parqueadero remoto.
type Parqueadero struct {
	ID     uuid.UUID `json:"id"`
	Estado string    `json:"estado"`
}

func (p Parqueadero) Activo() bool { return p.Estado == "ACTIVO" }

// EspacioRef es lo que este servicio guarda de un espacio asignado.
type EspacioRef struct {
	ID     uuid.UUID `json:"id"`
	Codigo string    `json:"codigo"`
}

var (
	separadoresPlaca = strings.NewReplacer(" ", "", "-", "", "\t", "")
	formatoPlaca     = regexp.MustCompile(`^[A-Z0-9]{3,10}$`)
)

// NormalizarPlaca pasa la placa a mayúsculas y quita espacios y guiones ("abc-123" → "ABC123").
func NormalizarPlaca(s string) string {
	return strings.ToUpper(separadoresPlaca.Replace(strings.TrimSpace(s)))
}

// Validar normaliza la petición y acumula todos los problemas encontrados en un ErrValidacion.
func Validar(req VehiculoRequest) (VehiculoRequest, error) {
	req.Placa = NormalizarPlaca(req.Placa)
	req.Marca = strings.TrimSpace(req.Marca)
	req.Color = strings.TrimSpace(req.Color)
	req.PropietarioNombre = strings.TrimSpace(req.PropietarioNombre)
	req.PropietarioDocumento = strings.TrimSpace(req.PropietarioDocumento)

	var detalles []Detalle
	agregar := func(campo, mensaje string) { detalles = append(detalles, Detalle{Campo: campo, Mensaje: mensaje}) }

	switch {
	case req.Placa == "":
		agregar("placa", "Es obligatoria")
	case !formatoPlaca.MatchString(req.Placa):
		agregar("placa", "Debe tener entre 3 y 10 letras o números (sin contar espacios ni guiones)")
	}
	if !req.Tipo.valido() {
		agregar("tipo", "Debe ser AUTOMOVIL, MOTOCICLETA o BICICLETA")
	}
	switch {
	case req.Marca == "" && req.Tipo != Bicicleta:
		agregar("marca", "Es obligatoria salvo para bicicletas")
	case utf8.RuneCountInString(req.Marca) > 50:
		agregar("marca", "Debe tener máximo 50 caracteres")
	}
	longitud("color", req.Color, 1, 30, agregar)
	longitud("propietarioNombre", req.PropietarioNombre, 3, 100, agregar)
	longitud("propietarioDocumento", req.PropietarioDocumento, 5, 20, agregar)

	if len(detalles) > 0 {
		return req, ErrValidacion("Datos inválidos", detalles...)
	}
	return req, nil
}

func longitud(campo, valor string, min, max int, agregar func(string, string)) {
	n := utf8.RuneCountInString(valor)
	switch {
	case n == 0:
		agregar(campo, "Es obligatorio")
	case n < min || n > max:
		agregar(campo, fmt.Sprintf("Debe tener entre %d y %d caracteres", min, max))
	}
}
