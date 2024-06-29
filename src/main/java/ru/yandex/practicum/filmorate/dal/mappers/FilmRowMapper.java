package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmDtoRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("film_description"));
        film.setReleaseDate(resultSet.getDate("film_releaseDate").toLocalDate());
        film.setDuration(resultSet.getInt("film_duration"));
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("mpa_id"));
        mpa.setName(resultSet.getString("mpa_status"));
        film.setMpa(mpa);
        List<Genres> genres = new ArrayList<>();
        do {
            long genreId = resultSet.getLong("genre_id");
            if (genreId > 0) {
                Genres genre = new Genres();
                genre.setId(genreId);
                genre.setName(resultSet.getString("genre_name"));
                genres.add(genre);
            }
        } while (resultSet.next());

        film.setGenre(genres);
        return film;
    }
}
