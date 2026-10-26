package vehiculo

import (
	"context"
	"errors"
	"testing"

	"github.com/google/uuid"
)

// repoFalso guarda vehículos en memoria; falloEspacio simula un error al guardar la asignación.
type repoFalso struct {
	vehiculos    map[uuid.UUID]Vehiculo
	falloEspacio error
	eliminados   []uuid.UUID
}

func (r *repoFalso) Crear(_ context.Context, v *Vehiculo) error {
	r.vehiculos[v.ID] = *v
	return nil
}

func (r *repoFalso) ObtenerPorID(_ context.Context, id uuid.UUID) (Vehiculo, error) {
	v, ok := r.vehiculos[id]
	if !ok {
		return v, ErrNoEncontrado("no existe")
	}
	return v, nil
}

func (r *repoFalso) Listar(context.Context, Filtro, int, int) ([]Vehiculo, int64, error) {
	return nil, 0, nil
}

func (r *repoFalso) Actualizar(_ context.Context, v *Vehiculo) error {
	r.vehiculos[v.ID] = *v
	return nil
}

func (r *repoFalso) ActualizarEspacio(_ context.Context, id uuid.UUID, _ *uuid.UUID, nuevo *EspacioRef) (Vehiculo, error) {
	if r.falloEspacio != nil {
		return Vehiculo{}, r.falloEspacio
	}
	v := r.vehiculos[id]
	v.EspacioID, v.EspacioCodigo = nil, nil
	if nuevo != nil {
		v.EspacioID, v.EspacioCodigo = &nuevo.ID, &nuevo.Codigo
	}
	r.vehiculos[id] = v
	return v, nil
}

func (r *repoFalso) ActualizarParqueadero(_ context.Context, id, parqueaderoID uuid.UUID) (Vehiculo, error) {
	v := r.vehiculos[id]
	v.ParqueaderoID, v.EspacioID, v.EspacioCodigo = parqueaderoID, nil, nil
	r.vehiculos[id] = v
	return v, nil
}

func (r *repoFalso) EliminarCon(_ context.Context, id uuid.UUID, antesDeConfirmar func() error) error {
	if err := antesDeConfirmar(); err != nil {
		return err
	}
	delete(r.vehiculos, id)
	r.eliminados = append(r.eliminados, id)
	return nil
}

func (r *repoFalso) ContarPorParqueadero(context.Context, uuid.UUID) (int64, error) { return 0, nil }

// clienteFalso registra las liberaciones; fallosLiberar hace fallar las primeras N llamadas.
type clienteFalso struct {
	estado        string
	liberados     []uuid.UUID
	fallosLiberar int
}

func (c *clienteFalso) ObtenerParqueadero(_ context.Context, id uuid.UUID) (Parqueadero, error) {
	return Parqueadero{ID: id, Estado: c.estado}, nil
}

func (c *clienteFalso) OcuparEspacio(_ context.Context, espacioID, _, _ uuid.UUID, _ TipoVehiculo) (EspacioRef, error) {
	return EspacioRef{ID: espacioID, Codigo: "A-01"}, nil
}

func (c *clienteFalso) LiberarEspacio(_ context.Context, espacioID, _ uuid.UUID) error {
	c.liberados = append(c.liberados, espacioID)
	if c.fallosLiberar > 0 {
		c.fallosLiberar--
		return ErrServicioNoDisponible("caído")
	}
	return nil
}

func nuevoEscenario(v Vehiculo) (*Service, *repoFalso, *clienteFalso) {
	repo := &repoFalso{vehiculos: map[uuid.UUID]Vehiculo{v.ID: v}}
	cliente := &clienteFalso{estado: "ACTIVO"}
	s := NewService(repo, cliente)
	s.espera = 0
	return s, repo, cliente
}

func codigoDe(err error) string {
	var e *Error
	if errors.As(err, &e) {
		return e.Codigo
	}
	return ""
}

