package org.example.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class MouseMovement {
    private int x;
    private int y;
    private int scroll;
}
