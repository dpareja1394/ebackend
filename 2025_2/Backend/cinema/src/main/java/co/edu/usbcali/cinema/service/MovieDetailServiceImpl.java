package co.edu.usbcali.cinema.service;

import co.edu.usbcali.cinema.domain.MovieDetail;
import co.edu.usbcali.cinema.dto.MovieDetailResponseDTO;
import co.edu.usbcali.cinema.mapper.MovieDetailMapper;
import co.edu.usbcali.cinema.repository.MovieDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieDetailServiceImpl implements MovieDetailService {

    private final MovieDetailRepository movieDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MovieDetailResponseDTO> findAll() {
        List<MovieDetail> movieDetails = movieDetailRepository.findAll();
        List<MovieDetailResponseDTO> responseDTOS = MovieDetailMapper.domainToResponseDTO(movieDetails);
        return responseDTOS;
    }
}