func TestAsignarCompensaSiFallaLaEscrituraLocal(t *testing.T) {
	v := Vehiculo{ID: uuid.New(), ParqueaderoID: uuid.New(), Tipo: Automovil}
	s, repo, cliente := nuevoEscenario(v)
	repo.falloEspacio = errors.New("bd caída")
	espacio := uuid.New()

	_, err := s.Asignar(context.Background(), v.ID, espacio)

	if err == nil || err.Error() != "bd caída" {
		t.Fatalf("se esperaba el error original, se obtuvo %v", err)
	}
	if len(cliente.liberados) != 1 || cliente.liberados[0] != espacio {
		t.Fatalf("se esperaba una liberación compensatoria de %s, hubo %v", espacio, cliente.liberados)
	}
}

func TestAsignarReintentaLaCompensacionHastaTresVeces(t *testing.T) {
	v := Vehiculo{ID: uuid.New(), ParqueaderoID: uuid.New(), Tipo: Automovil}
	s, repo, cliente := nuevoEscenario(v)
	repo.falloEspacio = errors.New("bd caída")
	cliente.fallosLiberar = 5

	_, _ = s.Asignar(context.Background(), v.ID, uuid.New())

	if len(cliente.liberados) != 3 {
		t.Fatalf("se esperaban 3 intentos de liberación, hubo %d", len(cliente.liberados))
	}
}

func TestAsignarConEspacioYaAsignadoEsConflicto(t *testing.T) {
	actual, codigo := uuid.New(), "A-01"
	v := Vehiculo{ID: uuid.New(), ParqueaderoID: uuid.New(), Tipo: Automovil, EspacioID: &actual, EspacioCodigo: &codigo}
	s, _, cliente := nuevoEscenario(v)

	_, err := s.Asignar(context.Background(), v.ID, uuid.New())

	if codigoDe(err) != CodigoConflicto {
		t.Fatalf("se esperaba CONFLICTO, se obtuvo %v", err)
	}
	if len(cliente.liberados) != 0 {
		t.Fatal("no debía liberar nada")
	}
}

func TestEliminarLiberaElEspacio(t *testing.T) {
	espacio, codigo := uuid.New(), "A-01"
	v := Vehiculo{ID: uuid.New(), ParqueaderoID: uuid.New(), Tipo: Automovil, EspacioID: &espacio, EspacioCodigo: &codigo}
	s, repo, cliente := nuevoEscenario(v)

	if err := s.Eliminar(context.Background(), v.ID); err != nil {
		t.Fatal(err)
	}
	if len(cliente.liberados) != 1 || cliente.liberados[0] != espacio {
		t.Fatalf("se esperaba liberar %s, hubo %v", espacio, cliente.liberados)
	}
	if len(repo.eliminados) != 1 {
		t.Fatal("el vehículo no se eliminó")
	}
}

func TestEliminarNoBorraSiLaLiberacionFalla(t *testing.T) {
	espacio := uuid.New()
	v := Vehiculo{ID: uuid.New(), ParqueaderoID: uuid.New(), Tipo: Automovil, EspacioID: &espacio}
	s, repo, cliente := nuevoEscenario(v)
	cliente.fallosLiberar = 1

	err := s.Eliminar(context.Background(), v.ID)

	if codigoDe(err) != CodigoServicioNoDisponible {
		t.Fatalf("se esperaba SERVICIO_NO_DISPONIBLE, se obtuvo %v", err)
	}
	if len(repo.eliminados) != 0 {
		t.Fatal("no debía eliminarse el vehículo")
	}
}

func TestRegistrarEnParqueaderoInactivoEsReglaDeNegocio(t *testing.T) {
	s, _, cliente := nuevoEscenario(Vehiculo{ID: uuid.New()})
	cliente.estado = "INACTIVO"
	req := VehiculoRequest{Placa: "ABC123", Tipo: Automovil, Marca: "Kia", Color: "Azul",
		PropietarioNombre: "Ana Pérez", PropietarioDocumento: "123456"}

	_, err := s.Registrar(context.Background(), uuid.New(), req)

	if codigoDe(err) != CodigoReglaNegocio {
		t.Fatalf("se esperaba REGLA_NEGOCIO, se obtuvo %v", err)
	}
}

func TestListarPorPlacaExigeDosCaracteres(t *testing.T) {
	s, _, _ := nuevoEscenario(Vehiculo{ID: uuid.New()})
	placa := "a-"

	_, _, err := s.Listar(context.Background(), nil, &placa, 0, 20)

	if codigoDe(err) != CodigoValidacion {
		t.Fatalf("se esperaba VALIDACION, se obtuvo %v", err)
	}
}
