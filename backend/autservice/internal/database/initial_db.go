package database

import (
	"context"
	"log"

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
		log.Fatalf("ошибка подключения к базе данных: %v", err)
	}

	query := `
	CREATE TABLE IF NOT EXISTS users (
		login TEXT PRIMARY KEY,
		role TEXT NOT NULL,
		password TEXT NOT NULL,
		phone TEXT NOT NULL
	);`

	_, err = dbx.ExecContext(ctx, query)
	if err != nil {
		log.Fatalf("ошибка создания таблицы: %v", err)
	}

	return NewDBX(dbx)
}
