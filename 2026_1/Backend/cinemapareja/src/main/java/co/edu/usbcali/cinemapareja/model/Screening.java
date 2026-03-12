package co.edu.usbcali.cinemapareja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "screening")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false, referencedColumnName = "id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false, referencedColumnName = "id")
    private Theater theater;

    @Column(name = "date_time", nullable = false)
    private Timestamp dateTime;
}
