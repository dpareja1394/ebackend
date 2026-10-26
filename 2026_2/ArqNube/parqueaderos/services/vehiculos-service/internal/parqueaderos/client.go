// Package parqueaderos es el cliente HTTP de la API interna de parqueaderos-service.
package parqueaderos

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/google/uuid"

	"parqueaderos/vehiculos-service/internal/platform"
	"parqueaderos/vehiculos-service/internal/vehiculo"
)

const noDisponible = "El servicio de parqueaderos no está disponible"

// Client llama a parqueaderos-service con timeout y traduce sus errores a errores de dominio (research.md R3).
type Client struct {
	baseURL string
	http    *http.Client
}

func NewClient(baseURL string, timeout time.Duration) *Client {
	return &Client{baseURL: baseURL, http: &http.Client{Timeout: timeout}}
}

func (c *Client) ObtenerParqueadero(ctx context.Context, id uuid.UUID) (vehiculo.Parqueadero, error) {
	var p vehiculo.Parqueadero
	err := c.do(ctx, http.MethodGet, "/internal/v1/parqueaderos/"+id.String(), nil, &p)
	var e *vehiculo.Error
	if errors.As(err, &e) && e.Codigo == vehiculo.CodigoNoEncontrado {
		return p, vehiculo.ErrNoEncontrado(fmt.Sprintf("El parqueadero %s no existe", id))
	}
	return p, err
}

func (c *Client) OcuparEspacio(ctx context.Context, espacioID, vehiculoID, parqueaderoID uuid.UUID,
	tipo vehiculo.TipoVehiculo) (vehiculo.EspacioRef, error) {
	var ref vehiculo.EspacioRef
	body := map[string]any{"vehiculoId": vehiculoID, "parqueaderoId": parqueaderoID, "tipoVehiculo": tipo}
	err := c.do(ctx, http.MethodPost, "/internal/v1/espacios/"+espacioID.String()+"/ocupacion", body, &ref)
	return ref, err
}

func (c *Client) LiberarEspacio(ctx context.Context, espacioID, vehiculoID uuid.UUID) error {
	path := "/internal/v1/espacios/" + espacioID.String() + "/ocupacion?vehiculoId=" + vehiculoID.String()
	return c.do(ctx, http.MethodDelete, path, nil, nil)
}

// do ejecuta la petición. Los 404/409/422 remotos conservan su código y mensaje; cualquier otro fallo
// (red, timeout, 5xx) se convierte en SERVICIO_NO_DISPONIBLE.
func (c *Client) do(ctx context.Context, method, path string, body, out any) error {
	var reader io.Reader
	if body != nil {
		b, err := json.Marshal(body)
		if err != nil {
			return err
		}
		reader = bytes.NewReader(b)
	}
	req, err := http.NewRequestWithContext(ctx, method, c.baseURL+path, reader)
	if err != nil {
		return err
	}
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	if id := platform.CorrelationID(ctx); id != "" {
		req.Header.Set("X-Correlation-Id", id)
	}

	resp, err := c.http.Do(req)
	if err != nil {
		platform.Logger(ctx).Warn("fallo llamando a parqueaderos-service", "method", method, "path", path, "error", err.Error())
		return vehiculo.ErrServicioNoDisponible(noDisponible)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		if out == nil || resp.StatusCode == http.StatusNoContent {
			return nil
		}
		if err := json.NewDecoder(resp.Body).Decode(out); err != nil {
			return vehiculo.ErrServicioNoDisponible(noDisponible)
		}
		return nil
	}

	var remoto struct {
		Codigo  string `json:"codigo"`
		Mensaje string `json:"mensaje"`
	}
	_ = json.NewDecoder(resp.Body).Decode(&remoto)
	switch resp.StatusCode {
	case http.StatusNotFound:
		return vehiculo.ErrNoEncontrado(mensajeOr(remoto.Mensaje, "Recurso no encontrado en parqueaderos-service"))
	case http.StatusConflict:
		return vehiculo.ErrConflicto(mensajeOr(remoto.Mensaje, "Conflicto en parqueaderos-service"))
	case http.StatusUnprocessableEntity:
		return vehiculo.ErrReglaNegocio(mensajeOr(remoto.Mensaje, "Regla de negocio incumplida"))
	case http.StatusBadRequest:
		return vehiculo.ErrValidacion(mensajeOr(remoto.Mensaje, "Petición inválida"))
	default:
		platform.Logger(ctx).Warn("respuesta inesperada de parqueaderos-service", "method", method, "path", path, "status", resp.StatusCode)
		return vehiculo.ErrServicioNoDisponible(noDisponible)
	}
}

func mensajeOr(m, def string) string {
	if m != "" {
		return m
	}
	return def
}
