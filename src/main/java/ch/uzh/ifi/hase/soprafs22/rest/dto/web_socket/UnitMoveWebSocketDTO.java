package ch.uzh.ifi.hase.soprafs22.rest.dto.web_socket;

import ch.uzh.ifi.hase.soprafs22.rest.dto.PositionDTO;

public class UnitMoveWebSocketDTO {
    private PositionDTO start;
    private PositionDTO destination;
    private Boolean hasMoved;

    public PositionDTO getStart() {
        return start;
    }

    public void setStart(PositionDTO start) {
        this.start = start;
    }

    public PositionDTO getDestination() {
        return destination;
    }

    public void setDestination(PositionDTO destination) {
        this.destination = destination;
    }

    public Boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(Boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
