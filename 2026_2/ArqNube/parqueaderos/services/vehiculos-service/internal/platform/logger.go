package platform

import (
	"context"
	"log/slog"
	"os"
)

type ctxKey struct{}

// NewLogger crea el logger JSON a stdout y lo instala como logger por defecto (Principio IV).
func NewLogger() *slog.Logger {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, nil)).With("service", "vehiculos-service")
	slog.SetDefault(logger)
	return logger
}

// WithCorrelationID guarda el identificador de correlación en el contexto de la petición.
func WithCorrelationID(ctx context.Context, id string) context.Context {
	return context.WithValue(ctx, ctxKey{}, id)
}

// CorrelationID devuelve el identificador de correlación del contexto, o "" si no hay.
func CorrelationID(ctx context.Context) string {
	id, _ := ctx.Value(ctxKey{}).(string)
	return id
}

// Logger devuelve el logger por defecto con el atributo correlationId de la petición.
func Logger(ctx context.Context) *slog.Logger {
	if id := CorrelationID(ctx); id != "" {
		return slog.Default().With("correlationId", id)
	}
	return slog.Default()
}
