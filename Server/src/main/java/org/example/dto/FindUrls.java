package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FindUrls {
    private String url;
    private String size;
    private LocalDateTime time;
    @JsonIgnore
    private boolean isSend = false;

    public FindUrls(String url, String size, LocalDateTime time) {
        this.url = url;
        this.size = size;
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FindUrls findUrls = (FindUrls) obj;
        return url.equals(findUrls.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
