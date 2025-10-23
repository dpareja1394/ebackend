package handlers

import (
	"demo_go/config"
	"demo_go/repositories"
	"github.com/gin-gonic/gin"
	"net/http"
)

type UserAccountHandler struct {
	userAccountRepository repositories.UserAccountRepository
}

func NewUserAccountHandler() UserAccountHandler {
	database := config.DB
	return UserAccountHandler{
		userAccountRepository: repositories.NewUserAccountRepository(database),
	}
}

// GetUserClientAccounts maneja la obtención de todas las cuentas de usuario
func (h *UserAccountHandler) GetUserClientAccounts(c *gin.Context) {
	userAccounts, err := h.userAccountRepository.GetUserClientAccounts()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	c.JSON(http.StatusOK, userAccounts)
}
