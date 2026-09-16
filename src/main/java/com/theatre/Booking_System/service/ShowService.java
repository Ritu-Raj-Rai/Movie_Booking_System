package com.theatre.Booking_System.service;

import com.theatre.Booking_System.dto.ShowResponseDTO;
import com.theatre.Booking_System.model.Show;
import com.theatre.Booking_System.repo.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;

    public List<ShowResponseDTO> getShowsByMovie(Long movieId) {
        return showRepository.findByMovieId(movieId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + id));
    }

    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    private ShowResponseDTO mapToDTO(Show show) {
        return ShowResponseDTO.builder()
                .id(show.getId())
                .movieTitle(show.getMovie().getTitle())
                .theatreName(show.getScreen().getTheatre().getName())
                .screenNumber(show.getScreen().getScreenNumber())
                .showTime(show.getShowTime())
                .price(show.getPrice())
                .build()
                ;
    }
}