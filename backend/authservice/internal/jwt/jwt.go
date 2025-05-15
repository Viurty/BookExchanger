package jwt

import (
	"log"
	"time"

	"fmt"
	"strings"

	jwt "github.com/golang-jwt/jwt/v4"
)

// Генерация токена
func GenerateJWT(jwt_secret []byte) (string, error) {
	now := time.Now()
	claims := jwt.RegisteredClaims{
		Issuer:    "example.com",
		IssuedAt:  jwt.NewNumericDate(now),
		ExpiresAt: jwt.NewNumericDate(now.Add(12 * time.Hour)),
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(jwt_secret)
}

// Проверка токена на валидность
func CheckToken(tokenString string, jwt_secret []byte) bool {
	tokenString = strings.TrimSpace(tokenString)
	if strings.HasPrefix(tokenString, "Bearer ") {
		tokenString = strings.TrimPrefix(tokenString, "Bearer ")
	}
	tokenString = strings.ReplaceAll(tokenString, "\n", "")
	tokenString = strings.ReplaceAll(tokenString, "\r", "")

	keyFunc := func(t *jwt.Token) (interface{}, error) {
		if t.Method != jwt.SigningMethodHS256 {
			return nil, fmt.Errorf("unexpected signing method: %v", t.Header["alg"])
		}
		return jwt_secret, nil
	}

	token, err := jwt.ParseWithClaims(tokenString, &jwt.RegisteredClaims{}, keyFunc)

	if err != nil {
		log.Printf("Ошибка разбора токена: %v", err)
		return false
	}
	if !token.Valid {
		log.Println("Токен недействителен")
		return false
	}

	claims, ok := token.Claims.(*jwt.RegisteredClaims)
	if !ok {
		log.Println("Неверный тип claims в токене")
		return false
	}
	if err := claims.Valid(); err != nil {
		log.Printf("Claims не прошли проверку: %v", err)
		return false
	}

	return true
}
