package net.codejavaspring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "apimovies")
public class ApiMovies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID autogenerado para la tabla

    @Column(name = "movie_id", nullable = false)
    private String movieId; // ID de la película (IMDB ID)

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID del usuario

    @Column(name = "title", nullable = false)
    private String title; // Título de la película

    @Column(name = "year", nullable = false)
    private int year; // Año de la película

    @Column(name = "director", nullable = false)
    private String director; // Director de la película

    @Column(name = "genre", nullable = false)
    private String genre; // Género de la película

    @Column(name = "plot", nullable = false)
    private String plot; // Resumen de la película

    @Column(name = "poster_url", nullable = true)
    private String posterUrl; // URL del póster de la película

    // Constructor vacío
    public ApiMovies() {}

    // Constructor con parámetros
    public ApiMovies(String movieId, Long userId, String title, int year, String director, String genre, String plot, String posterUrl) {
        this.movieId = movieId;
        this.userId = userId;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.plot = plot;
        this.posterUrl = posterUrl;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
