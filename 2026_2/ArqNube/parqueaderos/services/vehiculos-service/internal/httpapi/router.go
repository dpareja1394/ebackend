package httpapi

import (
	"context"
	"net/http"
	"time"

	"parqueaderos/vehiculos-service/internal/vehiculo"
)

// Pinger permite comprobar la base de datos desde el health check.
type Pinger interface {
	Ping(ctx context.Context) error
}

// API reúne las dependencias de los handlers.
type API struct {
	db      Pinger
	repo    vehiculo.Repositorio
	service *vehiculo.Service
}

func NewAPI(db Pinger, repo vehiculo.Repositorio, service *vehiculo.Service) *API {
	return &API{db: db, repo: repo, service: service}
}

// Handler arma el router (patrones de Go 1.22+) con los middlewares comunes.
func (a *API) Handler() http.Handler {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", a.health)

	// API pública (enrutada por el gateway).
	mux.HandleFunc("GET /api/v1/vehiculos", a.listar)
	mux.HandleFunc("POST /api/v1/vehiculos", a.registrar)
	mux.HandleFunc("GET /api/v1/vehiculos/{vehiculoId}", a.obtener)
	mux.HandleFunc("PUT /api/v1/vehiculos/{vehiculoId}", a.actualizar)
	mux.HandleFunc("DELETE /api/v1/vehiculos/{vehiculoId}", a.eliminar)
	mux.HandleFunc("POST /api/v1/vehiculos/{vehiculoId}/traslado", a.trasladar)
	mux.HandleFunc("POST /api/v1/vehiculos/{vehiculoId}/asignacion", a.asignar)
	mux.HandleFunc("PUT /api/v1/vehiculos/{vehiculoId}/asignacion", a.reasignar)
	mux.HandleFunc("DELETE /api/v1/vehiculos/{vehiculoId}/asignacion", a.liberar)

	// API interna (solo otros servicios; el gateway no la enruta).
	mux.HandleFunc("GET /internal/v1/vehiculos/conteo", a.contarVehiculos)
	mux.HandleFunc("GET /internal/v1/vehiculos/{vehiculoId}", a.referenciaVehiculo)

	mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		writeError(w, r, http.StatusNotFound, "NO_ENCONTRADO", "Recurso no encontrado", nil)
	})
	return correlationID(accessLog(recoverer(mux)))
}

func (a *API) health(w http.ResponseWriter, r *http.Request) {
	ctx, cancel := context.WithTimeout(r.Context(), 2*time.Second)
	defer cancel()
	if err := a.db.Ping(ctx); err != nil {
		writeJSON(w, http.StatusServiceUnavailable, map[string]string{"status": "DOWN"})
		return
	}
	writeJSON(w, http.StatusOK, map[string]string{"status": "UP"})
}
