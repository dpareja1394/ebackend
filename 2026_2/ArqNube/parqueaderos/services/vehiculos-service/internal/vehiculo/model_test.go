package vehiculo

import (
	"errors"
	"testing"
)

func TestNormalizarPlaca(t *testing.T) {
	casos := map[string]string{
		"abc-123":   "ABC123",
		" abc 123 ": "ABC123",
		"Abc12d":    "ABC12D",
		"":          "",
		"a-b-c":     "ABC",
	}
	for entrada, esperado := range casos {
		if got := NormalizarPlaca(entrada); got != esperado {
			t.Errorf("NormalizarPlaca(%q) = %q, se esperaba %q", entrada, got, esperado)
		}
	}
}

func valido() VehiculoRequest {
	return VehiculoRequest{
		Placa: "abc-123", Tipo: Automovil, Marca: "Mazda", Color: "Rojo",
		PropietarioNombre: "Ana Pérez", PropietarioDocumento: "1144000000",
	}
}

func TestValidar(t *testing.T) {
	casos := []struct {
		nombre   string
		cambiar  func(*VehiculoRequest)
		campoErr string // vacío = válido
	}{
		{"válido", func(*VehiculoRequest) {}, ""},
		{"bicicleta sin marca", func(r *VehiculoRequest) { r.Tipo, r.Marca = Bicicleta, "" }, ""},
		{"automóvil sin marca", func(r *VehiculoRequest) { r.Marca = "" }, "marca"},
		{"placa vacía", func(r *VehiculoRequest) { r.Placa = " - " }, "placa"},
		{"placa corta", func(r *VehiculoRequest) { r.Placa = "a1" }, "placa"},
		{"placa con símbolos", func(r *VehiculoRequest) { r.Placa = "ABC*12" }, "placa"},
		{"placa larga", func(r *VehiculoRequest) { r.Placa = "ABCDEFGHIJK" }, "placa"},
		{"tipo inválido", func(r *VehiculoRequest) { r.Tipo = "CAMION" }, "tipo"},
		{"sin color", func(r *VehiculoRequest) { r.Color = "  " }, "color"},
		{"nombre corto", func(r *VehiculoRequest) { r.PropietarioNombre = "Jo" }, "propietarioNombre"},
		{"documento corto", func(r *VehiculoRequest) { r.PropietarioDocumento = "123" }, "propietarioDocumento"},
	}
	for _, c := range casos {
		t.Run(c.nombre, func(t *testing.T) {
			req := valido()
			c.cambiar(&req)
			normalizado, err := Validar(req)
			if c.campoErr == "" {
				if err != nil {
					t.Fatalf("se esperaba válido, error: %v", err)
				}
				if normalizado.Placa != "ABC123" {
					t.Errorf("placa normalizada = %q", normalizado.Placa)
				}
				return
			}
			var e *Error
			if !errors.As(err, &e) || e.Codigo != CodigoValidacion {
				t.Fatalf("se esperaba error de validación, se obtuvo %v", err)
			}
			for _, d := range e.Detalles {
				if d.Campo == c.campoErr {
					return
				}
			}
			t.Errorf("no hay detalle para %q: %+v", c.campoErr, e.Detalles)
		})
	}
}
