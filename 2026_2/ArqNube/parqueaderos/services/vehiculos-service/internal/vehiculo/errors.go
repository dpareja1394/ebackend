// Package vehiculo contiene el dominio de vehículos: modelo, reglas, repositorio y servicio.
package vehiculo

// Códigos de error compartidos con todos los servicios (research.md R9).
const (
	CodigoValidacion           = "VALIDACION"
	CodigoNoEncontrado         = "NO_ENCONTRADO"
	CodigoConflicto            = "CONFLICTO"
	CodigoReglaNegocio         = "REGLA_NEGOCIO"
	CodigoServicioNoDisponible = "SERVICIO_NO_DISPONIBLE"
)

// Detalle describe un problema en un campo concreto de la petición.
type Detalle struct {
	Campo   string `json:"campo"`
	Mensaje string `json:"mensaje"`
}

// Error es un error de negocio que la capa HTTP traduce a una respuesta con su código.
type Error struct {
	Codigo   string
	Mensaje  string
	Detalles []Detalle
}

func (e *Error) Error() string { return e.Codigo + ": " + e.Mensaje }

func ErrValidacion(mensaje string, detalles ...Detalle) *Error {
	return &Error{Codigo: CodigoValidacion, Mensaje: mensaje, Detalles: detalles}
}

func ErrNoEncontrado(mensaje string) *Error {
	return &Error{Codigo: CodigoNoEncontrado, Mensaje: mensaje}
}

func ErrConflicto(mensaje string) *Error {
	return &Error{Codigo: CodigoConflicto, Mensaje: mensaje}
}

func ErrReglaNegocio(mensaje string) *Error {
	return &Error{Codigo: CodigoReglaNegocio, Mensaje: mensaje}
}

func ErrServicioNoDisponible(mensaje string) *Error {
	return &Error{Codigo: CodigoServicioNoDisponible, Mensaje: mensaje}
}
