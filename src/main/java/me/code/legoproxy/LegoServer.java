package me.code.legoproxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LegoServer {
    private String address;
    private int port;

    public int getPort() {
        return this.port;
    }

}
