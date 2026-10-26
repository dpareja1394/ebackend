package httpapi

import (
	"encoding/json"
	"errors"
	"io"
	"net/http"
	"strconv"

	"github.com/google/uuid"

	"parqueaderos/vehiculos-service/internal/vehiculo"
)

const (
	tamanoPorDefecto = 20
	tamanoMaximo     = 100
)

type pagina struct {
	Contenido      []vehiculo.Vehiculo `json:"contenido"`
	Pagina         int                 `json:"pagina"`
	Tamano         int                 `json:"tamano"`
	TotalElementos int64               `json:"totalElementos"`
	TotalPaginas   int                 `json:"totalPaginas"`
}

type registroRequest struct {
	vehiculo.VehiculoRequest
	ParqueaderoID string `json:"parqueaderoId"`
}

type trasladoRequest struct {
	ParqueaderoID string `json:"parqueaderoId"`
}

type asignacionRequest struct {
	EspacioID string `json:"espacioId"`
}

func (a *API) listar(w http.ResponseWriter, r *http.Request) {
	q := r.URL.Query()
	numPagina, tamano, err := paginacion(q.Get("pagina"), q.Get("tamano"))
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	var parqueaderoID *uuid.UUID
	if v := q.Get("parqueaderoId"); v != "" {
		id, err := uuidCampo("parqueaderoId", v)
		if err != nil {
			writeDomainError(w, r, err)
			return
		}
		parqueaderoID = &id
	}
	var placa *string
	if q.Has("placa") {
		p := q.Get("placa")
		placa = &p
	}

	vehiculos, total, err := a.service.Listar(r.Context(), parqueaderoID, placa, numPagina, tamano)
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	totalPaginas := int((total + int64(tamano) - 1) / int64(tamano))
	writeJSON(w, http.StatusOK, pagina{vehiculos, numPagina, tamano, total, totalPaginas})
}

func (a *API) registrar(w http.ResponseWriter, r *http.Request) {
	var req registroRequest
	if !decodificar(w, r, &req) {
		return
	}
	parqueaderoID, err := uuidCampo("parqueaderoId", req.ParqueaderoID)
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	v, err := a.service.Registrar(r.Context(), parqueaderoID, req.VehiculoRequest)
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	w.Header().Set("Location", "/api/v1/vehiculos/"+v.ID.String())
	writeJSON(w, http.StatusCreated, v)
}

func (a *API) obtener(w http.ResponseWriter, r *http.Request) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return
	}
	responder(w, r)(a.service.Obtener(r.Context(), id))
}

func (a *API) actualizar(w http.ResponseWriter, r *http.Request) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return
	}
	var req vehiculo.VehiculoRequest
	if !decodificar(w, r, &req) {
		return
	}
	responder(w, r)(a.service.Actualizar(r.Context(), id, req))
}

func (a *API) eliminar(w http.ResponseWriter, r *http.Request) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return
	}
	if err := a.service.Eliminar(r.Context(), id); err != nil {
		writeDomainError(w, r, err)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

func (a *API) trasladar(w http.ResponseWriter, r *http.Request) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return
	}
	var req trasladoRequest
	if !decodificar(w, r, &req) {
		return
	}
	destino, err := uuidCampo("parqueaderoId", req.ParqueaderoID)
	if err != nil {
		writeDomainError(w, r, err)
		return
	}
	responder(w, r)(a.service.Trasladar(r.Context(), id, destino))
}

func (a *API) asignar(w http.ResponseWriter, r *http.Request) {
	id, espacioID, ok := asignacionDePeticion(w, r)
	if !ok {
		return
	}
	responder(w, r)(a.service.Asignar(r.Context(), id, espacioID))
}

func (a *API) reasignar(w http.ResponseWriter, r *http.Request) {
	id, espacioID, ok := asignacionDePeticion(w, r)
	if !ok {
		return
	}
	responder(w, r)(a.service.Reasignar(r.Context(), id, espacioID))
}

func (a *API) liberar(w http.ResponseWriter, r *http.Request) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return
	}
	responder(w, r)(a.service.Liberar(r.Context(), id))
}

// responder escribe el vehículo con 200 o el error con su código.
func responder(w http.ResponseWriter, r *http.Request) func(vehiculo.Vehiculo, error) {
	return func(v vehiculo.Vehiculo, err error) {
		if err != nil {
			writeDomainError(w, r, err)
			return
		}
		writeJSON(w, http.StatusOK, v)
	}
}

func asignacionDePeticion(w http.ResponseWriter, r *http.Request) (uuid.UUID, uuid.UUID, bool) {
	id, ok := idDeRuta(w, r)
	if !ok {
		return uuid.Nil, uuid.Nil, false
	}
	var req asignacionRequest
	if !decodificar(w, r, &req) {
		return uuid.Nil, uuid.Nil, false
	}
	espacioID, err := uuidCampo("espacioId", req.EspacioID)
	if err != nil {
		writeDomainError(w, r, err)
		return uuid.Nil, uuid.Nil, false
	}
	return id, espacioID, true
}

func idDeRuta(w http.ResponseWriter, r *http.Request) (uuid.UUID, bool) {
	id, err := uuidCampo("vehiculoId", r.PathValue("vehiculoId"))
	if err != nil {
		writeDomainError(w, r, err)
		return uuid.Nil, false
	}
	return id, true
}

func uuidCampo(campo, valor string) (uuid.UUID, error) {
	if valor == "" {
		return uuid.Nil, vehiculo.ErrValidacion("Datos inválidos", vehiculo.Detalle{Campo: campo, Mensaje: "Es obligatorio"})
	}
	id, err := uuid.Parse(valor)
	if err != nil {
		return uuid.Nil, vehiculo.ErrValidacion("Datos inválidos",
			vehiculo.Detalle{Campo: campo, Mensaje: "Valor no válido: '" + valor + "'"})
	}
	return id, nil
}

// decodificar lee el cuerpo JSON; si es inválido responde 400 y devuelve false.
func decodificar(w http.ResponseWriter, r *http.Request, dst any) bool {
	err := json.NewDecoder(io.LimitReader(r.Body, 1<<20)).Decode(dst)
	if err == nil {
		return true
	}
	var detalles []vehiculo.Detalle
	var typeErr *json.UnmarshalTypeError
	if errors.As(err, &typeErr) && typeErr.Field != "" {
		detalles = append(detalles, vehiculo.Detalle{Campo: typeErr.Field, Mensaje: "Valor no válido"})
	}
	writeDomainError(w, r, vehiculo.ErrValidacion("El cuerpo de la petición no es válido", detalles...))
	return false
}

func paginacion(paginaTxt, tamanoTxt string) (int, int, error) {
	numPagina, tamano := 0, tamanoPorDefecto
	if paginaTxt != "" {
		n, err := strconv.Atoi(paginaTxt)
		if err != nil || n < 0 {
			return 0, 0, vehiculo.ErrValidacion("Debe ser mayor o igual a 0",
				vehiculo.Detalle{Campo: "pagina", Mensaje: "Debe ser mayor o igual a 0"})
		}
		numPagina = n
	}
	if tamanoTxt != "" {
		n, err := strconv.Atoi(tamanoTxt)
		if err != nil || n < 1 || n > tamanoMaximo {
			return 0, 0, vehiculo.ErrValidacion("Debe estar entre 1 y 100",
				vehiculo.Detalle{Campo: "tamano", Mensaje: "Debe estar entre 1 y 100"})
		}
		tamano = n
	}
	return numPagina, tamano, nil
}
