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

func NewUserAccountRepository(db *gorm.DB) UserAccountRepository {
  return userAccountRepositoryImpl{db: db}
}

func (u userAccountRepositoryImpl) GetUserClientAccounts() ([]domains.UserAccount, error) {
  var userAccounts []domains.UserAccount
  query := u.db.Table("user_account").Order("id ASC")

  err := query.Find(&userAccounts).Error
  
  if err != nil {
    return nil, err
  }
  return userAccounts, nil
}

