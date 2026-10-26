// Package httpapi expone la API HTTP del servicio: rutas, handlers, middlewares y errores.
package httpapi

import (
	"encoding/json"
	"errors"
	"net/http"
	"time"

	"parqueaderos/vehiculos-service/internal/platform"
	"parqueaderos/vehiculos-service/internal/vehiculo"
)

type errorResponse struct {
	Codigo        string             `json:"codigo"`
	Mensaje       string             `json:"mensaje"`
	Detalles      []vehiculo.Detalle `json:"detalles"`
	CorrelationID string             `json:"correlationId"`
	Timestamp     time.Time          `json:"timestamp"`
}

var statusPorCodigo = map[string]int{
	vehiculo.CodigoValidacion:           http.StatusBadRequest,
	vehiculo.CodigoNoEncontrado:         http.StatusNotFound,
	vehiculo.CodigoConflicto:            http.StatusConflict,
	vehiculo.CodigoReglaNegocio:         http.StatusUnprocessableEntity,
	vehiculo.CodigoServicioNoDisponible: http.StatusServiceUnavailable,
}

func writeJSON(w http.ResponseWriter, status int, body any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(body)
}

func writeError(w http.ResponseWriter, r *http.Request, status int, codigo, mensaje string, detalles []vehiculo.Detalle) {
	if detalles == nil {
		detalles = []vehiculo.Detalle{}
	}
	writeJSON(w, status, errorResponse{
		Codigo:        codigo,
		Mensaje:       mensaje,
		Detalles:      detalles,
		CorrelationID: platform.CorrelationID(r.Context()),
		Timestamp:     time.Now().UTC(),
	})
}

// writeDomainError traduce un error del dominio a la respuesta HTTP estándar.
func writeDomainError(w http.ResponseWriter, r *http.Request, err error) {
	var e *vehiculo.Error
	if errors.As(err, &e) {
		status, ok := statusPorCodigo[e.Codigo]
		if !ok {
			status = http.StatusInternalServerError
		}
		writeError(w, r, status, e.Codigo, e.Mensaje, e.Detalles)
		return
	}
	platform.Logger(r.Context()).Error("error no controlado", "error", err.Error())
	writeError(w, r, http.StatusInternalServerError, "ERROR_INTERNO", "Ocurrió un error inesperado", nil)
}
