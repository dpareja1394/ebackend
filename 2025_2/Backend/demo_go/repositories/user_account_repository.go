package repositories

import (
  "gorm.io/gorm"
  "demo_go/domains"
)

type UserAccountRepository interface {
  GetUserClientAccounts() ([]domains.UserAccount, error)
}

type userAccountRepositoryImpl struct {
  db *gorm.DB
}

