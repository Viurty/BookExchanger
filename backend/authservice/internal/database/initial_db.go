package database

import (
	"context"
	"fmt"

	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/jmoiron/sqlx"
)

type DBX struct {
	dbx *sqlx.DB
}

func NewDBX(dbx *sqlx.DB) *DBX {
	return &DBX{dbx: dbx}
}

func StartDB(dsn string, ctx context.Context) (*DBX, error) {
	dbx, err := sqlx.ConnectContext(ctx, "pgx", dsn)
	if err != nil {
		return nil, fmt.Errorf("ошибка подключения к базе данных: %w", err)
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
		_ = dbx.Close()
		return nil, fmt.Errorf("ошибка создания таблицы users: %w", err)
	}

	return NewDBX(dbx), nil
}
