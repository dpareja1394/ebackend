package vehiculo

import (
	"context"
	"errors"
	"fmt"
	"time"

	"github.com/google/uuid"

	"parqueaderos/vehiculos-service/internal/platform"
)

// ParqueaderosClient es lo que el servicio necesita de parqueaderos-service.
type ParqueaderosClient interface {
	ObtenerParqueadero(ctx context.Context, id uuid.UUID) (Parqueadero, error)
	OcuparEspacio(ctx context.Context, espacioID, vehiculoID, parqueaderoID uuid.UUID, tipo TipoVehiculo) (EspacioRef, error)
	LiberarEspacio(ctx context.Context, espacioID, vehiculoID uuid.UUID) error
}

const (
	intentosLiberacion = 3
	esperaLiberacion   = 200 * time.Millisecond
	minPrefijoPlaca    = 2
)

// Service aplica las reglas de vehículos (FR-011 a FR-018) y orquesta la asignación de espacios con
// parqueaderos-service mediante una saga síncrona con compensación (research.md R2).
type Service struct {
	repo         Repositorio
	parqueaderos ParqueaderosClient
	espera       time.Duration
}

func NewService(repo Repositorio, parqueaderos ParqueaderosClient) *Service {
	return &Service{repo: repo, parqueaderos: parqueaderos, espera: esperaLiberacion}
}

func (s *Service) Registrar(ctx context.Context, parqueaderoID uuid.UUID, req VehiculoRequest) (Vehiculo, error) {
	req, err := Validar(req)
	if err != nil {
		return Vehiculo{}, err
	}
	if err := s.verificarParqueaderoActivo(ctx, parqueaderoID); err != nil {
		return Vehiculo{}, err
	}
	v := Vehiculo{
		ID:                   uuid.New(),
		Placa:                req.Placa,
		Tipo:                 req.Tipo,
		Marca:                textoOpcional(req.Marca),
		Color:                req.Color,
		PropietarioNombre:    req.PropietarioNombre,
		PropietarioDocumento: req.PropietarioDocumento,
		ParqueaderoID:        parqueaderoID,
	}
	if err := s.repo.Crear(ctx, &v); err != nil {
		return Vehiculo{}, err
	}
	return v, nil
}

func (s *Service) Obtener(ctx context.Context, id uuid.UUID) (Vehiculo, error) {
	return s.repo.ObtenerPorID(ctx, id)
}

// Listar exige parqueadero o placa. La placa se busca por prefijo (mínimo 2 caracteres), FR-018.
func (s *Service) Listar(ctx context.Context, parqueaderoID *uuid.UUID, placa *string, pagina, tamano int) ([]Vehiculo, int64, error) {
	f := Filtro{ParqueaderoID: parqueaderoID}
	if placa != nil {
		f.PrefijoPlaca = NormalizarPlaca(*placa)
		if len(f.PrefijoPlaca) < minPrefijoPlaca {
			return nil, 0, ErrValidacion("La búsqueda por placa requiere al menos 2 caracteres",
				Detalle{Campo: "placa", Mensaje: "Escriba al menos 2 caracteres"})
		}
	}
	if f.ParqueaderoID == nil && f.PrefijoPlaca == "" {
		return nil, 0, ErrValidacion("Indique el parqueadero o la placa a buscar")
	}
	return s.repo.Listar(ctx, f, pagina, tamano)
}

// Actualizar cambia los datos del vehículo; el parqueadero y el espacio tienen sus propias operaciones.
func (s *Service) Actualizar(ctx context.Context, id uuid.UUID, req VehiculoRequest) (Vehiculo, error) {
	req, err := Validar(req)
	if err != nil {
		return Vehiculo{}, err
	}
	v, err := s.repo.ObtenerPorID(ctx, id)
	if err != nil {
		return Vehiculo{}, err
	}
	if v.EspacioID != nil && req.Tipo != v.Tipo {
		return Vehiculo{}, ErrConflicto("No se puede cambiar el tipo de un vehículo con espacio asignado")
	}
	v.Placa = req.Placa
	v.Tipo = req.Tipo
	v.Marca = textoOpcional(req.Marca)
	v.Color = req.Color
	v.PropietarioNombre = req.PropietarioNombre
	v.PropietarioDocumento = req.PropietarioDocumento
	if err := s.repo.Actualizar(ctx, &v); err != nil {
		return Vehiculo{}, err
	}
	return v, nil
}

// Eliminar borra el vehículo y libera su espacio (FR-017). El borrado solo se confirma si la liberación
// remota tuvo éxito, para no dejar un espacio ocupado por un vehículo inexistente.
func (s *Service) Eliminar(ctx context.Context, id uuid.UUID) error {
	v, err := s.repo.ObtenerPorID(ctx, id)
	if err != nil {
		return err
	}
	return s.repo.EliminarCon(ctx, id, func() error {
		if v.EspacioID == nil {
			return nil
		}
		return s.liberar(ctx, *v.EspacioID, v.ID)
	})
}

// Trasladar mueve el vehículo a otro parqueadero activo, liberando antes su espacio (FR-013, FR-013a).
func (s *Service) Trasladar(ctx context.Context, id, destinoID uuid.UUID) (Vehiculo, error) {
	v, err := s.repo.ObtenerPorID(ctx, id)
	if err != nil {
		return Vehiculo{}, err
	}
	if v.ParqueaderoID == destinoID {
		return Vehiculo{}, ErrReglaNegocio("El vehículo ya pertenece a ese parqueadero")
	}
	if err := s.verificarParqueaderoActivo(ctx, destinoID); err != nil {
		return Vehiculo{}, err
	}
	if v.EspacioID != nil {
		if err := s.liberar(ctx, *v.EspacioID, v.ID); err != nil {
			return Vehiculo{}, err
		}
	}
	return s.repo.ActualizarParqueadero(ctx, id, destinoID)
}

