package main

import (
	"demo_go/config"
)

func main() {
	// Crear una nueva instancia del router Gin
	//r := gin.Default()

	// Conectar a la base de datos
	config.ConnectDatabase()

	// Configurar las rutas
	//routes.SetupRoutes(r)

	// Iniciar el servidor en el puerto 8082
	//r.Run(":8082")
}
