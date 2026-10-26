package vehiculo

import (
	"context"
	"errors"
	"fmt"
	"strings"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
)

// Filtro de listado: por parqueadero, por prefijo de placa, o ambos.
type Filtro struct {
	ParqueaderoID *uuid.UUID
	PrefijoPlaca  string
}

// Repositorio es lo que el servicio necesita de la persistencia; se implementa con PostgreSQL.
type Repositorio interface {
	Crear(ctx context.Context, v *Vehiculo) error
	ObtenerPorID(ctx context.Context, id uuid.UUID) (Vehiculo, error)
	Listar(ctx context.Context, f Filtro, pagina, tamano int) ([]Vehiculo, int64, error)
	Actualizar(ctx context.Context, v *Vehiculo) error
	// ActualizarEspacio cambia el espacio solo si el vehículo sigue teniendo el espacio esperado
	// (control optimista: dos asignaciones simultáneas al mismo vehículo no se pisan).
	ActualizarEspacio(ctx context.Context, id uuid.UUID, esperado *uuid.UUID, nuevo *EspacioRef) (Vehiculo, error)
	ActualizarParqueadero(ctx context.Context, id, parqueaderoID uuid.UUID) (Vehiculo, error)
	// EliminarCon borra el vehículo en una transacción que solo se confirma si antesDeConfirmar no falla.
	EliminarCon(ctx context.Context, id uuid.UUID, antesDeConfirmar func() error) error
	ContarPorParqueadero(ctx context.Context, parqueaderoID uuid.UUID) (int64, error)
}

// PgRepository guarda los vehículos en PostgreSQL.
type PgRepository struct {
	pool *pgxpool.Pool
}

func NewPgRepository(pool *pgxpool.Pool) *PgRepository {
	return &PgRepository{pool: pool}
}

const columnas = `id, placa, tipo, marca, color, propietario_nombre, propietario_documento,
	parqueadero_id, espacio_id, espacio_codigo, creado_en, actualizado_en`

func scan(row pgx.Row) (Vehiculo, error) {
	var v Vehiculo
	err := row.Scan(&v.ID, &v.Placa, &v.Tipo, &v.Marca, &v.Color, &v.PropietarioNombre, &v.PropietarioDocumento,
		&v.ParqueaderoID, &v.EspacioID, &v.EspacioCodigo, &v.CreadoEn, &v.ActualizadoEn)
	return v, err
}

func (r *PgRepository) Crear(ctx context.Context, v *Vehiculo) error {
	row := r.pool.QueryRow(ctx, `
		INSERT INTO vehiculos (id, placa, tipo, marca, color, propietario_nombre, propietario_documento, parqueadero_id)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
		RETURNING `+columnas,
		v.ID, v.Placa, v.Tipo, v.Marca, v.Color, v.PropietarioNombre, v.PropietarioDocumento, v.ParqueaderoID)
	creado, err := scan(row)
	if err != nil {
		return traducir(err, v.Placa)
	}
	*v = creado
	return nil
}

func (r *PgRepository) ObtenerPorID(ctx context.Context, id uuid.UUID) (Vehiculo, error) {
	v, err := scan(r.pool.QueryRow(ctx, `SELECT `+columnas+` FROM vehiculos WHERE id = $1`, id))
	if errors.Is(err, pgx.ErrNoRows) {
		return v, noExiste(id)
	}
	return v, err
}

func (r *PgRepository) Listar(ctx context.Context, f Filtro, pagina, tamano int) ([]Vehiculo, int64, error) {
	var condiciones []string
	var args []any
	if f.ParqueaderoID != nil {
		args = append(args, *f.ParqueaderoID)
		condiciones = append(condiciones, fmt.Sprintf("parqueadero_id = $%d", len(args)))
	}
	if f.PrefijoPlaca != "" {
		// El prefijo ya viene normalizado (solo A-Z y 0-9), así que no hay comodines que escapar.
		args = append(args, f.PrefijoPlaca)
		condiciones = append(condiciones, fmt.Sprintf("placa LIKE $%d || '%%'", len(args)))
	}
	where := ""
	if len(condiciones) > 0 {
		where = " WHERE " + strings.Join(condiciones, " AND ")
	}

	var total int64
	if err := r.pool.QueryRow(ctx, `SELECT count(*) FROM vehiculos`+where, args...).Scan(&total); err != nil {
		return nil, 0, err
	}

	args = append(args, tamano, pagina*tamano)
	rows, err := r.pool.Query(ctx, fmt.Sprintf(`SELECT %s FROM vehiculos%s ORDER BY placa LIMIT $%d OFFSET $%d`,
		columnas, where, len(args)-1, len(args)), args...)
	if err != nil {
		return nil, 0, err
	}
	defer rows.Close()
	vehiculos := []Vehiculo{}
	for rows.Next() {
		v, err := scan(rows)
		if err != nil {
			return nil, 0, err
		}
		vehiculos = append(vehiculos, v)
	}
	return vehiculos, total, rows.Err()
}

