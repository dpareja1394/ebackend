package httpapi

import (
	"net/http"
	"time"

	"github.com/google/uuid"

	"parqueaderos/vehiculos-service/internal/platform"
)

const correlationHeader = "X-Correlation-Id"

// correlationID toma el X-Correlation-Id de la petición (o genera uno), lo pone en el contexto y en la respuesta.
func correlationID(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		id := r.Header.Get(correlationHeader)
		if id == "" {
			id = uuid.NewString()
		}
		w.Header().Set(correlationHeader, id)
		next.ServeHTTP(w, r.WithContext(platform.WithCorrelationID(r.Context(), id)))
	})
}

type statusRecorder struct {
	http.ResponseWriter
	status int
}

func (s *statusRecorder) WriteHeader(code int) {
	s.status = code
	s.ResponseWriter.WriteHeader(code)
}

// accessLog registra cada petición en JSON con su correlationId.
func accessLog(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		inicio := time.Now()
		rec := &statusRecorder{ResponseWriter: w, status: http.StatusOK}
		next.ServeHTTP(rec, r)
		if r.URL.Path == "/health" {
			return
		}
		platform.Logger(r.Context()).Info("petición atendida",
			"method", r.Method,
			"path", r.URL.Path,
			"status", rec.status,
			"durationMs", time.Since(inicio).Milliseconds())
	})
}

// recoverer convierte un panic en un 500 con el formato de error estándar.
func recoverer(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if p := recover(); p != nil {
				platform.Logger(r.Context()).Error("panic atendiendo petición", "panic", p)
				writeError(w, r, http.StatusInternalServerError, "ERROR_INTERNO", "Ocurrió un error inesperado", nil)
			}
		}()
		next.ServeHTTP(w, r)
	})
}
