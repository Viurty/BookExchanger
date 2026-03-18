package hash

import (
	"golang.org/x/crypto/bcrypt"
)

// Проверяем пароль
func CheckPassword(hash_password, password string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hash_password), []byte(password))
	return err == nil
}

// Шифруем пароль
func EncryptPassword(password string) (string, error) {
	hashPassword, err := bcrypt.GenerateFromPassword([]byte(password), 12)
	if err != nil {
		return "", err
	}
	return string(hashPassword), nil
}
