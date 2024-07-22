package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("film_description"));
        film.setReleaseDate(rs.getDate("film_releaseDate").toLocalDate());
        film.setDuration(rs.getInt("film_duration"));
        Mpa mpa = new Mpa();
        String mpaName = rs.getString("mpa_status");
        if (mpaName != null) {
            mpa.setName(mpaName);
            mpa.setId(rs.getLong("mpa_id"));
        }
        film.setMpa(mpa);
        return film;
    }
}
