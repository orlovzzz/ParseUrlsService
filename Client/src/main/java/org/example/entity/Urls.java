package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Urls {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String url;
    private String size;
    private LocalDateTime time;

    @Override
    public String toString() {
        return url + "\t" + size + "\t" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-DD HH:mm:ss")) + "\n";
    }
}
