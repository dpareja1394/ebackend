package httpapi

import (
	"net/http"

	"github.com/google/uuid"

	"parqueaderos/vehiculos-service/internal/vehiculo"
)

// Handlers de la API interna (/internal/v1), usada solo por otros servicios; el gateway no la enruta.

func (a *API) contarVehiculos(w http.ResponseWriter, r *http.Request) {
	id, err := uuid.Parse(r.URL.Query().Get("parqueaderoId"))
	if err != nil {
		writeDomainError(w, r, vehiculo.ErrValidacion("Parámetros inválidos",
			vehiculo.Detalle{Campo: "parqueaderoId", Mensaje: "Debe ser un UUID válido"}))
		return
	}
	cantidad, err := a.repo.ContarPorParqueadero(r.Context(), id)
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	writeJSON(w, http.StatusOK, map[string]any{"parqueaderoId": id, "cantidad": cantidad})
}

// referenciaVehiculo devuelve id, placa y espacio; lo usa la liberación forzada de espacios huérfanos.
func (a *API) referenciaVehiculo(w http.ResponseWriter, r *http.Request) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return
	}
	v, err := a.repo.ObtenerPorID(r.Context(), id)
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	writeJSON(w, http.StatusOK, map[string]any{"id": v.ID, "placa": v.Placa, "espacioId": v.EspacioID})
}
