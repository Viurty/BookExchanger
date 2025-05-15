package database

import (
	"context"
	"log"

	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/jmoiron/sqlx"
)

type DBX struct {
	dbx *sqlx.DB
}

func NewDBX(dbx *sqlx.DB) *DBX {
	return &DBX{dbx: dbx}
}

func StartDB(dsn string, ctx context.Context) *DBX {
	dbx, err := sqlx.ConnectContext(ctx, "pgx", dsn)
	if err != nil {
		log.Printf("ошибка подключения к базе данных: %v", err)
		return nil
	}

	query := `
	CREATE TABLE IF NOT EXISTS users (
		login TEXT PRIMARY KEY,
		role TEXT NOT NULL,
		password TEXT NOT NULL,
		phone TEXT NOT NULL,
		token TEXT
	);`

	_, err = dbx.ExecContext(ctx, query)
	if err != nil {
		log.Printf("ошибка создания таблицы: %v", err)
	}

	log.Printf("Датабаза подключена успешно!")
	return NewDBX(dbx)
}