func (r *PgRepository) Actualizar(ctx context.Context, v *Vehiculo) error {
	row := r.pool.QueryRow(ctx, `
		UPDATE vehiculos
		SET placa = $2, tipo = $3, marca = $4, color = $5, propietario_nombre = $6, propietario_documento = $7,
		    actualizado_en = now()
		WHERE id = $1
		RETURNING `+columnas,
		v.ID, v.Placa, v.Tipo, v.Marca, v.Color, v.PropietarioNombre, v.PropietarioDocumento)
	actualizado, err := scan(row)
	if errors.Is(err, pgx.ErrNoRows) {
		return noExiste(v.ID)
	}
	if err != nil {
		return traducir(err, v.Placa)
	}
	*v = actualizado
	return nil
}

func (r *PgRepository) ActualizarEspacio(ctx context.Context, id uuid.UUID, esperado *uuid.UUID, nuevo *EspacioRef) (Vehiculo, error) {
	var espacioID *uuid.UUID
	var codigo *string
	if nuevo != nil {
		espacioID, codigo = &nuevo.ID, &nuevo.Codigo
	}
	row := r.pool.QueryRow(ctx, `
		UPDATE vehiculos
		SET espacio_id = $3, espacio_codigo = $4, actualizado_en = now()
		WHERE id = $1 AND espacio_id IS NOT DISTINCT FROM $2
		RETURNING `+columnas,
		id, esperado, espacioID, codigo)
	v, err := scan(row)
	if errors.Is(err, pgx.ErrNoRows) {
		return v, ErrConflicto("El vehículo cambió mientras se procesaba la operación; intente de nuevo")
	}
	if err != nil {
		return v, traducir(err, "")
	}
	return v, nil
}

func (r *PgRepository) ActualizarParqueadero(ctx context.Context, id, parqueaderoID uuid.UUID) (Vehiculo, error) {
	row := r.pool.QueryRow(ctx, `
		UPDATE vehiculos
		SET parqueadero_id = $2, espacio_id = NULL, espacio_codigo = NULL, actualizado_en = now()
		WHERE id = $1
		RETURNING `+columnas,
		id, parqueaderoID)
	v, err := scan(row)
	if errors.Is(err, pgx.ErrNoRows) {
		return v, noExiste(id)
	}
	return v, err
}

func (r *PgRepository) EliminarCon(ctx context.Context, id uuid.UUID, antesDeConfirmar func() error) error {
	tx, err := r.pool.Begin(ctx)
	if err != nil {
		return err
	}
	defer tx.Rollback(ctx) //nolint:errcheck // tras Commit no hace nada

	tag, err := tx.Exec(ctx, `DELETE FROM vehiculos WHERE id = $1`, id)
	if err != nil {
		return err
	}
	if tag.RowsAffected() == 0 {
		return noExiste(id)
	}
	if err := antesDeConfirmar(); err != nil {
		return err
	}
	return tx.Commit(ctx)
}

func (r *PgRepository) ContarPorParqueadero(ctx context.Context, parqueaderoID uuid.UUID) (int64, error) {
	var cantidad int64
	err := r.pool.QueryRow(ctx, `SELECT count(*) FROM vehiculos WHERE parqueadero_id = $1`, parqueaderoID).Scan(&cantidad)
	return cantidad, err
}

func noExiste(id uuid.UUID) error {
	return ErrNoEncontrado(fmt.Sprintf("El vehículo %s no existe", id))
}

// traducir convierte las violaciones de unicidad en conflictos de negocio.
func traducir(err error, placa string) error {
	var pgErr *pgconn.PgError
	if errors.As(err, &pgErr) && pgErr.Code == "23505" {
		switch pgErr.ConstraintName {
		case "uq_vehiculos_placa":
			return ErrConflicto(fmt.Sprintf("La placa %s ya está registrada", placa))
		case "uq_vehiculos_espacio":
			return ErrConflicto("El espacio ya está asignado a otro vehículo")
		}
	}
	return err
}
