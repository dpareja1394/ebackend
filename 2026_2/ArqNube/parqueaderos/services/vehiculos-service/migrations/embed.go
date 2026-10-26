// Package migrations embebe los scripts SQL que se aplican al arrancar el servicio.
package migrations

import "embed"

//go:embed *.sql
var FS embed.FS
