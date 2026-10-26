// Package platform agrupa la infraestructura común del servicio: configuración, logs y base de datos.
package platform

import (
	"fmt"
	"os"
	"time"
)

// Config se lee solo de variables de entorno (Principio III).
type Config struct {
	Port                string
	DatabaseURL         string
	ParqueaderosURL     string
	ParqueaderosTimeout time.Duration
}

func LoadConfig() (Config, error) {
	cfg := Config{
		Port:            getenv("PORT", "8082"),
		DatabaseURL:     os.Getenv("DATABASE_URL"),
		ParqueaderosURL: getenv("PARQUEADEROS_URL", "http://localhost:8081"),
	}
	if cfg.DatabaseURL == "" {
		return cfg, fmt.Errorf("la variable DATABASE_URL es obligatoria")
	}
	timeout, err := time.ParseDuration(getenv("PARQUEADEROS_TIMEOUT", "2s"))
	if err != nil {
		return cfg, fmt.Errorf("PARQUEADEROS_TIMEOUT inválido: %w", err)
	}
	cfg.ParqueaderosTimeout = timeout
	return cfg, nil
}

func getenv(key, def string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return def
}
