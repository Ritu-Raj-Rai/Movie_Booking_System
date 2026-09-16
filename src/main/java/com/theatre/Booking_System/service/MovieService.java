package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.MovieResponseDTO;
import com.theatre.Booking_System.model.Movie;
import com.theatre.Booking_System.repo.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<MovieResponseDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MovieResponseDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        return mapToDTO(movie);
    }

    public MovieResponseDTO createMovie(Movie movie) {
        Movie saved = movieRepository.save(movie);
        return mapToDTO(saved);
    }

    private MovieResponseDTO mapToDTO(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .durationMinutes(movie.getDurationMinutes())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .releaseDate(movie.getReleaseDate())
                .build();
    }
}