package database

import (
	"context"
	"database/sql"
)

type UserFromDB struct {
	Login    string `json:"login" db:"login"`
	Role     string `json:"role" db:"role"`
	Password string `json:"password" db:"password"`
	Phone    string `json:"phone" db:"phone"`
}

func (d *DBX) Close() error {
	return d.dbx.Close()
}

func (d *DBX) GetUser(ctx context.Context, login string) (UserFromDB, error) {
	var user UserFromDB
	err := d.dbx.GetContext(ctx, &user, "SELECT * FROM users WHERE login=$1", login)
	return user, err
}

func (d *DBX) CreateUser(ctx context.Context, user UserFromDB) error {
	_, err := d.dbx.ExecContext(ctx, `INSERT INTO users (login, role, password, phone) VALUES ($1, $2, $3, $4);`, user.Login, user.Role, user.Password, user.Phone)
	return err
}

func (d *DBX) UpdateRole(ctx context.Context, user UserFromDB) error {
	res, err := d.dbx.ExecContext(ctx, "UPDATE users SET role = $1 WHERE login = $2;", user.Role, user.Login)
	rows, _ := res.RowsAffected()
	if rows == 0 {
		return sql.ErrNoRows
	}
	return err
}