// Asignar ubica el vehículo en un espacio (FR-014, FR-015). Primero ocupa el espacio remoto y luego guarda
// la referencia local; si lo segundo falla, compensa liberando el espacio.
func (s *Service) Asignar(ctx context.Context, id, espacioID uuid.UUID) (Vehiculo, error) {
	v, err := s.repo.ObtenerPorID(ctx, id)
	if err != nil {
		return Vehiculo{}, err
	}
	if v.EspacioID != nil {
		return Vehiculo{}, ErrConflicto(fmt.Sprintf(
			"El vehículo ya tiene asignado el espacio %s; libérelo o reasígnelo", valor(v.EspacioCodigo)))
	}
	return s.ocuparYGuardar(ctx, v, espacioID)
}

// Reasignar mueve el vehículo a otro espacio (FR-016): ocupa el nuevo, guarda y después libera el anterior.
func (s *Service) Reasignar(ctx context.Context, id, espacioID uuid.UUID) (Vehiculo, error) {
	v, err := s.repo.ObtenerPorID(ctx, id)
	if err != nil {
		return Vehiculo{}, err
	}
	if v.EspacioID == nil {
		return s.ocuparYGuardar(ctx, v, espacioID)
	}
	if *v.EspacioID == espacioID {
		return v, nil
	}
	anterior := *v.EspacioID
	actualizado, err := s.ocuparYGuardar(ctx, v, espacioID)
	if err != nil {
		return Vehiculo{}, err
	}
	// El vehículo ya quedó en el espacio nuevo; si el anterior no se puede liberar, queda huérfano y se
	// recupera con la liberación forzada (DELETE /api/v1/espacios/{id}/ocupacion).
	if err := s.liberarConReintentos(ctx, anterior, v.ID); err != nil {
		platform.Logger(ctx).Error("ESPACIO_HUERFANO: no se pudo liberar el espacio anterior al reasignar",
			"espacioId", anterior, "vehiculoId", v.ID, "error", err.Error())
	}
	return actualizado, nil
}

// Liberar quita el espacio al vehículo (FR-016). Si no tiene espacio, no hace nada.
func (s *Service) Liberar(ctx context.Context, id uuid.UUID) (Vehiculo, error) {
	v, err := s.repo.ObtenerPorID(ctx, id)
	if err != nil {
		return Vehiculo{}, err
	}
	if v.EspacioID == nil {
		return v, nil
	}
	if err := s.liberar(ctx, *v.EspacioID, v.ID); err != nil {
		return Vehiculo{}, err
	}
	return s.repo.ActualizarEspacio(ctx, v.ID, v.EspacioID, nil)
}

// ocuparYGuardar es el paso común de la saga: ocupar el espacio remoto y guardar la referencia local
// (solo si el vehículo sigue con el espacio que tenía al leerlo). Si no se puede guardar, compensa.
func (s *Service) ocuparYGuardar(ctx context.Context, v Vehiculo, espacioID uuid.UUID) (Vehiculo, error) {
	ref, err := s.parqueaderos.OcuparEspacio(ctx, espacioID, v.ID, v.ParqueaderoID, v.Tipo)
	if err != nil {
		return Vehiculo{}, err
	}
	actualizado, err := s.repo.ActualizarEspacio(ctx, v.ID, v.EspacioID, &ref)
	if err == nil {
		return actualizado, nil
	}
	if errLib := s.liberarConReintentos(ctx, espacioID, v.ID); errLib != nil {
		platform.Logger(ctx).Error("ESPACIO_HUERFANO: falló la compensación al asignar",
			"espacioId", espacioID, "vehiculoId", v.ID, "error", errLib.Error())
	}
	return Vehiculo{}, err
}

// liberarConReintentos libera un espacio (operación idempotente) hasta 3 veces. Usa un contexto que no se
// cancela con la petición, para que la compensación termine aunque el cliente se desconecte.
func (s *Service) liberarConReintentos(ctx context.Context, espacioID, vehiculoID uuid.UUID) error {
	ctx = context.WithoutCancel(ctx)
	var err error
	for intento := 1; intento <= intentosLiberacion; intento++ {
		if err = s.liberar(ctx, espacioID, vehiculoID); err == nil {
			return nil
		}
		if intento < intentosLiberacion {
			time.Sleep(s.espera)
		}
	}
	return err
}

// liberar libera el espacio remoto; si el espacio ya no existe, no hay nada que liberar.
func (s *Service) liberar(ctx context.Context, espacioID, vehiculoID uuid.UUID) error {
	err := s.parqueaderos.LiberarEspacio(ctx, espacioID, vehiculoID)
	var e *Error
	if errors.As(err, &e) && e.Codigo == CodigoNoEncontrado {
		return nil
	}
	return err
}

func (s *Service) verificarParqueaderoActivo(ctx context.Context, id uuid.UUID) error {
	p, err := s.parqueaderos.ObtenerParqueadero(ctx, id)
	if err != nil {
		return err
	}
	if !p.Activo() {
		return ErrReglaNegocio("El parqueadero está inactivo")
	}
	return nil
}

func textoOpcional(s string) *string {
	if s == "" {
		return nil
	}
	return &s
}

func valor(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}
