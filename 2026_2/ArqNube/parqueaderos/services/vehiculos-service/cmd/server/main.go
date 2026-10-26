package main

import (
	"context"
	"errors"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"parqueaderos/vehiculos-service/internal/httpapi"
	"parqueaderos/vehiculos-service/internal/parqueaderos"
	"parqueaderos/vehiculos-service/internal/platform"
	"parqueaderos/vehiculos-service/internal/vehiculo"
)

func main() {
	logger := platform.NewLogger()
	if err := run(); err != nil {
		logger.Error("el servicio terminó con error", "error", err.Error())
		os.Exit(1)
	}
}

func run() error {
	cfg, err := platform.LoadConfig()
	if err != nil {
		return err
	}

	ctx, stop := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer stop()

	pool, err := platform.ConnectDB(ctx, cfg.DatabaseURL)
	if err != nil {
		return err
	}
	defer pool.Close()

	repo := vehiculo.NewPgRepository(pool)
	cliente := parqueaderos.NewClient(cfg.ParqueaderosURL, cfg.ParqueaderosTimeout)
	api := httpapi.NewAPI(pool, repo, vehiculo.NewService(repo, cliente))

	srv := &http.Server{
		Addr:              ":" + cfg.Port,
		Handler:           api.Handler(),
		ReadHeaderTimeout: 5 * time.Second,
	}

	errCh := make(chan error, 1)
	go func() {
		slog.Info("vehiculos-service escuchando", "port", cfg.Port)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			errCh <- err
		}
		close(errCh)
	}()

	select {
	case err := <-errCh:
		return err
	case <-ctx.Done():
	}

	slog.Info("apagando vehiculos-service")
	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	return srv.Shutdown(shutdownCtx)
}
