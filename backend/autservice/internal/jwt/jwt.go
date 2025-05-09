package jwt

import (
	"time"

	jwt "github.com/golang-jwt/jwt/v4"
)

// Генерация токена
func GenerateJWT(jwt_secret []byte) (string, error) {
	now := time.Now()
	claims := jwt.RegisteredClaims{
		Issuer:    "example.com",
		IssuedAt:  jwt.NewNumericDate(now),
		ExpiresAt: jwt.NewNumericDate(now.Add(time.Hour * 1)),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(jwt_secret)
}

// Проверка токена на валидность
func CheckToken(tokenString string, jwt_secret []byte) bool {
	token, err := jwt.ParseWithClaims(tokenString, &jwt.RegisteredClaims{}, func(t *jwt.Token) (interface{}, error) {
		return jwt_secret, nil
	})

	if err != nil {
		return false
	}

	return token.Valid
}
